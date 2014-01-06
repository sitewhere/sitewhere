/*
 * IGrantedAuthorityCreateRequest.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.user.request;

/**
 * Interface for arguments needed to create a granted authority.
 * 
 * @author Derek
 */
public interface IGrantedAuthorityCreateRequest {

	/**
	 * Get the authority name.
	 * 
	 * @return
	 */
	public String getAuthority();

	/**
	 * Get the description.
	 * 
	 * @return
	 */
	public String getDescription();
}