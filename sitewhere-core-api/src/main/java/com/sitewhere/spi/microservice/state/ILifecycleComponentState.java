/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.state;

import java.util.List;
import java.util.UUID;

import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Captures state information for a lifecycle component.
 */
public interface ILifecycleComponentState {

    /**
     * Get component id.
     * 
     * @return
     */
    public UUID getComponentId();

    /**
     * Get component name.
     * 
     * @return
     */
    public String getComponentName();

    /**
     * Get component status.
     * 
     * @return
     */
    public LifecycleStatus getStatus();

    /**
     * Get error stack.
     * 
     * @return
     */
    public List<String> getErrorStack();

    /**
     * Get state for child components.
     * 
     * @return
     */
    public List<? extends ILifecycleComponentState> getChildComponentStates();
}