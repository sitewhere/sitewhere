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
import java.util.UUID;

import org.slf4j.cal10n.LocLogger;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.state.ILifecycleComponentState;

/**
 * Lifecycle methods used in SiteWhere components.
 */
public interface ILifecycleComponent {

    /**
     * Get the unique component id.
     * 
     * @return
     */
    public UUID getComponentId();

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
     * Get microservice that owns the component.
     * 
     * @return
     */
    public IMicroservice<? extends IFunctionIdentifier> getMicroservice();

    /**
     * Set microservice that owns the component.
     * 
     * @param microservice
     */
    public void setMicroservice(IMicroservice<? extends IFunctionIdentifier> microservice);

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
     * Get list of parameters associated with component.
     * 
     * @return
     */
    public List<ILifecycleComponentParameter<?>> getParameters();

    /**
     * Overridden in subclasses to initialize parameters.
     * 
     * @throws SiteWhereException
     */
    public void initializeParameters() throws SiteWhereException;

    /**
     * Get map of contained {@link ILifecycleComponent} elements by unique id.
     * 
     * @return
     */
    public Map<UUID, ILifecycleComponent> getLifecycleComponents();

    /**
     * Provisions the component and any nested components.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    public void lifecycleProvision(ILifecycleProgressMonitor monitor) throws SiteWhereException;

    /**
     * Provision the component.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    public void provision(ILifecycleProgressMonitor monitor) throws SiteWhereException;

    /**
     * Initializes the component while keeping up with lifeycle information.
     * 
     * @param monitor
     */
    public void lifecycleInitialize(ILifecycleProgressMonitor monitor);

    /**
     * Indicates to framework whether component can be initialized.
     * 
     * @return
     * @throws SiteWhereException
     */
    public boolean canInitialize() throws SiteWhereException;

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
     * @param require
     * @throws SiteWhereException
     */
    public void initializeNestedComponent(ILifecycleComponent component, ILifecycleProgressMonitor monitor,
	    boolean require) throws SiteWhereException;

    /**
     * Starts the component while keeping up with lifecycle information.
     */
    public void lifecycleStart(ILifecycleProgressMonitor monitor);

    /**
     * Indicates to framework whether component can be started.
     * 
     * @return
     * @throws SiteWhereException
     */
    public boolean canStart() throws SiteWhereException;

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
     * @param require
     * @throws SiteWhereException
     */
    public void startNestedComponent(ILifecycleComponent component, ILifecycleProgressMonitor monitor, boolean require)
	    throws SiteWhereException;

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
     * Stops the component while keeping up with lifecycle information. This version
     * allows constraints to be passed to the operation.
     * 
     * @param monitor
     * @param constraints
     */
    public void lifecycleStop(ILifecycleProgressMonitor monitor, ILifecycleConstraints constraints);

    /**
     * Indicates to framework whether component can be stopped.
     * 
     * @return
     * @throws SiteWhereException
     */
    public boolean canStop() throws SiteWhereException;

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
     * Stop a nested lifecycle component.
     * 
     * @param component
     * @param monitor
     * @param require
     * @throws SiteWhereException
     */
    public void stopNestedComponent(ILifecycleComponent component, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException;

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
     * Find components (including this component and nested components) that are of
     * the given type.
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
    public LocLogger getLogger();

    /**
     * Captures the state of this component and nested components recursively.
     * 
     * @return
     */
    public ILifecycleComponentState getComponentState();
}