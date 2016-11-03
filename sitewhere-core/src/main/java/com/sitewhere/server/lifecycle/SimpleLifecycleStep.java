package com.sitewhere.server.lifecycle;

import com.sitewhere.spi.server.lifecycle.ILifecycleStep;

/**
 * Single operation lifeycle step with execution left abstract.
 * 
 * @author Derek
 */
public abstract class SimpleLifecycleStep implements ILifecycleStep {

    /** Step name */
    private String name;

    public SimpleLifecycleStep(String name) {
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
}