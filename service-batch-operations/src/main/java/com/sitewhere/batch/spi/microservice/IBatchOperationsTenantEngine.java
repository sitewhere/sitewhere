/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.batch.spi.microservice;

import com.sitewhere.batch.configuration.BatchOperationsTenantConfiguration;
import com.sitewhere.batch.spi.IBatchOperationManager;
import com.sitewhere.batch.spi.kafka.IFailedBatchElementsProducer;
import com.sitewhere.batch.spi.kafka.IUnprocessedBatchElementsProducer;
import com.sitewhere.batch.spi.kafka.IUnprocessedBatchOperationsProducer;
import com.sitewhere.grpc.service.BatchManagementGrpc;
import com.sitewhere.microservice.api.batch.IBatchManagement;
import com.sitewhere.rdb.spi.IRdbEntityManagerProvider;
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

    /**
     * Get provider which provides an RDB entity manager for this tenant.
     * 
     * @return
     */
    public IRdbEntityManagerProvider getRdbEntityManagerProvider();
}