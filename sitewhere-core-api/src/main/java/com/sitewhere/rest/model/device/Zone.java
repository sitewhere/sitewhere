/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.common.Location;
import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.common.ILocation;
import com.sitewhere.spi.device.IZone;

/**
 * Model object for a zone.
 * 
 * @author dadams
 */
@JsonInclude(Include.NON_NULL)
public class Zone extends MetadataProviderEntity implements IZone, Serializable {

    /** Serialization version identifier */
    private static final long serialVersionUID = -5108019932881896046L;

    /** Unique device specification id */
    private UUID id;

    /** Unique zone token */
    private String token;

    /** Id for associated site */
    private UUID siteId;

    /** Displayed name */
    private String name;

    /** Zone coordinates */
    private List<Location> coordinates = new ArrayList<Location>();

    /** Border color */
    private String borderColor;

    /** Fill color */
    private String fillColor;

    /** Opacity */
    private Double opacity;

    /*
     * @see com.sitewhere.spi.device.IZone#getId()
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
     * @see com.sitewhere.spi.device.IZone#getToken()
     */
    @Override
    public String getToken() {
	return token;
    }

    public void setToken(String token) {
	this.token = token;
    }

    /*
     * @see com.sitewhere.spi.device.IZone#getSiteId()
     */
    @Override
    public UUID getSiteId() {
	return siteId;
    }

    public void setSiteId(UUID siteId) {
	this.siteId = siteId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IZone#getName()
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
     * @see com.sitewhere.spi.device.IZone#getCoordinates()
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<ILocation> getCoordinates() {
	return (List<ILocation>) (List<? extends ILocation>) coordinates;
    }

    public void setCoordinates(List<Location> coordinates) {
	this.coordinates = coordinates;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IZone#getBorderColor()
     */
    @Override
    public String getBorderColor() {
	return borderColor;
    }

    public void setBorderColor(String borderColor) {
	this.borderColor = borderColor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IZone#getFillColor()
     */
    @Override
    public String getFillColor() {
	return fillColor;
    }

    public void setFillColor(String fillColor) {
	this.fillColor = fillColor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IZone#getOpacity()
     */
    @Override
    public Double getOpacity() {
	return opacity;
    }

    public void setOpacity(Double opacity) {
	this.opacity = opacity;
    }

    /**
     * Create a copy of an SPI object. Used by web services for marshaling.
     * 
     * @param input
     * @return
     */
    public static Zone copy(IZone input) throws SiteWhereException {
	Zone result = new Zone();
	result.setToken(input.getToken());
	result.setSiteId(input.getSiteId());
	result.setName(input.getName());
	result.setCreatedDate(input.getCreatedDate());
	result.setBorderColor(input.getBorderColor());
	result.setFillColor(input.getFillColor());
	result.setOpacity(input.getOpacity());

	List<Location> coords = new ArrayList<Location>();
	for (ILocation location : input.getCoordinates()) {
	    coords.add(Location.copy(location));
	}
	result.setCoordinates(coords);

	MetadataProviderEntity.copy(input, result);
	return result;
    }
}