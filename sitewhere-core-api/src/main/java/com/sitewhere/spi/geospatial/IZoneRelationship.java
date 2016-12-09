/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.geospatial;

import com.sitewhere.spi.device.IZone;
import com.sitewhere.spi.device.event.IDeviceLocation;

/**
 * Represents a relationship between a location and a zone.
 * 
 * @author Derek
 */
public interface IZoneRelationship {

    /**
     * Get device location.
     * 
     * @return
     */
    public IDeviceLocation getLocation();

    /**
     * Get zone.
     * 
     * @return
     */
    public IZone getZone();

    /**
     * Get containment indicator.
     * 
     * @return
     */
    public ZoneContainment getContainment();
}