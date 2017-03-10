package com.sitewhere.server;

import com.sitewhere.spi.server.IModelInitializer;

/**
 * Base class for model initializer implementations.
 * 
 * @author Derek
 */
public class ModelInitializer implements IModelInitializer {

    /** Indicates if initializer is enabled */
    private boolean enabled = true;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.IModelInitializer#isEnabled()
     */
    @Override
    public boolean isEnabled() {
	return enabled;
    }

    public void setEnabled(boolean enabled) {
	this.enabled = enabled;
    }
}
