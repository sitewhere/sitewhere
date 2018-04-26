/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event;

import java.io.Serializable;

/**
 * Event that captures a change of state (either requested or after the fact)
 * for a device.
 * 
 * @author Derek
 */
public interface IDeviceStateChange extends IDeviceEvent, Serializable {

    /**
     * Get category of state change.
     * 
     * @return
     */
    public String getCategory();

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