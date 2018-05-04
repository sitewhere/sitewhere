/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.customer.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sitewhere.spi.customer.ICustomerType;
import com.sitewhere.spi.customer.request.ICustomerTypeCreateRequest;

/**
 * Provides information needed to create a customer type.
 * 
 * @author Derek
 */
public class CustomerTypeCreateRequest implements ICustomerTypeCreateRequest {

    /** Serial version UID */
    private static final long serialVersionUID = 2776911532638550509L;

    /** Alias token */
    private String token;

    /** Name */
    private String name;

    /** Description */
    private String description;

    /** Icon */
    private String icon;

    /** Metadata values */
    private Map<String, String> metadata;

    /** List of contained customer type tokens */
    private List<String> containedCustomerTypeTokens;

    /*
     * @see com.sitewhere.spi.customer.request.ICustomerTypeCreateRequest#getToken()
     */
    @Override
    public String getToken() {
	return token;
    }

    public void setToken(String token) {
	this.token = token;
    }

    /*
     * @see com.sitewhere.spi.customer.request.ICustomerTypeCreateRequest#getName()
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
     * com.sitewhere.spi.customer.request.ICustomerTypeCreateRequest#getDescription(
     * )
     */
    @Override
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    /*
     * @see com.sitewhere.spi.customer.request.ICustomerTypeCreateRequest#getIcon()
     */
    @Override
    public String getIcon() {
	return icon;
    }

    public void setIcon(String icon) {
	this.icon = icon;
    }

    /*
     * @see
     * com.sitewhere.spi.customer.request.ICustomerTypeCreateRequest#getMetadata()
     */
    @Override
    public Map<String, String> getMetadata() {
	return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
	this.metadata = metadata;
    }

    /*
     * @see com.sitewhere.spi.customer.request.ICustomerTypeCreateRequest#
     * getContainedCustomerTypeTokens()
     */
    @Override
    public List<String> getContainedCustomerTypeTokens() {
	return containedCustomerTypeTokens;
    }

    public void setContainedCustomerTypeTokens(List<String> containedCustomerTypeTokens) {
	this.containedCustomerTypeTokens = containedCustomerTypeTokens;
    }

    public static class Builder {

	/** Request being built */
	private CustomerTypeCreateRequest request = new CustomerTypeCreateRequest();

	public Builder(ICustomerType api) {
	    request.setToken(api.getToken());
	    request.setName(api.getName());
	    request.setDescription(api.getDescription());
	    request.setIcon(api.getIcon());
	    if (api.getMetadata() != null) {
		request.setMetadata(new HashMap<String, String>());
		request.getMetadata().putAll(api.getMetadata());
	    }
	}

	public Builder(String token, String name) {
	    request.setToken(token);
	    request.setName(name);
	    request.setDescription("");
	    request.setIcon("fa-question");
	}

	public Builder withDescription(String description) {
	    request.setDescription(description);
	    return this;
	}

	public Builder withIcon(String icon) {
	    request.setIcon(icon);
	    return this;
	}

	public Builder withContainedCustomerType(String customerTypeToken) {
	    if (request.getContainedCustomerTypeTokens() == null) {
		request.setContainedCustomerTypeTokens(new ArrayList<String>());
	    }
	    request.getContainedCustomerTypeTokens().add(customerTypeToken);
	    return this;
	}

	public Builder metadata(String name, String value) {
	    if (request.getMetadata() == null) {
		request.setMetadata(new HashMap<String, String>());
	    }
	    request.getMetadata().put(name, value);
	    return this;
	}

	public CustomerTypeCreateRequest build() {
	    return request;
	}
    }
}
