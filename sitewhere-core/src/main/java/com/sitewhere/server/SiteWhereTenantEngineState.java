/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server;

import com.sitewhere.spi.server.ISiteWhereTenantEngineState;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Contains information about the runtime
 * 
 * @author Derek
 *
 */
public class SiteWhereTenantEngineState implements ISiteWhereTenantEngineState {

	/** Lifecycle status */
	private LifecycleStatus lifecycleStatus;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.ISiteWhereTenantEngineState#getLifecycleStatus()
	 */
	public LifecycleStatus getLifecycleStatus() {
		return lifecycleStatus;
	}

	public void setLifecycleStatus(LifecycleStatus lifecycleStatus) {
		this.lifecycleStatus = lifecycleStatus;
	}
}