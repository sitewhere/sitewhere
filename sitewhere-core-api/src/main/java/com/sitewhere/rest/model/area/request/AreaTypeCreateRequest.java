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

import com.sitewhere.rest.model.common.request.BrandedEntityCreateRequest;
import com.sitewhere.spi.area.IAreaType;
import com.sitewhere.spi.area.request.IAreaTypeCreateRequest;

/**
 * Provides information needed to create an area type.
 * 
 * @author Derek
 */
public class AreaTypeCreateRequest extends BrandedEntityCreateRequest implements IAreaTypeCreateRequest {

    /** Serial version UID */
    private static final long serialVersionUID = 7654388850917582565L;

    /** Name */
    private String name;

    /** Description */
    private String description;

    /** List of contained area type tokens */
    private List<String> containedAreaTypeTokens;

    /*
     * @see com.sitewhere.spi.area.request.IAreaTypeCreateRequest#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * @see com.sitewhere.spi.area.request.IAreaTypeCreateRequest#getDescription()
     */
    @Override
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    /*
     * @see com.sitewhere.spi.area.request.IAreaTypeCreateRequest#
     * getContainedAreaTypeTokens()
     */
    @Override
    public List<String> getContainedAreaTypeTokens() {
	return containedAreaTypeTokens;
    }

    public void setContainedAreaTypeTokens(List<String> containedAreaTypeTokens) {
	this.containedAreaTypeTokens = containedAreaTypeTokens;
    }

    public static class Builder {

	/** Request being built */
	private AreaTypeCreateRequest request = new AreaTypeCreateRequest();

	public Builder(IAreaType api) {
	    request.setToken(api.getToken());
	    request.setName(api.getName());
	    request.setDescription(api.getDescription());
	    request.setIcon(api.getIcon());
	    if (api.getMetadata() != null) {
		request.setMetadata(new HashMap<String, String>());
		request.getMetadata().putAll(api.getMetadata());
	    }
	}

	public Builder(String token, String name) {
	    request.setToken(token);
	    request.setName(name);
	    request.setDescription("");
	    request.setIcon("fa-question");
	}

	public Builder withDescription(String description) {
	    request.setDescription(description);
	    return this;
	}

	public Builder withIcon(String icon) {
	    request.setIcon(icon);
	    return this;
	}

	public Builder withContainedAreaType(String areaTypeToken) {
	    if (request.getContainedAreaTypeTokens() == null) {
		request.setContainedAreaTypeTokens(new ArrayList<String>());
	    }
	    request.getContainedAreaTypeTokens().add(areaTypeToken);
	    return this;
	}

	public Builder metadata(String name, String value) {
	    if (request.getMetadata() == null) {
		request.setMetadata(new HashMap<String, String>());
	    }
	    request.getMetadata().put(name, value);
	    return this;
	}

	public AreaTypeCreateRequest build() {
	    return request;
	}
    }
}