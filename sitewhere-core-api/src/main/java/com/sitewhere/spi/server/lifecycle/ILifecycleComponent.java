/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.server.lifecycle;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
     * Get map of contained {@link ILifecycleComponent} elements by unique id.
     * 
     * @return
     */
    public Map<String, ILifecycleComponent> getLifecycleComponents();

    /**
     * Initializes the component while keeping up with lifeycle information.
     * 
     * @param monitor
     */
    public void lifecycleInitialize(ILifecycleProgressMonitor monitor);

    /**
     * Initialize the component.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException;

    /**
     * Initialize a nested component.
     * 
     * @param component
     * @param monitor
     * @throws SiteWhereException
     */
    public void initializeNestedComponent(ILifecycleComponent component, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException;

    /**
     * Starts the component while keeping up with lifecycle information.
     */
    public void lifecycleStart(ILifecycleProgressMonitor monitor);

    /**
     * Start the component.
     * 
     * @throws SiteWhereException
     */
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException;

    /**
     * Start component as a nested lifecycle component.
     * 
     * @param component
     * @param monitor
     * @param errorMessage
     * @param require
     * @throws SiteWhereException
     */
    public void startNestedComponent(ILifecycleComponent component, ILifecycleProgressMonitor monitor,
	    String errorMessage, boolean require) throws SiteWhereException;

    /**
     * Pauses the component while keeping up with lifecycle information.
     */
    public void lifecyclePause(ILifecycleProgressMonitor monitor);

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
    public void pause(ILifecycleProgressMonitor monitor) throws SiteWhereException;

    /**
     * Stops the component while keeping up with lifecycle information.
     * 
     * @param monitor
     */
    public void lifecycleStop(ILifecycleProgressMonitor monitor);

    /**
     * Stops the component while keeping up with lifecycle information. This
     * version allows constraints to be passed to the operation.
     * 
     * @param monitor
     * @param constraints
     */
    public void lifecycleStop(ILifecycleProgressMonitor monitor, ILifecycleConstraints constraints);

    /**
     * Stop the component.
     * 
     * @throws SiteWhereException
     */
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException;

    /**
     * Stop the component. This version allows constraints to be passed to the
     * operation.
     * 
     * @throws SiteWhereException
     */
    public void stop(ILifecycleProgressMonitor monitor, ILifecycleConstraints constraints) throws SiteWhereException;

    /**
     * Terminates the component while keeping up with lifecycle information.
     * 
     * @param monitor
     */
    public void lifecycleTerminate(ILifecycleProgressMonitor monitor);

    /**
     * Terminate the component.
     * 
     * @throws SiteWhereException
     */
    public void terminate(ILifecycleProgressMonitor monitor) throws SiteWhereException;

    /**
     * Called to track component lifecycle changes.
     * 
     * @param before
     * @param after
     */
    public void lifecycleStatusChanged(LifecycleStatus before, LifecycleStatus after);

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
     * Get date the component was created.
     * 
     * @return
     */
    public Date getCreatedDate();

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