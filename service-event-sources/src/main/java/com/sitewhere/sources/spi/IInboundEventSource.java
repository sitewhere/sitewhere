/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.spi;

import java.util.List;
import java.util.Map;

import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Entity that receives events from one or more {@link IInboundEventReceiver},
 * decodes them, and forwards them for processing.
 * 
 * @author Derek
 */
public interface IInboundEventSource<T> extends ITenantEngineLifecycleComponent {

    /**
     * Get unique id for event source.
     * 
     * @return
     */
    public String getSourceId();

    /**
     * Set event sources manager.
     * 
     * @param eventSourcesManager
     */
    public void setEventSourcesManager(IEventSourcesManager eventSourcesManager);

    /**
     * Set the device event decoder.
     * 
     * @param decoder
     */
    public void setDeviceEventDecoder(IDeviceEventDecoder<T> decoder);

    /**
     * Set the device event deduplicator.
     * 
     * @param deduplicator
     */
    public void setDeviceEventDeduplicator(IDeviceEventDeduplicator deduplicator);

    /**
     * Set the list of {@link IInboundEventReceiver} that feed this processor.
     * 
     * @param receivers
     */
    public void setInboundEventReceivers(List<IInboundEventReceiver<T>> receivers);

    /**
     * Get the raw payload as a byte array.
     * 
     * @param payload
     * @return
     */
    public byte[] getRawPayload(T payload);

    /**
     * Called by {@link IInboundEventReceiver} when an encoded event is
     * received.
     * 
     * @param receiver
     * @param encodedEvent
     * @param metadata
     * @throws EventDecodeException
     */
    public void onEncodedEventReceived(IInboundEventReceiver<T> receiver, T encodedEvent, Map<String, Object> metadata)
	    throws EventDecodeException;
}