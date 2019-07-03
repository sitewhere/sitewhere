/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.device.marshaling.CustomerMarshalHelper;
import com.sitewhere.device.marshaling.DeviceAssignmentMarshalHelper;
import com.sitewhere.grpc.client.event.BlockingDeviceEventManagement;
import com.sitewhere.rest.model.customer.request.CustomerCreateRequest;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.asset.DeviceAlertWithAsset;
import com.sitewhere.rest.model.device.asset.DeviceCommandInvocationWithAsset;
import com.sitewhere.rest.model.device.asset.DeviceCommandResponseWithAsset;
import com.sitewhere.rest.model.device.asset.DeviceLocationWithAsset;
import com.sitewhere.rest.model.device.asset.DeviceMeasurementsWithAsset;
import com.sitewhere.rest.model.device.asset.DeviceStateChangeWithAsset;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.area.AreaSearchCriteria;
import com.sitewhere.rest.model.search.customer.CustomerSearchCriteria;
import com.sitewhere.rest.model.search.device.DeviceAssignmentSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.area.IAreaType;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.customer.ICustomer;
import com.sitewhere.spi.customer.ICustomerType;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.event.DeviceEventIndex;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.label.ILabel;
import com.sitewhere.spi.label.ILabelGeneration;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.ITreeNode;
import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.web.annotation.SiteWhereCrossOrigin;
import com.sitewhere.web.rest.RestControllerBase;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Controller for customer operations.
 * 
 * @author Derek Adams
 */
@RestController
@SiteWhereCrossOrigin
@RequestMapping(value = "/customers")
@Api(value = "customers")
public class Customers extends RestControllerBase {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(Customers.class);

