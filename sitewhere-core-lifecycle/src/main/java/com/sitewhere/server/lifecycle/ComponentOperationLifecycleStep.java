package com.sitewhere.server.lifecycle;

import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleStep;

/**
 * Implementation of {@link ILifecycleStep} that represents a lifecycle
 * operation on a single component.
 * 
 * @author Derek
 */
public abstract class ComponentOperationLifecycleStep implements ILifecycleStep {

    /** Step name */
    private String name;

    /** Owner lifecycle component */
    private ILifecycleComponent owner;

    /** Nested lifecycle component */
    private ILifecycleComponent component;

    public ComponentOperationLifecycleStep(ILifecycleComponent owner, ILifecycleComponent component, String name) {
	this.owner = owner;
	this.component = component;
	this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleStep#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleStep#getOperationCount()
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