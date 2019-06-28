/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.configuration;

/**
 * Listener for {@link IConfigurationMonitor} events.
 * 
 * @author Derek
 */
public interface IConfigurationListener {

    /**
     * Called when configuration cache is initialized.
     */
    public void onConfigurationCacheInitialized();

    /**
     * Called when a configuration file is added.
     * 
     * @param path
     * @param data
     */
    public void onConfigurationAdded(String path, byte[] data);

    /**
     * Called when a configuration file is updated.
     * 
     * @param path
     * @param data
     */
    public void onConfigurationUpdated(String path, byte[] data);

    /**
     * Called when a configuration file is deleted.
     * 
     * @param path
     */
    public void onConfigurationDeleted(String path);
}