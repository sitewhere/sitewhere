/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.resource;

import com.sitewhere.spi.resource.IResource;
import com.sitewhere.spi.resource.ResourceType;

/**
 * Model implementation of {@link IResource}.
 * 
 * @author Derek
 */
public class Resource implements IResource {

    /** Resource path */
    private String path;

    /** Resource type */
    private ResourceType resourceType;

    /** Resource content */
    private byte[] content;

    /** Last modified date */
    private long lastModified;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.resource.IResource#getPath()
     */
    public String getPath() {
	return path;
    }

    public void setPath(String path) {
	this.path = path;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.resource.IResource#getResourceType()
     */
    public ResourceType getResourceType() {
	return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
	this.resourceType = resourceType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.resource.IResource#getContent()
     */
    public byte[] getContent() {
	return content;
    }

    public void setContent(byte[] content) {
	this.content = content;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.resource.IResource#getLastModified()
     */
    public long getLastModified() {
	return lastModified;
    }

    public void setLastModified(long lastModified) {
	this.lastModified = lastModified;
    }
}