/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.event.processor;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.processor.IOutboundEventProcessor;
import com.sitewhere.spi.device.event.processor.IOutboundEventProcessorChain;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Default implementation of {@link IOutboundEventProcessorChain} interface.
 * 
 * @author Derek
 */
public class DefaultOutboundEventProcessorChain extends LifecycleComponent implements
		IOutboundEventProcessorChain {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(DefaultOutboundEventProcessorChain.class);

	/** Indicates whether processing is enabled */
	private boolean processingEnabled = false;

	/** List of event processors */
	private List<IOutboundEventProcessor> processors = new ArrayList<IOutboundEventProcessor>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		getLifecycleComponents().clear();
		for (IOutboundEventProcessor processor : getProcessors()) {
			startNestedComponent(processor, false);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		for (IOutboundEventProcessor processor : getProcessors()) {
			processor.lifecycleStop();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.processor.IOutboundEventProcessorChain#
	 * setProcessingEnabled(boolean)
	 */
	@Override
	public void setProcessingEnabled(boolean enabled) {
		this.processingEnabled = enabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.processor.IOutboundEventProcessorChain#
	 * isProcessingEnabled()
	 */
	@Override
	public boolean isProcessingEnabled() {
		return processingEnabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IOutboundEventProcessor#onMeasurements
	 * (com.sitewhere.spi.device.event.IDeviceMeasurements)
	 */
	@Override
	public void onMeasurements(IDeviceMeasurements measurements) throws SiteWhereException {
		if (isProcessingEnabled()) {
			for (IOutboundEventProcessor processor : getProcessors()) {
				try {
					if (processor.getLifecycleStatus() == LifecycleStatus.Started) {
						processor.onMeasurements(measurements);
					} else {
						logSkipped(processor);
					}
				} catch (SiteWhereException e) {
					LOGGER.error(e);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IOutboundEventProcessor#onLocation(com
	 * .sitewhere.spi.device.event.IDeviceLocation)
	 */
	@Override
	public void onLocation(IDeviceLocation location) throws SiteWhereException {
		if (isProcessingEnabled()) {
			for (IOutboundEventProcessor processor : getProcessors()) {
				try {
					if (processor.getLifecycleStatus() == LifecycleStatus.Started) {
						processor.onLocation(location);
					} else {
						logSkipped(processor);
					}
				} catch (SiteWhereException e) {
					LOGGER.error(e);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IOutboundEventProcessor#onAlert(com.sitewhere
	 * .spi.device.event.IDeviceAlert)
	 */
	@Override
	public void onAlert(IDeviceAlert alert) throws SiteWhereException {
		if (isProcessingEnabled()) {
			for (IOutboundEventProcessor processor : getProcessors()) {
				try {
					if (processor.getLifecycleStatus() == LifecycleStatus.Started) {
						processor.onAlert(alert);
					} else {
						logSkipped(processor);
					}
				} catch (SiteWhereException e) {
					LOGGER.error(e);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IOutboundEventProcessor#onCommandInvocation
	 * (com.sitewhere.spi.device.event.IDeviceCommandInvocation)
	 */
	@Override
	public void onCommandInvocation(IDeviceCommandInvocation invocation) throws SiteWhereException {
		if (isProcessingEnabled()) {
			for (IOutboundEventProcessor processor : getProcessors()) {
				try {
					if (processor.getLifecycleStatus() == LifecycleStatus.Started) {
						processor.onCommandInvocation(invocation);
					} else {
						logSkipped(processor);
					}
				} catch (SiteWhereException e) {
					LOGGER.error(e);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IOutboundEventProcessor#onCommandResponse
	 * (com.sitewhere.spi.device.event.IDeviceCommandResponse)
	 */
	@Override
	public void onCommandResponse(IDeviceCommandResponse response) throws SiteWhereException {
		if (isProcessingEnabled()) {
			for (IOutboundEventProcessor processor : getProcessors()) {
				try {
					if (processor.getLifecycleStatus() == LifecycleStatus.Started) {
						processor.onCommandResponse(response);
					} else {
						logSkipped(processor);
					}
				} catch (SiteWhereException e) {
					LOGGER.error(e);
				}
			}
		}
	}

	/**
	 * Output log message indicating a processor was skipped.
	 * 
	 * @param processor
	 */
	protected void logSkipped(IOutboundEventProcessor processor) {
		getLogger().warn(
				"Skipping event processor " + processor.getComponentName() + " because its state is '"
						+ processor.getLifecycleStatus() + "'");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IOutboundEventProcessorChain#getProcessors
	 * ()
	 */
	@Override
	public List<IOutboundEventProcessor> getProcessors() {
		return processors;
	}

	public void setProcessors(List<IOutboundEventProcessor> processors) {
		this.processors = processors;
	}
}