/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.area;

import java.util.UUID;

import com.sitewhere.spi.common.ISiteWhereEntity;

/**
 * A polygonal area associated with a site that can be used to trigger events.
 * 
 * @author dadams
 */
public interface IZone extends IBoundedEntity, ISiteWhereEntity {

    /**
     * Get id for associated area.
     * 
     * @return
     */
    public UUID getAreaId();

    /**
     * Get display name.
     * 
     * @return
     */
    public String getName();

    /**
     * Get the border color.
     * 
     * @return
     */
    public String getBorderColor();

    /**
     * Get the fill color.
     * 
     * @return
     */
    public String getFillColor();

    /**
     * Get the opacity value.
     * 
     * @return
     */
    public Double getOpacity();
}