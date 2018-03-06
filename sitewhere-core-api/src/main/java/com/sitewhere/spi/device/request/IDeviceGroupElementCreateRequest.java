/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.request;

import java.util.List;
import java.util.UUID;

/**
 * Interface for arguments needed to create a device group element.
 * 
 * @author Derek
 */
public interface IDeviceGroupElementCreateRequest {

    /**
     * Get device id (null if nested group supplied).
     * 
     * @return
     */
    public UUID getDeviceId();

    /**
     * Get nested group id (null if device supplied).
     * 
     * @return
     */
    public UUID getNestedGroupId();

    /**
     * Get list of roles associated with element.
     * 
     * @return
     */
    public List<String> getRoles();
}