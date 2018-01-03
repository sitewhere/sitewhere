/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.spi;

import java.util.List;

import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Manages the list of outbound connectors for a tenant.
 * 
 * @author Derek
 */
public interface IOutboundConnectorsManager extends ITenantEngineLifecycleComponent {

    /**
     * Get list of outbound connectors.
     * 
     * @return
     */
    public List<IOutboundConnector> getOutboundConnectors();
}