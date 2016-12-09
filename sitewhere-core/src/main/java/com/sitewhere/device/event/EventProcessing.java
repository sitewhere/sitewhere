/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.event;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.device.communication.BlockingQueueInboundProcessingStrategy;
import com.sitewhere.device.communication.BlockingQueueOutboundProcessingStrategy;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.IInboundProcessingStrategy;
import com.sitewhere.spi.device.communication.IOutboundProcessingStrategy;
import com.sitewhere.spi.device.event.IEventProcessing;
import com.sitewhere.spi.device.event.processor.IInboundEventProcessorChain;
import com.sitewhere.spi.device.event.processor.IOutboundEventProcessorChain;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Default implementation of {@link IEventProcessing}.
 * 
 * @author Derek
 */
public class EventProcessing extends TenantLifecycleComponent implements IEventProcessing {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Configured inbound processing strategy */
    private IInboundProcessingStrategy inboundProcessingStrategy = new BlockingQueueInboundProcessingStrategy();

    /** Configured inbound event processor chain */
    private IInboundEventProcessorChain inboundEventProcessorChain;

    /** Configured outbound processing strategy */
    private IOutboundProcessingStrategy outboundProcessingStrategy = new BlockingQueueOutboundProcessingStrategy();

    /** Configured outbound event processor chain */
    private IOutboundEventProcessorChain outboundEventProcessorChain;

    public EventProcessing() {
	super(LifecycleComponentType.EventProcessing);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {

	// Enable outbound processor chain.
	if (getOutboundEventProcessorChain() != null) {
	    startNestedComponent(getOutboundEventProcessorChain(), monitor, "Outbound processor chain startup failed.",
		    true);
	    getOutboundEventProcessorChain().setProcessingEnabled(true);
	}

	// Enable inbound processor chain.
	if (getInboundEventProcessorChain() != null) {
	    startNestedComponent(getInboundEventProcessorChain(), monitor, "Inbound processor chain startup failed.",
		    true);
	}

	// Start outbound processing strategy.
	if (getOutboundProcessingStrategy() == null) {
	    throw new SiteWhereException("No outbound processing strategy configured for communication subsystem.");
	}
	startNestedComponent(getOutboundProcessingStrategy(), monitor, true);

	// Start inbound processing strategy.
	if (getInboundProcessingStrategy() == null) {
	    throw new SiteWhereException("No inbound processing strategy configured for communication subsystem.");
	}
	startNestedComponent(getInboundProcessingStrategy(), monitor, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop(com.sitewhere
     * .spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {

	// Stop inbound processing strategy.
	if (getInboundProcessingStrategy() != null) {
	    getInboundProcessingStrategy().lifecycleStop(monitor);
	}

	// Stop outbound processing strategy.
	if (getOutboundProcessingStrategy() != null) {
	    getOutboundProcessingStrategy().lifecycleStop(monitor);
	}

	if (getInboundEventProcessorChain() != null) {
	    getInboundEventProcessorChain().lifecycleStop(monitor);
	}

	if (getOutboundEventProcessorChain() != null) {
	    getOutboundEventProcessorChain().setProcessingEnabled(false);
	    getOutboundEventProcessorChain().lifecycleStop(monitor);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    public IInboundProcessingStrategy getInboundProcessingStrategy() {
	return inboundProcessingStrategy;
    }

    public void setInboundProcessingStrategy(IInboundProcessingStrategy inboundProcessingStrategy) {
	this.inboundProcessingStrategy = inboundProcessingStrategy;
    }

    public IInboundEventProcessorChain getInboundEventProcessorChain() {
	return inboundEventProcessorChain;
    }

    public void setInboundEventProcessorChain(IInboundEventProcessorChain inboundEventProcessorChain) {
	this.inboundEventProcessorChain = inboundEventProcessorChain;
    }

    public IOutboundProcessingStrategy getOutboundProcessingStrategy() {
	return outboundProcessingStrategy;
    }

    public void setOutboundProcessingStrategy(IOutboundProcessingStrategy outboundProcessingStrategy) {
	this.outboundProcessingStrategy = outboundProcessingStrategy;
    }

    public IOutboundEventProcessorChain getOutboundEventProcessorChain() {
	return outboundEventProcessorChain;
    }

    public void setOutboundEventProcessorChain(IOutboundEventProcessorChain outboundEventProcessorChain) {
	this.outboundEventProcessorChain = outboundEventProcessorChain;
    }
}