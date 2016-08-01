/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.tenant;

import com.sitewhere.SiteWhere;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.resource.IResource;
import com.sitewhere.spi.server.tenant.ISiteWhereTenantEngine;

/**
 * Utility class for common tenant functionality.
 * 
 * @author Derek
 */
public class TenantUtils {

	/**
	 * Get resource representation of the active configuration for a tenant.
	 * 
	 * @param tenantId
	 * @return
	 * @throws SiteWhereException
	 */
	public static IResource getActiveTenantConfiguration(String tenantId) throws SiteWhereException {
		ISiteWhereTenantEngine engine = SiteWhere.getServer().getTenantEngine(tenantId);
		if (engine == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidTenantEngineId, ErrorLevel.ERROR);
		}
		return engine.getTenantConfigurationResolver().getActiveTenantConfiguration();
	}

	/**
	 * Get resource representation of the staged configuration for a tenant.
	 * 
	 * @param tenantId
	 * @return
	 * @throws SiteWhereException
	 */
	public static IResource getStagedTenantConfiguration(String tenantId) throws SiteWhereException {
		ISiteWhereTenantEngine engine = SiteWhere.getServer().getTenantEngine(tenantId);
		if (engine == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidTenantEngineId, ErrorLevel.ERROR);
		}
		return engine.getTenantConfigurationResolver().getStagedTenantConfiguration();
	}

	/**
	 * Stage a new configuration for the given tenant.
	 * 
	 * @param tenantId
	 * @param configuration
	 * @throws SiteWhereException
	 */
	public static void stageTenantConfiguration(String tenantId, String configuration) throws SiteWhereException {
		ISiteWhereTenantEngine engine = SiteWhere.getServer().getTenantEngine(tenantId);
		if (engine == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidTenantEngineId, ErrorLevel.ERROR);
		}
		engine.getTenantConfigurationResolver().stageTenantConfiguration(configuration.getBytes());
	}
}