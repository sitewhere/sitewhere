/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.area;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.common.Location;
import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.common.ILocation;

/**
 * Model object for area information.
 * 
 * @author dadams
 */
@JsonInclude(Include.NON_NULL)
public class Area extends MetadataProviderEntity implements IArea {

    /** Serialization version identifier */
    private static final long serialVersionUID = -566693689485715028L;

    /** Unique area id */
    private UUID id;

    /** Unique token */
    private String token;

    /** Area type id */
    private UUID areaTypeId;

    /** Parent area id */
    private UUID parentAreaId;

    /** Area name */
    private String name;

    /** Area description */
    private String description;

    /** Image URL */
    private String imageUrl;

    /** Area boundary coordinates */
    private List<Location> coordinates = new ArrayList<Location>();

    /*
     * @see com.sitewhere.spi.area.IArea#getId()
     */
    @Override
    public UUID getId() {
	return id;
    }

    public void setId(UUID id) {
	this.id = id;
    }

    /*
     * @see com.sitewhere.spi.area.IArea#getToken()
     */
    @Override
    public String getToken() {
	return token;
    }

    public void setToken(String token) {
	this.token = token;
    }

    /*
     * @see com.sitewhere.spi.area.IArea#getAreaTypeId()
     */
    @Override
    public UUID getAreaTypeId() {
	return areaTypeId;
    }

    public void setAreaTypeId(UUID areaTypeId) {
	this.areaTypeId = areaTypeId;
    }

    /*
     * @see com.sitewhere.spi.area.IArea#getParentAreaId()
     */
    @Override
    public UUID getParentAreaId() {
	return parentAreaId;
    }

    public void setParentAreaId(UUID parentAreaId) {
	this.parentAreaId = parentAreaId;
    }

    /*
     * @see com.sitewhere.spi.area.IArea#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * @see com.sitewhere.spi.area.IArea#getDescription()
     */
    @Override
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    /*
     * @see com.sitewhere.spi.area.IArea#getImageUrl()
     */
    @Override
    public String getImageUrl() {
	return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
    }

    /*
     * @see com.sitewhere.spi.area.IBoundedEntity#getCoordinates()
     */
    @Override
    public List<? extends ILocation> getCoordinates() {
	return coordinates;
    }

    public void setCoordinates(List<Location> coordinates) {
	this.coordinates = coordinates;
    }
}