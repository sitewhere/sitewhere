/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.search.area;

import java.util.UUID;

import com.sitewhere.spi.search.ISearchCriteria;

/**
 * Search criteria particular to area searches.
 * 
 * @author Derek
 */
public interface IAreaSearchCriteria extends ISearchCriteria {

    /**
     * Indicates if only root elements are to be returned.
     * 
     * @return
     */
    public Boolean getRootOnly();

    /**
     * Requires that areas have the given area as a parent.
     * 
     * @return
     */
    public UUID getParentAreaId();
}