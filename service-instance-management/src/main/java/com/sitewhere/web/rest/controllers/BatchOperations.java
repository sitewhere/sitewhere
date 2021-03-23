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
package com.sitewhere.web.rest.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirements;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.api.batch.BatchElementMarshalHelper;
import com.sitewhere.microservice.api.batch.BatchOperationMarshalHelper;
import com.sitewhere.microservice.api.batch.BatchUtils;
import com.sitewhere.microservice.api.batch.IBatchManagement;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.schedule.IScheduleManagement;
import com.sitewhere.microservice.api.schedule.ScheduledJobHelper;
import com.sitewhere.rest.model.batch.request.BatchCommandInvocationRequest;
import com.sitewhere.rest.model.batch.request.InvocationByAssignmentCriteriaRequest;
import com.sitewhere.rest.model.batch.request.InvocationByDeviceCriteriaRequest;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.batch.BatchOperationSearchCriteria;
import com.sitewhere.rest.model.search.device.BatchElementSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.batch.IBatchElement;
import com.sitewhere.spi.batch.IBatchOperation;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest;
import com.sitewhere.spi.search.ISearchResults;

import io.swagger.annotations.Api;

/**
 * Controller for batch operations.
 */
@Path("/api/batch")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "batch")
@Tag(name = "Batch Operations", description = "Batch operations are used to interact with large groups of devices.")
@SecurityRequirements({ @SecurityRequirement(name = "jwtAuth", scopes = {}),
	@SecurityRequirement(name = "tenantIdHeader", scopes = {}),
	@SecurityRequirement(name = "tenantAuthHeader", scopes = {}) })
public class BatchOperations {

    @Inject
    private IInstanceManagementMicroservice microservice;

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(BatchOperations.class);

