/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.user.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sitewhere.persistence.Persistence;
import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.rest.model.search.tenant.TenantSearchCriteria;
import com.sitewhere.rest.model.tenant.request.TenantCreateRequest;
import com.sitewhere.rest.model.user.GrantedAuthority;
import com.sitewhere.rest.model.user.User;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.ITenantManagement;
import com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest;
import com.sitewhere.spi.user.request.IUserCreateRequest;

/**
 * Persistence logic for user management components.
 * 
 * @author Derek
 */
public class UserManagementPersistence extends Persistence {

    /** Password encoder */
    private static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Common logic for creating a user based on an incoming request.
     * 
     * @param source
     * @param encodePassword
     * @return
     * @throws SiteWhereException
     */
    public static User userCreateLogic(IUserCreateRequest request, boolean encodePassword) throws SiteWhereException {
	User user = new User();
	user.setId(UUID.randomUUID());

	// Use token if provided, otherwise generate one.
	if (request.getUsername() != null) {
	    user.setToken(request.getUsername());
	} else {
	    user.setToken(UUID.randomUUID().toString());
	}

	require("Username", request.getUsername());
	String password = (encodePassword) ? passwordEncoder.encode(request.getPassword()) : request.getPassword();

	user.setUsername(request.getUsername());
	user.setHashedPassword(password);
	user.setFirstName(request.getFirstName());
	user.setLastName(request.getLastName());
	user.setLastLogin(null);
	user.setStatus(request.getStatus());
	user.setAuthorities(request.getAuthorities());

	MetadataProvider.copy(request, user);
	Persistence.initializeEntityMetadata(user);
	return user;
    }

    /**
     * Common code for copying information from an update request to an existing
     * user.
     * 
     * @param source
     * @param target
     * @param encodePassword
     * @throws SiteWhereException
     */
    public static void userUpdateLogic(IUserCreateRequest source, User target, boolean encodePassword)
	    throws SiteWhereException {
	if (source.getUsername() != null) {
	    target.setUsername(source.getUsername());
	}
	if (source.getPassword() != null) {
	    String password = (encodePassword) ? passwordEncoder.encode(source.getPassword()) : source.getPassword();
	    target.setHashedPassword(password);
	}
	if (source.getFirstName() != null) {
	    target.setFirstName(source.getFirstName());
	}
	if (source.getLastName() != null) {
	    target.setLastName(source.getLastName());
	}
	if (source.getStatus() != null) {
	    target.setStatus(source.getStatus());
	}
	if (source.getAuthorities() != null) {
	    target.setAuthorities(source.getAuthorities());
	}
	if (source.getMetadata() != null) {
	    target.getMetadata().clear();
	    MetadataProvider.copy(source, target);
	}
	Persistence.setUpdatedEntityMetadata(target);
    }

    /**
     * Common logic for deleting a user. Takes care of related tasks such as
     * deleting user id from tenant authorized users.
     * 
     * @param username
     * @param tenantManagement
     * @throws SiteWhereException
     */
    public static void userDeleteLogic(String username, ITenantManagement tenantManagement) throws SiteWhereException {
	ISearchResults<ITenant> tenants = tenantManagement.listTenants(new TenantSearchCriteria(1, 0));
	for (ITenant tenant : tenants.getResults()) {
	    if (tenant.getAuthorizedUserIds().contains(username)) {
		TenantCreateRequest request = new TenantCreateRequest();
		List<String> ids = tenant.getAuthorizedUserIds();
		ids.remove(username);
		request.setAuthorizedUserIds(ids);
		tenantManagement.updateTenant(tenant.getId(), request);
	    }
	}
    }

    /**
     * Common logic for creating a granted authority based on an incoming request.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static GrantedAuthority grantedAuthorityCreateLogic(IGrantedAuthorityCreateRequest request)
	    throws SiteWhereException {
	GrantedAuthority auth = new GrantedAuthority();

	require("Authority", request.getAuthority());
	auth.setAuthority(request.getAuthority());

	auth.setDescription(request.getDescription());
	auth.setParent(request.getParent());
	auth.setGroup(request.isGroup());
	return auth;
    }

    /**
     * Common logic for encoding a plaintext password.
     * 
     * @param plaintext
     * @return
     */
    public static boolean passwordMatches(String plaintext, String encoded) {
	return passwordEncoder.matches(plaintext, encoded);
    }
}