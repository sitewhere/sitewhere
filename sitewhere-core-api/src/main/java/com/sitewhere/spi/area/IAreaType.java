/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.area;

import java.util.List;
import java.util.UUID;

import com.sitewhere.spi.common.ISiteWhereEntity;

/**
 * Represents a domain-specific area type that can be used for building a
 * hierarchical area model.
 * 
 * @author Derek
 */
public interface IAreaType extends ISiteWhereEntity {

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

    /**
     * Get list of area type ids which may be contained.
     * 
     * @return
     */
    public List<UUID> getContainedAreaTypeIds();
}