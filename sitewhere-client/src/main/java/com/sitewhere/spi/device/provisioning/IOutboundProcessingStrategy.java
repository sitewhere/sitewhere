/*
 * IOutboundProcessingStrategy.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.provisioning;

import com.sitewhere.spi.ISiteWhereLifecycle;
import com.sitewhere.spi.device.event.processor.IOutboundEventProcessor;
import com.sitewhere.spi.device.event.processor.IOutboundEventProcessorChain;

/**
 * Provides a strategy for reporting system events to interested
 * {@link IOutboundEventProcessor} elements registered on the
 * {@link IOutboundEventProcessorChain}.
 * 
 * @author Derek
 */
public interface IOutboundProcessingStrategy extends ISiteWhereLifecycle {

}