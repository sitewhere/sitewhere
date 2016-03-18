/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration;

import java.io.File;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.configuration.IGlobalConfigurationResolver;
import com.sitewhere.spi.system.IVersion;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Tenant configuration resolver that locates files relative to CATALINA_HOME for Tomcat.
 * 
 * @author Derek
 */
public class TomcatTenantConfigurationResolver extends FileSystemTenantConfigurationResolver {

	public TomcatTenantConfigurationResolver(ITenant tenant, IVersion version,
			IGlobalConfigurationResolver globalConfigurationResolver) {
		super(tenant, version, globalConfigurationResolver);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.configuration.FileSystemTenantConfigurationResolver#getTenantFolder()
	 */
	public File getTenantFolder() throws SiteWhereException {
		File root = new File(getGlobalConfigurationResolver().getConfigurationRoot());
		if (!root.exists()) {
			throw new SiteWhereException("Global configuration root not found.");
		}

		File tenants = new File(root, TENANTS_FOLDER);
		if (!tenants.exists()) {
			throw new SiteWhereException("Tenants folder not found.");
		}

		File tenantFolder = new File(tenants, getTenant().getId());
		if (!tenantFolder.exists()) {
			throw new SiteWhereException("Tenant folder not found for '" + getTenant().getId() + "'.");
		}

		return tenantFolder;
	}
}