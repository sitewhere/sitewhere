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
package com.sitewhere.batch.microservice;

import com.sitewhere.batch.BatchManagementTriggers;
import com.sitewhere.batch.configuration.BatchOperationsTenantConfiguration;
import com.sitewhere.batch.configuration.BatchOperationsTenantEngineModule;
import com.sitewhere.batch.grpc.BatchManagementImpl;
import com.sitewhere.batch.kafka.FailedBatchElementsProducer;
import com.sitewhere.batch.kafka.UnprocessedBatchElementsProducer;
import com.sitewhere.batch.kafka.UnprocessedBatchOperationsProducer;
import com.sitewhere.batch.spi.IBatchOperationManager;
import com.sitewhere.batch.spi.kafka.IFailedBatchElementsProducer;
import com.sitewhere.batch.spi.kafka.IUnprocessedBatchElementsProducer;
import com.sitewhere.batch.spi.kafka.IUnprocessedBatchOperationsProducer;
import com.sitewhere.batch.spi.microservice.IBatchOperationsMicroservice;
import com.sitewhere.batch.spi.microservice.IBatchOperationsTenantEngine;
import com.sitewhere.grpc.service.BatchManagementGrpc;
import com.sitewhere.microservice.api.batch.IBatchManagement;
import com.sitewhere.microservice.datastore.DatastoreDefinition;
import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.rdb.RdbPersistenceOptions;
import com.sitewhere.rdb.RdbTenantEngine;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineModule;

import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;

/**
 * Implementation of {@link IMicroserviceTenantEngine} that implements batch
 * operations functionality.
 */
