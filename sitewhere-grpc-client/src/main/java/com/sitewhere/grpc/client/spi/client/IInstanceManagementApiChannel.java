/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.spi.client;

import com.sitewhere.grpc.client.GrpcChannel;
import com.sitewhere.grpc.client.spi.IApiChannel;
import com.sitewhere.spi.instance.IInstanceManagement;

/**
 * Provides an {@link IApiChannel} that supplies the {@link IInstanceManagement}
 * API.
 */
public interface IInstanceManagementApiChannel<T extends GrpcChannel<?, ?>>
	extends IInstanceManagement, IApiChannel<T> {
}
