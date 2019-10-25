/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.scripting;

/**
 * Provides context used by an {@link IScriptSynchronizer} to properly
 * load/store script data.
 */
public interface IScriptContext {

    /**
     * Get base path (relative to root) where scripts are stored.
     * 
     * @return
     */
    String getBasePath();
}
