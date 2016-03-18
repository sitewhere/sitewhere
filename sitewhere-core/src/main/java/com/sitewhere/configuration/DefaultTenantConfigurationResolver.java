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
import com.sitewhere.spi.configuration.ITenantConfigurationResolver;
import com.sitewhere.spi.system.IVersion;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Default implementation of {@link ITenantConfigurationResolver} that resolves tenants
 * relative to the SITEWHERE_HOME/conf folder.
 * 
 * @author Derek
 */
public class DefaultTenantConfigurationResolver extends FileSystemTenantConfigurationResolver {

	public DefaultTenantConfigurationResolver(ITenant tenant, IVersion version,
			IGlobalConfigurationResolver globalConfigurationResolver) {
		super(tenant, version, globalConfigurationResolver);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.configuration.FileSystemTenantConfigurationResolver#getTenantFolder()
	 */
	@Override
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