/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.instance.user.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.codec.binary.Base64;
import org.apache.syncope.client.lib.SyncopeClient;
import org.apache.syncope.common.lib.SyncopeClientException;
import org.apache.syncope.common.lib.patch.AttrPatch;
import org.apache.syncope.common.lib.patch.PasswordPatch;
import org.apache.syncope.common.lib.patch.StringPatchItem;
import org.apache.syncope.common.lib.patch.UserPatch;
import org.apache.syncope.common.lib.to.AnyTypeClassTO;
import org.apache.syncope.common.lib.to.ApplicationTO;
import org.apache.syncope.common.lib.to.AttrTO;
import org.apache.syncope.common.lib.to.PagedResult;
import org.apache.syncope.common.lib.to.PlainSchemaTO;
import org.apache.syncope.common.lib.to.PrivilegeTO;
import org.apache.syncope.common.lib.to.RoleTO;
import org.apache.syncope.common.lib.to.UserTO;
import org.apache.syncope.common.lib.types.AttrSchemaType;
import org.apache.syncope.common.lib.types.SchemaType;
import org.apache.syncope.common.rest.api.beans.AnyQuery;
import org.apache.syncope.common.rest.api.service.AnyTypeClassService;
import org.apache.syncope.common.rest.api.service.ApplicationService;
import org.apache.syncope.common.rest.api.service.RoleService;
import org.apache.syncope.common.rest.api.service.SchemaService;
import org.apache.syncope.common.rest.api.service.UserService;

import com.google.inject.Inject;
import com.sitewhere.instance.configuration.InstanceManagementConfiguration;
import com.sitewhere.microservice.api.user.IUserManagement;
import com.sitewhere.microservice.lifecycle.AsyncStartLifecycleComponent;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.user.GrantedAuthority;
import com.sitewhere.rest.model.user.GrantedAuthoritySearchCriteria;
import com.sitewhere.rest.model.user.Role;
import com.sitewhere.rest.model.user.RoleSearchCriteria;
import com.sitewhere.rest.model.user.User;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
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

/**
 * Interact with Apache Synope instance to manage users.
 */
public class SyncopeUserManagement extends AsyncStartLifecycleComponent implements IUserManagement {

//    /** Number of seconds between fallback attempts for connecting to Syncope */
//    private static final int CONNECT_SECS_BETWEEN_RETRIES = 10;
//
//    /** Default Syncope username */
//    private static final String SYNCOPE_USERNAME = "synadmin";
//
//    /** Default Syncope password */
//    private static final String SYNCOPE_PASSWORD = "password";

    /** SiteWhere Syncope application key */
    private static final String SITEWHERE_APPLICATION_KEY = "sitewhere";

    /** Attribute that holds first name */
    private static final String ATTR_FIRST_NAME = "firstName";

    /** Attribute that holds JSON payload */
    private static final String ATTR_LAST_NAME = "lastName";

    /** Attribute that holds JSON payload */
    private static final String ATTR_JSON = "json";

    /** Time in minutes to wait between token refreshes */
    private static final int TOKEN_REFRESH_IN_MINUTES = 5;

    /** Configuration settings */
    private InstanceManagementConfiguration configuration;

    /** Client for interacting with Syncope */
    private SyncopeClient client;

    /** Indicates whether connection to Syncope failed */
    private AtomicBoolean connectionFailed = new AtomicBoolean(false);

    /** Allows blocking until Syncope is available */
    private CountDownLatch syncopeAvailable = new CountDownLatch(1);

    /** Provides thread for waiter */
    private ExecutorService waiter;

    /** Provides thread for refreshing access token */
    private ExecutorService refresher;

    @Inject
    public SyncopeUserManagement(InstanceManagementConfiguration configuration) {
	super(LifecycleComponentType.DataStore);
	this.configuration = configuration;
    }

