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
import com.sitewhere.spi.server.ISiteWhereTenantEngine;
import com.sitewhere.spi.system.IVersion;

/**
 * Utility class for common tenant functionality.
 * 
 * @author Derek
 */
public class TenantUtils {

	/**
	 * Get String representation of the configuration for a tenant.
	 * 
	 * @param tenantId
	 * @return
	 * @throws SiteWhereException
	 */
	public static String getTenantConfiguration(String tenantId) throws SiteWhereException {
		ISiteWhereTenantEngine engine = SiteWhere.getServer().getTenantEngine(tenantId);
		if (engine == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidTenantEngineId, ErrorLevel.ERROR);
		}
		IVersion version = SiteWhere.getServer().getVersion();
		return engine.getConfigurationResolver().getTenantConfiguration(engine.getTenant(), version);
	}
}
