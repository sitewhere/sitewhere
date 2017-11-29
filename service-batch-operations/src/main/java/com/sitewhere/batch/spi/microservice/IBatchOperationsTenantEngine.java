/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.spi.microservice;

import com.sitewhere.grpc.service.BatchManagementGrpc;
import com.sitewhere.spi.batch.IBatchManagement;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;

/**
 * Extends {@link IMicroserviceTenantEngine} with features specific to batch
 * operations.
 * 
 * @author Derek
 */
public interface IBatchOperationsTenantEngine extends IMicroserviceTenantEngine {

    /**
     * Get batch management implementation.
     * 
     * @return
     */
    public IBatchManagement getBatchManagement();

    /**
     * Get implementation class that wraps batch management with GRPC conversions.
     * 
     * @return
     */
    public BatchManagementGrpc.BatchManagementImplBase getBatchManagementImpl();
}