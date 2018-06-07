/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.area.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.common.Location;
import com.sitewhere.spi.area.request.IZoneCreateRequest;

/**
 * Provides parameters needed to create a new zone.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class ZoneCreateRequest implements IZoneCreateRequest {

    /** Serial version UID */
    private static final long serialVersionUID = -2226478978161539653L;

    /** Zone name */
    private String name;

    /** Locations that define zone boundaries */
    private List<Location> bounds = new ArrayList<Location>();

    /** Border color in UI */
    private String borderColor;

    /** Fill color in UI */
    private String fillColor;

    /** Opacity in UI */
    private Double opacity;

    /** Metadata values */
    private Map<String, String> metadata;

    /*
     * @see com.sitewhere.spi.area.request.IZoneCreateRequest#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * @see com.sitewhere.spi.area.request.IZoneCreateRequest#getBounds()
     */
    @Override
    public List<Location> getBounds() {
	return bounds;
    }

    public void setBounds(List<Location> bounds) {
	this.bounds = bounds;
    }

    /*
     * @see com.sitewhere.spi.area.request.IZoneCreateRequest#getBorderColor()
     */
    @Override
    public String getBorderColor() {
	return borderColor;
    }

    public void setBorderColor(String borderColor) {
	this.borderColor = borderColor;
    }

    /*
     * @see com.sitewhere.spi.area.request.IZoneCreateRequest#getFillColor()
     */
    @Override
    public String getFillColor() {
	return fillColor;
    }

    public void setFillColor(String fillColor) {
	this.fillColor = fillColor;
    }

    /*
     * @see com.sitewhere.spi.area.request.IZoneCreateRequest#getOpacity()
     */
    @Override
    public Double getOpacity() {
	return opacity;
    }

    public void setOpacity(Double opacity) {
	this.opacity = opacity;
    }

    /*
     * @see com.sitewhere.spi.area.request.IZoneCreateRequest#getMetadata()
     */
    @Override
    public Map<String, String> getMetadata() {
	return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
	this.metadata = metadata;
    }

    public static class Builder {

	/** Request being built */
	private ZoneCreateRequest request = new ZoneCreateRequest();

	public Builder(String name) {
	    request.setName(name);
	}

	public Builder withBorderColor(String borderColor) {
	    request.setBorderColor(borderColor);
	    return this;
	}

	public Builder withFillColor(String fillColor) {
	    request.setFillColor(fillColor);
	    return this;
	}

	public Builder withOpacity(double opacity) {
	    request.setOpacity(opacity);
	    return this;
	}

	public Builder coord(double latitude, double longitude) {
	    request.getBounds().add(new Location(latitude, longitude));
	    return this;
	}

	public Builder metadata(String name, String value) {
	    if (request.getMetadata() == null) {
		request.setMetadata(new HashMap<String, String>());
	    }
	    request.getMetadata().put(name, value);
	    return this;
	}

	public ZoneCreateRequest build() {
	    return request;
	}
    }
}