/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.server.lifecycle;

/**
 * Lifecycle status for component.
 * 
 * @author Derek
 */
public enum LifecycleStatus {

    /** Component is initializing */
    Initializing,

    /** Component is stopped */
    Stopped,

    /** Component is starting */
    Starting,

    /** Component is started */
    Started,

    /** Component is pausing */
    Pausing,

    /** Component is paused */
    Paused,

    /** Component is stopping */
    Stopping,

    /** Component is terminating */
    Terminating,

    /** Component is terminated */
    Terminated,

    /** Component startup failed with an error */
    Error;
}