/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.user.request;

import com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest;

/**
 * Holds fields needed to create a new granted authority.
 * 
 * @author Derek Adams
 */
public class GrantedAuthorityCreateRequest implements IGrantedAuthorityCreateRequest {

	/** Authority name */
	private String authority;

	/** Authority description */
	private String description;

	/** Parent authority */
	private String parent;

	/** Indicates whether authority is a group */
	private boolean group;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest#getAuthority()
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
	 * @see com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest#getDescription()
	 */
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest#getParent()
	 */
	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest#isGroup()
	 */
	public boolean isGroup() {
		return group;
	}

	public void setGroup(boolean group) {
		this.group = group;
	}
}