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
import com.sitewhere.spi.device.event.processor.IInboundEventProcessor;
import com.sitewhere.spi.device.event.processor.IInboundEventProcessorChain;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;

/**
 * Default implementation of {@link IInboundEventProcessorChain} interface.
 * 
 * @author Derek
 */
public class DefaultInboundEventProcessorChain extends LifecycleComponent implements
		IInboundEventProcessorChain {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(DefaultInboundEventProcessorChain.class);

	/** List of processors */
	private List<IInboundEventProcessor> processors = new ArrayList<IInboundEventProcessor>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		for (IInboundEventProcessor processor : getProcessors()) {
			processor.lifecycleStart();
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
		for (IInboundEventProcessor processor : getProcessors()) {
			processor.lifecycleStop();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IInboundEventProcessor#onRegistrationRequest
	 * (java.lang.String, java.lang.String,
	 * com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest)
	 */
	@Override
	public void onRegistrationRequest(String hardwareId, String originator, IDeviceRegistrationRequest request)
			throws SiteWhereException {
		for (IInboundEventProcessor processor : getProcessors()) {
			try {
				processor.onRegistrationRequest(hardwareId, originator, request);
			} catch (SiteWhereException e) {
				LOGGER.error("Processor failed to process registration request.", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.processor.IInboundEventProcessor#
	 * onDeviceCommandResponseRequest(java.lang.String, java.lang.String,
	 * com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest)
	 */
	@Override
	public void onDeviceCommandResponseRequest(String hardwareId, String originator,
			IDeviceCommandResponseCreateRequest request) throws SiteWhereException {
		for (IInboundEventProcessor processor : getProcessors()) {
			try {
				processor.onDeviceCommandResponseRequest(hardwareId, originator, request);
			} catch (SiteWhereException e) {
				LOGGER.error("Processor failed to process command response request.", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.processor.IInboundEventProcessor#
	 * onDeviceMeasurementsCreateRequest(java.lang.String, java.lang.String,
	 * com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest)
	 */
	@Override
	public void onDeviceMeasurementsCreateRequest(String hardwareId, String originator,
			IDeviceMeasurementsCreateRequest request) throws SiteWhereException {
		for (IInboundEventProcessor processor : getProcessors()) {
			try {
				processor.onDeviceMeasurementsCreateRequest(hardwareId, originator, request);
			} catch (SiteWhereException e) {
				LOGGER.error("Processor failed to process measurements create request.", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.processor.IInboundEventProcessor#
	 * onDeviceLocationCreateRequest(java.lang.String, java.lang.String,
	 * com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest)
	 */
	@Override
	public void onDeviceLocationCreateRequest(String hardwareId, String originator,
			IDeviceLocationCreateRequest request) throws SiteWhereException {
		for (IInboundEventProcessor processor : getProcessors()) {
			try {
				processor.onDeviceLocationCreateRequest(hardwareId, originator, request);
			} catch (SiteWhereException e) {
				LOGGER.error("Processor failed to process location create request.", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.processor.IInboundEventProcessor#
	 * onDeviceAlertCreateRequest(java.lang.String, java.lang.String,
	 * com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest)
	 */
	@Override
	public void onDeviceAlertCreateRequest(String hardwareId, String originator,
			IDeviceAlertCreateRequest request) throws SiteWhereException {
		for (IInboundEventProcessor processor : getProcessors()) {
			try {
				processor.onDeviceAlertCreateRequest(hardwareId, originator, request);
			} catch (SiteWhereException e) {
				LOGGER.error("Processor failed to process alert create request.", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IInboundEventProcessorChain#getProcessors
	 * ()
	 */
	public List<IInboundEventProcessor> getProcessors() {
		return processors;
	}

	public void setProcessors(List<IInboundEventProcessor> processors) {
		this.processors = processors;
	}
}