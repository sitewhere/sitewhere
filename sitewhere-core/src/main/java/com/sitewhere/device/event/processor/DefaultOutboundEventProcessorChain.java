/*
 * DefaultDeviceEventProcessorChain.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.event.processor;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.processor.IOutboundEventProcessor;
import com.sitewhere.spi.device.event.processor.IOutboundEventProcessorChain;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;

/**
 * Default implementation of {@link IOutboundEventProcessorChain} interface.
 * 
 * @author Derek
 */
public class DefaultOutboundEventProcessorChain implements IOutboundEventProcessorChain {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(DefaultOutboundEventProcessorChain.class);

	/** List of event processors */
	private List<IOutboundEventProcessor> processors = new ArrayList<IOutboundEventProcessor>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		LOGGER.info("Outbound event processor chain starting...");
		for (IOutboundEventProcessor processor : getProcessors()) {
			processor.start();
		}
		LOGGER.info("Outbound event processor chain started.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		LOGGER.info("Outbound event processor chain stopping...");
		for (IOutboundEventProcessor processor : getProcessors()) {
			processor.stop();
		}
		LOGGER.info("Outbound event processor chain stopped.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IOutboundEventProcessor#beforeMeasurements
	 * (com.sitewhere.spi.device.IDeviceAssignment,
	 * com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest)
	 */
	@Override
	public void beforeMeasurements(IDeviceAssignment assignment, IDeviceMeasurementsCreateRequest request)
			throws SiteWhereException {
		for (IOutboundEventProcessor processor : getProcessors()) {
			try {
				processor.beforeMeasurements(assignment, request);
			} catch (SiteWhereException e) {
				LOGGER.error(e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IOutboundEventProcessor#afterMeasurements
	 * (com.sitewhere.spi.device.event.IDeviceMeasurements)
	 */
	@Override
	public void afterMeasurements(IDeviceMeasurements measurements) throws SiteWhereException {
		for (IOutboundEventProcessor processor : getProcessors()) {
			try {
				processor.afterMeasurements(measurements);
			} catch (SiteWhereException e) {
				LOGGER.error(e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IOutboundEventProcessor#beforeLocation
	 * (com.sitewhere.spi.device.IDeviceAssignment,
	 * com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest)
	 */
	@Override
	public void beforeLocation(IDeviceAssignment assignment, IDeviceLocationCreateRequest request)
			throws SiteWhereException {
		for (IOutboundEventProcessor processor : getProcessors()) {
			try {
				processor.beforeLocation(assignment, request);
			} catch (SiteWhereException e) {
				LOGGER.error(e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IOutboundEventProcessor#afterLocation(
	 * com.sitewhere.spi.device.event.IDeviceLocation)
	 */
	@Override
	public void afterLocation(IDeviceLocation location) throws SiteWhereException {
		for (IOutboundEventProcessor processor : getProcessors()) {
			try {
				processor.afterLocation(location);
			} catch (SiteWhereException e) {
				LOGGER.error(e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IOutboundEventProcessor#beforeAlert(com
	 * .sitewhere.spi.device.IDeviceAssignment,
	 * com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest)
	 */
	@Override
	public void beforeAlert(IDeviceAssignment assignment, IDeviceAlertCreateRequest request)
			throws SiteWhereException {
		for (IOutboundEventProcessor processor : getProcessors()) {
			try {
				processor.beforeAlert(assignment, request);
			} catch (SiteWhereException e) {
				LOGGER.error(e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IOutboundEventProcessor#afterAlert(com
	 * .sitewhere.spi.device.event.IDeviceAlert)
	 */
	@Override
	public void afterAlert(IDeviceAlert alert) throws SiteWhereException {
		for (IOutboundEventProcessor processor : getProcessors()) {
			try {
				processor.afterAlert(alert);
			} catch (SiteWhereException e) {
				LOGGER.error(e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.processor.IOutboundEventProcessor#
	 * beforeCommandInvocation(com.sitewhere.spi.device.IDeviceAssignment,
	 * com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest)
	 */
	@Override
	public void beforeCommandInvocation(IDeviceAssignment assignment,
			IDeviceCommandInvocationCreateRequest request) throws SiteWhereException {
		for (IOutboundEventProcessor processor : getProcessors()) {
			try {
				processor.beforeCommandInvocation(assignment, request);
			} catch (SiteWhereException e) {
				LOGGER.error(e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IOutboundEventProcessor#afterCommandInvocation
	 * (com.sitewhere.spi.device.event.IDeviceCommandInvocation)
	 */
	@Override
	public void afterCommandInvocation(IDeviceCommandInvocation invocation) throws SiteWhereException {
		for (IOutboundEventProcessor processor : getProcessors()) {
			try {
				processor.afterCommandInvocation(invocation);
			} catch (SiteWhereException e) {
				LOGGER.error(e);
			}
		}
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