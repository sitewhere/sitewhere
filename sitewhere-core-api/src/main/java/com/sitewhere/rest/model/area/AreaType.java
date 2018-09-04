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
import com.sitewhere.rest.model.common.BrandedEntity;
import com.sitewhere.spi.area.IAreaType;

/**
 * Model object for an area type.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class AreaType extends BrandedEntity implements IAreaType {

    /** Serial version UID */
    private static final long serialVersionUID = -2894293965749361706L;

    /** Name */
    private String name;

    /** Description */
    private String description;

    /** List of contained area type ids */
    private List<UUID> containedAreaTypeIds = new ArrayList<>();

    /*
     * @see com.sitewhere.spi.area.IAreaType#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * @see com.sitewhere.spi.area.IAreaType#getDescription()
     */
    @Override
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    /*
     * @see com.sitewhere.spi.area.IAreaType#getContainedAreaTypeIds()
     */
    @Override
    public List<UUID> getContainedAreaTypeIds() {
	return containedAreaTypeIds;
    }

    public void setContainedAreaTypeIds(List<UUID> containedAreaTypeIds) {
	this.containedAreaTypeIds = containedAreaTypeIds;
    }
}