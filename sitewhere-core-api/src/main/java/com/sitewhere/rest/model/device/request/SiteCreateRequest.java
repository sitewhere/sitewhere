/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.request;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.device.SiteMapData;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.ISiteMapMetadata;
import com.sitewhere.spi.device.request.ISiteCreateRequest;

/**
 * Provides parameters needed to create a new site.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
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
    private SiteMapData map;

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

    public static class Builder {

	/** Request being built */
	private SiteCreateRequest request = new SiteCreateRequest();

	public Builder(ISite api) {
	    request.setToken(api.getToken());
	    request.setName(api.getName());
	    request.setDescription(api.getDescription());
	    request.setImageUrl(api.getImageUrl());
	    request.setMetadata(new HashMap<String, String>());
	    request.getMetadata().putAll(api.getMetadata());
	    try {
		request.setMap(SiteMapData.copy(api.getMap()));
	    } catch (SiteWhereException e) {
	    }
	}

	public Builder(String token, String name) {
	    request.setToken(token);
	    request.setName(name);
	    request.setDescription("");
	    request.setImageUrl("https://s3.amazonaws.com/sitewhere-demo/construction/construction.jpg");
	}

	public Builder withDescription(String description) {
	    request.setDescription(description);
	    return this;
	}

	public Builder withImageUrl(String imageUrl) {
	    request.setImageUrl(imageUrl);
	    return this;
	}

	public Builder openStreetMap(double latitude, double longitude, int zoomLevel) {
	    SiteMapData map = new SiteMapData();
	    try {
		map.setType("openstreetmap");
		map.addOrReplaceMetadata(ISiteMapMetadata.MAP_CENTER_LATITUDE, String.valueOf(latitude));
		map.addOrReplaceMetadata(ISiteMapMetadata.MAP_CENTER_LONGITUDE, String.valueOf(longitude));
		map.addOrReplaceMetadata(ISiteMapMetadata.MAP_ZOOM_LEVEL, String.valueOf(zoomLevel));
		request.setMap(map);
	    } catch (SiteWhereException e) {
		throw new RuntimeException(e);
	    }
	    return this;
	}

	public Builder mapquestMap(double latitude, double longitude, int zoomLevel) {
	    SiteMapData map = new SiteMapData();
	    try {
		map.setType("mapquest");
		map.addOrReplaceMetadata(ISiteMapMetadata.MAP_CENTER_LATITUDE, String.valueOf(latitude));
		map.addOrReplaceMetadata(ISiteMapMetadata.MAP_CENTER_LONGITUDE, String.valueOf(longitude));
		map.addOrReplaceMetadata(ISiteMapMetadata.MAP_ZOOM_LEVEL, String.valueOf(zoomLevel));
		request.setMap(map);
	    } catch (SiteWhereException e) {
		throw new RuntimeException(e);
	    }
	    return this;
	}

	public Builder metadata(String name, String value) {
	    if (request.getMetadata() == null) {
		request.setMetadata(new HashMap<String, String>());
	    }
	    request.getMetadata().put(name, value);
	    return this;
	}

	public SiteCreateRequest build() {
	    return request;
	}
    }
}