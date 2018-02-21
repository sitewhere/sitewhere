/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.search.area;

import java.util.UUID;

import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.spi.search.area.IAreaSearchCriteria;

/**
 * Model object for properties used in area searches.
 * 
 * @author Derek
 */
public class AreaSearchCriteria extends SearchCriteria implements IAreaSearchCriteria {

    /** Only return root areas */
    private Boolean rootOnly;

    /** Only return areas with the given parent */
    private UUID parentAreaId;

    /** Only return areas of the given type */
    private UUID areaTypeId;

    public AreaSearchCriteria(int pageNumber, int pageSize) {
	super(pageNumber, pageSize);
    }

    /*
     * @see com.sitewhere.spi.search.area.IAreaSearchCriteria#getRootOnly()
     */
    @Override
    public Boolean getRootOnly() {
	return rootOnly;
    }

    public void setRootOnly(Boolean rootOnly) {
	this.rootOnly = rootOnly;
    }

    /*
     * @see com.sitewhere.spi.search.area.IAreaSearchCriteria#getParentAreaId()
     */
    @Override
    public UUID getParentAreaId() {
	return parentAreaId;
    }

    public void setParentAreaId(UUID parentAreaId) {
	this.parentAreaId = parentAreaId;
    }

    /*
     * @see com.sitewhere.spi.search.area.IAreaSearchCriteria#getAreaTypeId()
     */
    @Override
    public UUID getAreaTypeId() {
	return areaTypeId;
    }

    public void setAreaTypeId(UUID areaTypeId) {
	this.areaTypeId = areaTypeId;
    }
}
