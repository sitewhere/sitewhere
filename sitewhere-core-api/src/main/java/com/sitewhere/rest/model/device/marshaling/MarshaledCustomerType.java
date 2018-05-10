/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.marshaling;

import java.util.List;

import com.sitewhere.rest.model.customer.CustomerType;
import com.sitewhere.spi.customer.ICustomerType;

/**
 * Extends {@link CustomerType} to support fields that can be included on REST
 * calls.
 * 
 * @author Derek
 */
public class MarshaledCustomerType extends CustomerType {

    /** Serial version UID */
    private static final long serialVersionUID = 5902204555479363292L;

    /** List of contained customer types */
    private List<ICustomerType> containedCustomerTypes;

    public List<ICustomerType> getContainedCustomerTypes() {
	return containedCustomerTypes;
    }

    public void setContainedCustomerTypes(List<ICustomerType> containedCustomerTypes) {
	this.containedCustomerTypes = containedCustomerTypes;
    }
}
