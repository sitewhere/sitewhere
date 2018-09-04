/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.customer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.common.BrandedEntity;
import com.sitewhere.spi.customer.ICustomerType;

/**
 * Model object for a customer type.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class CustomerType extends BrandedEntity implements ICustomerType {

    /** Serial version UID */
    private static final long serialVersionUID = -2203663173210728449L;

    /** Name */
    private String name;

    /** Description */
    private String description;

    /** List of contained area type ids */
    private List<UUID> containedCustomerTypeIds = new ArrayList<>();

    /*
     * @see com.sitewhere.spi.customer.ICustomerType#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * @see com.sitewhere.spi.customer.ICustomerType#getDescription()
     */
    @Override
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    /*
     * @see com.sitewhere.spi.customer.ICustomerType#getContainedCustomerTypeIds()
     */
    @Override
    public List<UUID> getContainedCustomerTypeIds() {
	return containedCustomerTypeIds;
    }

    public void setContainedCustomerTypeIds(List<UUID> containedCustomerTypeIds) {
	this.containedCustomerTypeIds = containedCustomerTypeIds;
    }
}
