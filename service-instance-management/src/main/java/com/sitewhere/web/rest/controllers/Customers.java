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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

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
import com.sitewhere.microservice.api.device.CustomerMarshalHelper;
import com.sitewhere.microservice.api.device.DeviceAssignmentMarshalHelper;
import com.sitewhere.microservice.api.device.DeviceAssignmentSummaryMarshalHelper;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.device.asset.DeviceAlertWithAsset;
import com.sitewhere.microservice.api.device.asset.DeviceCommandInvocationWithAsset;
import com.sitewhere.microservice.api.device.asset.DeviceCommandResponseWithAsset;
import com.sitewhere.microservice.api.device.asset.DeviceLocationWithAsset;
import com.sitewhere.microservice.api.device.asset.DeviceMeasurementsWithAsset;
import com.sitewhere.microservice.api.device.asset.DeviceStateChangeWithAsset;
import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.microservice.api.label.ILabelGeneration;
import com.sitewhere.rest.model.customer.request.CustomerCreateRequest;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.DeviceAssignmentSummary;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.customer.CustomerSearchCriteria;
import com.sitewhere.rest.model.search.device.DeviceAssignmentSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.customer.ICustomer;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceAssignmentSummary;
import com.sitewhere.spi.device.event.DeviceEventIndex;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.label.ILabel;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchResults;

import io.swagger.annotations.Api;

/**
 * Controller for customer operations.
 */