    /*
     * @see com.sitewhere.microservice.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Wait for connection in background thread.
	this.waiter = Executors.newSingleThreadExecutor(new SyncopeWaiterThreadFactory());
	getWaiter().execute(new SyncopeConnectionWaiter());

	// Prepare executor for refreshing access token.
	this.refresher = Executors.newSingleThreadExecutor();
	refresher.execute(new SyncopeConnectionRefresher());
    }

    /*
     * @see com.sitewhere.spi.microservice.lifecycle.IAsyncStartLifecycleComponent#
     * asyncStart()
     */
    @Override
    public void asyncStart() throws SiteWhereException {
	// Block until Syncope is available.
	try {
	    getSyncopeAvailable().await();
	} catch (InterruptedException e) {
	    getLogger().info("Interrupted while waiting for Syncope to become available.");
	    return;
	}

	// Verify that connection was successful.
	if (getConnectionFailed().get()) {
	    throw new SiteWhereException("Connection to Syncope could not be established.");
	}

//	String domain = getClient().getDomain();
//	getLogger().info(String.format("Syncope client connected to %s:%d using domain %s.",
//		getConfiguration().getUserManagement().getSyncopeHost(),
//		getConfiguration().getUserManagement().getSyncopePort(), domain));

	// Verify that SiteWhere application exists.
	getOrCreateSiteWhereApplication();
    }

    /**
     * Get or create Syncope application for SiteWhere.
     *
     * @throws SiteWhereException
     */
    protected ApplicationTO getOrCreateSiteWhereApplication() throws SiteWhereException {
	try {
	    return getApplicationService().read(SITEWHERE_APPLICATION_KEY);
	} catch (SyncopeClientException e) {
	    getLogger().info("SiteWhere Syncope application descriptor not found. Creating descriptor...");
	    ApplicationTO application = new ApplicationTO();
	    application.setDescription("SiteWhere Application");
	    application.setKey(SITEWHERE_APPLICATION_KEY);
	    getApplicationService().create(application);
	    getLogger()
		    .info(String.format("Created application '%s' with SiteWhere key.", application.getDescription()));

	    // First name user attribute.
	    PlainSchemaTO firstName = new PlainSchemaTO();
	    firstName.setKey(ATTR_FIRST_NAME);
	    firstName.setType(AttrSchemaType.String);
	    getSchemaService().create(SchemaType.PLAIN, firstName);

	    // Last name user attribute.
	    PlainSchemaTO lastName = new PlainSchemaTO();
	    lastName.setKey(ATTR_LAST_NAME);
	    lastName.setType(AttrSchemaType.String);
	    getSchemaService().create(SchemaType.PLAIN, lastName);

	    // JSON user attribute.
	    PlainSchemaTO json = new PlainSchemaTO();
	    json.setKey(ATTR_JSON);
	    json.setType(AttrSchemaType.Binary);
	    getSchemaService().create(SchemaType.PLAIN, json);

	    AnyTypeClassTO baseUser = new AnyTypeClassTO();
	    baseUser.setKey("BaseUser");
	    baseUser.getPlainSchemas().add(ATTR_FIRST_NAME);
	    baseUser.getPlainSchemas().add(ATTR_LAST_NAME);
	    baseUser.getPlainSchemas().add(ATTR_JSON);
	    getAnyTypeClassService().update(baseUser);

	    return application;
	}
    }

    /*
     * @see
     * com.sitewhere.microservice.lifecycle.LifecycleComponent#stop(com.sitewhere.
     * spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (this.waiter != null) {
	    this.waiter.shutdownNow();
	}
	if (this.refresher != null) {
	    this.refresher.shutdownNow();
	}
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#createUser(com.sitewhere.
     * spi.user.request.IUserCreateRequest, java.lang.Boolean)
     */
    @Override
    public IUser createUser(IUserCreateRequest request, Boolean encodePassword) throws SiteWhereException {
	User swuser = UserManagementPersistenceLogic.userCreateLogic(request, encodePassword);

	UserTO user = new UserTO();
	user.setRealm("/");
	user.setCreationDate(swuser.getCreatedDate());
	user.setCreator(swuser.getCreatedBy());
	user.setUsername(request.getUsername());
	user.setPassword(request.getPassword());
	user.getRoles().addAll(request.getRoles());
	user.getPlainAttrs().add(createAttribute(ATTR_FIRST_NAME, request.getFirstName()));
	user.getPlainAttrs().add(createAttribute(ATTR_LAST_NAME, request.getLastName()));
	user.getPlainAttrs().add(createAttribute(ATTR_JSON,
		Base64.encodeBase64String(MarshalUtils.marshalJsonAsString(swuser).getBytes())));
	try {
	    getUserService().create(user, true);
	} catch (Throwable t) {
	    throw new SiteWhereException("Unable to create user.", t);
	}

	return swuser;
    }

