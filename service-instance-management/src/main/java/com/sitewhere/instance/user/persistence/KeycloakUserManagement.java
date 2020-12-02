/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.instance.user.persistence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.ServerInfoResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.info.SystemInfoRepresentation;

import com.google.inject.Inject;
import com.sitewhere.instance.configuration.InstanceManagementConfiguration;
import com.sitewhere.microservice.api.user.IUserManagement;
import com.sitewhere.microservice.lifecycle.AsyncStartLifecycleComponent;
import com.sitewhere.rest.model.search.Pager;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.user.GrantedAuthority;
import com.sitewhere.rest.model.user.Role;
import com.sitewhere.rest.model.user.User;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IGrantedAuthoritySearchCriteria;
import com.sitewhere.spi.user.IRole;
import com.sitewhere.spi.user.IRoleSearchCriteria;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.spi.user.IUserSearchCriteria;
import com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest;
import com.sitewhere.spi.user.request.IRoleCreateRequest;
import com.sitewhere.spi.user.request.IUserCreateRequest;

import io.sitewhere.k8s.api.ISiteWhereKubernetesClient;

/**
 * Implementation of {@link IUserManagement} that interacts with an underlying
 * Keycloak instance.
 */
public class KeycloakUserManagement extends AsyncStartLifecycleComponent implements IUserManagement {

    /** Client id for OpenID Connect support */
    private static final String CLIENT_ID_OPENID_CONNECT = "sitewhere-openid";

    /** Configuration settings */
    private InstanceManagementConfiguration configuration;

    /** Keycloak client */
    private Keycloak keycloak;

    @Inject
    public KeycloakUserManagement(InstanceManagementConfiguration configuration) {
	super(LifecycleComponentType.DataStore);
	this.configuration = configuration;
    }

    /*
     * @see com.sitewhere.spi.microservice.lifecycle.IAsyncStartLifecycleComponent#
     * asyncStart()
     */
    @Override
    public void asyncStart() throws SiteWhereException {
	IInstanceSettings settings = getMicroservice().getInstanceSettings();
	String serviceName = settings.getKeycloakServiceName() + "." + ISiteWhereKubernetesClient.NS_SITEWHERE_SYSTEM;

	// Create Keycloak API client and test it.
	String url = String.format("http://%s:%s/auth", serviceName, settings.getKeycloakApiPort());
	this.keycloak = KeycloakBuilder.builder().serverUrl(url).realm(settings.getKeycloakMasterRealm())
		.username(settings.getKeycloakMasterUsername()).password(settings.getKeycloakMasterPassword())
		.clientId("admin-cli").build();
	ServerInfoResource server = getKeycloak().serverInfo();
	SystemInfoRepresentation system = server.getInfo().getSystemInfo();
	getLogger().info(String.format("Keycloak API validated as version '%s'.", system.getVersion()));

	// Make sure that the expected realm exists.
	assureRealmExists();

	// Register an OpenID Connect client for the realm.
	assureOpenIdClient();
    }

    /**
     * Assure that the expected Keycloak realm exists.
     * 
     * @throws SiteWhereException
     */
    protected void assureRealmExists() throws SiteWhereException {
	String realmName = getMicroservice().getInstanceSettings().getKeycloakRealm();
	try {
	    getRealmResource().toRepresentation();
	    getLogger().info(String.format("Realm for instance was found (%s).", realmName));
	} catch (NotFoundException e) {
	    getLogger().info(String.format("Realm for instance was not found (%s). Creating...", realmName));
	    try {
		RealmRepresentation newRealm = new RealmRepresentation();
		newRealm.setId(realmName);
		newRealm.setRealm(realmName);
		newRealm.setDisplayName("SiteWhere");
		newRealm.setEnabled(true);
		getKeycloak().realms().create(newRealm);
		getLogger().info(String.format("Successfully created realm for instance (%s).", realmName));
	    } catch (Exception e1) {
		throw new SiteWhereException(String.format("Unable to create realm for instance (%s).", realmName), e1);
	    }
	}
    }

