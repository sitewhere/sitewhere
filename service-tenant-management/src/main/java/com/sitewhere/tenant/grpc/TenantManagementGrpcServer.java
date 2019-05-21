/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.tenant.grpc;

import com.sitewhere.microservice.grpc.GrpcServer;
import com.sitewhere.spi.microservice.grpc.IGrpcSettings;
import com.sitewhere.spi.tenant.ITenantAdministration;
import com.sitewhere.spi.tenant.ITenantManagement;
import com.sitewhere.tenant.spi.grpc.ITenantManagementGrpcServer;
import com.sitewhere.tenant.spi.microservice.ITenantManagementMicroservice;

/**
 * Hosts a GRPC server that handles tenant management requests.
 * 
 * @author Derek
 */
public class TenantManagementGrpcServer extends GrpcServer implements ITenantManagementGrpcServer {

    public TenantManagementGrpcServer(ITenantManagementMicroservice<?> microservice, ITenantManagement tenantManagement,
	    ITenantAdministration tenantAdministration) {
	super(new TenantManagementImpl(microservice, tenantManagement, tenantAdministration),
		IGrpcSettings.DEFAULT_API_PORT, IGrpcSettings.DEFAULT_API_HEALTH_PORT);
    }
}