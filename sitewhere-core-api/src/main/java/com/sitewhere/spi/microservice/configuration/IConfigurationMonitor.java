/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.configuration;

import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Monitors configuration nodes in Zk and allows microservices to respond to
 * configuration changes.
 * 
 * @author Derek
 */
public interface IConfigurationMonitor extends ILifecycleComponent {

    /**
     * Get configuration data for the given path.
     * 
     * @param path
     * @return
     * @throws SiteWhereException
     */
    public byte[] getConfigurationDataFor(String path) throws SiteWhereException;

    /**
     * Get list of listeners.
     * 
     * @return
     */
    public List<IConfigurationListener> getListeners();
}