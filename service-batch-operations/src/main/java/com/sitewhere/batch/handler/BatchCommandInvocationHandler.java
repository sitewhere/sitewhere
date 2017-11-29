/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.handler;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.batch.BatchOperationTypes;
import com.sitewhere.batch.spi.IBatchOperationHandler;
import com.sitewhere.batch.spi.microservice.IBatchOperationsMicroservice;
import com.sitewhere.grpc.model.spi.client.IDeviceEventManagementApiChannel;
import com.sitewhere.grpc.model.spi.client.IDeviceManagementApiChannel;
import com.sitewhere.rest.model.device.event.request.DeviceCommandInvocationCreateRequest;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.batch.ElementProcessingStatus;
import com.sitewhere.spi.batch.IBatchElement;
import com.sitewhere.spi.batch.IBatchOperation;
import com.sitewhere.spi.batch.request.IBatchCommandInvocationRequest;
import com.sitewhere.spi.batch.request.IBatchElementUpdateRequest;
import com.sitewhere.spi.batch.request.IBatchOperationCreateRequest;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.CommandInitiator;
import com.sitewhere.spi.device.event.CommandTarget;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;

/**
 * Operation handler for batch command invocations.
 * 
 * @author Derek
 */
public class BatchCommandInvocationHandler extends TenantEngineLifecycleComponent implements IBatchOperationHandler {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /*
     * @see com.sitewhere.batch.spi.IBatchOperationHandler#getOperationType()
     */
    @Override
    public String getOperationType() {
	return BatchOperationTypes.OPERATION_BATCH_COMMAND_INVOCATION;
    }

    /*
     * @see
     * com.sitewhere.batch.spi.IBatchOperationHandler#process(com.sitewhere.spi.
     * batch.IBatchOperation, com.sitewhere.spi.batch.IBatchElement,
     * com.sitewhere.spi.device.request.IBatchElementUpdateRequest)
     */
    @Override
    public ElementProcessingStatus process(IBatchOperation operation, IBatchElement element,
	    IBatchElementUpdateRequest updated) throws SiteWhereException {
	getLogger().info("Processing command invocation: " + element.getHardwareId());

	// Find information about the command to be executed.
	String commandToken = operation.getParameters().get(IBatchCommandInvocationRequest.PARAM_COMMAND_TOKEN);
	if (commandToken == null) {
	    throw new SiteWhereException("Command token not found in batch command invocation request.");
	}
	IDeviceCommand command = getDeviceManagement().getDeviceCommandByToken(commandToken);
	if (command == null) {
	    throw new SiteWhereException("Invalid command token referenced by batch command invocation.");
	}

	// Find information about the device to execute the command against.
	IDevice device = getDeviceManagement().getDeviceByHardwareId(element.getHardwareId());
	if (device == null) {
	    throw new SiteWhereException("Invalid device hardware id in command invocation.");
	}

	// Find the current assignment information for the device.
	IDeviceAssignment assignment = getDeviceManagement().getDeviceAssignmentByToken(device.getAssignmentToken());
	if (assignment == null) {
	    getLogger().info("Device is not currently assigned. Skipping command invocation.");
	    return ElementProcessingStatus.Failed;
	}

	// Create the request.
	DeviceCommandInvocationCreateRequest request = new DeviceCommandInvocationCreateRequest();
	request.setCommandToken(commandToken);
	request.setInitiator(CommandInitiator.BatchOperation);
	request.setInitiatorId(null);
	request.setTarget(CommandTarget.Assignment);
	request.setTargetId(assignment.getToken());
	request.setParameterValues(operation.getMetadata());
	Map<String, String> metadata = new HashMap<String, String>();
	metadata.put(IBatchOperationCreateRequest.META_BATCH_OPERATION_ID, operation.getToken());
	request.setMetadata(metadata);

	// Invoke the command.
	IDeviceCommandInvocation invocation = getDeviceEventManagement().addDeviceCommandInvocation(assignment,
		request);
	updated.getMetadata().put(IBatchCommandInvocationRequest.META_INVOCATION_EVENT_ID, invocation.getId());

	return ElementProcessingStatus.Succeeded;
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    public IDeviceManagementApiChannel getDeviceManagement() {
	return ((IBatchOperationsMicroservice) getTenantEngine().getMicroservice()).getDeviceManagementApiChannel();
    }

    public IDeviceEventManagementApiChannel getDeviceEventManagement() {
	return ((IBatchOperationsMicroservice) getTenantEngine().getMicroservice())
		.getDeviceEventManagementApiChannel();
    }
}