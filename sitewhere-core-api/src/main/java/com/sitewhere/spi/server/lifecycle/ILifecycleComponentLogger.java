/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.server.lifecycle;

import org.apache.commons.logging.Log;

/**
 * Logging interface which allows lifecycle component metadata to be captured
 * along with other logging information.
 * 
 * @author Derek
 */
public interface ILifecycleComponentLogger extends Log {

    /**
     * Get lifeycle component associated with logger.
     * 
     * @return
     */
    public ILifecycleComponent getLifecycleComponent();
}