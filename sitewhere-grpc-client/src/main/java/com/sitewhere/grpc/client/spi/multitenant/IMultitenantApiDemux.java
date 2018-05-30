/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.spi.multitenant;

import com.sitewhere.grpc.client.spi.IApiDemux;

/**
 * Adds behaviors specific to APIs that are provided at the tenant engine rather
 * than microservice level.
 * 
 * @author Derek
 */
public interface IMultitenantApiDemux<T extends IMultitenantApiChannel<?>> extends IApiDemux<T> {
}