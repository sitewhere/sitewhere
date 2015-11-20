/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.communication;

import java.util.List;

import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

/**
 * Entity that receives events from one or more {@link IInboundEventReceiver}, decodes
 * them, and forwards them for processing.
 * 
 * @author Derek
 */
public interface IInboundEventSource<T> extends ITenantLifecycleComponent {

	/**
	 * Get unique id for event source.
	 * 
	 * @return
	 */
	public String getSourceId();

	/**
	 * Indicates whether assignment state should be updated with events.
	 * 
	 * @return
	 */
	public boolean isUpdateAssignmentState();

	/**
	 * Set the device event decoder.
	 * 
	 * @param decoder
	 */
	public void setDeviceEventDecoder(IDeviceEventDecoder<T> decoder);

	/**
	 * Set the strategy for submitting inbound events into the bus.
	 * 
	 * @param strategy
	 */
	public void setInboundProcessingStrategy(IInboundProcessingStrategy strategy);

	/**
	 * Set the list of {@link IInboundEventReceiver} that feed this processor.
	 * 
	 * @param receivers
	 */
	public void setInboundEventReceivers(List<IInboundEventReceiver<T>> receivers);

	/**
	 * Called by {@link IInboundEventReceiver} when an encoded event is received.
	 * 
	 * @param receiver
	 * @param encodedEvent
	 */
	public void onEncodedEventReceived(IInboundEventReceiver<T> receiver, T encodedEvent);
}