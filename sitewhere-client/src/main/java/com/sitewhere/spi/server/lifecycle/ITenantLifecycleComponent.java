/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.server.lifecycle;

import com.sitewhere.spi.user.ITenant;

/**
 * Extends {@link ILifecycleComponent} with tenant-specific functionality.
 * 
 * @author Derek
 */
public interface ITenantLifecycleComponent extends ILifecycleComponent {

	/**
	 * Set tenant for component.
	 * 
	 * @param tenant
	 */
	public void setTenant(ITenant tenant);

	/**
	 * Get tenant for component.
	 * 
	 * @return
	 */
	public ITenant getTenant();
}