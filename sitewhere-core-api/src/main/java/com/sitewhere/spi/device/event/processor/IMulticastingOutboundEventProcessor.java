/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event.processor;

import com.sitewhere.spi.device.event.processor.multicast.IDeviceEventMulticaster;
import com.sitewhere.spi.device.event.processor.routing.IRouteBuilder;

/**
 * Extends {@link IOutboundEventProcessor} with routing capabilities.
 * 
 * @author Derek
 */
public interface IMulticastingOutboundEventProcessor<T> extends IOutboundEventProcessor {

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