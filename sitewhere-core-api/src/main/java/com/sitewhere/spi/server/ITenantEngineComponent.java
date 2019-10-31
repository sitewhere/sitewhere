/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.server;

import java.io.Serializable;

import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Interface for tenant engine components.
 */
public interface ITenantEngineComponent extends Serializable {

    /**
     * Get component id.
     * 
     * @return
     */
    public String getId();

    /**
     * Get component name.
     * 
     * @return
     */
    public String getName();

    /**
     * Get component type.
     * 
     * @return
     */
    public LifecycleComponentType getType();

    /**
     * Get component status.
     * 
     * @return
     */
    public LifecycleStatus getStatus();

    /**
     * Get parent component id.
     * 
     * @return
     */
    public String getParentId();
}