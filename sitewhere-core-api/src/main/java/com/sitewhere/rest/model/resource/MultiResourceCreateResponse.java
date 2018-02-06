/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.resource;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.spi.resource.IMultiResourceCreateResponse;
import com.sitewhere.spi.resource.IResource;
import com.sitewhere.spi.resource.IResourceCreateError;

/**
 * Model implementation of {@link IMultiResourceCreateResponse}.
 * 
 * @author Derek
 */
public class MultiResourceCreateResponse implements IMultiResourceCreateResponse {

    /** List of resources that were created */
    private List<IResource> createdResources = new ArrayList<IResource>();

    /** List of errors in creating resources */
    private List<IResourceCreateError> errors = new ArrayList<IResourceCreateError>();

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.resource.IMultiResourceCreateResponse#
     * getCreatedResources()
     */
    public List<IResource> getCreatedResources() {
	return createdResources;
    }

    public void setCreatedResources(List<IResource> createdResources) {
	this.createdResources = createdResources;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.resource.IMultiResourceCreateResponse#getErrors()
     */
    public List<IResourceCreateError> getErrors() {
	return errors;
    }

    public void setErrors(List<IResourceCreateError> errors) {
	this.errors = errors;
    }
}