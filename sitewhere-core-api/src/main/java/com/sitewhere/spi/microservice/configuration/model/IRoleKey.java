/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.configuration.model;

/**
 * Unique key for a configuration role.
 */
public interface IRoleKey {

    /**
     * Get unique key.
     * 
     * @return
     */
    public String getId();
}