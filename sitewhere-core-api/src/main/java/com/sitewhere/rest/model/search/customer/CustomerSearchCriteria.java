/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.search.customer;

import java.util.UUID;

import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.spi.search.customer.ICustomerSearchCriteria;

/**
 * Model object for properties used in customer searches.
 * 
 * @author Derek
 */
public class CustomerSearchCriteria extends SearchCriteria implements ICustomerSearchCriteria {

    /** Only return root customers */
    private Boolean rootOnly;

    /** Only return customers with the given parent */
    private UUID parentCustomerId;

    /** Only return customers of the given type */
    private UUID customerTypeId;

    public CustomerSearchCriteria(int pageNumber, int pageSize) {
	super(pageNumber, pageSize);
    }

    /*
     * @see com.sitewhere.spi.search.customer.ICustomerSearchCriteria#getRootOnly()
     */
    @Override
    public Boolean getRootOnly() {
	return rootOnly;
    }

    public void setRootOnly(Boolean rootOnly) {
	this.rootOnly = rootOnly;
    }

    /*
     * @see
     * com.sitewhere.spi.search.customer.ICustomerSearchCriteria#getParentCustomerId
     * ()
     */
    @Override
    public UUID getParentCustomerId() {
	return parentCustomerId;
    }

    public void setParentCustomerId(UUID parentCustomerId) {
	this.parentCustomerId = parentCustomerId;
    }

    /*
     * @see
     * com.sitewhere.spi.search.customer.ICustomerSearchCriteria#getCustomerTypeId()
     */
    @Override
    public UUID getCustomerTypeId() {
	return customerTypeId;
    }

    public void setCustomerTypeId(UUID customerTypeId) {
	this.customerTypeId = customerTypeId;
    }
}