    /**
     * Create a new area.
     * 
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Create new customer")
    @Secured({ SiteWhereRoles.REST })
    public ICustomer createCustomer(@RequestBody CustomerCreateRequest input) throws SiteWhereException {
	return getDeviceManagement().createCustomer(input);
    }

    /**
     * Get information for a given customer based on token.
     * 
     * @param customerToken
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{customerToken:.+}", method = RequestMethod.GET)
    @ApiOperation(value = "Get customer by token")
    @Secured({ SiteWhereRoles.REST })
    public ICustomer getCustomerByToken(
	    @ApiParam(value = "Token that identifies customer", required = true) @PathVariable String customerToken,
	    @ApiParam(value = "Include customer type", required = false) @RequestParam(defaultValue = "true") boolean includeCustomerType,
	    @ApiParam(value = "Include parent customer information", required = false) @RequestParam(defaultValue = "true") boolean includeParentCustomer)
	    throws SiteWhereException {
	ICustomer existing = assertCustomer(customerToken);
	CustomerMarshalHelper helper = new CustomerMarshalHelper(getDeviceManagement(), getAssetManagement());
	helper.setIncludeCustomerType(includeCustomerType);
	helper.setIncludeParentCustomer(includeParentCustomer);
	return helper.convert(existing);
    }

    /**
     * Update information for a customer.
     * 
     * @param customerToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{customerToken:.+}", method = RequestMethod.PUT)
    @ApiOperation(value = "Update existing customer")
    @Secured({ SiteWhereRoles.REST })
    public ICustomer updateCustomer(
	    @ApiParam(value = "Token that identifies customer", required = true) @PathVariable String customerToken,
	    @RequestBody CustomerCreateRequest request) throws SiteWhereException {
	ICustomer existing = assertCustomer(customerToken);
	return getDeviceManagement().updateCustomer(existing.getId(), request);
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
    @RequestMapping(value = "/{customerToken}/label/{generatorId}", method = RequestMethod.GET)
    @ApiOperation(value = "Get label for customer")
    public ResponseEntity<byte[]> getCustomerLabel(
	    @ApiParam(value = "Token that identifies customer", required = true) @PathVariable String customerToken,
	    @ApiParam(value = "Generator id", required = true) @PathVariable String generatorId,
	    HttpServletRequest servletRequest, HttpServletResponse response) throws SiteWhereException {
	ICustomer existing = assertCustomer(customerToken);
	ILabel label = getLabelGeneration().getCustomerLabel(generatorId, existing.getId());
	if (label == null) {
	    return ResponseEntity.notFound().build();
	}
	final HttpHeaders headers = new HttpHeaders();
	headers.setContentType(MediaType.IMAGE_PNG);
	return new ResponseEntity<byte[]>(label.getContent(), headers, HttpStatus.OK);
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
    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "List customers matching criteria")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<ICustomer> listCustomers(
	    @ApiParam(value = "Limit to root elements", required = false) @RequestParam(required = false, defaultValue = "true") Boolean rootOnly,
	    @ApiParam(value = "Limit by parent customer token", required = false) @RequestParam(required = false) String parentCustomerToken,
	    @ApiParam(value = "Limit by customer type token", required = false) @RequestParam(required = false) String customerTypeToken,
	    @ApiParam(value = "Include customer type", required = false) @RequestParam(defaultValue = "false") boolean includeCustomerType,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize)
	    throws SiteWhereException {
	// Build criteria.
	CustomerSearchCriteria criteria = buildCustomerSearchCriteria(page, pageSize, rootOnly, parentCustomerToken,
		customerTypeToken);

	// Perform search.
	ISearchResults<ICustomer> matches = getDeviceManagement().listCustomers(criteria);
	CustomerMarshalHelper helper = new CustomerMarshalHelper(getDeviceManagement(), getAssetManagement());
	helper.setIncludeCustomerType(includeCustomerType);

	List<ICustomer> results = new ArrayList<ICustomer>();
	for (ICustomer customer : matches.getResults()) {
	    results.add(helper.convert(customer));
	}
	return new SearchResults<ICustomer>(results, matches.getNumResults());
    }

    /**
     * List all customers in a hierarchical tree format.
     * 
     * @return
     * @throws SiteWhereException
     */
    @GetMapping(value = "/tree")
    @ApiOperation(value = "List all customers in tree format")
    public List<ITreeNode> getCustomersTree() throws SiteWhereException {
	return getDeviceManagement().getCustomersTree();
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
	criteria.setRootOnly(rootOnly);

	// Look up parent customer if provided.
	if (parentCustomerToken != null) {
	    ICustomer parent = getDeviceManagement().getCustomerByToken(parentCustomerToken);
	    if (parent == null) {
		throw new SiteWhereException("Invalid parent customer token.");
	    }
	    criteria.setParentCustomerId(parent.getId());
	}

	// Look up customer type if provided.
	if (customerTypeToken != null) {
	    ICustomerType customerType = getDeviceManagement().getCustomerTypeByToken(customerTypeToken);
	    if (customerType == null) {
		throw new SiteWhereException("Invalid customer type token.");
	    }
	    criteria.setCustomerTypeId(customerType.getId());
	}

	return criteria;
    }

    /**
     * Build area search criteria from parameters.
     * 
     * @param page
     * @param pageSize
     * @param rootOnly
     * @param parentAreaToken
     * @param areaTypeToken
     * @return
     * @throws SiteWhereException
     */
    protected AreaSearchCriteria buildAreaSearchCriteria(int page, int pageSize, boolean rootOnly,
	    String parentAreaToken, String areaTypeToken) throws SiteWhereException {
	// Build criteria.
	AreaSearchCriteria criteria = new AreaSearchCriteria(page, pageSize);
	criteria.setRootOnly(rootOnly);

	// Look up parent area if provided.
	if (parentAreaToken != null) {
	    IArea parent = getDeviceManagement().getAreaByToken(parentAreaToken);
	    if (parent != null) {
		criteria.setParentAreaId(parent.getId());
	    }
	}

	// Look up area type if provided.
	if (areaTypeToken != null) {
	    IAreaType areaType = getDeviceManagement().getAreaTypeByToken(areaTypeToken);
	    if (areaType != null) {
		criteria.setAreaTypeId(areaType.getId());
	    }
	}

	return criteria;
    }

