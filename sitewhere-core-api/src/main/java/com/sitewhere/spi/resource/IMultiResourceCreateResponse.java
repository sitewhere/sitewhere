/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.resource;

import java.util.List;

/**
 * Indicates status of creating multiple resources.
 * 
 * @author Derek
 */
public interface IMultiResourceCreateResponse {

    /**
     * Get list of created resources.
     * 
     * @return
     */
    public List<IResource> getCreatedResources();

    /**
     * Get list of errors creating resources.
     * 
     * @return
     */
    public List<IResourceCreateError> getErrors();
}