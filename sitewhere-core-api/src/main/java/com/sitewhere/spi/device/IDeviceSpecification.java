/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device;

import com.sitewhere.spi.common.IMetadataProviderEntity;
import com.sitewhere.spi.device.element.IDeviceElementSchema;

/**
 * Specifies details about a given type of device.
 * 
 * @author Derek
 */
public interface IDeviceSpecification extends IMetadataProviderEntity {

    /**
     * Get unique device specification token.
     * 
     * @return
     */
    public String getToken();

    /**
     * Get name that describes specification.
     * 
     * @return
     */
    public String getName();

    /**
     * Get asset module id.
     * 
     * @return
     */
    public String getAssetModuleId();

    /**
     * Get unique id within asset module.
     * 
     * @return
     */
    public String getAssetId();

    /**
     * Indicates whether this device contains other devices.
     * 
     * @return
     */
    public DeviceContainerPolicy getContainerPolicy();

    /**
     * Get schema that describes how nested devices are arranged.
     * 
     * @return
     */
    public IDeviceElementSchema getDeviceElementSchema();
}