/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server;

import com.sitewhere.spi.server.IModelInitializer;

/**
 * Base class for model initializer implementations.
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
