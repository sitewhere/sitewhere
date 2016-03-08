/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.server.tenant;

import com.sitewhere.spi.user.ITenant;

/**
 * Used by components that do not need to implement full lifecycle support, but should be
 * aware of which tenant they are executing in.
 * 
 * @author Derek
 */
public interface ITenantAware {

	/**
	 * Get associated tenant.
	 * 
	 * @return
	 */
	public ITenant getTenant();

	/**
	 * Set associated tenant.
	 * 
	 * @param tenant
	 */
	public void setTenant(ITenant tenant);
}