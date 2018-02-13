/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.area.request;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

import com.sitewhere.spi.area.IAreaMapData;

/**
 * Interface for arguments needed to create an area.
 * 
 * @author Derek
 */
public interface IAreaCreateRequest extends Serializable {

    /**
     * Site token. Can be set to null if token should be auto-generated.
     * 
     * @return
     */
    public String getToken();

    /**
     * Get id of corresponding area type.
     * 
     * @return
     */
    public UUID getAreaTypeId();

    /**
     * Get id of parent id (null if none).
     * 
     * @return
     */
    public UUID getParentAreaId();

    /**
     * Get name.
     * 
     * @return
     */
    public String getName();

    /**
     * Get description.
     * 
     * @return
     */
    public String getDescription();

    /**
     * Get URL for logo image.
     * 
     * @return
     */
    public String getImageUrl();

    /**
     * Get map information.
     * 
     * @return
     */
    public IAreaMapData getMap();

    /**
     * Get metadata values.
     * 
     * @return
     */
    public Map<String, String> getMetadata();
}