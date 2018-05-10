/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.marshaling;

import java.util.List;

import com.sitewhere.rest.model.customer.Customer;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.spi.customer.ICustomer;
import com.sitewhere.spi.customer.ICustomerType;

/**
 * Extends {@link Customer} to support fields that can be included on REST
 * calls.
 * 
 * @author Derek
 */
public class MarshaledCustomer extends Customer {

    /** Serial version UID */
    private static final long serialVersionUID = -359588909786939167L;

    /** Customer type */
    private ICustomerType customerType;

    /** Parent customer information */
    private ICustomer parentCustomer;

    /** List of assignments for area */
    private List<DeviceAssignment> deviceAssignments;

    public ICustomerType getCustomerType() {
	return customerType;
    }

    public void setCustomerType(ICustomerType customerType) {
	this.customerType = customerType;
    }

    public ICustomer getParentCustomer() {
	return parentCustomer;
    }

    public void setParentCustomer(ICustomer parentCustomer) {
	this.parentCustomer = parentCustomer;
    }

    public List<DeviceAssignment> getDeviceAssignments() {
	return deviceAssignments;
    }

    public void setDeviceAssignments(List<DeviceAssignment> deviceAssignments) {
	this.deviceAssignments = deviceAssignments;
    }
}
