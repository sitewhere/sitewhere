/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.request;

import java.util.List;

import com.sitewhere.spi.device.group.GroupElementType;

/**
 * Interface for arguments needed to create a device group element.
 * 
 * @author Derek
 */
public interface IDeviceGroupElementCreateRequest {

    /**
     * Get the element type.
     * 
     * @return
     */
    public GroupElementType getType();

    /**
     * Get the element id (relative to element type).
     * 
     * @return
     */
    public String getElementId();

    /**
     * Get list of roles associated with element.
     * 
     * @return
     */
    public List<String> getRoles();
}