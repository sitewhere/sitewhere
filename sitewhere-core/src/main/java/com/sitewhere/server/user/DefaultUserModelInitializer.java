/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sitewhere.core.user.ISiteWhereAuthorities;
import com.sitewhere.rest.model.user.request.GrantedAuthorityCreateRequest;
import com.sitewhere.rest.model.user.request.TenantCreateRequest;
import com.sitewhere.rest.model.user.request.UserCreateRequest;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.user.IUserModelInitializer;
import com.sitewhere.spi.user.AccountStatus;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.ITenant;
import com.sitewhere.spi.user.IUserManagement;

/**
 * Used to load a default user and granted authorities into an empty user model. This acts
 * as a bootstrap for systems that have just been installed.
 * 
 * @author Derek
 */
public class DefaultUserModelInitializer implements IUserModelInitializer {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(DefaultUserModelInitializer.class);

	/** Default administrator username */
	public static final String DEFAULT_USERNAME = "admin";

	/** Default administrator password */
	public static final String DEFAULT_PASSWORD = "password";

	/** Default tenant id */
	public static final String DEFAULT_TENANT_ID = "default";

	/** Default tenant name */
	public static final String DEFAULT_TENANT_NAME = "Default Tenant";

	/** Default tenant logo URL */
	public static final String DEFAULT_TENANT_LOGO =
			"https://s3.amazonaws.com/sitewhere-demo/sitewhere-small.png";

	/** Default tenant auth token */
	public static final String DEFAULT_TENANT_TOKEN = "sitewhere1234567890";

	/** Prefix for create authority */
	public static final String PREFIX_CREATE_AUTH = "[Create Authority]";

	/** Prefix for create user */
	public static final String PREFIX_CREATE_USER = "[Create User]";

	/** Prefix for create tenant */
	public static final String PREFIX_CREATE_TENANT = "[Create Tenant]";

	/** User management instance */
	private IUserManagement userManagement;

	/** Indiates whether model should be initialized if no console is available for input */
	private boolean initializeIfNoConsole = false;

	/**
	 * Initialize the user model with a expected list of granted authorities and default
	 * user.
	 * 
	 * @throws SiteWhereException
	 */
	public void initialize(IUserManagement userManagement) throws SiteWhereException {
		setUserManagement(userManagement);

		// Use the system account for logging "created by" on created elements.
		SecurityContextHolder.getContext().setAuthentication(SiteWhereServer.getSystemAuthentication());

		GrantedAuthorityCreateRequest gaReq = new GrantedAuthorityCreateRequest();

		// Create authenticated user authority.
		IGrantedAuthority authUser =
				getUserManagement().getGrantedAuthorityByName(ISiteWhereAuthorities.AUTH_AUTHENTICATED_USER);
		if (authUser == null) {
			gaReq.setAuthority(ISiteWhereAuthorities.AUTH_AUTHENTICATED_USER);
			gaReq.setDescription("Log in to the system and perform basic functions.");
			authUser = getUserManagement().createGrantedAuthority(gaReq);
			LOGGER.info(PREFIX_CREATE_AUTH + " " + ISiteWhereAuthorities.AUTH_AUTHENTICATED_USER);
		}

		// Create user administration authority.
		IGrantedAuthority userAdmin =
				getUserManagement().getGrantedAuthorityByName(ISiteWhereAuthorities.AUTH_ADMIN_USERS);
		if (userAdmin == null) {
			gaReq.setAuthority(ISiteWhereAuthorities.AUTH_ADMIN_USERS);
			gaReq.setDescription("Create, Maintain, and delete user accounts.");
			userAdmin = getUserManagement().createGrantedAuthority(gaReq);
			LOGGER.info(PREFIX_CREATE_AUTH + " " + ISiteWhereAuthorities.AUTH_ADMIN_USERS);
		}

		// Create site administration authority.
		IGrantedAuthority siteAdmin =
				getUserManagement().getGrantedAuthorityByName(ISiteWhereAuthorities.AUTH_ADMIN_SITES);
		if (siteAdmin == null) {
			gaReq.setAuthority(ISiteWhereAuthorities.AUTH_ADMIN_SITES);
			gaReq.setDescription("Create, Maintain, and delete site information.");
			siteAdmin = getUserManagement().createGrantedAuthority(gaReq);
			LOGGER.info(PREFIX_CREATE_AUTH + " " + ISiteWhereAuthorities.AUTH_ADMIN_SITES);
		}

		List<String> auths = new ArrayList<String>();
		auths.add(authUser.getAuthority());
		auths.add(userAdmin.getAuthority());
		auths.add(siteAdmin.getAuthority());

		UserCreateRequest ureq = new UserCreateRequest();
		ureq.setFirstName("Admin");
		ureq.setLastName("User");
		ureq.setUsername(DEFAULT_USERNAME);
		ureq.setPassword(DEFAULT_PASSWORD);
		ureq.setAuthorities(auths);
		ureq.setStatus(AccountStatus.Active);

		getUserManagement().createUser(ureq);

		ITenant tenant = getUserManagement().getTenantById(DEFAULT_TENANT_ID);
		if (tenant == null) {
			TenantCreateRequest treq = new TenantCreateRequest();
			treq.setId(DEFAULT_TENANT_ID);
			treq.setName(DEFAULT_TENANT_NAME);
			treq.setLogoUrl(DEFAULT_TENANT_LOGO);
			treq.setAuthorizedUserIds(Arrays.asList(new String[] { DEFAULT_USERNAME }));
			treq.setAuthenticationToken(DEFAULT_TENANT_TOKEN);
			tenant = getUserManagement().createTenant(treq);
			LOGGER.info(PREFIX_CREATE_TENANT + " " + tenant.getId());
		}

		SecurityContextHolder.getContext().setAuthentication(null);
		LOGGER.info(PREFIX_CREATE_USER + " " + ureq.getUsername());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.IModelInitializer#isInitializeIfNoConsole()
	 */
	public boolean isInitializeIfNoConsole() {
		return initializeIfNoConsole;
	}

	public void setInitializeIfNoConsole(boolean initializeIfNoConsole) {
		this.initializeIfNoConsole = initializeIfNoConsole;
	}

	public IUserManagement getUserManagement() {
		return userManagement;
	}

	public void setUserManagement(IUserManagement userManagement) {
		this.userManagement = userManagement;
	}
}