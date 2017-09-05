/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event;

import com.sitewhere.spi.device.communication.IInboundProcessingStrategy;
import com.sitewhere.spi.device.communication.IOutboundProcessingStrategy;
import com.sitewhere.spi.device.event.processor.IInboundEventProcessorChain;
import com.sitewhere.spi.device.event.processor.IOutboundEventProcessorChain;
import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

/**
 * Base interface for system components that enable the event processing
 * pipeline.
 * 
 * @author Derek
 */
public interface IEventProcessing extends ITenantLifecycleComponent {

    /**
     * Get the strategy for moving decoded events into the inbound chain.
     * 
     * @return
     */
    public IInboundProcessingStrategy getInboundProcessingStrategy();

    /**
     * Get chain of processor that will act on inbound data.
     * 
     * @return
     */
    public IInboundEventProcessorChain getInboundEventProcessorChain();

    /**
     * Get the strategy for moving processed events into the outbound chain.
     * 
     * @return
     */
    public IOutboundProcessingStrategy getOutboundProcessingStrategy();

    /**
     * Get chain of processors that will act on outbound data.
     * 
     * @return
     */
    public IOutboundEventProcessorChain getOutboundEventProcessorChain();
}