/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.common;

import java.util.Map;

import com.sitewhere.spi.SiteWhereException;

/**
 * Interface for an entity that has associated metadata.
 * 
 * @author dadams
 */
public interface IMetadataProvider {

    /**
     * Add or replace a metadata field.
     * 
     * @param name
     * @param value
     */
    public void addOrReplaceMetadata(String name, String value) throws SiteWhereException;

    /**
     * Remove a metadata field.
     * 
     * @param name
     * @return
     */
    public String removeMetadata(String name);

    /**
     * Get value of metadata field.
     * 
     * @param name
     * @return
     */
    public String getMetadata(String name);

    /**
     * Get a map of all metadata.
     * 
     * @return
     */
    public Map<String, String> getMetadata();

    /**
     * Clear existing metadata.
     */
    public void clearMetadata();
}