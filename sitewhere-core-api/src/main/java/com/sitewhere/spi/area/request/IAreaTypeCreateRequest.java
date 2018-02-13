/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.area.request;

/**
 * Provides information needed to create an area type.
 * 
 * @author Derek
 */
public interface IAreaTypeCreateRequest {

    /**
     * Get token that acts as an alias for area type id.
     * 
     * @return
     */
    public String getToken();

    /**
     * Get the area type name.
     * 
     * @return
     */
    public String getName();

    /**
     * Get the area type description.
     * 
     * @return
     */
    public String getDescription();

    /**
     * Get icon shown for area type.
     * 
     * @return
     */
    public String getIcon();
}