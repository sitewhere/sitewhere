/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.user.persistence;

import java.util.List;

import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

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
    private static MessageDigestPasswordEncoder passwordEncoder = new ShaPasswordEncoder();

    /**
     * Common logic for creating a user based on an incoming request.
     * 
     * @param source
     * @param encodePassword
     * @return
     * @throws SiteWhereException
     */
    public static User userCreateLogic(IUserCreateRequest source, boolean encodePassword) throws SiteWhereException {
	String password = (encodePassword) ? passwordEncoder.encodePassword(source.getPassword(), null)
		: source.getPassword();

	User user = new User();

	require(source.getUsername());
	user.setUsername(source.getUsername());

	user.setHashedPassword(password);
	user.setFirstName(source.getFirstName());
	user.setLastName(source.getLastName());
	user.setLastLogin(null);
	user.setStatus(source.getStatus());
	user.setAuthorities(source.getAuthorities());

	MetadataProvider.copy(source, user);
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
	    String password = (encodePassword) ? passwordEncoder.encodePassword(source.getPassword(), null)
		    : source.getPassword();
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
     * @throws SiteWhereException
     */
    public static void userDeleteLogic(String username) throws SiteWhereException {
	ITenantManagement management = getTenantManagement();
	ISearchResults<ITenant> tenants = management.listTenants(new TenantSearchCriteria(1, 0));
	for (ITenant tenant : tenants.getResults()) {
	    if (tenant.getAuthorizedUserIds().contains(username)) {
		TenantCreateRequest request = new TenantCreateRequest();
		List<String> ids = tenant.getAuthorizedUserIds();
		ids.remove(username);
		request.setAuthorizedUserIds(ids);
		management.updateTenant(tenant.getId(), request);
	    }
	}
    }

    /**
     * Common logic for creating a granted authority based on an incoming
     * request.
     * 
     * @param source
     * @return
     * @throws SiteWhereException
     */
    public static GrantedAuthority grantedAuthorityCreateLogic(IGrantedAuthorityCreateRequest source)
	    throws SiteWhereException {
	GrantedAuthority auth = new GrantedAuthority();

	require(source.getAuthority());
	auth.setAuthority(source.getAuthority());

	auth.setDescription(source.getDescription());
	auth.setParent(source.getParent());
	auth.setGroup(source.isGroup());
	return auth;
    }

    /**
     * Common logic for encoding a plaintext password.
     * 
     * @param plaintext
     * @return
     */
    public static String encodePassword(String plaintext) {
	return passwordEncoder.encodePassword(plaintext, null);
    }

    private static ITenantManagement getTenantManagement() {
	return null;
    }
}