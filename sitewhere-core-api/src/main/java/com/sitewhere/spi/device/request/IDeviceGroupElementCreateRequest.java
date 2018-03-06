/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.request;

import java.util.List;

/**
 * Interface for arguments needed to create a device group element.
 * 
 * @author Derek
 */
public interface IDeviceGroupElementCreateRequest {

    /**
     * Get device token (null if nested group supplied).
     * 
     * @return
     */
    public String getDeviceToken();

    /**
     * Get nested group token (null if device supplied).
     * 
     * @return
     */
    public String getNestedGroupToken();

    /**
     * Get list of roles associated with element.
     * 
     * @return
     */
    public List<String> getRoles();
}