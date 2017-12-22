/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device;

import java.io.Serializable;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.spi.device.ISite;

/**
 * Model object for site information.
 * 
 * @author dadams
 */
@JsonInclude(Include.NON_NULL)
public class Site extends MetadataProviderEntity implements ISite, Serializable {

    /** Serialization version identifier */
    private static final long serialVersionUID = -566693689485715028L;

    /** Unique site id */
    private UUID id;

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

    /*
     * @see com.sitewhere.spi.device.ISite#getId()
     */
    @Override
    public UUID getId() {
	return id;
    }

    public void setId(UUID id) {
	this.id = id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.ISite#getToken()
     */
    @Override
    public String getToken() {
	return token;
    }

    public void setToken(String token) {
	this.token = token;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.ISite#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.ISite#getDescription()
     */
    @Override
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.ISite#getImageUrl()
     */
    @Override
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
    @Override
    public SiteMapData getMap() {
	return map;
    }

    public void setMap(SiteMapData map) {
	this.map = map;
    }
}