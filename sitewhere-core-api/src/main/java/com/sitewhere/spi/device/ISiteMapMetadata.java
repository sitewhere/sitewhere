/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device;

/**
 * Holds constants for site map metadata fields.
 * 
 * @author Derek
 */
public interface ISiteMapMetadata {

    /** Latitude value for initial map position */
    public static String MAP_CENTER_LATITUDE = "centerLatitude";

    /** Longitude value for initial map position */
    public static String MAP_CENTER_LONGITUDE = "centerLongitude";

    /** Initial zoom level */
    public static String MAP_ZOOM_LEVEL = "zoomLevel";
}