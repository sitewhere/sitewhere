/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.request;

import java.io.Serializable;
import java.util.Map;

import com.sitewhere.spi.common.IAccessible;
import com.sitewhere.spi.device.DeviceContainerPolicy;
import com.sitewhere.spi.device.element.IDeviceElementSchema;

/**
 * Interface for arguments needed to create a device type.
 * 
 * @author Derek
 */
public interface IDeviceTypeCreateRequest extends IAccessible, Serializable {

    /**
     * Allows the type token to be specified. (Optional)
     * 
     * @return
     */
    public String getToken();

    /**
     * Get container policy.
     * 
     * @return
     */
    public DeviceContainerPolicy getContainerPolicy();

    /**
     * Get {@link IDeviceElementSchema} for locating nested devices.
     * 
     * @return
     */
    public IDeviceElementSchema getDeviceElementSchema();

    /**
     * Get metadata values.
     * 
     * @return
     */
    public Map<String, String> getMetadata();
}