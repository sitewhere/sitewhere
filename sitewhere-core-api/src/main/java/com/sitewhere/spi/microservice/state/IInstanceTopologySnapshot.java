/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.state;

import java.util.Map;

/**
 * Snapshot of instance topology at a given point in time.
 */
public interface IInstanceTopologySnapshot {

    /**
     * Get map of topology entries by module identifier.
     * 
     * @return
     */
    public Map<String, IInstanceTopologyEntry> getTopologyEntriesByIdentifier();
}