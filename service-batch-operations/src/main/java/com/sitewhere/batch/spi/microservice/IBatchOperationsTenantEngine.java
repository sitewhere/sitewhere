/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.spi.microservice;

import com.sitewhere.batch.configuration.BatchOperationsTenantConfiguration;
import com.sitewhere.batch.spi.IBatchOperationManager;
import com.sitewhere.batch.spi.kafka.IFailedBatchElementsProducer;
import com.sitewhere.batch.spi.kafka.IUnprocessedBatchElementsProducer;
import com.sitewhere.batch.spi.kafka.IUnprocessedBatchOperationsProducer;
import com.sitewhere.grpc.service.BatchManagementGrpc;
import com.sitewhere.microservice.api.batch.IBatchManagement;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;

/**
 * Extends {@link IMicroserviceTenantEngine} with features specific to batch
 * operations.
 */
public interface IBatchOperationsTenantEngine extends IMicroserviceTenantEngine<BatchOperationsTenantConfiguration> {

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

    /**
     * Get batch operation manager.
     * 
     * @return
     */
    public IBatchOperationManager getBatchOperationManager();

    /**
     * Get Kafka producer for unprocessed batch operations.
     * 
     * @return
     */
    public IUnprocessedBatchOperationsProducer getUnprocessedBatchOperationsProducer();

    /**
     * Get Kafka producer for unprocessed batch elements.
     * 
     * @return
     */
    public IUnprocessedBatchElementsProducer getUnprocessedBatchElementsProducer();

    /**
     * Get Kafka producer for failed batch elements.
     * 
     * @return
     */
    public IFailedBatchElementsProducer getFailedBatchElementsProducer();
}