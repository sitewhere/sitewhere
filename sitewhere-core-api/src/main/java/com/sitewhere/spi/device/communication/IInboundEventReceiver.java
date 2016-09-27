/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.communication;

import java.util.Map;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

/**
 * Handles receipt of device event information from an underlying transport.
 * 
 * @author Derek
 */
public interface IInboundEventReceiver<T> extends ITenantLifecycleComponent {

    /**
     * Get name shown in user interfaces when referencing receiver.
     * 
     * @return
     */
    public String getDisplayName();

    /**
     * Called when an event payload is received.
     * 
     * @param payload
     * @param metadata
     * @throws SiteWhereException
     */
    public void onEventPayloadReceived(T payload, Map<String, Object> metadata) throws EventDecodeException;

    /**
     * Set the parent event source that will process events.
     * 
     * @param source
     */
    public void setEventSource(IInboundEventSource<T> source);

    /**
     * Get the parent event source.
     * 
     * @return
     */
    public IInboundEventSource<T> getEventSource();
}