    protected AttrTO createAttribute(String name, String value) {
	AttrTO.Builder attr = new AttrTO.Builder();
	return attr.schema(name).value(value).build();
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#importUser(com.sitewhere.
     * spi.user.IUser, boolean)
     */
    @Override
    public IUser importUser(IUser user, boolean overwrite) throws SiteWhereException {
	throw new RuntimeException("Not implemented.");
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#authenticate(java.lang.
     * String, java.lang.String, boolean)
     */
    @Override
    public IUser authenticate(String username, String password, boolean updateLastLogin) throws SiteWhereException {
	throw new RuntimeException("Not implemented.");
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#updateUser(java.lang.
     * String, com.sitewhere.spi.user.request.IUserCreateRequest, boolean)
     */
    @Override
    public IUser updateUser(String username, IUserCreateRequest request, boolean encodePassword)
	    throws SiteWhereException {
	UserTO user = getUserService().read(username);
	if (user == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidUsername, ErrorLevel.ERROR);
	}
	User swuser = User.copy(convertUser(user));
	UserManagementPersistenceLogic.userUpdateLogic(request, swuser, encodePassword);
	UserPatch userPatch = new UserPatch();
	userPatch.setKey(user.getKey());
	if (request.getPassword() != null) {
	    userPatch.setPassword(new PasswordPatch.Builder().value(request.getPassword()).build());
	}
	if (request.getFirstName() != null) {
	    userPatch.getPlainAttrs().add(
		    new AttrPatch.Builder().attrTO(createAttribute(ATTR_FIRST_NAME, request.getFirstName())).build());
	}
	if (request.getLastName() != null) {
	    userPatch.getPlainAttrs().add(
		    new AttrPatch.Builder().attrTO(createAttribute(ATTR_LAST_NAME, request.getLastName())).build());

	}
	userPatch.getPlainAttrs()
		.add(new AttrPatch.Builder()
			.attrTO(createAttribute(ATTR_JSON,
				Base64.encodeBase64String(MarshalUtils.marshalJsonAsString(swuser).getBytes())))
			.build());

	request.getRoles().forEach(role -> {
	    userPatch.getRoles().add(new StringPatchItem.Builder().value(role).build());
	});

