/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.lifecycle;

import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleStep;

/**
 * Implementation of {@link ILifecycleStep} that represents a lifecycle
 * operation on a single component.
 */
public abstract class ComponentOperationLifecycleStep implements ILifecycleStep {

    /** Owner lifecycle component */
    private ILifecycleComponent owner;

    /** Nested lifecycle component */
    private ILifecycleComponent component;

    public ComponentOperationLifecycleStep(ILifecycleComponent owner, ILifecycleComponent component) {
	this.owner = owner;
	this.component = component;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleStep#getName()
     */
    @Override
    public String getName() {
	return getComponent().getComponentName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleStep#getOperationCount()
     */
    @Override
    public int getOperationCount() {
	return 1;
    }

    public ILifecycleComponent getOwner() {
	return owner;
    }

    public void setOwner(ILifecycleComponent owner) {
	this.owner = owner;
    }

    public ILifecycleComponent getComponent() {
	return component;
    }

    public void setComponent(ILifecycleComponent component) {
	this.component = component;
    }
}