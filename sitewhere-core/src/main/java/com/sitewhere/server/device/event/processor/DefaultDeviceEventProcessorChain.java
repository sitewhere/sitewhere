/*
 * DefaultDeviceEventProcessorChain.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.device.event.processor;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.processor.IDeviceEventProcessor;
import com.sitewhere.spi.device.event.processor.IDeviceEventProcessorChain;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;

/**
 * Default implementation of {@link IDeviceEventProcessorChain} interface.
 * 
 * @author Derek
 */
public class DefaultDeviceEventProcessorChain implements IDeviceEventProcessorChain, InitializingBean {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(DefaultDeviceEventProcessorChain.class);

	/** List of event processors */
	private List<IDeviceEventProcessor> processors = new ArrayList<IDeviceEventProcessor>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.processor.IDeviceEventProcessor#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		for (IDeviceEventProcessor processor : getProcessors()) {
			processor.start();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.processor.IDeviceEventProcessor#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IDeviceEventProcessor#beforeMeasurements
	 * (com.sitewhere.spi.device.IDeviceAssignment,
	 * com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest)
	 */
	@Override
	public void beforeMeasurements(IDeviceAssignment assignment, IDeviceMeasurementsCreateRequest request)
			throws SiteWhereException {
		for (IDeviceEventProcessor processor : getProcessors()) {
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
	 * com.sitewhere.spi.device.event.processor.IDeviceEventProcessor#afterMeasurements
	 * (com.sitewhere.spi.device.event.IDeviceMeasurements)
	 */
	@Override
	public void afterMeasurements(IDeviceMeasurements measurements) throws SiteWhereException {
		for (IDeviceEventProcessor processor : getProcessors()) {
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
	 * com.sitewhere.spi.device.event.processor.IDeviceEventProcessor#beforeLocation(com
	 * .sitewhere.spi.device.IDeviceAssignment,
	 * com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest)
	 */
	@Override
	public void beforeLocation(IDeviceAssignment assignment, IDeviceLocationCreateRequest request)
			throws SiteWhereException {
		for (IDeviceEventProcessor processor : getProcessors()) {
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
	 * com.sitewhere.spi.device.event.processor.IDeviceEventProcessor#afterLocation(com
	 * .sitewhere.spi.device.event.IDeviceLocation)
	 */
	@Override
	public void afterLocation(IDeviceLocation location) throws SiteWhereException {
		for (IDeviceEventProcessor processor : getProcessors()) {
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
	 * com.sitewhere.spi.device.event.processor.IDeviceEventProcessor#beforeAlert(com.
	 * sitewhere.spi.device.IDeviceAssignment,
	 * com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest)
	 */
	@Override
	public void beforeAlert(IDeviceAssignment assignment, IDeviceAlertCreateRequest request)
			throws SiteWhereException {
		for (IDeviceEventProcessor processor : getProcessors()) {
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
	 * com.sitewhere.spi.device.event.processor.IDeviceEventProcessor#afterAlert(com.sitewhere
	 * .spi.device.event.IDeviceAlert)
	 */
	@Override
	public void afterAlert(IDeviceAlert alert) throws SiteWhereException {
		for (IDeviceEventProcessor processor : getProcessors()) {
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
	 * @see
	 * com.sitewhere.spi.device.event.processor.IDeviceEventProcessor#beforeCommandInvocation
	 * (com.sitewhere.spi.device.IDeviceAssignment,
	 * com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest)
	 */
	@Override
	public void beforeCommandInvocation(IDeviceAssignment assignment,
			IDeviceCommandInvocationCreateRequest request) throws SiteWhereException {
		for (IDeviceEventProcessor processor : getProcessors()) {
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
	 * com.sitewhere.spi.device.event.processor.IDeviceEventProcessor#afterCommandInvocation
	 * (com.sitewhere.spi.device.event.IDeviceCommandInvocation)
	 */
	@Override
	public void afterCommandInvocation(IDeviceCommandInvocation invocation) throws SiteWhereException {
		for (IDeviceEventProcessor processor : getProcessors()) {
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
	 * com.sitewhere.spi.device.event.processor.IDeviceEventProcessorChain#getProcessors()
	 */
	@Override
	public List<IDeviceEventProcessor> getProcessors() {
		return processors;
	}

	public void setProcessors(List<IDeviceEventProcessor> processors) {
		this.processors = processors;
	}
}