public class BatchOperationsTenantEngine extends RdbTenantEngine<BatchOperationsTenantConfiguration>
	implements IBatchOperationsTenantEngine {

    /** Batch management persistence implementation */
    private IBatchManagement batchManagement;

    /** Responds to batch management GRPC requests */
    private BatchManagementGrpc.BatchManagementImplBase batchManagementImpl;

    /** Batch operation manager */
    private IBatchOperationManager batchOperationManager;

    /** Unprocessed batch operations producer */
    private IUnprocessedBatchOperationsProducer unprocessedBatchOperationsProducer;

    /** Unprocessed batch elements producer */
    private IUnprocessedBatchElementsProducer unprocessedBatchElementsProducer;

    /** Failed batch elements producer */
    private IFailedBatchElementsProducer failedBatchElementsProducer;

    public BatchOperationsTenantEngine(SiteWhereTenantEngine engine) {
	super(engine);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getConfigurationClass()
     */
    @Override
    public Class<BatchOperationsTenantConfiguration> getConfigurationClass() {
	return BatchOperationsTenantConfiguration.class;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * createConfigurationModule()
     */
    @Override
    public ITenantEngineModule<BatchOperationsTenantConfiguration> createConfigurationModule() {
	return new BatchOperationsTenantEngineModule(this, getActiveConfiguration());
    }

    /*
     * @see com.sitewhere.rdb.spi.IRdbTenantEngine#getDatastoreDefinition()
     */
    @Override
    public DatastoreDefinition getDatastoreDefinition() {
	return getActiveConfiguration().getDatastore();
    }

    /*
     * @see com.sitewhere.rdb.spi.IRdbTenantEngine#getEntitiesBasePackage()
     */
    @Override
    public String getEntitiesBasePackage() {
	return "com.sitewhere.batch.persistence.rdb.entity";
    }

    /*
     * @see com.sitewhere.rdb.RdbTenantEngine#getPersistenceOptions()
     */
    @Override
    public RdbPersistenceOptions getPersistenceOptions() {
	RdbPersistenceOptions options = new RdbPersistenceOptions();
	// options.setHbmToDdlAuto("update");
	return options;
    }

    /*
     * @see com.sitewhere.microservice.multitenant.MicroserviceTenantEngine#
     * loadEngineComponents()
     */
    @Override
    public void loadEngineComponents() throws SiteWhereException {
	IBatchManagement implementation = getInjector().getInstance(IBatchManagement.class);
	this.batchManagement = new BatchManagementTriggers(implementation);
	this.batchManagementImpl = new BatchManagementImpl((IBatchOperationsMicroservice) getMicroservice(),
		getBatchManagement());
	this.batchOperationManager = getInjector().getInstance(IBatchOperationManager.class);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantInitialize(com.sitewhere.spi.microservice.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.tenantInitialize(monitor);

	// Create Kafka components.
	this.unprocessedBatchOperationsProducer = new UnprocessedBatchOperationsProducer();
	this.unprocessedBatchElementsProducer = new UnprocessedBatchElementsProducer();
	this.failedBatchElementsProducer = new FailedBatchElementsProducer();

	// Create step that will initialize components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getComponentName());

	// Initialize batch management persistence.
	init.addInitializeStep(this, getBatchManagement(), true);

	// Initialize batch operation manager.
	init.addInitializeStep(this, getBatchOperationManager(), true);

	// Initialize unprocessed batch operations producer.
	init.addInitializeStep(this, getUnprocessedBatchOperationsProducer(), true);

	// Initialize unprocessed batch elements producer.
	init.addInitializeStep(this, getUnprocessedBatchElementsProducer(), true);

	// Initialize failed batch elements producer.
	init.addInitializeStep(this, getFailedBatchElementsProducer(), true);

	// Execute initialization steps.
	init.execute(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantStart(com.sitewhere.spi.microservice.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantStart(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.tenantStart(monitor);

	// Create step that will start components.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getComponentName());

	// Start batch management persistence.
	start.addStartStep(this, getBatchManagement(), true);

	// Start batch operation manager.
	start.addStartStep(this, getBatchOperationManager(), true);

	// Start unprocessed batch operations producer.
	start.addStartStep(this, getUnprocessedBatchOperationsProducer(), true);

	// Start unprocessed batch elements producer.
	start.addStartStep(this, getUnprocessedBatchElementsProducer(), true);

	// Start failed batch elements producer.
	start.addStartStep(this, getFailedBatchElementsProducer(), true);

	// Execute startup steps.
	start.execute(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantStop(com.sitewhere.spi.microservice.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantStop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.tenantStop(monitor);

	// Create step that will stop components.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getComponentName());

	// Stop failed batch elements producer.
	stop.addStopStep(this, getFailedBatchElementsProducer());

	// Stop unprocessed batch elements producer.
	stop.addStopStep(this, getUnprocessedBatchElementsProducer());

	// Stop unprocessed batch operations producer.
	stop.addStopStep(this, getUnprocessedBatchOperationsProducer());

	// Stop batch operation manager.
	stop.addStopStep(this, getBatchOperationManager());

	// Stop batch management persistence.
	stop.addStopStep(this, getBatchManagement());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /*
     * @see com.sitewhere.batch.spi.microservice.IBatchOperationsTenantEngine#
     * getBatchManagement()
     */
    @Override
    public IBatchManagement getBatchManagement() {
	return batchManagement;
    }

    public void setBatchManagement(IBatchManagement batchManagement) {
	this.batchManagement = batchManagement;
    }

    /*
     * @see com.sitewhere.batch.spi.microservice.IBatchOperationsTenantEngine#
     * getBatchManagementImpl()
     */
    @Override
    public BatchManagementGrpc.BatchManagementImplBase getBatchManagementImpl() {
	return batchManagementImpl;
    }

    public void setBatchManagementImpl(BatchManagementGrpc.BatchManagementImplBase batchManagementImpl) {
	this.batchManagementImpl = batchManagementImpl;
    }

    /*
     * @see com.sitewhere.batch.spi.microservice.IBatchOperationsTenantEngine#
     * getBatchOperationManager()
     */
    @Override
    public IBatchOperationManager getBatchOperationManager() {
	return batchOperationManager;
    }

    public void setBatchOperationManager(IBatchOperationManager batchOperationManager) {
	this.batchOperationManager = batchOperationManager;
    }

    /*
     * @see com.sitewhere.batch.spi.microservice.IBatchOperationsTenantEngine#
     * getUnprocessedBatchOperationsProducer()
     */
    @Override
    public IUnprocessedBatchOperationsProducer getUnprocessedBatchOperationsProducer() {
	return unprocessedBatchOperationsProducer;
    }

    public void setUnprocessedBatchOperationsProducer(
	    IUnprocessedBatchOperationsProducer unprocessedBatchOperationsProducer) {
	this.unprocessedBatchOperationsProducer = unprocessedBatchOperationsProducer;
    }

    /*
     * @see com.sitewhere.batch.spi.microservice.IBatchOperationsTenantEngine#
     * getUnprocessedBatchElementsProducer()
     */
    @Override
    public IUnprocessedBatchElementsProducer getUnprocessedBatchElementsProducer() {
	return unprocessedBatchElementsProducer;
    }

    public void setUnprocessedBatchElementsProducer(
	    IUnprocessedBatchElementsProducer unprocessedBatchElementsProducer) {
	this.unprocessedBatchElementsProducer = unprocessedBatchElementsProducer;
    }

    /*
     * @see com.sitewhere.batch.spi.microservice.IBatchOperationsTenantEngine#
     * getFailedBatchElementsProducer()
     */
    @Override
    public IFailedBatchElementsProducer getFailedBatchElementsProducer() {
	return failedBatchElementsProducer;
    }

    public void setFailedBatchElementsProducer(IFailedBatchElementsProducer failedBatchElementsProducer) {
	this.failedBatchElementsProducer = failedBatchElementsProducer;
    }
}