    /**
     * Assure that openid-connect client exists.
     * 
     * @throws SiteWhereException
     */
    protected void assureOpenIdClient() throws SiteWhereException {
	try {
	    ClientResource clientResource = getRealmResource().clients().get(CLIENT_ID_OPENID_CONNECT);
	    ClientRepresentation client = clientResource.toRepresentation();
	    getLogger().info(String.format("OpenID Connect client was found (%s).", client.getId()));
	} catch (NotFoundException e) {
	    getLogger().info(
		    String.format("OpenID Connect client was not found (%s). Creating...", CLIENT_ID_OPENID_CONNECT));
	    try {
		ClientRepresentation newClient = new ClientRepresentation();
		newClient.setId(CLIENT_ID_OPENID_CONNECT);
		newClient.setName("OpenId Connect");
		newClient.setStandardFlowEnabled(true);
		newClient.setDirectAccessGrantsEnabled(true);
		newClient.setProtocol("openid-connect");
		newClient.setPublicClient(false);
		newClient.setRedirectUris(Collections.singletonList("http://*"));
		newClient.setSecret(getMicroservice().getInstanceSettings().getKeycloakOidcSecret());
		newClient.setEnabled(true);
		Response result = getRealmResource().clients().create(newClient);
		if (result.getStatus() == HttpStatus.SC_CONFLICT) {
		    getLogger().info(String.format("Found existing OpenID Connect client (%s).", newClient.getId()));
		} else if (result.getStatus() != HttpStatus.SC_CREATED) {
		    throw new SiteWhereException(result.getStatusInfo().getReasonPhrase());
		} else {
		    getLogger().info(String.format("Created OpenID Connect client (%s).", newClient.getId()));
		}
	    } catch (Exception e1) {
		throw new SiteWhereException(
			String.format("Unable to create realm for instance (%s).", CLIENT_ID_OPENID_CONNECT), e1);
	    }
	}
    }

    /**
     * Get realm resource based on configured value.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected RealmResource getRealmResource() throws SiteWhereException {
	String realmName = getMicroservice().getInstanceSettings().getKeycloakRealm();
	return getKeycloak().realm(realmName);
    }

    /**
     * Convert Keycloak user to SW model representation.
     * 
     * @param kc
     * @param includeRoles
     * @return
     * @throws SiteWhereException
     */
    protected User convert(UserRepresentation kc, boolean includeRoles) throws SiteWhereException {
	User user = new User();
	user.setUsername(kc.getUsername());
	user.setFirstName(kc.getFirstName());
	user.setLastName(kc.getLastName());
	user.setEmail(kc.getEmail());
	user.setCreatedDate(new Date(kc.getCreatedTimestamp()));
	if (includeRoles) {
	    user.setRoles(new ArrayList<>());
	    List<String> groups = kc.getGroups();
	    if (groups != null) {
		for (String group : groups) {
		    GroupResource groupResource = getRealmResource().groups().group(group);
		    Role role = convert(groupResource.toRepresentation(), true);
		    user.getRoles().add(role);
		}
	    }
	}
	return user;
    }

    /**
     * Convert Keycloak role to SW granted authority.
     * 
     * @param kc
     * @return
     */
    protected GrantedAuthority convert(RoleRepresentation kc) {
	GrantedAuthority auth = new GrantedAuthority();
	auth.setAuthority(kc.getName());
	auth.setDescription(kc.getDescription());
	return auth;
    }

