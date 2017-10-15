/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.spi.microservice;

import com.sitewhere.microservice.spi.multitenant.IMultitenantMicroservice;

/**
 * Microservice that provides device management functionality.
 * 
 * @author Derek
 */
public interface IDeviceManagementMicroservice extends IMultitenantMicroservice<IDeviceManagementTenantEngine> {
}