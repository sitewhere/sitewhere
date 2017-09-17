/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.lifecycle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sitewhere.spi.ServerStartupException;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleConstraints;
import com.sitewhere.spi.server.lifecycle.ILifecycleHierarchyRoot;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Base class for implementing {@link ILifecycleComponent}.
 * 
 * @author Derek
 */
public abstract class LifecycleComponent implements ILifecycleComponent {

    /** Unique component id */
    private String componentId = UUID.randomUUID().toString();

    /** Component type */
    private LifecycleComponentType componentType;

    /** Date/time component was created */
    private Date createdDate = new Date();

    /** Lifecycle status indicator */
    private LifecycleStatus lifecycleStatus = LifecycleStatus.Stopped;

    /** Last error encountered in lifecycle operations */
    private SiteWhereException lifecycleError;

    /** Map of contained lifecycle components */
    private Map<String, ILifecycleComponent> lifecycleComponents = new HashMap<String, ILifecycleComponent>();

    public LifecycleComponent() {
	this(LifecycleComponentType.Other);
    }

    public LifecycleComponent(LifecycleComponentType type) {
	this.componentType = type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getComponentId()
     */
    public String getComponentId() {
	return componentId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getComponentName()
     */
    @Override
    public String getComponentName() {
	return getClass().getSimpleName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getComponentType()
     */
    public LifecycleComponentType getComponentType() {
	return componentType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getCreatedDate()
     */
    @Override
    public Date getCreatedDate() {
	return createdDate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#
     * lifecycleInitialize(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void lifecycleInitialize(ILifecycleProgressMonitor monitor) {
	try {
	    // Verify that component can be initialized.
	    if (!canInitialize()) {
		return;
	    }
	    setLifecycleStatus(LifecycleStatus.Initializing);
	    getLogger().info(getComponentName() + " state transitioned to INITIALIZING.");

	    initialize(monitor);
	    setLifecycleStatus(LifecycleStatus.Stopped);
	    getLogger().info(getComponentName() + " state transitioned to INITIALIZED.");
	} catch (SiteWhereException e) {
	    setLifecycleStatus(LifecycleStatus.InitializationError);
	    setLifecycleError(e);
	    getLogger().error(getComponentName() + " state transitioned to ERROR.", e);
	} catch (Throwable t) {
	    setLifecycleStatus(LifecycleStatus.InitializationError);
	    setLifecycleError(new SiteWhereException(t));
	    getLogger().error(getComponentName() + " state transitioned to ERROR.", t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#canInitialize()
     */
    @Override
    public boolean canInitialize() throws SiteWhereException {
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#initialize(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#
     * initializeNestedComponent(com.sitewhere.spi.server.lifecycle.
     * ILifecycleComponent,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor, boolean)
     */
    @Override
    public void initializeNestedComponent(ILifecycleComponent component, ILifecycleProgressMonitor monitor,
	    String errorMessage, boolean require) throws SiteWhereException {
	component.lifecycleInitialize(monitor);
	if (require) {
	    if (component.getLifecycleStatus() == LifecycleStatus.LifecycleError) {
		throw new ServerStartupException(component, errorMessage);
	    }
	}
	getLifecycleComponents().put(component.getComponentId(), component);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#lifecycleStart(com
     * .sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void lifecycleStart(ILifecycleProgressMonitor monitor) {
	try {
	    // Verify that component can be started.
	    if (!canStart()) {
		return;
	    }

	    LifecycleStatus old = getLifecycleStatus();
	    setLifecycleStatus(LifecycleStatus.Starting);
	    getLogger().info(getComponentName() + " state transitioned to STARTING.");

	    if (old != LifecycleStatus.Paused) {
		start(monitor);
	    }
	    setLifecycleStatus(LifecycleStatus.Started);
	    getLogger().info(getComponentName() + " state transitioned to STARTED.");
	} catch (SiteWhereException e) {
	    setLifecycleStatus(LifecycleStatus.LifecycleError);
	    setLifecycleError(e);
	    getLogger().error(getComponentName() + " state transitioned to ERROR.", e);
	} catch (Throwable t) {
	    setLifecycleStatus(LifecycleStatus.LifecycleError);
	    setLifecycleError(new SiteWhereException(t));
	    getLogger().error(getComponentName() + " state transitioned to ERROR.", t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#canStart()
     */
    @Override
    public boolean canStart() throws SiteWhereException {
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    /**
     * Starts a nested {@link ILifecycleComponent}. Uses default message.
     * 
     * @param component
     * @param monitor
     * @param require
     * @throws SiteWhereException
     */
    public void startNestedComponent(ILifecycleComponent component, ILifecycleProgressMonitor monitor, boolean require)
	    throws SiteWhereException {
	startNestedComponent(component, monitor, getComponentName() + " failed to start.", require);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#
     * startNestedComponent(com.sitewhere.spi.server.lifecycle.
     * ILifecycleComponent,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor,
     * java.lang.String, boolean)
     */
    public void startNestedComponent(ILifecycleComponent component, ILifecycleProgressMonitor monitor,
	    String errorMessage, boolean require) throws SiteWhereException {
	component.lifecycleStart(monitor);
	if (require) {
	    if (component.getLifecycleStatus() == LifecycleStatus.LifecycleError) {
		throw new ServerStartupException(component, errorMessage);
	    }
	}
	getLifecycleComponents().put(component.getComponentId(), component);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#lifecyclePause(com
     * .sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void lifecyclePause(ILifecycleProgressMonitor monitor) {
	setLifecycleStatus(LifecycleStatus.Pausing);
	getLogger().info(getComponentName() + " state transitioned to PAUSING.");
	try {
	    pause(monitor);
	    setLifecycleStatus(LifecycleStatus.Paused);
	    getLogger().info(getComponentName() + " state transitioned to PAUSED.");
	} catch (SiteWhereException e) {
	    setLifecycleStatus(LifecycleStatus.LifecycleError);
	    setLifecycleError(e);
	    getLogger().error(getComponentName() + " state transitioned to ERROR.", e);
	} catch (Throwable t) {
	    setLifecycleStatus(LifecycleStatus.LifecycleError);
	    setLifecycleError(new SiteWhereException(t));
	    getLogger().error(getComponentName() + " state transitioned to ERROR.", t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#pause(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void pause(ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#canPause()
     */
    public boolean canPause() throws SiteWhereException {
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#lifecycleStop(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void lifecycleStop(ILifecycleProgressMonitor monitor) {
	lifecycleStop(monitor, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#lifecycleStop(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor,
     * com.sitewhere.spi.server.lifecycle.ILifecycleConstraints)
     */
    @Override
    public void lifecycleStop(ILifecycleProgressMonitor monitor, ILifecycleConstraints constraints) {
	try {
	    // Verify that we are allowed to stop component.
	    if (!canStop()) {
		return;
	    }

	    setLifecycleStatus(LifecycleStatus.Stopping);
	    getLogger().info(getComponentName() + " state transitioned to STOPPING.");

	    if (constraints == null) {
		stop(monitor);
	    } else {
		stop(monitor, constraints);
	    }
	    setLifecycleStatus(LifecycleStatus.Stopped);
	    getLogger().info(getComponentName() + " state transitioned to STOPPED.");
	} catch (SiteWhereException e) {
	    setLifecycleStatus(LifecycleStatus.LifecycleError);
	    setLifecycleError(e);
	    getLogger().error(getComponentName() + " state transitioned to ERROR.", e);
	} catch (Throwable t) {
	    setLifecycleStatus(LifecycleStatus.LifecycleError);
	    setLifecycleError(new SiteWhereException(t));
	    getLogger().error(getComponentName() + " state transitioned to ERROR.", t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#canStop()
     */
    @Override
    public boolean canStop() throws SiteWhereException {
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop(com.sitewhere
     * .spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop(com.sitewhere
     * .spi.server.lifecycle.ILifecycleProgressMonitor,
     * com.sitewhere.spi.server.lifecycle.ILifecycleConstraints)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor, ILifecycleConstraints constraints) throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#lifecycleTerminate
     * (com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void lifecycleTerminate(ILifecycleProgressMonitor monitor) {
	setLifecycleStatus(LifecycleStatus.Terminating);
	getLogger().info(getComponentName() + " state transitioned to TERMINATING.");
	try {
	    terminate(monitor);
	    setLifecycleStatus(LifecycleStatus.Terminated);
	    getLogger().info(getComponentName() + " state transitioned to TERMINATED.");
	} catch (SiteWhereException e) {
	    setLifecycleStatus(LifecycleStatus.LifecycleError);
	    setLifecycleError(e);
	    getLogger().error(getComponentName() + " state transitioned to ERROR.", e);
	} catch (Throwable t) {
	    setLifecycleStatus(LifecycleStatus.LifecycleError);
	    setLifecycleError(new SiteWhereException(t));
	    getLogger().error(getComponentName() + " state transitioned to ERROR.", t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#terminate(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void terminate(ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#
     * lifecycleStatusChanged(com.sitewhere.spi.server.lifecycle.
     * LifecycleStatus, com.sitewhere.spi.server.lifecycle.LifecycleStatus)
     */
    @Override
    public void lifecycleStatusChanged(LifecycleStatus before, LifecycleStatus after) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#
     * findComponentsOfType(com
     * .sitewhere.spi.server.lifecycle.LifecycleComponentType)
     */
    @Override
    public List<ILifecycleComponent> findComponentsOfType(LifecycleComponentType type) throws SiteWhereException {
	List<ILifecycleComponent> matches = new ArrayList<ILifecycleComponent>();
	findComponentsOfType(this, matches, type);
	return matches;
    }

    /**
     * Recursive matching of nested components to find those of the given type.
     * 
     * @param current
     * @param matches
     * @param type
     * @throws SiteWhereException
     */
    public void findComponentsOfType(ILifecycleComponent current, List<ILifecycleComponent> matches,
	    LifecycleComponentType type) throws SiteWhereException {
	if (current.getComponentType() == type) {
	    matches.add(current);
	}
	for (ILifecycleComponent child : current.getLifecycleComponents().values()) {
	    findComponentsOfType(child, matches, type);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#logState()
     */
    public void logState() {
	getLogger().info("\n\n" + getComponentName() + " State:\n" + logState("", this) + "\n");
    }

    /**
     * Recursively log state for a component.
     * 
     * @param pad
     * @param component
     */
    protected String logState(String pad, ILifecycleComponent component) {
	String entry = "\n" + pad + "+ " + component.getComponentName() + " " + component.getLifecycleStatus();
	List<ILifecycleComponent> subcomponents = new ArrayList<ILifecycleComponent>(
		component.getLifecycleComponents().values());

	// Sort components by created date.
	Collections.sort(subcomponents, new Comparator<ILifecycleComponent>() {

	    @Override
	    public int compare(ILifecycleComponent o1, ILifecycleComponent o2) {
		return o1.getCreatedDate().compareTo(o2.getCreatedDate());
	    }
	});
	for (ILifecycleComponent subcomponent : subcomponents) {
	    entry = entry + logState("  " + pad, subcomponent);
	}
	return entry;
    }

    /**
     * Build a component map by recursively navigating the component tree.
     * 
     * @return
     */
    protected Map<String, ILifecycleComponent> buildComponentMap() {
	Map<String, ILifecycleComponent> map = new HashMap<String, ILifecycleComponent>();
	buildComponentMap(this, map);
	return map;
    }

    /**
     * Recursively navigates component structure and creates a map of components
     * by id.
     * 
     * @param current
     * @param map
     */
    protected static void buildComponentMap(ILifecycleComponent current, Map<String, ILifecycleComponent> map) {
	map.put(current.getComponentId(), current);
	for (ILifecycleComponent sub : current.getLifecycleComponents().values()) {
	    // Root components have a separate hierarchy.
	    if (!(sub instanceof ILifecycleHierarchyRoot)) {
		buildComponentMap(sub, map);
	    }
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLifecycleStatus
     * ()
     */
    public LifecycleStatus getLifecycleStatus() {
	return lifecycleStatus;
    }

    public void setLifecycleStatus(LifecycleStatus lifecycleStatus) {
	LifecycleStatus old = this.lifecycleStatus;
	this.lifecycleStatus = lifecycleStatus;

	if (old != lifecycleStatus) {
	    lifecycleStatusChanged(old, lifecycleStatus);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLifecycleError(
     * )
     */
    public SiteWhereException getLifecycleError() {
	return lifecycleError;
    }

    public void setLifecycleError(SiteWhereException lifecycleError) {
	this.lifecycleError = lifecycleError;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#
     * getLifecycleComponents()
     */
    public Map<String, ILifecycleComponent> getLifecycleComponents() {
	return lifecycleComponents;
    }

    public void setLifecycleComponents(Map<String, ILifecycleComponent> lifecycleComponents) {
	this.lifecycleComponents = lifecycleComponents;
    }
}