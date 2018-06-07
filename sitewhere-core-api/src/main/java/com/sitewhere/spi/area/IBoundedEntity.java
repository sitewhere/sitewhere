/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.area;

import java.util.List;

import com.sitewhere.spi.common.ILocation;

/**
 * Entity that contains geospatial coordinates that provide boundary
 * information.
 * 
 * @author Derek
 */
public interface IBoundedEntity {

    /**
     * Get list of locations that defines the bounding object.
     * 
     * @return
     */
    public List<? extends ILocation> getBounds();
}