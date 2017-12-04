/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.state;

import java.util.List;

/**
 * Snapshot of instance topology at a given point in time.
 * 
 * @author Derek
 */
public interface IInstanceTopologySnapshot {

    /**
     * Get list of microservice topology entries.
     * 
     * @return
     */
    public List<IInstanceTopologyEntry> getTopologyEntries();
}