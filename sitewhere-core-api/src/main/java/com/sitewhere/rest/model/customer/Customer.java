/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.customer;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.common.BrandedEntity;
import com.sitewhere.spi.customer.ICustomer;

/**
 * Model object for customer information.
 * 
 * @author dadams
 */
@JsonInclude(Include.NON_NULL)
public class Customer extends BrandedEntity implements ICustomer {

    /** Serial version UID */
    private static final long serialVersionUID = -3637398272972673831L;

    /** Customer type id */
    private UUID customerTypeId;

    /** Parent customer id */
    private UUID parentCustomerId;

    /** Area name */
    private String name;

    /** Area description */
    private String description;

    /*
     * @see com.sitewhere.spi.customer.ICustomer#getCustomerTypeId()
     */
    @Override
    public UUID getCustomerTypeId() {
	return customerTypeId;
    }

    public void setCustomerTypeId(UUID customerTypeId) {
	this.customerTypeId = customerTypeId;
    }

    /*
     * @see com.sitewhere.spi.customer.ICustomer#getParentCustomerId()
     */
    @Override
    public UUID getParentCustomerId() {
	return parentCustomerId;
    }

    public void setParentCustomerId(UUID parentCustomerId) {
	this.parentCustomerId = parentCustomerId;
    }

    /*
     * @see com.sitewhere.spi.customer.ICustomer#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * @see com.sitewhere.spi.customer.ICustomer#getDescription()
     */
    @Override
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }
}
