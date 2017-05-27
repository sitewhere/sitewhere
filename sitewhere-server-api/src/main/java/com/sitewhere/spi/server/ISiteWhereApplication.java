/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.server;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.system.IVersion;

/**
 * Common interface implemented by SiteWhere applications.
 * 
 * @author Derek
 */
public interface ISiteWhereApplication {

    /**
     * Get SiteWhere server class managed by this application.
     * 
     * @return
     * @throws SiteWhereException
     */
    public Class<? extends ISiteWhereServer> getServerClass() throws SiteWhereException;

    /**
     * Get class that provides version information.
     * 
     * @return
     * @throws SiteWhereException
     */
    public Class<? extends IVersion> getVersionClass() throws SiteWhereException;

    /**
     * Creates progress monitor used when the application is started.
     * 
     * @return
     */
    public ILifecycleProgressMonitor createStartupProgressMonitor();
}