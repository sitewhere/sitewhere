/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.tenant;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sitewhere.rest.model.tenant.request.TenantCreateRequest;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.server.user.DefaultUserModelInitializer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.tenant.ITenantModelInitializer;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.ITenantManagement;

/**
 * Used to load a default tenant into an empty tenant model. This acts as a
 * bootstrap for systems that have just been installed.
 * 
 * @author Derek
 */
public class DefaultTenantModelInitializer implements ITenantModelInitializer {

	/** Static logger instance */
	private static Logger LOGGER = LogManager.getLogger();

	/** Default tenant id */
	public static final String DEFAULT_TENANT_ID = "default";

	/** Default tenant name */
	public static final String DEFAULT_TENANT_NAME = "Default Tenant";

	/** Default tenant logo URL */
	public static final String DEFAULT_TENANT_LOGO = "https://s3.amazonaws.com/sitewhere-demo/sitewhere-small.png";

	/** Default tenant auth token */
	public static final String DEFAULT_TENANT_TOKEN = "sitewhere1234567890";

	/** Prefix for create tenant */
	public static final String PREFIX_CREATE_TENANT = "[Create Tenant]";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.server.tenant.ITenantModelInitializer#initialize(com.
	 * sitewhere. spi.tenant.ITenantManagement)
	 */
	@Override
	public void initialize(ITenantManagement tenantManagement) throws SiteWhereException {
		// Use the system account for logging "created by" on created elements.
		SecurityContextHolder.getContext().setAuthentication(SiteWhereServer.getSystemAuthentication());

		ITenant tenant = tenantManagement.getTenantById(DEFAULT_TENANT_ID);
		if (tenant == null) {
			TenantCreateRequest treq = new TenantCreateRequest();
			treq.setId(DEFAULT_TENANT_ID);
			treq.setName(DEFAULT_TENANT_NAME);
			treq.setLogoUrl(DEFAULT_TENANT_LOGO);
			treq.setAuthorizedUserIds(Arrays.asList(new String[] { DefaultUserModelInitializer.DEFAULT_USERNAME,
					DefaultUserModelInitializer.NOADMIN_CREDENTIAL }));
			treq.setAuthenticationToken(DEFAULT_TENANT_TOKEN);
			tenant = tenantManagement.createTenant(treq);
			LOGGER.info(PREFIX_CREATE_TENANT + " " + tenant.getId());
		}

		SecurityContextHolder.getContext().setAuthentication(null);
	}
}