/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.routing;

import com.sitewhere.commands.spi.IOutboundCommandRouter;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Abstract base class for {@link IOutboundCommandRouter} implementations.
 */
public abstract class OutboundCommandRouter extends TenantEngineLifecycleComponent implements IOutboundCommandRouter {

    public OutboundCommandRouter() {
	super(LifecycleComponentType.CommandRouter);
    }
}