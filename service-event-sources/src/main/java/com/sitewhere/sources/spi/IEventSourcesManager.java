/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.spi;

import java.util.List;

import com.sitewhere.spi.device.communication.IInboundEventSource;
import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

/**
 * Manages the list of event sources for a tenant.
 * 
 * @author Derek
 */
public interface IEventSourcesManager extends ITenantLifecycleComponent {

    /**
     * Get list of inbound event sources.
     * 
     * @return
     */
    public List<IInboundEventSource<?>> getEventSources();
}