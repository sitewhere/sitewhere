/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.resource;

/**
 * Base interface for persistent system resources such as configuration files
 * and scripts.
 * 
 * @author Derek
 */
public interface IResource {

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

    /**
     * Get last modified date for resource.
     * 
     * @return
     */
    public long getLastModified();
}