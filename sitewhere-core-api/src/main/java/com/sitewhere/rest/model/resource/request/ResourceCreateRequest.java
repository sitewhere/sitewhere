/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.resource.request;

import com.sitewhere.spi.resource.IResource;
import com.sitewhere.spi.resource.ResourceType;
import com.sitewhere.spi.resource.request.IResourceCreateRequest;

/**
 * Model implementation for {@link IResourceCreateRequest}.
 * 
 * @author Derek
 */
public class ResourceCreateRequest implements IResourceCreateRequest {

    /** Serial version UID */
    private static final long serialVersionUID = 9088874531782572049L;

    /** Resource path */
    private String path;

    /** Resource type */
    private ResourceType resourceType;

    /** Resource content */
    private byte[] content;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.resource.request.IResourceCreateRequest#getPath()
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
     * @see
     * com.sitewhere.spi.resource.request.IResourceCreateRequest#getResourceType
     * ()
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
     * @see
     * com.sitewhere.spi.resource.request.IResourceCreateRequest#getContent()
     */
    public byte[] getContent() {
	return content;
    }

    public void setContent(byte[] content) {
	this.content = content;
    }

    public static class Builder {

	/** Request being built */
	private ResourceCreateRequest request = new ResourceCreateRequest();

	public Builder(IResource existing) {
	    request.setResourceType(existing.getResourceType());
	    request.setPath(existing.getPath());
	    request.setContent(existing.getContent());
	}

	public ResourceCreateRequest build() {
	    return request;
	}
    }
}