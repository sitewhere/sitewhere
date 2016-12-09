/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.asset.request;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.spi.asset.IPersonAsset;
import com.sitewhere.spi.asset.request.IPersonAssetCreateRequest;

/**
 * REST model implementation of {@link IPersonAssetCreateRequest}.
 * 
 * @author Derek
 */
public class PersonAssetCreateRequest extends AssetCreateRequest implements IPersonAssetCreateRequest {

    /** Serial version UID */
    private static final long serialVersionUID = 2735460736378688736L;

    /** Username */
    private String userName;

    /** Email address */
    private String emailAddress;

    /** Roles */
    private List<String> roles = new ArrayList<String>();

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.request.IPersonAssetCreateRequest#getUserName()
     */
    public String getUserName() {
	return userName;
    }

    public void setUserName(String userName) {
	this.userName = userName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.request.IPersonAssetCreateRequest#getEmailAddress
     * ()
     */
    public String getEmailAddress() {
	return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
	this.emailAddress = emailAddress;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.request.IPersonAssetCreateRequest#getRoles()
     */
    public List<String> getRoles() {
	return roles;
    }

    public void setRoles(List<String> roles) {
	this.roles = roles;
    }

    public static class Builder {

	/** Request being built */
	private PersonAssetCreateRequest request = new PersonAssetCreateRequest();

	public Builder(IPersonAsset asset) {
	    this(asset.getId(), asset.getName(), asset.getImageUrl());
	    request.setUserName(asset.getUserName());
	    request.setEmailAddress(asset.getEmailAddress());
	    request.getRoles().addAll(asset.getRoles());
	    request.getProperties().putAll(asset.getProperties());
	}

	public Builder(String id, String name, String imageUrl) {
	    request.setId(id);
	    request.setName(name);
	    request.setImageUrl(imageUrl);
	}

	public Builder withProperty(String name, String value) {
	    request.getProperties().put(name, value);
	    return this;
	}

	public Builder withUsername(String userName) {
	    request.setUserName(userName);
	    return this;
	}

	public Builder withEmailAddress(String emailAddress) {
	    request.setEmailAddress(emailAddress);
	    return this;
	}

	public Builder withRole(String role) {
	    request.getRoles().add(role);
	    return this;
	}

	public PersonAssetCreateRequest build() {
	    return request;
	}
    }
}