    /**
     * Delete information for a given customer based on token.
     * 
     * @param customerToken
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{customerToken:.+}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete customer by token")
    @Secured({ SiteWhereRoles.REST })
    public ICustomer deleteCustomer(
	    @ApiParam(value = "Token that identifies customer", required = true) @PathVariable String customerToken)
	    throws SiteWhereException {
	ICustomer existing = assertCustomer(customerToken);
	return getDeviceManagement().deleteCustomer(existing.getId());
    }

    /**
     * Get device measurements for a customer.
     * 
     * @param customerToken
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @param response
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{customerToken}/measurements", method = RequestMethod.GET)
    @ApiOperation(value = "List measurements for a customer")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDeviceMeasurement> listDeviceMeasurementsForCustomer(
	    @ApiParam(value = "Token that identifies customer", required = true) @PathVariable String customerToken,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @RequestParam(required = false) String startDate,
	    @ApiParam(value = "End date", required = false) @RequestParam(required = false) String endDate,
	    HttpServletResponse response) throws SiteWhereException {
	List<UUID> customers = resolveCustomerIds(customerToken, true, getDeviceManagement());
	IDateRangeSearchCriteria criteria = Assignments.createDateRangeSearchCriteria(page, pageSize, startDate,
		endDate, response);
	ISearchResults<IDeviceMeasurement> results = getDeviceEventManagement()
		.listDeviceMeasurementsForIndex(DeviceEventIndex.Customer, customers, criteria);

	List<IDeviceMeasurement> wrapped = new ArrayList<IDeviceMeasurement>();
	for (IDeviceMeasurement result : results.getResults()) {
	    wrapped.add(new DeviceMeasurementsWithAsset(result, getAssetManagement()));
	}
	return new SearchResults<IDeviceMeasurement>(wrapped, results.getNumResults());
    }

    /**
     * Get device locations for a customer.
     * 
     * @param customerToken
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @param response
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{customerToken}/locations", method = RequestMethod.GET)
    @ApiOperation(value = "List locations for a customer")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDeviceLocation> listDeviceLocationsForCustomer(
	    @ApiParam(value = "Token that identifies customer", required = true) @PathVariable String customerToken,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @RequestParam(required = false) String startDate,
	    @ApiParam(value = "End date", required = false) @RequestParam(required = false) String endDate,
	    HttpServletResponse response) throws SiteWhereException {
	List<UUID> customers = resolveCustomerIds(customerToken, true, getDeviceManagement());
	IDateRangeSearchCriteria criteria = Assignments.createDateRangeSearchCriteria(page, pageSize, startDate,
		endDate, response);
	ISearchResults<IDeviceLocation> results = getDeviceEventManagement()
		.listDeviceLocationsForIndex(DeviceEventIndex.Customer, customers, criteria);

	// Marshal with asset info since multiple assignments might match.
	List<IDeviceLocation> wrapped = new ArrayList<IDeviceLocation>();
	for (IDeviceLocation result : results.getResults()) {
	    wrapped.add(new DeviceLocationWithAsset(result, getAssetManagement()));
	}
	return new SearchResults<IDeviceLocation>(wrapped, results.getNumResults());
    }

    /**
     * Get device alerts for a customer.
     * 
     * @param customerToken
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @param response
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{customerToken}/alerts", method = RequestMethod.GET)
    @ApiOperation(value = "List alerts for a customer")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDeviceAlert> listDeviceAlertsForCustomer(
	    @ApiParam(value = "Token that identifies customer", required = true) @PathVariable String customerToken,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @RequestParam(required = false) String startDate,
	    @ApiParam(value = "End date", required = false) @RequestParam(required = false) String endDate,
	    HttpServletResponse response) throws SiteWhereException {
	List<UUID> customers = resolveCustomerIds(customerToken, true, getDeviceManagement());
	IDateRangeSearchCriteria criteria = Assignments.createDateRangeSearchCriteria(page, pageSize, startDate,
		endDate, response);
	ISearchResults<IDeviceAlert> results = getDeviceEventManagement()
		.listDeviceAlertsForIndex(DeviceEventIndex.Customer, customers, criteria);

	// Marshal with asset info since multiple assignments might match.
	List<IDeviceAlert> wrapped = new ArrayList<IDeviceAlert>();
	for (IDeviceAlert result : results.getResults()) {
	    wrapped.add(new DeviceAlertWithAsset(result, getAssetManagement()));
	}
	return new SearchResults<IDeviceAlert>(wrapped, results.getNumResults());
    }

    /**
     * Get device command invocations for a customer.
     * 
     * @param customerToken
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @param response
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{customerToken}/invocations", method = RequestMethod.GET)
    @ApiOperation(value = "List command invocations for a customer")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForCustomer(
	    @ApiParam(value = "Token that identifies customer", required = true) @PathVariable String customerToken,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @RequestParam(required = false) String startDate,
	    @ApiParam(value = "End date", required = false) @RequestParam(required = false) String endDate,
	    HttpServletResponse response) throws SiteWhereException {
	List<UUID> customers = resolveCustomerIds(customerToken, true, getDeviceManagement());
	IDateRangeSearchCriteria criteria = Assignments.createDateRangeSearchCriteria(page, pageSize, startDate,
		endDate, response);
	ISearchResults<IDeviceCommandInvocation> results = getDeviceEventManagement()
		.listDeviceCommandInvocationsForIndex(DeviceEventIndex.Customer, customers, criteria);

	// Marshal with asset info since multiple assignments might match.
	List<IDeviceCommandInvocation> wrapped = new ArrayList<IDeviceCommandInvocation>();
	for (IDeviceCommandInvocation result : results.getResults()) {
	    wrapped.add(new DeviceCommandInvocationWithAsset(result, getAssetManagement()));
	}
	return new SearchResults<IDeviceCommandInvocation>(wrapped, results.getNumResults());
    }

    /**
     * Get device command responses for a customer.
     * 
     * @param customerToken
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @param response
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{customerToken}/responses", method = RequestMethod.GET)
    @ApiOperation(value = "List command responses for a customer")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForCustomer(
	    @ApiParam(value = "Token that identifies customer", required = true) @PathVariable String customerToken,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @RequestParam(required = false) String startDate,
	    @ApiParam(value = "End date", required = false) @RequestParam(required = false) String endDate,
	    HttpServletResponse response) throws SiteWhereException {
	List<UUID> customers = resolveCustomerIds(customerToken, true, getDeviceManagement());
	IDateRangeSearchCriteria criteria = Assignments.createDateRangeSearchCriteria(page, pageSize, startDate,
		endDate, response);
	ISearchResults<IDeviceCommandResponse> results = getDeviceEventManagement()
		.listDeviceCommandResponsesForIndex(DeviceEventIndex.Customer, customers, criteria);

	// Marshal with asset info since multiple assignments might match.
	List<IDeviceCommandResponse> wrapped = new ArrayList<IDeviceCommandResponse>();
	for (IDeviceCommandResponse result : results.getResults()) {
	    wrapped.add(new DeviceCommandResponseWithAsset(result, getAssetManagement()));
	}
	return new SearchResults<IDeviceCommandResponse>(wrapped, results.getNumResults());
    }

    /**
     * Get device state changes for a customer.
     * 
     * @param customerToken
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @param response
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{customerToken}/statechanges", method = RequestMethod.GET)
    @ApiOperation(value = "List state changes associated with a customer")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDeviceStateChange> listDeviceStateChangesForCustomer(
	    @ApiParam(value = "Token that identifies customer", required = true) @PathVariable String customerToken,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @RequestParam(required = false) String startDate,
	    @ApiParam(value = "End date", required = false) @RequestParam(required = false) String endDate,
	    HttpServletResponse response) throws SiteWhereException {
	List<UUID> customers = resolveCustomerIds(customerToken, true, getDeviceManagement());
	IDateRangeSearchCriteria criteria = Assignments.createDateRangeSearchCriteria(page, pageSize, startDate,
		endDate, response);
	ISearchResults<IDeviceStateChange> results = getDeviceEventManagement()
		.listDeviceStateChangesForIndex(DeviceEventIndex.Customer, customers, criteria);

	// Marshal with asset info since multiple assignments might match.
	List<IDeviceStateChange> wrapped = new ArrayList<IDeviceStateChange>();
	for (IDeviceStateChange result : results.getResults()) {
	    wrapped.add(new DeviceStateChangeWithAsset(result, getAssetManagement()));
	}
	return new SearchResults<IDeviceStateChange>(wrapped, results.getNumResults());
    }

    /**
     * Find device assignments associated with a customer.
     * 
     * @param customerToken
     * @param status
     * @param includeDevice
     * @param includeAsset
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{customerToken}/assignments", method = RequestMethod.GET)
    @ApiOperation(value = "List device assignments for a customer")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<DeviceAssignment> listAssignmentsForCustomer(
	    @ApiParam(value = "Token that identifies customer", required = true) @PathVariable String customerToken,
	    @ApiParam(value = "Limit results to the given status", required = false) @RequestParam(required = false) String status,
	    @ApiParam(value = "Include device information", required = false) @RequestParam(defaultValue = "false") boolean includeDevice,
	    @ApiParam(value = "Include customer information", required = false) @RequestParam(defaultValue = "false") boolean includeCustomer,
	    @ApiParam(value = "Include area information", required = false) @RequestParam(defaultValue = "false") boolean includeArea,
	    @ApiParam(value = "Include asset information", required = false) @RequestParam(defaultValue = "false") boolean includeAsset,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize)
	    throws SiteWhereException {
	DeviceAssignmentSearchCriteria criteria = new DeviceAssignmentSearchCriteria(page, pageSize);
	DeviceAssignmentStatus decodedStatus = (status != null) ? DeviceAssignmentStatus.valueOf(status) : null;
	if (decodedStatus != null) {
	    criteria.setStatus(decodedStatus);
	}
	List<UUID> customers = resolveCustomerIds(customerToken, true, getDeviceManagement());
	criteria.setCustomerIds(customers);

	ISearchResults<IDeviceAssignment> matches = getDeviceManagement().listDeviceAssignments(criteria);
	DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper(getDeviceManagement());
	helper.setIncludeDevice(includeDevice);
	helper.setIncludeCustomer(includeCustomer);
	helper.setIncludeArea(includeArea);
	helper.setIncludeAsset(includeAsset);
	List<DeviceAssignment> converted = new ArrayList<DeviceAssignment>();
	for (IDeviceAssignment assignment : matches.getResults()) {
	    converted.add(helper.convert(assignment, getAssetManagement()));
	}
	return new SearchResults<DeviceAssignment>(converted, matches.getNumResults());
    }

    /**
     * Resolve ids recursively for contained customers based on customer token.
     * 
     * @param customerToken
     * @param recursive
     * @param deviceManagement
     * @return
     * @throws SiteWhereException
     */
    public static List<UUID> resolveCustomerIds(String customerToken, boolean recursive,
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
	List<ICustomer> children = deviceManagement.getCustomerChildren(current.getToken());
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

    private IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagementApiChannel();
    }

    private IDeviceEventManagement getDeviceEventManagement() {
	return new BlockingDeviceEventManagement(getMicroservice().getDeviceEventManagementApiChannel());
    }

    private IAssetManagement getAssetManagement() {
	return getMicroservice().getAssetManagementApiChannel();
    }

    private ILabelGeneration getLabelGeneration() {
	return getMicroservice().getLabelGenerationApiChannel();
    }
}