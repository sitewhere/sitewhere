/*
 * IUserCreateRequest.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.user.request;

import java.util.List;

import com.sitewhere.spi.common.IMetadataProvider;
import com.sitewhere.spi.user.AccountStatus;

/**
 * Interface for arguments needed to create a user.
 * 
 * @author Derek
 */
public interface IUserCreateRequest extends IMetadataProvider {

	/**
	 * Get the username.
	 * 
	 * @return
	 */
	public String getUsername();

	/**
	 * Get the password.
	 * 
	 * @return
	 */
	public String getPassword();

	/**
	 * Get the common name.
	 * 
	 * @return
	 */
	public String getFirstName();

	/**
	 * Get the surname.
	 * 
	 * @return
	 */
	public String getLastName();

	/**
	 * Get the account status.
	 * 
	 * @return
	 */
	public AccountStatus getStatus();

	/**
	 * Get the list of granted authorities.
	 * 
	 * @return
	 */
	public List<String> getAuthorities();
}