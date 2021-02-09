/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.batch.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sitewhere.batch.BatchOperationTypes;
import com.sitewhere.batch.spi.IBatchOperationHandler;
import com.sitewhere.batch.spi.microservice.IBatchOperationsMicroservice;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.event.DeviceEventRequestBuilder;
import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.rest.model.device.event.request.DeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.batch.ElementProcessingStatus;
import com.sitewhere.spi.batch.IBatchElement;
import com.sitewhere.spi.batch.IBatchOperation;
import com.sitewhere.spi.batch.request.IBatchCommandInvocationRequest;
import com.sitewhere.spi.batch.request.IBatchElementCreateRequest;
import com.sitewhere.spi.batch.request.IBatchOperationCreateRequest;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.CommandInitiator;
import com.sitewhere.spi.device.event.CommandTarget;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceEventContext;

/**
 * Operation handler for batch command invocations.
 */
public class BatchCommandInvocationHandler extends TenantEngineLifecycleComponent implements IBatchOperationHandler {

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
	    IBatchElementCreateRequest updated) throws SiteWhereException {
	getLogger().info("Processing command invocation: " + element.getDeviceId());

	// Find information about the device to execute the command against.
	IDevice device = getDeviceManagement().getDevice(element.getDeviceId());
	if (device == null) {
	    throw new SiteWhereException("Invalid device id in command invocation.");
	}

	// Find information about the command to be executed.
	String deviceCommandToken = operation.getParameters().get(IBatchCommandInvocationRequest.PARAM_COMMAND_TOKEN);
	if (deviceCommandToken == null) {
	    throw new SiteWhereException("Command token not found in batch command invocation request.");
	}
	IDeviceCommand command = getDeviceManagement().getDeviceCommandByToken(device.getDeviceTypeId(),
		deviceCommandToken);
	if (command == null) {
	    IDeviceType type = getDeviceManagement().getDeviceType(device.getDeviceTypeId());
	    throw new SiteWhereException(String.format(
		    "Invalid command token (%s) for device type '%s' referenced by batch command invocation.",
		    deviceCommandToken, type.getName()));
	}

	// Find the current assignment information for the device.
	List<? extends IDeviceAssignment> active = getDeviceManagement().getActiveDeviceAssignments(device.getId());
	if (active.size() == 0) {
	    getLogger().info("Device is not currently assigned. Skipping command invocation.");
	    return ElementProcessingStatus.Failed;
	}

	// TODO: Should batch operation target multiple assignments?
	IDeviceAssignment target = active.get(0);

	// Create the request.
	DeviceCommandInvocationCreateRequest request = new DeviceCommandInvocationCreateRequest();
	request.setCommandToken(command.getToken());
	request.setInitiator(CommandInitiator.BatchOperation);
	request.setInitiatorId(operation.getId().toString());
	request.setTarget(CommandTarget.Assignment);
	request.setTargetId(target.getToken());
	request.setParameterValues(operation.getMetadata());
	Map<String, String> metadata = new HashMap<String, String>();
	metadata.put(IBatchOperationCreateRequest.META_BATCH_OPERATION_TOKEN, operation.getToken());
	request.setMetadata(metadata);

	// Invoke the command.
	IDeviceEventContext context = DeviceEventRequestBuilder.getContextForAssignment(getDeviceManagement(), target);
	IDeviceCommandInvocation invocation = getDeviceEventManagement().addDeviceCommandInvocations(context, request)
		.get(0);
	updated.getMetadata().put(IBatchCommandInvocationRequest.META_INVOCATION_EVENT_ID,
		invocation.getId().toString());

	return ElementProcessingStatus.Succeeded;
    }

    public IDeviceManagement getDeviceManagement() {
	return ((IBatchOperationsMicroservice) getMicroservice()).getDeviceManagement();
    }

    public IDeviceEventManagement getDeviceEventManagement() {
	return ((IBatchOperationsMicroservice) getMicroservice()).getDeviceEventManagementApiChannel();
    }
}