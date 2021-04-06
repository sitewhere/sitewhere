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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
import com.sitewhere.rest.model.device.marshaling.MarshaledCustomer;
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
import com.sitewhere.spi.search.ITreeNode;

/**
 * Controller for customer operations.
 */
@RestController
@RequestMapping("/api/customers")
public class Customers {

    @Autowired
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
    @PostMapping
    public ICustomer createCustomer(@RequestBody CustomerCreateRequest input) throws SiteWhereException {
	return getDeviceManagement().createCustomer(input);
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
    @GetMapping("/{customerToken}")
    public MarshaledCustomer getCustomerByToken(@PathVariable String customerToken,
	    @RequestParam(defaultValue = "true", required = false) boolean includeCustomerType,
	    @RequestParam(defaultValue = "true", required = false) boolean includeParentCustomer)
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
    @PutMapping("/{customerToken}")
    public ICustomer updateCustomer(@PathVariable String customerToken, @RequestBody CustomerCreateRequest request)
	    throws SiteWhereException {
	ICustomer existing = assertCustomer(customerToken);
	return getDeviceManagement().updateCustomer(existing.getId(), request);
    }

    /**
     * Get label for customer based on a specific generator.
     * 
     * @param customerToken
     * @param generatorId
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{customerToken}/label/{generatorId}")
    public ResponseEntity<?> getCustomerLabel(@PathVariable String customerToken, @PathVariable String generatorId)
	    throws SiteWhereException {
	ICustomer existing = assertCustomer(customerToken);
	ILabel label = getLabelGeneration().getCustomerLabel(generatorId, existing.getId());
	if (label == null) {
	    return ResponseEntity.notFound().build();
	}
	return ResponseEntity.ok(label.getContent());
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
    @GetMapping
    public SearchResults<ICustomer> listCustomers(
	    @RequestParam(defaultValue = "true", required = false) Boolean rootOnly,
	    @RequestParam(required = false) String parentCustomerToken,
	    @RequestParam(required = false) String customerTypeToken,
	    @RequestParam(defaultValue = "false", required = false) boolean includeCustomerType,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize) throws SiteWhereException {
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
	return new SearchResults<ICustomer>(results, matches.getNumResults());
    }

    /**
     * List all customers in a hierarchical tree format.
     * 
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/tree")
    public List<? extends ITreeNode> getCustomersTree() throws SiteWhereException {
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
    @DeleteMapping("/{customerToken}")
    public ICustomer deleteCustomer(@PathVariable String customerToken) throws SiteWhereException {
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
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{customerToken}/measurements")
    public SearchResults<IDeviceMeasurement> listDeviceMeasurementsForCustomer(@PathVariable String customerToken,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize,
	    @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate)
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
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{customerToken}/locations")
    public SearchResults<IDeviceLocation> listDeviceLocationsForCustomer(@PathVariable String customerToken,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize,
	    @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate)
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
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{customerToken}/alerts")
    public SearchResults<IDeviceAlert> listDeviceAlertsForCustomer(@PathVariable String customerToken,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize,
	    @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate)
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
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{customerToken}/invocations")
    public SearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForCustomer(
	    @PathVariable String customerToken, @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize,
	    @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate)
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
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{customerToken}/responses")
    public SearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForCustomer(
	    @PathVariable String customerToken, @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize,
	    @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate)
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
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{customerToken}/statechanges")
    public SearchResults<IDeviceStateChange> listDeviceStateChangesForCustomer(@PathVariable String customerToken,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize,
	    @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate)
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
	return new SearchResults<IDeviceStateChange>(wrapped, results.getNumResults());
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
    @GetMapping("/{customerToken}/assignments")
    public SearchResults<DeviceAssignment> listAssignmentsForCustomer(@PathVariable String customerToken,
	    @RequestParam(required = false) String status,
	    @RequestParam(defaultValue = "false", required = false) boolean includeDevice,
	    @RequestParam(defaultValue = "false", required = false) boolean includeCustomer,
	    @RequestParam(defaultValue = "false", required = false) boolean includeArea,
	    @RequestParam(defaultValue = "false", required = false) boolean includeAsset,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize) throws SiteWhereException {
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
	return new SearchResults<DeviceAssignment>(converted, matches.getNumResults());
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
    @GetMapping("/{customerToken}/assignments/summaries")
    public SearchResults<DeviceAssignmentSummary> listAssignmentSummariesForCustomer(@PathVariable String customerToken,
	    @RequestParam(required = false) String status,
	    @RequestParam(defaultValue = "false", required = false) boolean includeAsset,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize) throws SiteWhereException {
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
	return new SearchResults<DeviceAssignmentSummary>(converted, matches.getNumResults());
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