/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.user;

import com.sitewhere.spi.user.IGrantedAuthority;

/**
 * Model object for a granted authority.
 * 
 * @author Derek Adams
 */
public class GrantedAuthority implements IGrantedAuthority {

	/** Authority */
	private String authority;

	/** Description */
	private String description;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.IGrantedAuthority#getAuthority()
	 */
	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.IGrantedAuthority#getDescription()
	 */
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Copy contents from the SPI class.
	 * 
	 * @param input
	 * @return
	 */
	public static GrantedAuthority copy(IGrantedAuthority input) {
		GrantedAuthority result = new GrantedAuthority();
		result.setAuthority(input.getAuthority());
		result.setDescription(input.getDescription());
		return result;
	}
}