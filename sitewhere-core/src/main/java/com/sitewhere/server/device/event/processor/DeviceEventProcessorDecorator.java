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

import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.rest.model.device.DeviceManagementDecorator;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceEventBatch;
import com.sitewhere.spi.device.event.IDeviceEventBatchResponse;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.processor.IDeviceEventProcessorChain;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
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
	 * com.sitewhere.rest.model.device.DeviceManagementDecorator#addDeviceEventBatch(java
	 * .lang.String, com.sitewhere.spi.device.event.IDeviceEventBatch)
	 */
	@Override
	public IDeviceEventBatchResponse addDeviceEventBatch(String assignmentToken, IDeviceEventBatch batch)
			throws SiteWhereException {
		return SiteWherePersistence.deviceEventBatchLogic(assignmentToken, batch, this);
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
		chain.beforeMeasurements(assignment, request);
		IDeviceMeasurements result = super.addDeviceMeasurements(assignment, request);
		chain.afterMeasurements(result);
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
		chain.beforeLocation(assignment, request);
		IDeviceLocation result = super.addDeviceLocation(assignment, request);
		chain.afterLocation(result);
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
		chain.beforeAlert(assignment, request);
		IDeviceAlert result = super.addDeviceAlert(assignment, request);
		chain.afterAlert(result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.rest.model.device.DeviceManagementDecorator#addDeviceCommandInvocation
	 * (com.sitewhere.spi.device.IDeviceAssignment,
	 * com.sitewhere.spi.device.command.IDeviceCommand,
	 * com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest)
	 */
	@Override
	public IDeviceCommandInvocation addDeviceCommandInvocation(IDeviceAssignment assignment,
			IDeviceCommand command, IDeviceCommandInvocationCreateRequest request) throws SiteWhereException {
		chain.beforeCommandInvocation(assignment, request);
		IDeviceCommandInvocation result = super.addDeviceCommandInvocation(assignment, command, request);
		chain.afterCommandInvocation(result);
		return result;
	}
}