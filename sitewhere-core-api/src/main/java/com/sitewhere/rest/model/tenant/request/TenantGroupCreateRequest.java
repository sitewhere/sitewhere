/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.tenant.request;

import java.io.Serializable;

import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.spi.tenant.request.ITenantGroupCreateRequest;

/**
 * Model object for attributes needed to create a tenant group.
 * 
 * @author Derek
 */
public class TenantGroupCreateRequest extends MetadataProvider
		implements ITenantGroupCreateRequest, Serializable {

	/** Serial version UID */
	private static final long serialVersionUID = -6593183022611071721L;

	/** Unique tenant group id */
	private String id;

	/** Group name */
	private String name;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.tenant.request.ITenantGroupCreateRequest#getId()
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.tenant.request.ITenantGroupCreateRequest#getName()
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
