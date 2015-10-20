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
import com.sitewhere.spi.user.SiteWhereAuthority;

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

		List<String> all = createGrantedAuthorities();

		UserCreateRequest ureq = new UserCreateRequest();
		ureq.setFirstName("Admin");
		ureq.setLastName("User");
		ureq.setUsername(DEFAULT_USERNAME);
		ureq.setPassword(DEFAULT_PASSWORD);
		ureq.setAuthorities(all);
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

	/**
	 * Create any granted authorities that do not already exist.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	protected List<String> createGrantedAuthorities() throws SiteWhereException {
		List<String> ids = new ArrayList<String>();
		SiteWhereAuthority[] auths = SiteWhereAuthority.values();
		for (SiteWhereAuthority auth : auths) {
			IGrantedAuthority existing = getUserManagement().getGrantedAuthorityByName(auth.getName());
			if (existing == null) {
				GrantedAuthorityCreateRequest request = new GrantedAuthorityCreateRequest();
				request.setAuthority(auth.getName());
				request.setDescription(auth.getDescription());
				request.setParent(auth.getParent());
				request.setGroup(auth.isGroup());
				existing = getUserManagement().createGrantedAuthority(request);
				LOGGER.info(PREFIX_CREATE_AUTH + " " + auth.getName());
			}
			if (!auth.isGroup()) {
				ids.add(auth.getName());
			}
		}
		return ids;
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