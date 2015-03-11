/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.ISite;

/**
 * Model object for site information.
 * 
 * @author dadams
 */
@JsonInclude(Include.NON_NULL)
public class Site extends MetadataProviderEntity implements ISite, Serializable {

	/** Serialization version identifier */
	private static final long serialVersionUID = 3080612757299957486L;

	/** Unique token */
	private String token;

	/** Site name */
	private String name;

	/** Site description */
	private String description;

	/** Image URL */
	private String imageUrl;

	/** Map data */
	private SiteMapData map = new SiteMapData();

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.ISite#getMap()
	 */
	public SiteMapData getMap() {
		return map;
	}

	public void setMap(SiteMapData map) {
		this.map = map;
	}

	/**
	 * Create a copy of an SPI object. Used by web services for marshaling.
	 * 
	 * @param input
	 * @return
	 */
	public static Site copy(ISite input) throws SiteWhereException {
		Site result = new Site();
		result.setToken(input.getToken());
		result.setName(input.getName());
		result.setDescription(input.getDescription());
		result.setImageUrl(input.getImageUrl());
		result.setMap(SiteMapData.copy(input.getMap()));
		MetadataProviderEntity.copy(input, result);
		return result;
	}
}