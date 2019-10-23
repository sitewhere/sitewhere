/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.configuration;

import java.util.List;

import io.sitewhere.k8s.crd.instance.SiteWhereInstance;

/**
 * Monitors instance configuration and notifies listeners of changes.
 */
public interface IInstanceConfigurationMonitor {

    /**
     * Get instance-wide configuration.
     * 
     * @return
     */
    public SiteWhereInstance getInstanceConfiguration();

    /**
     * Get list of listeners.
     * 
     * @return
     */
    public List<IInstanceConfigurationListener> getListeners();
}