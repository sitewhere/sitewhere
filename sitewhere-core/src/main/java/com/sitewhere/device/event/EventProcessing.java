/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.event;

import org.apache.log4j.Logger;

import com.sitewhere.device.communication.BlockingQueueInboundProcessingStrategy;
import com.sitewhere.device.communication.BlockingQueueOutboundProcessingStrategy;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.IInboundProcessingStrategy;
import com.sitewhere.spi.device.communication.IOutboundProcessingStrategy;
import com.sitewhere.spi.device.event.IEventProcessing;
import com.sitewhere.spi.device.event.processor.IInboundEventProcessorChain;
import com.sitewhere.spi.device.event.processor.IOutboundEventProcessorChain;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.server.tenant.ITenantHazelcastAware;
import com.sitewhere.spi.server.tenant.ITenantHazelcastConfiguration;

/**
 * Default implementation of {@link IEventProcessing}.
 * 
 * @author Derek
 */
public class EventProcessing extends TenantLifecycleComponent
		implements IEventProcessing, ITenantHazelcastAware {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(EventProcessing.class);

	/** Configured inbound processing strategy */
	private IInboundProcessingStrategy inboundProcessingStrategy =
			new BlockingQueueInboundProcessingStrategy();

	/** Configured inbound event processor chain */
	private IInboundEventProcessorChain inboundEventProcessorChain;

	/** Configured outbound processing strategy */
	private IOutboundProcessingStrategy outboundProcessingStrategy =
			new BlockingQueueOutboundProcessingStrategy();

	/** Configured outbound event processor chain */
	private IOutboundEventProcessorChain outboundEventProcessorChain;

	public EventProcessing() {
		super(LifecycleComponentType.EventProcessing);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {

		// Enable outbound processor chain.
		if (getOutboundEventProcessorChain() != null) {
			startNestedComponent(getOutboundEventProcessorChain(), "Outbound processor chain startup failed.",
					true);
			getOutboundEventProcessorChain().setProcessingEnabled(true);
		}

		// Enable inbound processor chain.
		if (getInboundEventProcessorChain() != null) {
			startNestedComponent(getInboundEventProcessorChain(), "Inbound processor chain startup failed.",
					true);
		}

		// Start outbound processing strategy.
		if (getOutboundProcessingStrategy() == null) {
			throw new SiteWhereException(
					"No outbound processing strategy configured for communication subsystem.");
		}
		startNestedComponent(getOutboundProcessingStrategy(), true);

		// Start inbound processing strategy.
		if (getInboundProcessingStrategy() == null) {
			throw new SiteWhereException(
					"No inbound processing strategy configured for communication subsystem.");
		}
		startNestedComponent(getInboundProcessingStrategy(), true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {

		// Stop inbound processing strategy.
		if (getInboundProcessingStrategy() != null) {
			getInboundProcessingStrategy().lifecycleStop();
		}

		// Stop outbound processing strategy.
		if (getOutboundProcessingStrategy() != null) {
			getOutboundProcessingStrategy().lifecycleStop();
		}

		if (getInboundEventProcessorChain() != null) {
			getInboundEventProcessorChain().lifecycleStop();
		}

		if (getOutboundEventProcessorChain() != null) {
			getOutboundEventProcessorChain().setProcessingEnabled(false);
			getOutboundEventProcessorChain().lifecycleStop();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.server.tenant.ITenantHazelcastAware#setHazelcastConfiguration(com
	 * .sitewhere.spi.server.tenant.ITenantHazelcastConfiguration)
	 */
	@Override
	public void setHazelcastConfiguration(ITenantHazelcastConfiguration configuration) {
		if (getOutboundEventProcessorChain() instanceof ITenantHazelcastAware) {
			((ITenantHazelcastAware) getOutboundEventProcessorChain()).setHazelcastConfiguration(
					configuration);
		}
		if (getInboundEventProcessorChain() instanceof ITenantHazelcastAware) {
			((ITenantHazelcastAware) getInboundEventProcessorChain()).setHazelcastConfiguration(
					configuration);
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