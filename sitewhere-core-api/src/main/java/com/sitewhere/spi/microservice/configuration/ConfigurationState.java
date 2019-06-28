/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.configuration;

/**
 * Enumerates states for process of loading configuration.
 * 
 * @author Derek
 */
public enum ConfigurationState {

    /** Configuration has not been loaded */
    Unloaded,

    /** Configuration loading */
    Loading,

    /** Configuration has not started */
    Stopped,

    /** Configuration has been initialized */
    Initialized,

    /** Configuration failed */
    Failed,

    /** Configuration succeeded */
    Started,

    /** Configuration shutting down */
    Stopping;
}