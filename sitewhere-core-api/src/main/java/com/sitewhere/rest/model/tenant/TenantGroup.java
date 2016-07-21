/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.tenant;

import java.io.Serializable;

import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.tenant.ITenantGroup;

/**
 * Model object for a tenant group.
 * 
 * @author Derek
 */
public class TenantGroup extends MetadataProviderEntity implements ITenantGroup, Serializable {

	/** Serial version UID */
	private static final long serialVersionUID = 8286511120595519596L;

	/** Unique tenant group token */
	private String token;

	/** Group name */
	private String name;

	/** Group description */
	private String description;

	/** Group image URL */
	private String imageUrl;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.tenant.ITenantGroup#getToken()
	 */
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.tenant.ITenantGroup#getName()
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.tenant.ITenantGroup#getDescription()
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
	 * @see com.sitewhere.spi.tenant.ITenantGroup#getImageUrl()
	 */
	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public static TenantGroup copy(ITenantGroup input) throws SiteWhereException {
		TenantGroup result = new TenantGroup();
		result.setToken(input.getToken());
		result.setName(input.getName());
		result.setDescription(input.getDescription());
		result.setImageUrl(input.getImageUrl());
		MetadataProviderEntity.copy(input, result);
		return result;
	}
}