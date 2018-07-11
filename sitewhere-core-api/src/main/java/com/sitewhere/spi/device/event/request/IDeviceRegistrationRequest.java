/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event.request;

import com.sitewhere.spi.device.request.IDeviceCreateRequest;

/**
 * Interface for arguments needed to register a device.
 * 
 * @author Derek
 */
public interface IDeviceRegistrationRequest extends IDeviceCreateRequest {

    /**
     * Get token for customer to which device should be assigned.
     * 
     * @return
     */
    public String getCustomerToken();

    /**
     * Get token for area to which device should be assigned.
     * 
     * @return
     */
    public String getAreaToken();
}