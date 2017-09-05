/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.common;

import java.io.Serializable;

/**
 * Coordinates that identify a point in 3d space.
 * 
 * @author dadams
 */
public interface ILocation extends Serializable {

    /**
     * Get latitude measurement.
     * 
     * @return
     */
    public Double getLatitude();

    /**
     * Get longitude measurement.
     * 
     * @return
     */
    public Double getLongitude();

    /**
     * Get elevation measurement.
     * 
     * @return
     */
    public Double getElevation();
}