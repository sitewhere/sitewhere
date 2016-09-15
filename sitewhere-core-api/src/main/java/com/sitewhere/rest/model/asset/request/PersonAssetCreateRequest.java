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
}