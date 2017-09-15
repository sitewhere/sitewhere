package com.sitewhere.microservice.spi.configuration;

/**
 * Enumerates states for process of loading configuration.
 * 
 * @author Derek
 */
public enum ConfigurationState {

    /** Configuration has not started */
    NotStarted,

    /** Configuration loading */
    Loading,

    /** Configuration failed */
    Failed,

    /** Configuration succeeded */
    Succeeded;
}