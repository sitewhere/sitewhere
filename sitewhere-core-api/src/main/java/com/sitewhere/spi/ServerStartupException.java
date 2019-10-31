/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi;

import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Exception on server startup that will prevent server from functioning.
 */
public class ServerStartupException extends SiteWhereException {

    /** Serial version UID */
    private static final long serialVersionUID = 3458605782783632700L;

    /** Component that caused startup to fail */
    private ILifecycleComponent component;

    public ServerStartupException(ILifecycleComponent component, String message, Throwable t) {
	super(message, t);
	this.component = component;
    }

    public ILifecycleComponent getComponent() {
	return component;
    }

    public void setComponent(ILifecycleComponent component) {
	this.component = component;
    }
}