	try {
	    getUserService().update(userPatch);
	} catch (Throwable t) {
	    throw new SiteWhereException("Unable to update user.", t);
	}
	return swuser;
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#getUserByUsername(java.
     * lang.String)
     */
    @Override
    public IUser getUserByUsername(String username) throws SiteWhereException {
	try {
	    UserTO match = getUserService().read(username);
	    return convertUser(match);
	} catch (Throwable t) {
	    throw new SiteWhereException("Unable to get user by username.", t);
	}
    }

    /**
     * Convert Syncope user to SiteWhere user.
     *
     * @param user
     * @return
     * @throws SiteWhereException
     */
    protected IUser convertUser(UserTO user) throws SiteWhereException {
	Optional<AttrTO> json = user.getPlainAttr(ATTR_JSON);
	if (json.isPresent()) {
	    String encoded = new String(json.get().getValues().get(0).getBytes());
	    User swuser = MarshalUtils.unmarshalJson(Base64.decodeBase64(encoded), User.class);
	    user.setPassword(user.getPassword());
	    user.setUsername(user.getUsername());
	    swuser.setCreatedBy(user.getCreator());
	    swuser.setCreatedDate(user.getCreationDate());
	    swuser.setToken(user.getToken());

	    for (String role : user.getRoles()) {
		IRole roleByName = getRoleByName(role);
		swuser.getRoles().add(roleByName);
	    }
	    return swuser;
	}
	throw new SiteWhereException("Syncope user did not contain JSON data.");
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#listUsers(com.sitewhere.
     * spi.user.IUserSearchCriteria)
     */
    @Override
    public ISearchResults<IUser> listUsers(IUserSearchCriteria criteria) throws SiteWhereException {
	PagedResult<UserTO> users = getUserService()
		.search(new AnyQuery.Builder().page(criteria.getPageNumber() - 1).size(criteria.getPageSize()).build());
	List<IUser> matches = new ArrayList<>();
	for (UserTO user : users.getResult()) {
	    matches.add(convertUser(user));
	}
	return new SearchResults<>(matches, users.getTotalCount());
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#deleteUser(java.lang.
     * String)
     */
    @Override
    public IUser deleteUser(String username) throws SiteWhereException {
	getLogger().info("Deleting SiteWhere User: {}", username);
	try {
	    UserTO user = getUserService().read(username);
	    if (user == null) {
		throw new SiteWhereSystemException(ErrorCode.InvalidUsername, ErrorLevel.ERROR);
	    }
	    User swuser = User.copy(convertUser(user));
	    try {
		getUserService().delete(user.getKey());
	    } catch (Throwable t) {
		throw new SiteWhereException("Unable to update user.", t);
	    }
	    return swuser;
	} catch (Throwable t) {
	    throw new SiteWhereException("Unable to get user by username.", t);
	}
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#createGrantedAuthority(
     * com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest)
     */
    @Override
    public IGrantedAuthority createGrantedAuthority(IGrantedAuthorityCreateRequest request) throws SiteWhereException {
	IGrantedAuthority existing = getGrantedAuthorityByName(request.getAuthority());
	if (existing != null) {
	    throw new SiteWhereSystemException(ErrorCode.DuplicateAuthority, ErrorLevel.ERROR);
	}
	GrantedAuthority auth = UserManagementPersistenceLogic.grantedAuthorityCreateLogic(request);

	// Convert auth to priv and save to application.
	ApplicationTO application = getOrCreateSiteWhereApplication();
	application.getPrivileges().add(createPrivilegeFromAuthorityRequest(auth));
	try {
	    getApplicationService().update(application);
	} catch (Throwable t) {
	    throw new SiteWhereException(
		    String.format("Unable to create granted authority '%s'.", request.getAuthority()), t);
	}

	return auth;
    }

    /**
     * Create a Syncope privilege from an SiteWhere authority.
     *
     * @param authority
     * @return
     * @throws SiteWhereException
     */
    protected PrivilegeTO createPrivilegeFromAuthorityRequest(GrantedAuthority authority) throws SiteWhereException {
	PrivilegeTO priv = new PrivilegeTO();
	priv.setApplication(SITEWHERE_APPLICATION_KEY);
	priv.setDescription(authority.getDescription());
	priv.setKey(authority.getAuthority());
	priv.setSpec(MarshalUtils.marshalJsonAsString(authority));
	return priv;
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#getGrantedAuthorityByName
     * (java.lang.String)
     */
    @Override
    public IGrantedAuthority getGrantedAuthorityByName(String name) throws SiteWhereException {
	ISearchResults<IGrantedAuthority> auths = listGrantedAuthorities(new GrantedAuthoritySearchCriteria(1, 0));
	for (IGrantedAuthority auth : auths.getResults()) {
	    if (auth.getAuthority().equals(name)) {
		return auth;
	    }
	}
	return null;
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
	throw new RuntimeException("Not implemented.");
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#listGrantedAuthorities(
     * com.sitewhere.spi.user.IGrantedAuthoritySearchCriteria)
     */
    @Override
    public ISearchResults<IGrantedAuthority> listGrantedAuthorities(IGrantedAuthoritySearchCriteria criteria)
	    throws SiteWhereException {
	ApplicationTO application = getOrCreateSiteWhereApplication();
	List<PrivilegeTO> privs = application.getPrivileges();

	List<IGrantedAuthority> auths = new ArrayList<>();
	if (privs != null) {
	    privs.forEach(priv -> {
		auths.add(MarshalUtils.unmarshalJson(priv.getSpec().getBytes(), GrantedAuthority.class));
	    });
	}
	return new SearchResults<IGrantedAuthority>(auths);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#deleteGrantedAuthority(
     * java.lang.String)
     */
    @Override
    public void deleteGrantedAuthority(String authority) throws SiteWhereException {
	PrivilegeTO toRemove = null;
	ApplicationTO application = getOrCreateSiteWhereApplication();
	List<PrivilegeTO> privs = application.getPrivileges();
	for (PrivilegeTO priv : privs) {
	    if (priv.getKey().equals(authority)) {
		toRemove = priv;
	    }
	}
	if (toRemove != null) {
	    application.getPrivileges().remove(toRemove);
	    try {
		getApplicationService().update(application);
	    } catch (Throwable t) {
		throw new SiteWhereException(String.format("Unable to delete granted authority '%s'.", authority), t);
	    }
	}
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#deleteGrantedAuthority(
     * java.lang.String)
     */
    @Override
    public List<IRole> getRoles(String username) throws SiteWhereException {
	IUser user = getUserByUsername(username);
	return user.getRoles();
    }

    /*
     * @see com.sitewhere.microservice.api.user.IUserManagement#addRoles(
     * java.lang.String, java.util.List)
     */
    @Override
    public List<IRole> addRoles(String username, List<String> roles) throws SiteWhereException {
	try {
	    UserTO userTO = getUserService().read(username);
	    if (userTO != null) {
		userTO.getRoles().addAll(roles);
		getUserService().update(userTO);
	    } else {
		throw new SiteWhereException("Unable to get user by username.");
	    }
	    return getRoles(username);
	} catch (Throwable t) {
	    throw new SiteWhereException("Unable to get user by username.", t);
	}
    }

    /*
     * @see com.sitewhere.microservice.api.user.IUserManagement#removeRoles(
     * java.lang.String, java.util.List)
     */
    @Override
    public List<IRole> removeRoles(String username, List<String> roles) throws SiteWhereException {
	UserTO userTO = getUserService().read(username);
	if (userTO != null) {
	    userTO.getRoles().removeAll(roles);
	    getUserService().update(userTO);
	} else {
	    throw new SiteWhereException("Unable to get user by username.");
	}
	return getRoles(username);
    }

    /*
     * @see com.sitewhere.microservice.api.user.IUserManagement#createRole(
     * com.sitewhere.spi.user.request.IRoleCreateRequest)
     */
    @Override
    public IRole createRole(IRoleCreateRequest request) throws SiteWhereException {
	RoleTO roleTO = new RoleTO();
	roleTO.setKey(request.getRole());
	roleTO.getPrivileges().addAll(request.getAuthorities());
	getRoleService().create(roleTO);
	return convertRole(roleTO);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#getRoleByName(java.lang.
     * String)
     */
    @Override
    public IRole getRoleByName(String name) throws SiteWhereException {
	ISearchResults<IRole> iRoleISearchResults = listRoles(new RoleSearchCriteria(1, 0));
	for (IRole role : iRoleISearchResults.getResults()) {
	    if (role.getRole().equals(name)) {
		return role;
	    }
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#updateRole(java.lang.
     * String, com.sitewhere.spi.user.request.IRoleCreateRequest)
     */
    @Override
    public IRole updateRole(String name, IRoleCreateRequest request) throws SiteWhereException {
	RoleTO roleToUpdate = getRoleService().read(name);
	roleToUpdate.getPrivileges().clear();
	roleToUpdate.getPrivileges().addAll(request.getAuthorities());
	getRoleService().update(roleToUpdate);
	return convertRole(roleToUpdate);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#listRoles(com.sitewhere.
     * spi.user.IRoleSearchCriteria)
     */
    @Override
    public ISearchResults<IRole> listRoles(IRoleSearchCriteria criteria) throws SiteWhereException {
	List<RoleTO> allRoles = getRoleService().list();
	List<IRole> roles = new ArrayList<>();
	if (allRoles != null) {
	    for (RoleTO roleTO : allRoles) {
		roles.add(convertRole(roleTO));
	    }
	}
	return new SearchResults<IRole>(roles);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#deleteRole(java.lang.
     * String)
     */
    @Override
    public void deleteRole(String role) throws SiteWhereException {
	try {
	    getRoleService().delete(role);
	} catch (Throwable t) {
	    throw new SiteWhereException("Unable to delete user by username.");
	}
    }

    protected IRole convertRole(RoleTO roleTO) throws SiteWhereException {
	Role sRole = new Role();
	sRole.setRole(roleTO.getKey());
	// sRole.setDescription(SiteWhereRole.valueOf(roleTO.getKey()).getDescription());
	for (String grantedAuthority : roleTO.getPrivileges()) {
	    IGrantedAuthority grantedAuthorityByName = getGrantedAuthorityByName(grantedAuthority);
	    sRole.getAuthorities().add(grantedAuthorityByName);
	}
	return sRole;
    }

    /**
     * Waits for Syncope connection to become available.
     */
    protected class SyncopeConnectionWaiter implements Runnable {

	@Override
//	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void run() {
//	    String syncopeUrl = String.format("http://%s:%d/syncope/rest",
//		    getConfiguration().getUserManagement().getSyncopeHost(),
//		    getConfiguration().getUserManagement().getSyncopePort());
//	    SyncopeClientFactoryBean clientFactory = new SyncopeClientFactoryBean().setAddress(syncopeUrl);
//
//	    getLogger().info(String.format("Attempting to connect to Syncope at '%s:%d' as '%s'...",
//		    getConfiguration().getUserManagement().getSyncopeHost(),
//		    getConfiguration().getUserManagement().getSyncopePort(), SYNCOPE_USERNAME));
//	    Callable<Boolean> connectCheck = () -> {
//		SyncopeUserManagement.this.client = clientFactory.create(SYNCOPE_USERNAME, SYNCOPE_PASSWORD);
//		getSyncopeAvailable().countDown();
//		return true;
//	    };
//	    RetryConfig config = new RetryConfigBuilder().retryOnAnyException().retryIndefinitely()
//		    .withDelayBetweenTries(Duration.ofSeconds(CONNECT_SECS_BETWEEN_RETRIES)).withFixedBackoff().build();
//	    RetryListener listener = new RetryListener<Boolean>() {
//
//		@Override
//		public void onEvent(Status<Boolean> status) {
//		    getLogger().info(String.format(
//			    "Unable to connect to Syncope[%s] on attempt %d [%s] (total wait so far %dms). Retrying after fallback...",
//			    syncopeUrl, status.getTotalTries(), status.getLastExceptionThatCausedRetry().getMessage(),
//			    status.getTotalElapsedDuration().toMillis()));
//		    getLogger().error("Unable to connect.", status.getLastExceptionThatCausedRetry());
//		}
//	    };
//	    new CallExecutorBuilder().config(config).afterFailedTryListener(listener).build().execute(connectCheck);
	}
    }

    /**
     * Refreshes access token at a given interval.
     */
    protected class SyncopeConnectionRefresher implements Runnable {

	@Override
	public void run() {
	    while (true) {
		try {
		    if (getSyncopeAvailable().getCount() == 0) {
			getLogger().debug("Refreshing Syncope access token...");
			try {
			    getClient().refresh();
			} catch (Throwable t) {
			    getLogger().error("Unable to refresh Syncope access token.", t);
			}
		    } else {
			getLogger().debug("Skipping Syncope token refresh until connection is established.");
		    }
		} catch (Throwable t) {
		    getLogger().error("Exception refreshing Syncope access token.", t);
		}
		try {
		    Thread.sleep(TOKEN_REFRESH_IN_MINUTES * 60 * 1000);
		} catch (InterruptedException e) {
		    getLogger().info("Syncope token refresher thread stopped.");
		    return;
		}
	    }
	}
    }

    /** Used for naming microservice heartbeat thread */
    private class SyncopeWaiterThreadFactory implements ThreadFactory {

	public Thread newThread(Runnable r) {
	    return new Thread(r, "Syncope Init");
	}
    }

    protected SyncopeClient getClient() {
	return client;
    }

    protected AtomicBoolean getConnectionFailed() {
	return connectionFailed;
    }

    protected CountDownLatch getSyncopeAvailable() {
	return syncopeAvailable;
    }

    protected ApplicationService getApplicationService() {
	return client.getService(ApplicationService.class);
    }

    protected UserService getUserService() {
	return client.getService(UserService.class);
    }

    protected SchemaService getSchemaService() {
	return client.getService(SchemaService.class);
    }

    protected RoleService getRoleService() {
	return client.getService(RoleService.class);
    }

    protected AnyTypeClassService getAnyTypeClassService() {
	return client.getService(AnyTypeClassService.class);
    }

    protected ExecutorService getWaiter() {
	return waiter;
    }

    protected ExecutorService getRefresher() {
	return refresher;
    }

    protected InstanceManagementConfiguration getConfiguration() {
	return configuration;
    }
}
