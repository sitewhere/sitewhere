/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.spi.client;

import com.sitewhere.grpc.client.MultitenantGrpcChannel;
import com.sitewhere.grpc.client.spi.multitenant.IMultitenantApiChannel;
import com.sitewhere.spi.device.IDeviceManagement;

/**
 * Provides an {@link IMultitenantApiChannel} that supplies the
 * {@link IDeviceManagement}. API.
 * 
 * @author Derek
 */
public interface IDeviceManagementApiChannel<T extends MultitenantGrpcChannel<?, ?>>
	extends IDeviceManagement, IMultitenantApiChannel<T> {
}