/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device;

import java.util.List;

import com.sitewhere.spi.common.ILocation;
import com.sitewhere.spi.common.IMetadataProviderEntity;

/**
 * A polygonal area associated with a site that can be used to trigger events.
 * 
 * @author dadams
 */
public interface IZone extends IMetadataProviderEntity {

    /**
     * Get unique zone token.
     * 
     * @return
     */
    public String getToken();

    /**
     * Get token for associated site.
     * 
     * @return
     */
    public String getSiteToken();

    /**
     * Get display name.
     * 
     * @return
     */
    public String getName();

    /**
     * Get list of coordinates that defines the zone.
     * 
     * @return
     */
    public List<ILocation> getCoordinates();

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