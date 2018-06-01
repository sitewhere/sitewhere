/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.server.lifecycle;

import org.slf4j.Logger;

import com.sitewhere.spi.microservice.logging.LogLevel;

/**
 * Logging interface which allows lifecycle component metadata to be captured
 * along with other logging information.
 * 
 * @author Derek
 */
public interface ILifecycleComponentLogger extends Logger {

    /**
     * Get lifeycle component associated with logger.
     * 
     * @return
     */
    public ILifecycleComponent getLifecycleComponent();

    /**
     * Any log output below this level for the lifecycle component will be elevated
     * to this level.
     * 
     * @param level
     */
    public void setLogLevelOverride(LogLevel level);
}