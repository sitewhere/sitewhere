/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event.request;

/**
 * Request from a device to update its state in SiteWhere.
 * 
 * @author Derek
 */
public interface IDeviceStateChangeCreateRequest extends IDeviceEventCreateRequest {

    /** State change category for registration */
    public static final String ATTRIBUTE_REGISTRATION = "registration";

    /** State change category for device assignment */
    public static final String ATTRIBUTE_ASSIGNMENT = "assignment";

    /** State change category for presence management */
    public static final String ATTRIBUTE_PRESENCE = "presence";

    /**
     * Get attribute being changed.
     * 
     * @return
     */
    public String getAttribute();

    /**
     * Get type of state change.
     * 
     * @return
     */
    public String getType();

    /**
     * Get the previous (or assumed previous) state.
     * 
     * @return
     */
    public String getPreviousState();

    /**
     * Get the requested new state.
     * 
     * @return
     */
    public String getNewState();
}