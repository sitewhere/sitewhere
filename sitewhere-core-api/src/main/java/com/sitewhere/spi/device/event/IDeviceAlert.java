/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event;

/**
 * Interface for an event from a device indiacating and exceptional condition.
 * 
 * @author dadams
 */
public interface IDeviceAlert extends IDeviceEvent {

    /**
     * Get source of the alert.
     * 
     * @return
     */
    public AlertSource getSource();

    /**
     * Get severity of alert.
     * 
     * @return
     */
    public AlertLevel getLevel();

    /**
     * Get the alert type indicator.
     * 
     * @return
     */
    public String getType();

    /**
     * Get the alert message.
     * 
     * @return
     */
    public String getMessage();
}