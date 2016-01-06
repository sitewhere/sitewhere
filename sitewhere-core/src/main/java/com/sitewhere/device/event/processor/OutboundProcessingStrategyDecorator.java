/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.event.processor;

import com.sitewhere.SiteWhere;
import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.device.DeviceManagementDecorator;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.batch.IBatchOperation;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.communication.IOutboundProcessingStrategy;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEventBatch;
import com.sitewhere.spi.device.event.IDeviceEventBatchResponse;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.processor.IOutboundEventProcessorChain;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.request.IBatchCommandInvocationRequest;
import com.sitewhere.spi.device.request.IBatchOperationCreateRequest;

/**
 * Acts as a decorator for injecting a {@link IOutboundEventProcessorChain} into the
 * default processing flow.
 * 
 * @author Derek
 */
public class OutboundProcessingStrategyDecorator extends DeviceManagementDecorator {

	/** Cached strategy */
	private IOutboundProcessingStrategy strategy;

	public OutboundProcessingStrategyDecorator(IDeviceManagement delegate) {
		super(delegate);
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
	 * (java.lang.String,
	 * com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest)
	 */
	@Override
	public IDeviceMeasurements addDeviceMeasurements(String assignmentToken,
			IDeviceMeasurementsCreateRequest request) throws SiteWhereException {
		IDeviceMeasurements result = super.addDeviceMeasurements(assignmentToken, request);
		getOutboundProcessingStrategy().onMeasurements(result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.rest.model.device.DeviceManagementDecorator#addDeviceLocation(java
	 * .lang.String, com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest)
	 */
	@Override
	public IDeviceLocation addDeviceLocation(String assignmentToken, IDeviceLocationCreateRequest request)
			throws SiteWhereException {
		IDeviceLocation result = super.addDeviceLocation(assignmentToken, request);
		getOutboundProcessingStrategy().onLocation(result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.rest.model.device.DeviceManagementDecorator#addDeviceAlert(java.lang
	 * .String, com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest)
	 */
	@Override
	public IDeviceAlert addDeviceAlert(String assignmentToken, IDeviceAlertCreateRequest request)
			throws SiteWhereException {
		IDeviceAlert result = super.addDeviceAlert(assignmentToken, request);
		getOutboundProcessingStrategy().onAlert(result);
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
		IDeviceCommandInvocation result = super.addDeviceCommandInvocation(assignmentToken, command, request);
		getOutboundProcessingStrategy().onCommandInvocation(result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.rest.model.device.DeviceManagementDecorator#addDeviceCommandResponse
	 * (java.lang.String,
	 * com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest)
	 */
	@Override
	public IDeviceCommandResponse addDeviceCommandResponse(String assignmentToken,
			IDeviceCommandResponseCreateRequest request) throws SiteWhereException {
		IDeviceCommandResponse result = super.addDeviceCommandResponse(assignmentToken, request);
		getOutboundProcessingStrategy().onCommandResponse(result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.device.DeviceManagementDecorator#createBatchOperation(com.sitewhere
	 * .spi.device.request.IBatchOperationCreateRequest)
	 */
	@Override
	public IBatchOperation createBatchOperation(IBatchOperationCreateRequest request)
			throws SiteWhereException {
		IBatchOperation result = super.createBatchOperation(request);
		getOutboundProcessingStrategy().onBatchOperation(result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.device.DeviceManagementDecorator#createBatchCommandInvocation(com
	 * .sitewhere.spi.device.request.IBatchCommandInvocationRequest)
	 */
	@Override
	public IBatchOperation createBatchCommandInvocation(IBatchCommandInvocationRequest request)
			throws SiteWhereException {
		IBatchOperation result = super.createBatchCommandInvocation(request);
		getOutboundProcessingStrategy().onBatchOperation(result);
		return result;
	}

	/**
	 * Get the configured outbound processing strategy.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	protected IOutboundProcessingStrategy getOutboundProcessingStrategy() throws SiteWhereException {
		if (strategy == null) {
			strategy = SiteWhere.getServer().getEventProcessing(getTenant()).getOutboundProcessingStrategy();
		}
		return strategy;
	}
}