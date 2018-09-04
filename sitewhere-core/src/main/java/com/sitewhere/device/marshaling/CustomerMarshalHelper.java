/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.marshaling;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.sitewhere.rest.model.common.BrandedEntity;
import com.sitewhere.rest.model.customer.Customer;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.marshaling.MarshaledCustomer;
import com.sitewhere.rest.model.search.device.DeviceAssignmentSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.customer.ICustomer;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Configurable helper class that allows {@link Customer} model objects to be
 * created from {@link ICustomer} SPI objects.
 * 
 * @author Derek
 */
public class CustomerMarshalHelper {

    /** Device management */
    private IDeviceManagement deviceManagement;

    /** Asset management */
    private IAssetManagement assetManagement;

    /** Include customer type information */
    private boolean includeCustomerType = false;

    /** Include parent customer information */
    private boolean includeParentCustomer = false;

    /** Indicates whether assignments for customer should be included */
    private boolean includeAssignments = false;

    /** Device assignment marshal helper */
    private DeviceAssignmentMarshalHelper assignmentHelper;

    public CustomerMarshalHelper(IDeviceManagement deviceManagement, IAssetManagement assetManagement) {
	this.deviceManagement = deviceManagement;
	this.assetManagement = assetManagement;

	this.assignmentHelper = new DeviceAssignmentMarshalHelper(deviceManagement);
	assignmentHelper.setIncludeDevice(true);
	assignmentHelper.setIncludeAsset(true);
	assignmentHelper.setIncludeArea(false);
    }

    /**
     * Convert the SPI into a model object based on marshaling parameters.
     * 
     * @param source
     * @return
     * @throws SiteWhereException
     */
    public MarshaledCustomer convert(ICustomer source) throws SiteWhereException {
	if (source == null) {
	    return null;
	}
	MarshaledCustomer customer = new MarshaledCustomer();
	customer.setCustomerTypeId(source.getCustomerTypeId());
	customer.setParentCustomerId(source.getParentCustomerId());
	customer.setName(source.getName());
	customer.setDescription(source.getDescription());
	BrandedEntity.copy(source, customer);
	if (isIncludeCustomerType()) {
	    customer.setCustomerType(getDeviceManagement().getCustomerType(source.getCustomerTypeId()));
	}
	if (isIncludeParentCustomer()) {
	    if (source.getParentCustomerId() != null) {
		ICustomer parent = getDeviceManagement().getCustomer(source.getParentCustomerId());
		customer.setParentCustomer(parent);
	    }
	}
	if (isIncludeAssignments()) {
	    List<UUID> customerIds = new ArrayList<>();
	    customerIds.add(customer.getId());
	    DeviceAssignmentSearchCriteria criteria = new DeviceAssignmentSearchCriteria(1, 0);
	    criteria.setStatus(DeviceAssignmentStatus.Active);
	    criteria.setCustomerIds(customerIds);
	    ISearchResults<IDeviceAssignment> matches = getDeviceManagement().listDeviceAssignments(criteria);
	    List<DeviceAssignment> assignments = new ArrayList<DeviceAssignment>();
	    for (IDeviceAssignment match : matches.getResults()) {
		assignments.add(assignmentHelper.convert(match, getAssetManagement()));
	    }
	    customer.setDeviceAssignments(assignments);
	}
	return customer;
    }

    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    public void setDeviceManagement(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }

    public IAssetManagement getAssetManagement() {
	return assetManagement;
    }

    public void setAssetManagement(IAssetManagement assetManagement) {
	this.assetManagement = assetManagement;
    }

    public boolean isIncludeCustomerType() {
	return includeCustomerType;
    }

    public void setIncludeCustomerType(boolean includeCustomerType) {
	this.includeCustomerType = includeCustomerType;
    }

    public boolean isIncludeParentCustomer() {
	return includeParentCustomer;
    }

    public void setIncludeParentCustomer(boolean includeParentCustomer) {
	this.includeParentCustomer = includeParentCustomer;
    }

    public boolean isIncludeAssignments() {
	return includeAssignments;
    }

    public void setIncludeAssignments(boolean includeAssignments) {
	this.includeAssignments = includeAssignments;
    }
}
