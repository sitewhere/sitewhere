/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.request;

import java.util.Map;

import com.sitewhere.spi.device.ISiteMapData;

/**
 * Interface for arguments needed to create a site.
 * 
 * @author Derek
 */
public interface ISiteCreateRequest {

    /**
     * Site token. Can be set to null if token should be auto-generated.
     * 
     * @return
     */
    public String getToken();

    /**
     * Get site name.
     * 
     * @return
     */
    public String getName();

    /**
     * Get site description.
     * 
     * @return
     */
    public String getDescription();

    /**
     * Get URL for site logo image.
     * 
     * @return
     */
    public String getImageUrl();

    /**
     * Get map information.
     * 
     * @return
     */
    public ISiteMapData getMap();

    /**
     * Get metadata values.
     * 
     * @return
     */
    public Map<String, String> getMetadata();
}