    /**
     * Convert a Keycloak group to SW role.
     * 
     * @param kc
     * @param includeAuthorities
     * @return
     * @throws SiteWhereException
     */
    protected Role convert(GroupRepresentation kc, boolean includeAuthorities) throws SiteWhereException {
	Role role = new Role();
	role.setRole(kc.getName());
	role.setDescription(kc.getPath());
	if (includeAuthorities) {
	    role.setAuthorities(new ArrayList<>());
	    List<String> realmRoles = kc.getRealmRoles();
	    if (realmRoles != null) {
		for (String realmRole : realmRoles) {
		    RoleRepresentation kcRole = getRealmResource().rolesById().getRole(realmRole);
		    role.getAuthorities().add(convert(kcRole));
		}
	    }
	}
	return role;
    }

    /**
     * Create a user representation based on API request.
     * 
     * @param request
     * @return
     */
    protected UserRepresentation createUserFromRequest(IUserCreateRequest request) {
	CredentialRepresentation credential = new CredentialRepresentation();
	credential.setType(CredentialRepresentation.PASSWORD);
	credential.setValue(request.getPassword());

	UserRepresentation user = new UserRepresentation();
	user.setUsername(request.getUsername());
	user.setFirstName(request.getFirstName());
	user.setLastName(request.getLastName());
	user.setEmail(request.getEmail());
	user.setCredentials(Arrays.asList(credential));
	user.setEnabled(true);
	return user;
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#createUser(com.sitewhere.
     * spi.user.request.IUserCreateRequest, java.lang.Boolean)
     */
    @Override
    public IUser createUser(IUserCreateRequest request, Boolean encodePassword) throws SiteWhereException {
	UserRepresentation user = createUserFromRequest(request);
	Response result = getRealmResource().users().create(user);
	if (result.getStatus() != HttpStatus.SC_CREATED) {
	    throw new SiteWhereException(result.getStatusInfo().getReasonPhrase());
	}
	return getUserByUsername(request.getUsername());
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#importUser(com.sitewhere.
     * spi.user.IUser, boolean)
     */
    @Override
    public IUser importUser(IUser user, boolean overwrite) throws SiteWhereException {
	throw new SiteWhereException("Not implemented.");
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#authenticate(java.lang.
     * String, java.lang.String, boolean)
     */
    @Override
    public IUser authenticate(String username, String password, boolean updateLastLogin) throws SiteWhereException {
	UserResource userResource = getRealmResource().users().get(username);
	if (userResource != null) {
	    List<CredentialRepresentation> credentials = userResource.credentials();
	    for (CredentialRepresentation credential : credentials) {
		if (credential.getType() == CredentialRepresentation.PASSWORD) {
		    if (password != null && password.equals(credential.getValue())) {
			return getUserByUsername(username);
		    }
		}
	    }
	}
	throw new SiteWhereException("User credentials do not match.");
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#updateUser(java.lang.
     * String, com.sitewhere.spi.user.request.IUserCreateRequest, boolean)
     */
    @Override
    public IUser updateUser(String username, IUserCreateRequest request, boolean encodePassword)
	    throws SiteWhereException {
	UserRepresentation user = createUserFromRequest(request);
	getRealmResource().users().get(username).update(user);
	return getUserByUsername(request.getUsername());
    }

