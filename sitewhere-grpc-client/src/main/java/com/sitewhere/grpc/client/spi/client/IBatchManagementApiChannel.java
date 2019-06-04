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
import com.sitewhere.spi.batch.IBatchManagement;

/**
 * Provides an {@link IMultitenantApiChannel} that supplies the
 * {@link IBatchManagement}. API.
 * 
 * @author Derek
 */
public interface IBatchManagementApiChannel<T extends MultitenantGrpcChannel<?, ?>>
	extends IBatchManagement, IMultitenantApiChannel<T> {
}