    /**
     * Get batch operation by token.
     * 
     * @param batchToken
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{batchToken}")
    @Operation(summary = "Get batch operation by unique token", description = "Get batch operation by unique token")
    public Response getBatchOperationByToken(
	    @Parameter(description = "Unique token that identifies batch operation", required = true) @PathParam("batchToken") String batchToken)
	    throws SiteWhereException {
	IBatchOperation batch = getBatchManagement().getBatchOperationByToken(batchToken);
	if (batch == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidBatchOperationToken, ErrorLevel.ERROR);
	}
	BatchOperationMarshalHelper helper = new BatchOperationMarshalHelper();
	return Response.ok(helper.convert(batch)).build();
    }

    /**
     * List batch operations that match the given criteria.
     * 
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Operation(summary = "List batch operations", description = "List batch operations")
    public Response listBatchOperations(
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize)
	    throws SiteWhereException {
	BatchOperationSearchCriteria criteria = new BatchOperationSearchCriteria(page, pageSize);

	ISearchResults<? extends IBatchOperation> results = getBatchManagement().listBatchOperations(criteria);
	BatchOperationMarshalHelper helper = new BatchOperationMarshalHelper();
	List<IBatchOperation> converted = new ArrayList<IBatchOperation>();
	for (IBatchOperation op : results.getResults()) {
	    converted.add(helper.convert(op));
	}
	return Response.ok(new SearchResults<IBatchOperation>(converted, results.getNumResults())).build();
    }

    /**
     * List batch operation elements that match criteria.
     * 
     * @param operationToken
     * @param includeDevice
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{operationToken}/elements")
    @Operation(summary = "List batch operation elements", description = "List batch operation elements")
    public Response listBatchOperationElements(
	    @Parameter(description = "Unique batch operation token", required = true) @PathParam("operationToken") String operationToken,
	    @Parameter(description = "Include device information", required = false) @QueryParam("includeDevice") @DefaultValue("false") boolean includeDevice,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize)
	    throws SiteWhereException {
	IBatchOperation batchOperation = assureBatchOperation(operationToken);
	BatchElementSearchCriteria criteria = new BatchElementSearchCriteria(page, pageSize);
	ISearchResults<? extends IBatchElement> results = getBatchManagement().listBatchElements(batchOperation.getId(),
		criteria);

	BatchElementMarshalHelper helper = new BatchElementMarshalHelper();
	helper.setIncludeDevice(includeDevice);
	List<IBatchElement> converted = new ArrayList<IBatchElement>();
	for (IBatchElement element : results.getResults()) {
	    converted.add(helper.convert(element, getDeviceManagement()));
	}
	return Response.ok(new SearchResults<IBatchElement>(converted, results.getNumResults())).build();
    }

    /**
     * Create a batch command invocation.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Path("/command")
    @Operation(summary = "Create new batch command invocation", description = "Create new batch command invocation")
    public Response createBatchCommandInvocation(@RequestBody BatchCommandInvocationRequest request)
	    throws SiteWhereException {
	IBatchOperation result = getBatchManagement().createBatchCommandInvocation(request);
	BatchOperationMarshalHelper helper = new BatchOperationMarshalHelper();
	return Response.ok(helper.convert(result)).build();
    }

    /**
     * Create a batch operation that invokes a command for all devices that match
     * the given criteria.
     * 
     * @param request
     * @param scheduleToken
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Path("/command/criteria/device")
    @Operation(summary = "Create batch command operation based on device criteria", description = "Create batch command operation based on device criteria")
    public Response createInvocationsByDeviceCriteria(@RequestBody InvocationByDeviceCriteriaRequest request,
	    @Parameter(description = "Schedule token", required = false) @QueryParam("scheduleToken") String scheduleToken)
	    throws SiteWhereException {
	if (scheduleToken != null) {
	    IScheduledJobCreateRequest job = ScheduledJobHelper
		    .createBatchCommandInvocationJobForDeviceCriteria(request, scheduleToken);
	    return Response.ok(getScheduleManagement().createScheduledJob(job)).build();
	} else {
	    // Resolve tokens for devices matching criteria.
	    List<String> deviceTokens = BatchUtils.resolveDeviceTokensForDeviceCriteria(request, getDeviceManagement(),
		    getAssetManagement());

	    // Create batch command invocation.
	    BatchCommandInvocationRequest invoke = new BatchCommandInvocationRequest();
	    invoke.setToken(request.getToken());
	    invoke.setCommandToken(request.getCommandToken());
	    invoke.setParameterValues(request.getParameterValues());
	    invoke.setDeviceTokens(deviceTokens);

	    IBatchOperation result = getBatchManagement().createBatchCommandInvocation(invoke);
	    BatchOperationMarshalHelper helper = new BatchOperationMarshalHelper();
	    return Response.ok(helper.convert(result)).build();
	}
    }

    /**
     * Create batch command invocation based on device assignment criteria.
     * 
     * @param request
     * @param scheduleToken
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Path("/command/criteria/assignment")
    @Operation(summary = "Create batch command invocation based on device assignment criteria", description = "Create batch command invocation based on device assignment criteria")
    public Response createInvocationsByAssignmentCriteria(@RequestBody InvocationByAssignmentCriteriaRequest request,
	    @Parameter(description = "Schedule token", required = false) @QueryParam("scheduleToken") String scheduleToken)
	    throws SiteWhereException {
	if (scheduleToken != null) {
	    IScheduledJobCreateRequest job = ScheduledJobHelper
		    .createBatchCommandInvocationJobForAssignmentCriteria(request, scheduleToken);
	    return Response.ok(getScheduleManagement().createScheduledJob(job)).build();
	} else {
	    // Resolve tokens for devices matching criteria.
	    List<String> deviceTokens = BatchUtils.resolveDeviceTokensForAssignmentCriteria(request,
		    getDeviceManagement(), getAssetManagement());

	    // Create batch command invocation.
	    BatchCommandInvocationRequest invoke = new BatchCommandInvocationRequest();
	    invoke.setToken(request.getToken());
	    invoke.setCommandToken(request.getCommandToken());
	    invoke.setParameterValues(request.getParameterValues());
	    invoke.setDeviceTokens(deviceTokens);

	    IBatchOperation result = getBatchManagement().createBatchCommandInvocation(invoke);
	    BatchOperationMarshalHelper helper = new BatchOperationMarshalHelper();
	    return Response.ok(helper.convert(result)).build();
	}
    }

    /**
     * Get a device command by unique id. Throw an exception if not found.
     * 
     * @param deviceCommandId
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceCommand assureDeviceCommand(UUID deviceCommandId) throws SiteWhereException {
	IDeviceCommand command = getDeviceManagement().getDeviceCommand(deviceCommandId);
	if (command == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceCommandToken, ErrorLevel.ERROR);
	}
	return command;
    }

    /**
     * Verify that the batch operation associated with the given token exists.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected IBatchOperation assureBatchOperation(String token) throws SiteWhereException {
	IBatchOperation batchOperation = getBatchManagement().getBatchOperationByToken(token);
	if (batchOperation == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidBatchOperationToken, ErrorLevel.ERROR);
	}
	return batchOperation;
    }

    private IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagement();
    }

    private IAssetManagement getAssetManagement() {
	return getMicroservice().getAssetManagement();
    }

    protected IBatchManagement getBatchManagement() {
	return getMicroservice().getBatchManagementApiChannel();
    }

    protected IScheduleManagement getScheduleManagement() {
	return getMicroservice().getScheduleManagementApiChannel();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}