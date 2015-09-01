/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.tenant;

import java.util.concurrent.Callable;

import com.sitewhere.spi.command.ICommandResponse;
import com.sitewhere.spi.server.ISiteWhereTenantEngine;

/**
 * Base class for commands executed on a tenant engine.
 * 
 * @author Derek
 */
public abstract class TenantEngineCommand implements Callable<ICommandResponse> {

	/** Tenant engine */
	private ISiteWhereTenantEngine engine;

	public ISiteWhereTenantEngine getEngine() {
		return engine;
	}

	public void setEngine(ISiteWhereTenantEngine engine) {
		this.engine = engine;
	}
}