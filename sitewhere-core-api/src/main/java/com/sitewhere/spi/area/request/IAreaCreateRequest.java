/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.area.request;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.sitewhere.spi.common.ILocation;

/**
 * Interface for arguments needed to create an area.
 * 
 * @author Derek
 */
public interface IAreaCreateRequest extends Serializable {

    /**
     * Area token. Can be set to null if token should be auto-generated.
     * 
     * @return
     */
    public String getToken();

    /**
     * Get token for corresponding area type.
     * 
     * @return
     */
    public String getAreaTypeToken();

    /**
     * Get token for parent area (null if none).
     * 
     * @return
     */
    public String getParentAreaToken();

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
     * Get list of coordinates that defines the area bounds.
     * 
     * @return
     */
    public List<? extends ILocation> getCoordinates();

    /**
     * Get metadata values.
     * 
     * @return
     */
    public Map<String, String> getMetadata();
}