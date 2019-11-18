/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.spi;

import com.sitewhere.connectors.spi.multicast.IDeviceEventMulticaster;
import com.sitewhere.connectors.spi.routing.IRouteBuilder;

/**
 * Extends {@link IOutboundConnector} with routing capabilities.
 */
public interface IMulticastingOutboundConnector<T> extends IOutboundConnector {

    /**
     * Get the configured multcaster implementation.
     * 
     * @return
     */
    public IDeviceEventMulticaster<T> getMulticaster();

    /**
     * Get the configured route builder implementation.
     * 
     * @return
     */
    public IRouteBuilder<T> getRouteBuilder();
}