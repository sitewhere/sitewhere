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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.common.Location;
import com.sitewhere.rest.model.common.request.BrandedEntityCreateRequest;
import com.sitewhere.spi.area.request.IAreaCreateRequest;

/**
 * Provides parameters needed to create a new area.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class AreaCreateRequest extends BrandedEntityCreateRequest implements IAreaCreateRequest {

    /** Serial version UID */
    private static final long serialVersionUID = 2232101100201358496L;

    /** Area type token */
    public String areaTypeToken;

    /** Parent area token */
    public String parentAreaToken;

    /** Site name */
    private String name;

    /** Site description */
    private String description;

    /** Locations that define area boundaries */
    private List<Location> bounds = new ArrayList<Location>();

    /*
     * @see com.sitewhere.spi.area.request.IAreaCreateRequest#getAreaTypeToken()
     */
    @Override
    public String getAreaTypeToken() {
	return areaTypeToken;
    }

    public void setAreaTypeToken(String areaTypeToken) {
	this.areaTypeToken = areaTypeToken;
    }

    /*
     * @see com.sitewhere.spi.area.request.IAreaCreateRequest#getParentAreaToken()
     */
    @Override
    public String getParentAreaToken() {
	return parentAreaToken;
    }

    public void setParentAreaToken(String parentAreaToken) {
	this.parentAreaToken = parentAreaToken;
    }

    /*
     * @see com.sitewhere.spi.area.request.IAreaCreateRequest#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * @see com.sitewhere.spi.area.request.IAreaCreateRequest#getDescription()
     */
    @Override
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    /*
     * @see com.sitewhere.spi.area.request.IAreaCreateRequest#getBounds()
     */
    @Override
    public List<Location> getBounds() {
	return bounds;
    }

    public void setBounds(List<Location> bounds) {
	this.bounds = bounds;
    }

    public static class Builder {

	/** Request being built */
	private AreaCreateRequest request = new AreaCreateRequest();

	public Builder(String areaTypeToken, String parentAreaToken, String token, String name) {
	    request.setAreaTypeToken(areaTypeToken);
	    request.setParentAreaToken(parentAreaToken);
	    request.setToken(token);
	    request.setName(name);
	    request.setDescription("");
	    request.setImageUrl("https://s3.amazonaws.com/sitewhere-demo/broken-link.png");
	}

	public Builder withDescription(String description) {
	    request.setDescription(description);
	    return this;
	}

	public Builder withImageUrl(String imageUrl) {
	    request.setImageUrl(imageUrl);
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

	public AreaCreateRequest build() {
	    return request;
	}
    }
}