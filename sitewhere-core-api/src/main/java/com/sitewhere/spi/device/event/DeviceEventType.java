/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event;

/**
 * Indicates event type.
 * 
 * @author Derek
 */
public enum DeviceEventType {

    /** Measurement */
    Measurement,

    /** Geospatial location */
    Location,

    /** Exception condtion alert */
    Alert,

    /** Invocation of device command */
    CommandInvocation,

    /** Response to device command invocation */
    CommandResponse,

    /** Device state change */
    StateChange,
}