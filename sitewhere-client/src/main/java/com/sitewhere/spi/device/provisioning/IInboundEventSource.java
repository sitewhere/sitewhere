/*
 * IDeviceEventProcessor.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.provisioning;

import java.util.List;

import com.sitewhere.spi.ISiteWhereLifecycle;

/**
 * Entity that receives events from one or more {@link IInboundEventReceiver}, decodes
 * them, and forwards them for processing.
 * 
 * @author Derek
 */
public interface IInboundEventSource<T> extends ISiteWhereLifecycle {

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
}