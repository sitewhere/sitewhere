/*
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package com.sitewhere.spi.asset;

import java.util.List;

/**
 * Interface for an asset that is a person.
 * 
 * @author dadams
 */
public interface IPersonAsset extends IAsset {

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

	/**
	 * Get the primary photo URL.
	 * 
	 * @return
	 */
	public String getPhotoUrl();
}