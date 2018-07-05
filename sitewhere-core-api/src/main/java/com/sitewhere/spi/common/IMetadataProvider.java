/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.common;

import java.util.Map;

/**
 * Interface for an entity that has associated metadata.
 * 
 * @author dadams
 */
public interface IMetadataProvider {

    /**
     * Get a map of all metadata.
     * 
     * @return
     */
    public Map<String, String> getMetadata();
}