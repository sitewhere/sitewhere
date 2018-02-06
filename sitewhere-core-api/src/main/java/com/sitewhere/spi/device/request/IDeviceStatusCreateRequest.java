/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.request;

import com.sitewhere.spi.common.IMetadataProvider;

/**
 * Information needed to create a device status.
 * 
 * @author Derek
 */
public interface IDeviceStatusCreateRequest extends IMetadataProvider {

    /**
     * Get the unique status code.
     * 
     * @return
     */
    public String getCode();

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
     * Border color for user interface.
     * 
     * @return
     */
    public String getBorderColor();

    /**
     * Icon for user interface.
     * 
     * @return
     */
    public String getIcon();
}