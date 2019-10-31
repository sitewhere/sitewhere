/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.microservice.state;

import java.util.List;
import java.util.UUID;

import com.sitewhere.spi.microservice.state.ILifecycleComponentState;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Model implementation of {@link ILifecycleComponentState}.
 */
public class LifecycleComponentState implements ILifecycleComponentState {

    /** Component id */
    private UUID componentId;

    /** Component name */
    private String componentName;

    /** Component status */
    private LifecycleStatus status;

    /** Error stack */
    private List<String> errorStack;

    /** State for child components */
    private List<LifecycleComponentState> childComponentStates;

    /*
     * @see
     * com.sitewhere.spi.microservice.state.ILifecycleComponentState#getComponentId(
     * )
     */
    @Override
    public UUID getComponentId() {
	return componentId;
    }

    public void setComponentId(UUID componentId) {
	this.componentId = componentId;
    }

    /*
     * @see com.sitewhere.spi.microservice.state.ILifecycleComponentState#
     * getComponentName()
     */
    @Override
    public String getComponentName() {
	return componentName;
    }

    public void setComponentName(String componentName) {
	this.componentName = componentName;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.state.ILifecycleComponentState#getStatus()
     */
    @Override
    public LifecycleStatus getStatus() {
	return status;
    }

    public void setStatus(LifecycleStatus status) {
	this.status = status;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.state.ILifecycleComponentState#getErrorStack()
     */
    @Override
    public List<String> getErrorStack() {
	return errorStack;
    }

    public void setErrorStack(List<String> errorStack) {
	this.errorStack = errorStack;
    }

    /*
     * @see com.sitewhere.spi.microservice.state.ILifecycleComponentState#
     * getChildComponentStates()
     */
    @Override
    public List<LifecycleComponentState> getChildComponentStates() {
	return childComponentStates;
    }

    public void setChildComponentStates(List<LifecycleComponentState> childComponentStates) {
	this.childComponentStates = childComponentStates;
    }
}
