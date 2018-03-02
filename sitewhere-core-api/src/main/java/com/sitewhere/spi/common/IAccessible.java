/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.common;

/**
 * Interface for entities that describe themselves in a human-friendly way.
 * 
 * @author Derek
 */
public interface IAccessible {

    /**
     * Get the group name.
     * 
     * @return
     */
    public String getName();

    /**
     * Get the group description.
     * 
     * @return
     */
    public String getDescription();

    /**
     * Get URL for image.
     * 
     * @return
     */
    public String getImageUrl();
}