/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device;

/**
 * Indicates a possible status for a device. A device status is tied to a device
 * specification and can be used for customizing user interfaces.
 * 
 * @author Derek
 */
public interface IDeviceStatus {

    /**
     * Get the unique status code.
     * 
     * @return
     */
    public String getCode();

    /**
     * Get token for the parent specification.
     * 
     * @return
     */
    public String getSpecificationToken();

    /**
     * Name displayed in user interface.
     * 
     * @return
     */
    public String getName();

    /**
     * Background color for user interface.
     * 
     * @return
     */
    public String getBackgroundColor();

    /**
     * Foreground color for user interface.
     * 
     * @return
     */
    public String getForegroundColor();

    /**
     * Icon for user interface.
     * 
     * @return
     */
    public String getIcon();
}