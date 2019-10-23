/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.grpc.tenant;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.instance.spi.tenant.grpc.ITenantManagementGrpcServer;
import com.sitewhere.microservice.grpc.GrpcServer;
import com.sitewhere.spi.microservice.grpc.IGrpcSettings;
import com.sitewhere.spi.tenant.ITenantManagement;

/**
 * Hosts a GRPC server that handles tenant management requests.
 */
public class TenantManagementGrpcServer extends GrpcServer implements ITenantManagementGrpcServer {

    public TenantManagementGrpcServer(IInstanceManagementMicroservice<?> microservice,
	    ITenantManagement tenantManagement) {
	super(new TenantManagementImpl(microservice, tenantManagement), IGrpcSettings.DEFAULT_API_PORT,
		IGrpcSettings.DEFAULT_API_HEALTH_PORT);
    }
}