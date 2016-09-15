/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.server;

import java.io.Serializable;
import java.util.List;

import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Contains runtime information about a tenant engine.
 * 
 * @author Derek
 */
public interface ISiteWhereTenantEngineState extends Serializable {

    /**
     * Get lifecycle status of tenant engine.
     * 
     * @return
     */
    public LifecycleStatus getLifecycleStatus();

    /**
     * Get state of tenant engine component hierarchy.
     * 
     * @return
     */
    public List<ITenantEngineComponent> getComponentHierarchyState();

    /**
     * Indicates if a new configuration has been staged, but not applied.
     * 
     * @return
     */
    public boolean isStaged();
}