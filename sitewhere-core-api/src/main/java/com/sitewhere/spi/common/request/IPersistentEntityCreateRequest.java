/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.common.request;

import java.io.Serializable;

import com.sitewhere.spi.common.IMetadataProvider;

/**
 * Contains base fields for creating a persistent entity.
 * 
 * @author Derek
 */
public interface IPersistentEntityCreateRequest extends Serializable, IMetadataProvider {

    /**
     * Get reference token.
     * 
     * @return
     */
    public String getToken();
}