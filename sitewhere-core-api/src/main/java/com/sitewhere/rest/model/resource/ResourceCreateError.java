/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.resource;

import com.sitewhere.spi.resource.IResourceCreateError;
import com.sitewhere.spi.resource.ResourceCreateFailReason;

/**
 * Model implementation for {@link IResourceCreateError}.
 * 
 * @author Derek
 */
public class ResourceCreateError implements IResourceCreateError {

    /** Path that failed */
    private String path;

    /** Reason for failure */
    private ResourceCreateFailReason reason;

    public String getPath() {
	return path;
    }

    public void setPath(String path) {
	this.path = path;
    }

    public ResourceCreateFailReason getReason() {
	return reason;
    }

    public void setReason(ResourceCreateFailReason reason) {
	this.reason = reason;
    }
}