/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.asset.request;

import java.util.HashMap;
import java.util.Map;

import com.sitewhere.spi.asset.request.IAssetCreateRequest;

/**
 * REST model implementation of {@link IAssetCreateRequest}.
 * 
 * @author Derek
 */
public class AssetCreateRequest implements IAssetCreateRequest {

	/** Asset id */
	private String id;

	/** Asset name */
	private String name;

	/** URL pointing to asset image */
	private String imageUrl;

	/** Asset properties */
	private Map<String, String> properties = new HashMap<String, String>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.request.IAssetCreateRequest#getId()
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
	 * @see com.sitewhere.spi.asset.request.IAssetCreateRequest#getName()
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
	 * @see com.sitewhere.spi.asset.request.IAssetCreateRequest#getImageUrl()
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
	 * @see com.sitewhere.spi.asset.request.IAssetCreateRequest#getProperties()
	 */
	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
}