    /**
     * Find a single user matching the given username.
     * 
     * @param username
     * @return
     * @throws SiteWhereException
     */
    protected UserRepresentation findSingleUserByUsername(String username) throws SiteWhereException {
	List<UserRepresentation> matches = getRealmResource().users().search(username, true);
	if (matches.size() > 1) {
	    throw new SiteWhereException(String.format("Matched username: %s", matches.get(0).getUsername()));
	} else if (matches.size() == 0) {
	    return null;
	}
	return matches.get(0);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#getUserByUsername(java.
     * lang.String)
     */
    @Override
    public IUser getUserByUsername(String username) throws SiteWhereException {
	try {
	    UserRepresentation match = findSingleUserByUsername(username);
	    return convert(match, true);
	} catch (NotFoundException e) {
	    return null;
	}
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#listUsers(com.sitewhere.
     * spi.user.IUserSearchCriteria)
     */
    @Override
    public ISearchResults<IUser> listUsers(IUserSearchCriteria criteria) throws SiteWhereException {
	List<UserRepresentation> kcUsers = getRealmResource().users().list();
	Pager<IUser> pager = new Pager<IUser>(criteria);
	for (UserRepresentation kcUser : kcUsers) {
	    IUser user = convert(kcUser, false);
	    pager.process(user);
	}
	return new SearchResults<IUser>(pager.getResults(), pager.getTotal());
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#deleteUser(java.lang.
     * String)
     */
    @Override
    public IUser deleteUser(String username) throws SiteWhereException {
	UserRepresentation match = findSingleUserByUsername(username);
	if (match == null) {
	    return null;
	}
	Response result = getRealmResource().users().delete(username);
	if (result.getStatus() != HttpStatus.SC_OK) {
	    throw new SiteWhereException(result.getStatusInfo().getReasonPhrase());
	}
	return convert(match, false);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#createGrantedAuthority(
     * com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest)
     */
    @Override
    public IGrantedAuthority createGrantedAuthority(IGrantedAuthorityCreateRequest request) throws SiteWhereException {
	RoleRepresentation role = new RoleRepresentation();
	role.setComposite(request.isGroup());
	role.setName(request.getAuthority());
	role.setDescription(request.getDescription());
	getRealmResource().roles().create(role);
	role = getKeycloakRoleByName(request.getAuthority());

	if (request.getParent() != null) {
	    try {
		RoleRepresentation parent = getKeycloakRoleByName(request.getParent());
		getRealmResource().rolesById().addComposites(parent.getId(), Collections.singletonList(role));
	    } catch (NotFoundException e) {
		getLogger().warn(
			String.format("Unable to composite role to non-existent parent: %s", request.getParent()));
	    }
	}

	return convert(role);
    }