@Path("/api/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "customers")
@Tag(name = "Customers", description = "Customers are used to provide device ownership context for device assignments.")
@SecurityRequirements({ @SecurityRequirement(name = "jwtAuth", scopes = {}),
	@SecurityRequirement(name = "tenantIdHeader", scopes = {}),
	@SecurityRequirement(name = "tenantAuthHeader", scopes = {}) })
public class Customers {

    @Inject
    private IInstanceManagementMicroservice microservice;

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(Customers.class);

    /**
     * Create a new customer.
     * 
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Operation(summary = "Create new customer", description = "Create new customer")
    public Response createCustomer(@RequestBody CustomerCreateRequest input) throws SiteWhereException {
	return Response.ok(getDeviceManagement().createCustomer(input)).build();
    }

    /**
     * Get information for a given customer based on token.
     * 
     * @param customerToken
     * @param includeCustomerType
     * @param includeParentCustomer
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{customerToken}")
    @Operation(summary = "Get customer by token", description = "Get customer by token")
    public Response getCustomerByToken(
	    @Parameter(description = "Token that identifies customer", required = true) @PathParam("customerToken") String customerToken,
	    @Parameter(description = "Include customer type", required = false) @QueryParam("includeCustomerType") @DefaultValue("true") boolean includeCustomerType,
	    @Parameter(description = "Include parent customer information", required = false) @QueryParam("includeParentCustomer") @DefaultValue("true") boolean includeParentCustomer)
	    throws SiteWhereException {
	ICustomer existing = assertCustomer(customerToken);
	CustomerMarshalHelper helper = new CustomerMarshalHelper(getDeviceManagement(), getAssetManagement());
	helper.setIncludeCustomerType(includeCustomerType);
	helper.setIncludeParentCustomer(includeParentCustomer);
	return Response.ok(helper.convert(existing)).build();
    }

    /**
     * Update information for a customer.
     * 
     * @param customerToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PUT
    @Path("/{customerToken}")
    @Operation(summary = "Update existing customer", description = "Update existing customer")
    public Response updateCustomer(
	    @Parameter(description = "Token that identifies customer", required = true) @PathParam("customerToken") String customerToken,
	    @RequestBody CustomerCreateRequest request) throws SiteWhereException {
	ICustomer existing = assertCustomer(customerToken);
	return Response.ok(getDeviceManagement().updateCustomer(existing.getId(), request)).build();
    }

    /**
     * Get label for customer based on a specific generator.
     * 
     * @param customerToken
     * @param generatorId
     * @param servletRequest
     * @param response
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{customerToken}/label/{generatorId}")
    @Produces("image/png")
    @Operation(summary = "Get label for customer", description = "Get label for customer")
    public Response getCustomerLabel(
	    @Parameter(description = "Token that identifies customer", required = true) @PathParam("customerToken") String customerToken,
	    @Parameter(description = "Generator id", required = true) @PathParam("generatorId") String generatorId)
	    throws SiteWhereException {
	ICustomer existing = assertCustomer(customerToken);
	ILabel label = getLabelGeneration().getCustomerLabel(generatorId, existing.getId());
	if (label == null) {
	    return Response.status(Status.NOT_FOUND).build();
	}
	return Response.ok(label.getContent()).build();
    }

    /**
     * List customers matching criteria.
     * 
     * @param rootOnly
     * @param parentCustomerToken
     * @param customerTypeToken
     * @param includeCustomerType
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Operation(summary = "List customers matching criteria", description = "List customers matching criteria")
    public Response listCustomers(
	    @Parameter(description = "Limit to root elements", required = false) @QueryParam("rootOnly") @DefaultValue("true") Boolean rootOnly,
	    @Parameter(description = "Limit by parent customer token", required = false) @QueryParam("parentCustomerToken") String parentCustomerToken,
	    @Parameter(description = "Limit by customer type token", required = false) @QueryParam("customerTypeToken") String customerTypeToken,
	    @Parameter(description = "Include customer type", required = false) @QueryParam("includeCustomerType") @DefaultValue("false") boolean includeCustomerType,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize)
	    throws SiteWhereException {
	// Build criteria.
	CustomerSearchCriteria criteria = buildCustomerSearchCriteria(page, pageSize, rootOnly, parentCustomerToken,
		customerTypeToken);

	// Perform search.
	ISearchResults<? extends ICustomer> matches = getDeviceManagement().listCustomers(criteria);
	CustomerMarshalHelper helper = new CustomerMarshalHelper(getDeviceManagement(), getAssetManagement());
	helper.setIncludeCustomerType(includeCustomerType);

	List<ICustomer> results = new ArrayList<ICustomer>();
	for (ICustomer customer : matches.getResults()) {
	    results.add(helper.convert(customer));
	}
	return Response.ok(new SearchResults<ICustomer>(results, matches.getNumResults())).build();
    }

    /**
     * List all customers in a hierarchical tree format.
     * 
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/tree")
    @Operation(summary = "List all customers in tree format", description = "List all customers in tree format")
    public Response getCustomersTree() throws SiteWhereException {
	return Response.ok(getDeviceManagement().getCustomersTree()).build();
    }

    /**
     * Build customer search criteria from parameters.
     * 
     * @param page
     * @param pageSize
     * @param rootOnly
     * @param parentAreaToken
     * @param areaTypeToken
     * @return
     * @throws SiteWhereException
     */
    protected CustomerSearchCriteria buildCustomerSearchCriteria(int page, int pageSize, boolean rootOnly,
	    String parentCustomerToken, String customerTypeToken) throws SiteWhereException {
	// Build criteria.
	CustomerSearchCriteria criteria = new CustomerSearchCriteria(page, pageSize);
	criteria.setParentCustomerToken(parentCustomerToken);
	criteria.setCustomerTypeToken(customerTypeToken);
	criteria.setRootOnly(rootOnly);
	return criteria;
    }

    /**
     * Delete information for a given customer based on token.
     * 
     * @param customerToken
     * @return
     * @throws SiteWhereException
     */
    @DELETE
    @Path("/{customerToken}")
    @Operation(summary = "Delete customer by token", description = "Delete customer by token")
    public Response deleteCustomer(
	    @Parameter(description = "Token that identifies customer", required = true) @PathParam("customerToken") String customerToken)
	    throws SiteWhereException {
	ICustomer existing = assertCustomer(customerToken);
	return Response.ok(getDeviceManagement().deleteCustomer(existing.getId())).build();
    }

    /**
     * Get device measurements for a customer.
     * 
     * @param customerToken
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{customerToken}/measurements")
    @Operation(summary = "List measurements for a customer", description = "List measurements for a customer")
    public Response listDeviceMeasurementsForCustomer(
	    @Parameter(description = "Token that identifies customer", required = true) @PathParam("customerToken") String customerToken,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @Parameter(description = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @Parameter(description = "End date", required = false) @QueryParam("endDate") String endDate)
	    throws SiteWhereException {
	List<UUID> customers = resolveCustomerIdsRecursive(customerToken, true, getDeviceManagement());
	IDateRangeSearchCriteria criteria = Assignments.createDateRangeSearchCriteria(page, pageSize, startDate,
		endDate);
	ISearchResults<IDeviceMeasurement> results = getDeviceEventManagement()
		.listDeviceMeasurementsForIndex(DeviceEventIndex.Customer, customers, criteria);

	List<IDeviceMeasurement> wrapped = new ArrayList<IDeviceMeasurement>();
	for (IDeviceMeasurement result : results.getResults()) {
	    wrapped.add(new DeviceMeasurementsWithAsset(result, getAssetManagement()));
	}
	return Response.ok(new SearchResults<IDeviceMeasurement>(wrapped, results.getNumResults())).build();
    }

    /**
     * Get device locations for a customer.
     * 
     * @param customerToken
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @return
     */
    @GET
    @Path("/{customerToken}/locations")
    @Operation(summary = "List locations for a customer", description = "List locations for a customer")
    public Response listDeviceLocationsForCustomer(
	    @Parameter(description = "Token that identifies customer", required = true) @PathParam("customerToken") String customerToken,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @Parameter(description = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @Parameter(description = "End date", required = false) @QueryParam("endDate") String endDate)
	    throws SiteWhereException {
	List<UUID> customers = resolveCustomerIdsRecursive(customerToken, true, getDeviceManagement());
	IDateRangeSearchCriteria criteria = Assignments.createDateRangeSearchCriteria(page, pageSize, startDate,
		endDate);
	ISearchResults<IDeviceLocation> results = getDeviceEventManagement()
		.listDeviceLocationsForIndex(DeviceEventIndex.Customer, customers, criteria);

	// Marshal with asset info since multiple assignments might match.
	List<IDeviceLocation> wrapped = new ArrayList<IDeviceLocation>();
	for (IDeviceLocation result : results.getResults()) {
	    wrapped.add(new DeviceLocationWithAsset(result, getAssetManagement()));
	}
	return Response.ok(new SearchResults<IDeviceLocation>(wrapped, results.getNumResults())).build();
    }

    /**
     * Get device alerts for a customer.
     * 
     * @param customerToken
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{customerToken}/alerts")
    @Operation(summary = "List alerts for a customer", description = "List alerts for a customer")
    public Response listDeviceAlertsForCustomer(
	    @Parameter(description = "Token that identifies customer", required = true) @PathParam("customerToken") String customerToken,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @Parameter(description = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @Parameter(description = "End date", required = false) @QueryParam("endDate") String endDate)
	    throws SiteWhereException {
	List<UUID> customers = resolveCustomerIdsRecursive(customerToken, true, getDeviceManagement());
	IDateRangeSearchCriteria criteria = Assignments.createDateRangeSearchCriteria(page, pageSize, startDate,
		endDate);
	ISearchResults<IDeviceAlert> results = getDeviceEventManagement()
		.listDeviceAlertsForIndex(DeviceEventIndex.Customer, customers, criteria);

	// Marshal with asset info since multiple assignments might match.
	List<IDeviceAlert> wrapped = new ArrayList<IDeviceAlert>();
	for (IDeviceAlert result : results.getResults()) {
	    wrapped.add(new DeviceAlertWithAsset(result, getAssetManagement()));
	}
	return Response.ok(new SearchResults<IDeviceAlert>(wrapped, results.getNumResults())).build();
    }

    /**
     * Get device command invocations for a customer.
     * 
     * @param customerToken
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{customerToken}/invocations")
    @Operation(summary = "List command invocations for a customer", description = "List command invocations for a customer")
    public Response listDeviceCommandInvocationsForCustomer(
	    @Parameter(description = "Token that identifies customer", required = true) @PathParam("customerToken") String customerToken,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @Parameter(description = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @Parameter(description = "End date", required = false) @QueryParam("endDate") String endDate)
	    throws SiteWhereException {
	List<UUID> customers = resolveCustomerIdsRecursive(customerToken, true, getDeviceManagement());
	IDateRangeSearchCriteria criteria = Assignments.createDateRangeSearchCriteria(page, pageSize, startDate,
		endDate);
	ISearchResults<IDeviceCommandInvocation> results = getDeviceEventManagement()
		.listDeviceCommandInvocationsForIndex(DeviceEventIndex.Customer, customers, criteria);

	// Marshal with asset info since multiple assignments might match.
	List<IDeviceCommandInvocation> wrapped = new ArrayList<IDeviceCommandInvocation>();
	for (IDeviceCommandInvocation result : results.getResults()) {
	    wrapped.add(new DeviceCommandInvocationWithAsset(result, getAssetManagement()));
	}
	return Response.ok(new SearchResults<IDeviceCommandInvocation>(wrapped, results.getNumResults())).build();
    }

    /**
     * Get device command responses for a customer.
     * 
     * @param customerToken
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{customerToken}/responses")
    @Operation(summary = "List command responses for a customer", description = "List command responses for a customer")
    public Response listDeviceCommandResponsesForCustomer(
	    @Parameter(description = "Token that identifies customer", required = true) @PathParam("customerToken") String customerToken,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @Parameter(description = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @Parameter(description = "End date", required = false) @QueryParam("endDate") String endDate)
	    throws SiteWhereException {
	List<UUID> customers = resolveCustomerIdsRecursive(customerToken, true, getDeviceManagement());
	IDateRangeSearchCriteria criteria = Assignments.createDateRangeSearchCriteria(page, pageSize, startDate,
		endDate);
	ISearchResults<IDeviceCommandResponse> results = getDeviceEventManagement()
		.listDeviceCommandResponsesForIndex(DeviceEventIndex.Customer, customers, criteria);

	// Marshal with asset info since multiple assignments might match.
	List<IDeviceCommandResponse> wrapped = new ArrayList<IDeviceCommandResponse>();
	for (IDeviceCommandResponse result : results.getResults()) {
	    wrapped.add(new DeviceCommandResponseWithAsset(result, getAssetManagement()));
	}
	return Response.ok(new SearchResults<IDeviceCommandResponse>(wrapped, results.getNumResults())).build();
    }

    /**
     * Get device state changes for a customer.
     * 
     * @param customerToken
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{customerToken}/statechanges")
    @Operation(summary = "List state changes associated with a customer", description = "List state changes associated with a customer")
    public Response listDeviceStateChangesForCustomer(
	    @Parameter(description = "Token that identifies customer", required = true) @PathParam("customerToken") String customerToken,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @Parameter(description = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @Parameter(description = "End date", required = false) @QueryParam("endDate") String endDate)
	    throws SiteWhereException {
	List<UUID> customers = resolveCustomerIdsRecursive(customerToken, true, getDeviceManagement());
	IDateRangeSearchCriteria criteria = Assignments.createDateRangeSearchCriteria(page, pageSize, startDate,
		endDate);
	ISearchResults<IDeviceStateChange> results = getDeviceEventManagement()
		.listDeviceStateChangesForIndex(DeviceEventIndex.Customer, customers, criteria);

	// Marshal with asset info since multiple assignments might match.
	List<IDeviceStateChange> wrapped = new ArrayList<IDeviceStateChange>();
	for (IDeviceStateChange result : results.getResults()) {
	    wrapped.add(new DeviceStateChangeWithAsset(result, getAssetManagement()));
	}
	return Response.ok(new SearchResults<IDeviceStateChange>(wrapped, results.getNumResults())).build();
    }

    /**
     * Find device assignments associated with a customer.
     * 
     * @param customerToken
     * @param status
     * @param includeDevice
     * @param includeCustomer
     * @param includeArea
     * @param includeAsset
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{customerToken}/assignments")
    @Operation(summary = "List device assignments for a customer", description = "List device assignments for a customer")
    public Response listAssignmentsForCustomer(
	    @Parameter(description = "Token that identifies customer", required = true) @PathParam("customerToken") String customerToken,
	    @Parameter(description = "Limit results to the given status", required = false) @QueryParam("status") String status,
	    @Parameter(description = "Include device information", required = false) @QueryParam("includeDevice") @DefaultValue("false") boolean includeDevice,
	    @Parameter(description = "Include customer information", required = false) @QueryParam("includeCustomer") @DefaultValue("false") boolean includeCustomer,
	    @Parameter(description = "Include area information", required = false) @QueryParam("includeArea") @DefaultValue("false") boolean includeArea,
	    @Parameter(description = "Include asset information", required = false) @QueryParam("includeAsset") @DefaultValue("false") boolean includeAsset,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize)
	    throws SiteWhereException {
	DeviceAssignmentSearchCriteria criteria = new DeviceAssignmentSearchCriteria(page, pageSize);
	DeviceAssignmentStatus decodedStatus = (status != null) ? DeviceAssignmentStatus.valueOf(status) : null;
	if (decodedStatus != null) {
	    criteria.setAssignmentStatuses(Collections.singletonList(decodedStatus));
	}
	List<String> customers = resolveCustomerTokensRecursive(customerToken, true, getDeviceManagement());
	criteria.setCustomerTokens(customers);

	ISearchResults<? extends IDeviceAssignment> matches = getDeviceManagement().listDeviceAssignments(criteria);
	DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper(getDeviceManagement());
	helper.setIncludeDevice(includeDevice);
	helper.setIncludeCustomer(includeCustomer);
	helper.setIncludeArea(includeArea);
	helper.setIncludeAsset(includeAsset);
	List<DeviceAssignment> converted = new ArrayList<DeviceAssignment>();
	for (IDeviceAssignment assignment : matches.getResults()) {
	    converted.add(helper.convert(assignment, getAssetManagement()));
	}
	return Response.ok(new SearchResults<DeviceAssignment>(converted, matches.getNumResults())).build();
    }

    /**
     * List summary information for customer device assignments.
     * 
     * @param customerToken
     * @param status
     * @param includeAsset
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{customerToken}/assignments/summaries")
    @Operation(summary = "List device assignments for a customer", description = "List device assignments for a customer")
    public Response listAssignmentSummariesForCustomer(
	    @Parameter(description = "Token that identifies customer", required = true) @PathParam("customerToken") String customerToken,
	    @Parameter(description = "Limit results to the given status", required = false) @QueryParam("status") String status,
	    @Parameter(description = "Include asset information", required = false) @QueryParam("includeAsset") @DefaultValue("false") boolean includeAsset,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize)
	    throws SiteWhereException {
	DeviceAssignmentSearchCriteria criteria = new DeviceAssignmentSearchCriteria(page, pageSize);
	DeviceAssignmentStatus decodedStatus = (status != null) ? DeviceAssignmentStatus.valueOf(status) : null;
	if (decodedStatus != null) {
	    criteria.setAssignmentStatuses(Collections.singletonList(decodedStatus));
	}
	List<String> customers = resolveCustomerTokensRecursive(customerToken, true, getDeviceManagement());
	criteria.setCustomerTokens(customers);

	ISearchResults<? extends IDeviceAssignmentSummary> matches = getDeviceManagement()
		.listDeviceAssignmentSummaries(criteria);
	DeviceAssignmentSummaryMarshalHelper helper = new DeviceAssignmentSummaryMarshalHelper();
	helper.setIncludeAsset(includeAsset);
	List<DeviceAssignmentSummary> converted = new ArrayList<DeviceAssignmentSummary>();
	for (IDeviceAssignmentSummary assignment : matches.getResults()) {
	    converted.add(helper.convert(assignment, getAssetManagement()));
	}
	return Response.ok(new SearchResults<DeviceAssignmentSummary>(converted, matches.getNumResults())).build();
    }

    /**
     * Resolve tokens for all customers that are children of a given customer.
     * 
     * @param customerToken
     * @param recursive
     * @param deviceManagement
     * @return
     * @throws SiteWhereException
     */
    public static List<String> resolveCustomerTokensRecursive(String customerToken, boolean recursive,
	    IDeviceManagement deviceManagement) throws SiteWhereException {
	List<ICustomer> customers = resolveCustomers(customerToken, recursive, deviceManagement);
	List<String> ids = new ArrayList<>();
	for (ICustomer customer : customers) {
	    ids.add(customer.getToken());
	}
	return ids;
    }

    /**
     * Resolve ids for all customers that are children of a given customer.
     * 
     * @param customerToken
     * @param recursive
     * @param deviceManagement
     * @return
     * @throws SiteWhereException
     */
    public static List<UUID> resolveCustomerIdsRecursive(String customerToken, boolean recursive,
	    IDeviceManagement deviceManagement) throws SiteWhereException {
	List<ICustomer> customers = resolveCustomers(customerToken, recursive, deviceManagement);
	List<UUID> ids = new ArrayList<>();
	for (ICustomer customer : customers) {
	    ids.add(customer.getId());
	}
	return ids;
    }

    /**
     * Resolve areas including nested areas.
     * 
     * @param areaToken
     * @param recursive
     * @param deviceManagement
     * @return
     * @throws SiteWhereException
     */
    public static List<ICustomer> resolveCustomers(String customerToken, boolean recursive,
	    IDeviceManagement deviceManagement) throws SiteWhereException {
	ICustomer existing = deviceManagement.getCustomerByToken(customerToken);
	if (existing == null) {
	    return new ArrayList<ICustomer>();
	}
	Map<String, ICustomer> resolved = new HashMap<>();
	resolveCustomersRecursively(existing, recursive, resolved, deviceManagement);
	List<ICustomer> response = new ArrayList<>();
	response.addAll(resolved.values());
	return response;
    }

    /**
     * Resolve customers recursively.
     * 
     * @param current
     * @param recursive
     * @param matches
     * @param deviceManagement
     * @throws SiteWhereException
     */
    protected static void resolveCustomersRecursively(ICustomer current, boolean recursive,
	    Map<String, ICustomer> matches, IDeviceManagement deviceManagement) throws SiteWhereException {
	matches.put(current.getToken(), current);
	List<? extends ICustomer> children = deviceManagement.getCustomerChildren(current.getToken());
	for (ICustomer child : children) {
	    resolveCustomersRecursively(child, recursive, matches, deviceManagement);
	}
    }

    /**
     * Get customer associated with token or throw an exception if invalid.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected ICustomer assertCustomer(String token) throws SiteWhereException {
	ICustomer customer = getDeviceManagement().getCustomerByToken(token);
	if (customer == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidCustomerToken, ErrorLevel.ERROR);
	}
	return customer;
    }

    protected IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagement();
    }

    protected IDeviceEventManagement getDeviceEventManagement() {
	return getMicroservice().getDeviceEventManagementApiChannel();
    }

    protected IAssetManagement getAssetManagement() {
	return getMicroservice().getAssetManagement();
    }

    protected ILabelGeneration getLabelGeneration() {
	return getMicroservice().getLabelGenerationApiChannel();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}