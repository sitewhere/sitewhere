/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.request;

import java.util.List;

import com.sitewhere.spi.common.IAccessible;
import com.sitewhere.spi.common.IImageProvider;
import com.sitewhere.spi.common.request.IPersistentEntityCreateRequest;

/**
 * Interface for arguments needed to create a device group.
 * 
 * @author Derek
 */
public interface IDeviceGroupCreateRequest extends IAccessible, IImageProvider, IPersistentEntityCreateRequest {

    /**
     * Get list of roles associated with group.
     * 
     * @return
     */
    public List<String> getRoles();
}