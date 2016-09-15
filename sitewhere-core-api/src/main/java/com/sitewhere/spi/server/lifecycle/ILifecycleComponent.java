/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.server.lifecycle;

import java.util.List;

import org.apache.logging.log4j.Logger;

import com.sitewhere.spi.SiteWhereException;

/**
 * Lifecycle methods used in SiteWhere components.
 * 
 * @author Derek
 */
public interface ILifecycleComponent {

    /**
     * Get the unique component id.
     * 
     * @return
     */
    public String getComponentId();

    /**
     * Get human-readable name shown for component.
     * 
     * @return
     */
    public String getComponentName();

    /**
     * Get component type.
     * 
     * @return
     */
    public LifecycleComponentType getComponentType();

    /**
     * Get current lifecycle status.
     * 
     * @return
     */
    public LifecycleStatus getLifecycleStatus();

    /**
     * Gets the last lifecycle error that occurred.
     * 
     * @return
     */
    public SiteWhereException getLifecycleError();

    /**
     * Get the list of contained {@link ILifecycleComponent} elements.
     * 
     * @return
     */
    public List<ILifecycleComponent> getLifecycleComponents();

    /**
     * Starts the component while keeping up with lifecycle information.
     */
    public void lifecycleStart();

    /**
     * Start the component.
     * 
     * @throws SiteWhereException
     */
    public void start() throws SiteWhereException;

    /**
     * Pauses the component while keeping up with lifecycle information.
     */
    public void lifecyclePause();

    /**
     * Indicates to framework whether component can be paused.
     * 
     * @return
     * @throws SiteWhereException
     */
    public boolean canPause() throws SiteWhereException;

    /**
     * Pause the component.
     * 
     * @throws SiteWhereException
     */
    public void pause() throws SiteWhereException;

    /**
     * Stops the component while keeping up with lifecycle information.
     */
    public void lifecycleStop();

    /**
     * Stop the component.
     * 
     * @throws SiteWhereException
     */
    public void stop() throws SiteWhereException;

    /**
     * Find components (including this component and nested components) that are
     * of the given type.
     * 
     * @param type
     * @return
     * @throws SiteWhereException
     */
    public List<ILifecycleComponent> findComponentsOfType(LifecycleComponentType type) throws SiteWhereException;

    /**
     * Get component logger.
     * 
     * @return
     */
    public Logger getLogger();

    /**
     * Logs the state of this component and all nested components.
     */
    public void logState();
}