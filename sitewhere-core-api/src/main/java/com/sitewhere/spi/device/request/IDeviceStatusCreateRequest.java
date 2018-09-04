/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.request;

import com.sitewhere.spi.common.IColorProvider;
import com.sitewhere.spi.common.IIconProvider;
import com.sitewhere.spi.common.request.IPersistentEntityCreateRequest;

/**
 * Information needed to create a device status.
 * 
 * @author Derek
 */
public interface IDeviceStatusCreateRequest extends IPersistentEntityCreateRequest, IColorProvider, IIconProvider {

    /**
     * Get token for device type command belongs to.
     * 
     * @return
     */
    public String getDeviceTypeToken();

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
}