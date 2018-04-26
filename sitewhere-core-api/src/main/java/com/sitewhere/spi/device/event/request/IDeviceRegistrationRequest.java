/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event.request;

import java.io.Serializable;

import com.sitewhere.spi.common.IMetadataProvider;

/**
 * Interface for arguments needed to register a device.
 * 
 * @author Derek
 */
public interface IDeviceRegistrationRequest extends IMetadataProvider, Serializable {

    /**
     * Get device token to be registered.
     * 
     * @return
     */
    public String getDeviceToken();

    /**
     * Get token for device type token.
     * 
     * @return
     */
    public String getDeviceTypeToken();

    /**
     * Get token for area to which device should be assigned.
     * 
     * @return
     */
    public String getAreaToken();
}