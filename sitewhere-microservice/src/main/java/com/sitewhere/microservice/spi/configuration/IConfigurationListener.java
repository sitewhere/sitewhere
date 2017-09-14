package com.sitewhere.microservice.spi.configuration;

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