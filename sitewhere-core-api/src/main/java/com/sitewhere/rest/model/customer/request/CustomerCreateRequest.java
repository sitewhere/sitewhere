/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.customer.request;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.common.request.PersistentEntityCreateRequest;
import com.sitewhere.spi.customer.request.ICustomerCreateRequest;

/**
 * Provides parameters needed to create a new customer.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class CustomerCreateRequest extends PersistentEntityCreateRequest implements ICustomerCreateRequest {

    /** Serial version UID */
    private static final long serialVersionUID = -1744483793661325449L;

    /** Customer type token */
    public String customerTypeToken;

    /** Parent customer token */
    public String parentCustomerToken;

    /** Site name */
    private String name;

    /** Site description */
    private String description;

    /** Logo image URL */
    private String imageUrl;

    /*
     * @see com.sitewhere.spi.customer.request.ICustomerCreateRequest#
     * getCustomerTypeToken()
     */
    @Override
    public String getCustomerTypeToken() {
	return customerTypeToken;
    }

    public void setCustomerTypeToken(String customerTypeToken) {
	this.customerTypeToken = customerTypeToken;
    }

    /*
     * @see com.sitewhere.spi.customer.request.ICustomerCreateRequest#
     * getParentCustomerToken()
     */
    @Override
    public String getParentCustomerToken() {
	return parentCustomerToken;
    }

    public void setParentCustomerToken(String parentCustomerToken) {
	this.parentCustomerToken = parentCustomerToken;
    }

    /*
     * @see com.sitewhere.spi.customer.request.ICustomerCreateRequest#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * @see
     * com.sitewhere.spi.customer.request.ICustomerCreateRequest#getDescription()
     */
    @Override
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    /*
     * @see com.sitewhere.spi.customer.request.ICustomerCreateRequest#getImageUrl()
     */
    @Override
    public String getImageUrl() {
	return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
    }

    public static class Builder {

	/** Request being built */
	private CustomerCreateRequest request = new CustomerCreateRequest();

	public Builder(String customerTypeToken, String parentCustomerToken, String token, String name) {
	    request.setCustomerTypeToken(customerTypeToken);
	    request.setParentCustomerToken(parentCustomerToken);
	    request.setToken(token);
	    request.setName(name);
	    request.setDescription("");
	    request.setImageUrl("https://s3.amazonaws.com/sitewhere-demo/broken-link.png");
	}

	public Builder withDescription(String description) {
	    request.setDescription(description);
	    return this;
	}

	public Builder withImageUrl(String imageUrl) {
	    request.setImageUrl(imageUrl);
	    return this;
	}

	public Builder metadata(String name, String value) {
	    if (request.getMetadata() == null) {
		request.setMetadata(new HashMap<String, String>());
	    }
	    request.getMetadata().put(name, value);
	    return this;
	}

	public CustomerCreateRequest build() {
	    return request;
	}
    }
}
