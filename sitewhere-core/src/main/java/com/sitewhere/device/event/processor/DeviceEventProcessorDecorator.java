/*
 * DeviceEventProcessorDecorator.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.event.processor;

import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.rest.model.device.DeviceManagementDecorator;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceEventBatch;
import com.sitewhere.spi.device.event.IDeviceEventBatchResponse;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.processor.IOutboundEventProcessorChain;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;

/**
 * Acts as a decorator for injecting a {@link IOutboundEventProcessorChain} into the
 * default processing flow.
 * 
 * @author Derek
 */
public class DeviceEventProcessorDecorator extends DeviceManagementDecorator {

	/** Processor chain */
	private IOutboundEventProcessorChain chain;

	public DeviceEventProcessorDecorator(IDeviceManagement delegate, IOutboundEventProcessorChain chain) {
		super(delegate);
		this.chain = chain;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.rest.model.device.DeviceManagementDecorator#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		super.start();
		this.chain.start();
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
		return SiteWherePersistence.deviceEventBatchLogic(assignmentToken, batch, this, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.rest.model.device.DeviceManagementDecorator#addDeviceMeasurements
	 * (java.lang.String,
	 * com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest, boolean)
	 */
	@Override
	public IDeviceMeasurements addDeviceMeasurements(String assignmentToken,
			IDeviceMeasurementsCreateRequest request, boolean updateState) throws SiteWhereException {
		chain.beforeMeasurements(assignmentToken, request);
		IDeviceMeasurements result = super.addDeviceMeasurements(assignmentToken, request, updateState);
		chain.afterMeasurements(result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.rest.model.device.DeviceManagementDecorator#addDeviceLocation(java
	 * .lang.String, com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest,
	 * boolean)
	 */
	@Override
	public IDeviceLocation addDeviceLocation(String assignmentToken, IDeviceLocationCreateRequest request,
			boolean updateState) throws SiteWhereException {
		chain.beforeLocation(assignmentToken, request);
		IDeviceLocation result = super.addDeviceLocation(assignmentToken, request, updateState);
		chain.afterLocation(result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.rest.model.device.DeviceManagementDecorator#addDeviceAlert(java.lang
	 * .String, com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest, boolean)
	 */
	@Override
	public IDeviceAlert addDeviceAlert(String assignmentToken, IDeviceAlertCreateRequest request,
			boolean updateState) throws SiteWhereException {
		chain.beforeAlert(assignmentToken, request);
		IDeviceAlert result = super.addDeviceAlert(assignmentToken, request, updateState);
		chain.afterAlert(result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.rest.model.device.DeviceManagementDecorator#addDeviceCommandInvocation
	 * (java.lang.String, com.sitewhere.spi.device.command.IDeviceCommand,
	 * com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest)
	 */
	@Override
	public IDeviceCommandInvocation addDeviceCommandInvocation(String assignmentToken,
			IDeviceCommand command, IDeviceCommandInvocationCreateRequest request) throws SiteWhereException {
		chain.beforeCommandInvocation(assignmentToken, request);
		IDeviceCommandInvocation result = super.addDeviceCommandInvocation(assignmentToken, command, request);
		chain.afterCommandInvocation(result);
		return result;
	}
}