    /**
     * Get Keycloak role by name.
     * 
     * @param name
     * @return
     * @throws SiteWhereException
     */
    protected RoleRepresentation getKeycloakRoleByName(String name) throws SiteWhereException {
	List<RoleRepresentation> all = getRealmResource().roles().list();
	for (RoleRepresentation current : all) {
	    if (current.getName().equals(name)) {
		return current;
	    }
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#getGrantedAuthorityByName
     * (java.lang.String)
     */
    @Override
    public IGrantedAuthority getGrantedAuthorityByName(String name) throws SiteWhereException {
	RoleRepresentation match = getKeycloakRoleByName(name);
	return match == null ? null : convert(match);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#updateGrantedAuthority(
     * java.lang.String,
     * com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest)
     */
    @Override
    public IGrantedAuthority updateGrantedAuthority(String name, IGrantedAuthorityCreateRequest request)
	    throws SiteWhereException {
	throw new SiteWhereException("Not implemented.");
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#listGrantedAuthorities(
     * com.sitewhere.spi.user.IGrantedAuthoritySearchCriteria)
     */
    @Override
    public ISearchResults<IGrantedAuthority> listGrantedAuthorities(IGrantedAuthoritySearchCriteria criteria)
	    throws SiteWhereException {
	List<RoleRepresentation> kcRoles = getRealmResource().roles().list();
	Pager<IGrantedAuthority> pager = new Pager<IGrantedAuthority>(criteria);
	for (RoleRepresentation kcRole : kcRoles) {
	    IGrantedAuthority authority = convert(kcRole);
	    pager.process(authority);
	}
	return new SearchResults<IGrantedAuthority>(pager.getResults(), pager.getTotal());
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#deleteGrantedAuthority(
     * java.lang.String)
     */
    @Override
    public void deleteGrantedAuthority(String authority) throws SiteWhereException {
	getRealmResource().roles().deleteRole(authority);
    }

    /*
     * @see com.sitewhere.microservice.api.user.IUserManagement#getRoles(java.lang.
     * String)
     */
    @Override
    public List<IRole> getRoles(String username) throws SiteWhereException {
	IUser user = getUserByUsername(username);
	if (user == null) {
	    throw new SiteWhereException(String.format("User not found: %s", username));
	}
	return user.getRoles();
    }

    /*
     * @see com.sitewhere.microservice.api.user.IUserManagement#addRoles(java.lang.
     * String, java.util.List)
     */
    @Override
    public List<IRole> addRoles(String username, List<String> roles) throws SiteWhereException {
	UserResource userResource = getRealmResource().users().get(username);
	if (userResource != null) {
	    List<IRole> created = new ArrayList<>();
	    for (String role : roles) {
		userResource.joinGroup(role);
		created.add(convert(getRealmResource().groups().group(role).toRepresentation(), false));
	    }
	    return created;
	}
	throw new SiteWhereException(String.format("User not found: %s", username));
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#removeRoles(java.lang.
     * String, java.util.List)
     */
    @Override
    public List<IRole> removeRoles(String username, List<String> roles) throws SiteWhereException {
	UserResource userResource = getRealmResource().users().get(username);
	if (userResource != null) {
	    List<IRole> removed = new ArrayList<>();
	    for (String role : roles) {
		userResource.leaveGroup(role);
		removed.add(convert(getRealmResource().groups().group(role).toRepresentation(), false));
	    }
	    return removed;
	}
	throw new SiteWhereException(String.format("User not found: %s", username));
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#createRole(com.sitewhere.
     * spi.user.request.IRoleCreateRequest)
     */
    @Override
    public IRole createRole(IRoleCreateRequest request) throws SiteWhereException {
	GroupRepresentation group = new GroupRepresentation();
	group.setName(request.getRole());
	Response result = getRealmResource().groups().add(group);
	if (result.getStatus() != HttpStatus.SC_CREATED) {
	    throw new SiteWhereException(result.getStatusInfo().getReasonPhrase());
	}
	String location = result.getLocation().getPath();
	String id = location.substring(location.lastIndexOf('/') + 1);
	List<RoleRepresentation> roles = new ArrayList<>();
	for (String authority : request.getAuthorities()) {
	    roles.add(getRealmResource().roles().get(authority).toRepresentation());
	}
	getRealmResource().groups().group(id).roles().realmLevel().add(roles);
	return getRoleByName(group.getName());
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#getRoleByName(java.lang.
     * String)
     */
    @Override
    public IRole getRoleByName(String name) throws SiteWhereException {
	try {
	    List<GroupRepresentation> matches = getRealmResource().groups().groups(name, 0, 1);
	    if (matches.size() == 0) {
		return null;
	    }
	    return convert(matches.get(0), true);
	} catch (NotFoundException e) {
	    return null;
	}
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#updateRole(java.lang.
     * String, com.sitewhere.spi.user.request.IRoleCreateRequest)
     */
    @Override
    public IRole updateRole(String name, IRoleCreateRequest request) throws SiteWhereException {
	throw new SiteWhereException("Not implemented.");
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#listRoles(com.sitewhere.
     * spi.user.IRoleSearchCriteria)
     */
    @Override
    public ISearchResults<IRole> listRoles(IRoleSearchCriteria criteria) throws SiteWhereException {
	List<GroupRepresentation> kcGroups = getRealmResource().groups().groups();
	Pager<IRole> pager = new Pager<IRole>(criteria);
	for (GroupRepresentation kcGroup : kcGroups) {
	    IRole role = convert(kcGroup, false);
	    pager.process(role);
	}
	return new SearchResults<IRole>(pager.getResults(), pager.getTotal());
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#deleteRole(java.lang.
     * String)
     */
    @Override
    public void deleteRole(String role) throws SiteWhereException {
	getRealmResource().groups().group(role).remove();
    }

    protected InstanceManagementConfiguration getConfiguration() {
	return configuration;
    }

    protected Keycloak getKeycloak() {
	return keycloak;
    }
}
