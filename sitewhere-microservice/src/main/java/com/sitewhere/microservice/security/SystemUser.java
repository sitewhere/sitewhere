/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.security;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.sitewhere.rest.model.user.GrantedAuthority;
import com.sitewhere.rest.model.user.User;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.security.ISystemUser;
import com.sitewhere.spi.microservice.security.ITokenManagement;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.spi.user.SiteWhereAuthority;

/**
 * Bean that provides a system "superuser" that allows microservices to
 * authenticate with other microservices.
 */
public class SystemUser implements ISystemUser {

    /** Number of seconds between renewing JWT */
    private static final int RENEW_INTERVAL_SEC = 60 * 60;

    /** Default to one year expiration for system users */
    private static final int SYSTEM_USER_TOKEN_EXPIRATION_IN_MINS = 60 * 24 * 365;

    /** JWT token management */
    private ITokenManagement tokenManagement;

    /** System user information */
    private IUser user = SystemUser.createUser();

    /** System user authorities */
    private List<IGrantedAuthority> auths = SystemUser.getNonGroupAuthorities();

    /** Last authentication result */
    private SiteWhereAuthentication last = null;

    /** Last time JWT was generated */
    private long lastGenerated = 0;

    /*
     * @see com.sitewhere.spi.microservice.security.ISystemUser#getAuthentication()
     */
    @Override
    public SiteWhereAuthentication getAuthentication() throws SiteWhereException {
	if ((System.currentTimeMillis() - lastGenerated) > (RENEW_INTERVAL_SEC * 1000)) {
	    String jwt = tokenManagement.generateToken(user, SYSTEM_USER_TOKEN_EXPIRATION_IN_MINS);
	    SiteWhereUserDetails details = new SiteWhereUserDetails(user, auths);
	    this.last = new SiteWhereAuthentication(details, jwt);
	    this.lastGenerated = System.currentTimeMillis();
	}
	return this.last;
    }

    /*
     * @see com.sitewhere.spi.microservice.security.ISystemUser#
     * getAuthenticationForTenant(com.sitewhere.spi.tenant.ITenant)
     */
    @Override
    public SiteWhereAuthentication getAuthenticationForTenant(ITenant tenant) throws SiteWhereException {
	SiteWhereAuthentication auth = getAuthentication();
	auth.setTenant(tenant);
	return auth;
    }

    /**
     * Create default (fully authenticated) system user.
     * 
     * @return
     */
    private static IUser createUser() {
	User user = new User();
	user.setUsername("system");
	user.setFirstName("System");
	user.setLastName("User");
	user.setCreatedDate(new Date());

	List<IGrantedAuthority> auths = getNonGroupAuthorities();
	List<String> roles = auths.stream().map(x -> x.getAuthority()).collect(Collectors.toList());

	user.setAuthorities(roles);
	return user;
    }

    private static List<IGrantedAuthority> getNonGroupAuthorities() {
	List<IGrantedAuthority> matches = new ArrayList<IGrantedAuthority>();
	for (SiteWhereAuthority auth : SiteWhereAuthority.values()) {
	    if (!auth.isGroup()) {
		GrantedAuthority ga = new GrantedAuthority();
		ga.setAuthority(auth.getName());
		ga.setDescription(auth.getDescription());
		ga.setParent(auth.getParent());
		matches.add(ga);
	    }
	}
	return matches;
    }
}