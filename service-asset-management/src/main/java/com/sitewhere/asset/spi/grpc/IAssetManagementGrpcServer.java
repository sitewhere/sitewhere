/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.spi.grpc;

import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

/**
 * Binds to a port and listens for asset management GRPC requests.
 * 
 * @author Derek
 */
public interface IAssetManagementGrpcServer extends ITenantLifecycleComponent {
}