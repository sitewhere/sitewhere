/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.asset.request;

import java.util.List;

import com.sitewhere.spi.asset.IPersonAsset;

/**
 * Information needed to create an {@link IPersonAsset}.
 * 
 * @author Derek
 */
public interface IPersonAssetCreateRequest extends IAssetCreateRequest {

	/**
	 * Get the unique username.
	 * 
	 * @return
	 */
	public String getUserName();

	//
	/**
	 * Get the email address.
	 * 
	 * @return
	 */
	public String getEmailAddress();

	/**
	 * Get a list of roles for the person.
	 * 
	 * @return
	 */
	public List<String> getRoles();
}