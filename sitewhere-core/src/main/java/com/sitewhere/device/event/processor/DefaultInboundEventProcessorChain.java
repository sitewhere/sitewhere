/*
 * DefaultInboundEventProcessorChain.java 
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
import com.sitewhere.spi.device.event.processor.IInboundEventProcessor;
import com.sitewhere.spi.device.event.processor.IInboundEventProcessorChain;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;
import com.sitewhere.spi.device.provisioning.IDecodedDeviceEventRequest;

/**
 * Default implementation of {@link IInboundEventProcessorChain} interface.
 * 
 * @author Derek
 */
public class DefaultInboundEventProcessorChain implements IInboundEventProcessorChain {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(DefaultInboundEventProcessorChain.class);

	/** List of processors */
	private List<IInboundEventProcessor> processors = new ArrayList<IInboundEventProcessor>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		LOGGER.info("Inbound event processor chain starting...");
		for (IInboundEventProcessor processor : getProcessors()) {
			processor.start();
		}
		LOGGER.info("Inbound event processor chain started.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		LOGGER.info("Inbound event processor chain stopping...");
		for (IInboundEventProcessor processor : getProcessors()) {
			processor.stop();
		}
		LOGGER.info("Inbound event processor chain stopped.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.processor.IInboundEventProcessor#
	 * onDecodedDeviceEventRequest
	 * (com.sitewhere.spi.device.provisioning.IDecodedDeviceEventRequest)
	 */
	@Override
	public void onDecodedDeviceEventRequest(IDecodedDeviceEventRequest decoded) throws SiteWhereException {
		for (IInboundEventProcessor processor : getProcessors()) {
			try {
				processor.onDecodedDeviceEventRequest(decoded);
				if (decoded.getRequest() instanceof IDeviceRegistrationRequest) {
					onRegistrationRequest(decoded.getHardwareId(), decoded.getOriginator(),
							(IDeviceRegistrationRequest) decoded.getRequest());
				} else if (decoded.getRequest() instanceof IDeviceCommandResponseCreateRequest) {
					onDeviceCommandResponseRequest(decoded.getHardwareId(), decoded.getOriginator(),
							(IDeviceCommandResponseCreateRequest) decoded.getRequest());
				} else if (decoded.getRequest() instanceof IDeviceMeasurementsCreateRequest) {
					onDeviceMeasurementsCreateRequest(decoded.getHardwareId(), decoded.getOriginator(),
							(IDeviceMeasurementsCreateRequest) decoded.getRequest());
				} else if (decoded.getRequest() instanceof IDeviceLocationCreateRequest) {
					onDeviceLocationCreateRequest(decoded.getHardwareId(), decoded.getOriginator(),
							(IDeviceLocationCreateRequest) decoded.getRequest());
				} else if (decoded.getRequest() instanceof IDeviceAlertCreateRequest) {
					onDeviceAlertCreateRequest(decoded.getHardwareId(), decoded.getOriginator(),
							(IDeviceAlertCreateRequest) decoded.getRequest());
				} else {
					LOGGER.error("Decoded device event request could not be routed: "
							+ decoded.getRequest().getClass().getName());
				}
			} catch (SiteWhereException e) {
				LOGGER.error(e);
			}
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
				LOGGER.error(e);
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
				LOGGER.error(e);
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
				LOGGER.error(e);
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
				LOGGER.error(e);
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
				LOGGER.error(e);
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