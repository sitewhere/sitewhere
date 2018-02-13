/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.request;

import java.util.Map;

import com.sitewhere.spi.asset.IAssetReference;
import com.sitewhere.spi.device.DeviceAssignmentType;

/**
 * Interface for arguments needed to create a device assignment.
 * 
 * @author Derek
 */
public interface IDeviceAssignmentCreateRequest {

    /**
     * Get token for assignment. (Auto-assign if null).
     * 
     * @return
     */
    public String getToken();

    /**
     * Get the unique device hardware id.
     * 
     * @return
     */
    public String getDeviceHardwareId();

    /**
     * Get token of area where device is assigned.
     * 
     * @return
     */
    public String getAreaToken();

    /**
     * Get assignment type.
     * 
     * @return
     */
    public DeviceAssignmentType getAssignmentType();

    /**
     * Get asset reference.
     * 
     * @return
     */
    public IAssetReference getAssetReference();

    /**
     * Get metadata values.
     * 
     * @return
     */
    public Map<String, String> getMetadata();
}