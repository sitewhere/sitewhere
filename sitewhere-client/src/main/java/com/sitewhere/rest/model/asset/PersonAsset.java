/*
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package com.sitewhere.rest.model.asset;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IPersonAsset;

/**
 * Model class for a person asset.
 * 
 * @author dadams
 */
public class PersonAsset extends Asset implements IPersonAsset {

	/** Asset username */
	private String userName;

	/** Asset email address */
	private String emailAddress;

	/** List of roles */
	private List<String> roles = new ArrayList<String>();

	public PersonAsset() {
		setType(AssetType.Person);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IPersonAsset#getUserName()
	 */
	@Override
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IPersonAsset#getEmailAddress()
	 */
	@Override
	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IPersonAsset#getRoles()
	 */
	@Override
	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
}