/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.area.request;

import java.util.List;

import com.sitewhere.spi.common.IAccessible;
import com.sitewhere.spi.common.request.IPersistentEntityCreateRequest;

/**
 * Provides information needed to create an area type.
 * 
 * @author Derek
 */
public interface IAreaTypeCreateRequest extends IAccessible, IPersistentEntityCreateRequest {

    /**
     * Get icon shown for area type.
     * 
     * @return
     */
    public String getIcon();

    /**
     * Get list of area type tokens which may be contained.
     * 
     * @return
     */
    public List<String> getContainedAreaTypeTokens();
}