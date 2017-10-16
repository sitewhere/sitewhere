/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.resource.request;

import java.io.Serializable;

import com.sitewhere.spi.resource.ResourceType;

/**
 * Request for creating a new resource.
 * 
 * @author Derek
 */
public interface IResourceCreateRequest extends Serializable {

    /**
     * Get unique resource path.
     * 
     * @return
     */
    public String getPath();

    /**
     * Get type of resource.
     * 
     * @return
     */
    public ResourceType getResourceType();

    /**
     * Get resource content.
     * 
     * @return
     */
    public byte[] getContent();
}