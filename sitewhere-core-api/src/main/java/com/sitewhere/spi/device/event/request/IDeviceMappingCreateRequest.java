/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event.request;

/**
 * Information needed to map a device to a slot on a composite device.
 * 
 * @author Derek
 */
public interface IDeviceMappingCreateRequest {

    /**
     * Get hardware id of composite device that will add device mapping.
     * 
     * @return
     */
    public String getCompositeDeviceHardwareId();

    /**
     * Get path in device element schema to be mapped.
     * 
     * @return
     */
    public String getMappingPath();
}