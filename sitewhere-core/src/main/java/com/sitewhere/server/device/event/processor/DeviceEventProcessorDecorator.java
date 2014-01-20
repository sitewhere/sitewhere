/*
 * DeviceEventProcessorDecorator.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.device.event.processor;

import com.sitewhere.rest.model.device.DeviceManagementDecorator;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.processor.IDeviceEventProcessor;
import com.sitewhere.spi.device.event.processor.IDeviceEventProcessorChain;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;

/**
 * Acts as a decorator for injecting a device event processor chain into the default
 * processing flow.
 * 
 * @author Derek
 */
public class DeviceEventProcessorDecorator extends DeviceManagementDecorator {

	/** Processor chain */
	private IDeviceEventProcessorChain chain;

	public DeviceEventProcessorDecorator(IDeviceManagement delegate, IDeviceEventProcessorChain chain) {
		super(delegate);
		this.chain = chain;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.rest.model.device.DeviceManagementDecorator#addDeviceMeasurements
	 * (com.sitewhere.spi.device.IDeviceAssignment,
	 * com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest)
	 */
	@Override
	public IDeviceMeasurements addDeviceMeasurements(IDeviceAssignment assignment,
			IDeviceMeasurementsCreateRequest request) throws SiteWhereException {
		for (IDeviceEventProcessor processor : chain.getProcessors()) {
			processor.beforeMeasurements(assignment, request);
		}
		IDeviceMeasurements result = super.addDeviceMeasurements(assignment, request);
		for (IDeviceEventProcessor processor : chain.getProcessors()) {
			processor.afterMeasurements(result);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.rest.model.device.DeviceManagementDecorator#addDeviceLocation(com
	 * .sitewhere.spi.device.IDeviceAssignment,
	 * com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest)
	 */
	@Override
	public IDeviceLocation addDeviceLocation(IDeviceAssignment assignment,
			IDeviceLocationCreateRequest request) throws SiteWhereException {
		for (IDeviceEventProcessor processor : chain.getProcessors()) {
			processor.beforeLocation(assignment, request);
		}
		IDeviceLocation result = super.addDeviceLocation(assignment, request);
		for (IDeviceEventProcessor processor : chain.getProcessors()) {
			processor.afterLocation(result);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.rest.model.device.DeviceManagementDecorator#addDeviceAlert(com.sitewhere
	 * .spi.device.IDeviceAssignment,
	 * com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest)
	 */
	@Override
	public IDeviceAlert addDeviceAlert(IDeviceAssignment assignment, IDeviceAlertCreateRequest request)
			throws SiteWhereException {
		for (IDeviceEventProcessor processor : chain.getProcessors()) {
			processor.beforeAlert(assignment, request);
		}
		IDeviceAlert result = super.addDeviceAlert(assignment, request);
		for (IDeviceEventProcessor processor : chain.getProcessors()) {
			processor.afterAlert(result);
		}
		return result;
	}
}