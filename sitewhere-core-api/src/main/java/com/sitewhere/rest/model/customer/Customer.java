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
import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.spi.customer.ICustomer;

/**
 * Model object for customer information.
 * 
 * @author dadams
 */
@JsonInclude(Include.NON_NULL)
public class Customer extends MetadataProviderEntity implements ICustomer {

    /** Serial version UID */
    private static final long serialVersionUID = -3637398272972673831L;

    /** Unique area id */
    private UUID id;

    /** Unique token */
    private String token;

    /** Customer type id */
    private UUID customerTypeId;

    /** Parent customer id */
    private UUID parentCustomerId;

    /** Area name */
    private String name;

    /** Area description */
    private String description;

    /** Image URL */
    private String imageUrl;

    /*
     * @see com.sitewhere.spi.common.ISiteWhereEntity#getId()
     */
    @Override
    public UUID getId() {
	return id;
    }

    public void setId(UUID id) {
	this.id = id;
    }

    /*
     * @see com.sitewhere.spi.common.ISiteWhereEntity#getToken()
     */
    @Override
    public String getToken() {
	return token;
    }

    public void setToken(String token) {
	this.token = token;
    }

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

    /*
     * @see com.sitewhere.spi.customer.ICustomer#getImageUrl()
     */
    @Override
    public String getImageUrl() {
	return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
    }
}
