/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.request;

import java.io.Serializable;
import java.util.Map;

import com.sitewhere.rest.model.device.SiteMapData;
import com.sitewhere.spi.device.request.ISiteCreateRequest;

/**
 * Provides parameters needed to create a new site.
 * 
 * @author Derek
 */
public class SiteCreateRequest implements ISiteCreateRequest, Serializable {

	/** Serialization version identifier */
	private static final long serialVersionUID = 574323736888872612L;

	/** Unique token */
	private String token;

	/** Site name */
	private String name;

	/** Site description */
	private String description;

	/** Logo image URL */
	private String imageUrl;

	/** Map data */
	private SiteMapData map = new SiteMapData();

	/** Metadata values */
	private Map<String, String> metadata;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.request.ISiteCreateRequest#getToken()
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
	 * @see com.sitewhere.spi.device.request.ISiteCreateRequest#getName()
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
	 * @see com.sitewhere.spi.device.request.ISiteCreateRequest#getDescription()
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
	 * @see com.sitewhere.spi.device.request.ISiteCreateRequest#getImageUrl()
	 */
	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.request.ISiteCreateRequest#getMap()
	 */
	public SiteMapData getMap() {
		return map;
	}

	public void setMap(SiteMapData map) {
		this.map = map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.request.ISiteCreateRequest#getMetadata()
	 */
	public Map<String, String> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}
}