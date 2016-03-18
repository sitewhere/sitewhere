/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.server.tenant;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.IModelInitializer;
import com.sitewhere.spi.tenant.ITenantManagement;

/**
 * Class that initializes the tenant model with data needed to bootstrap the system.
 * 
 * @author Derek
 */
public interface ITenantModelInitializer extends IModelInitializer {

	/**
	 * Initialize the tenant model.
	 * 
	 * @param tenantManagement
	 * @throws SiteWhereException
	 */
	public void initialize(ITenantManagement tenantManagement) throws SiteWhereException;
}
