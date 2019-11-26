/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.microservice;

import com.sitewhere.batch.configuration.BatchOperationsTenantConfiguration;
import com.sitewhere.batch.kafka.FailedBatchElementsProducer;
import com.sitewhere.batch.kafka.UnprocessedBatchElementsProducer;
import com.sitewhere.batch.kafka.UnprocessedBatchOperationsProducer;
import com.sitewhere.batch.spi.IBatchOperationManager;
import com.sitewhere.batch.spi.kafka.IFailedBatchElementsProducer;
import com.sitewhere.batch.spi.kafka.IUnprocessedBatchElementsProducer;
import com.sitewhere.batch.spi.kafka.IUnprocessedBatchOperationsProducer;
import com.sitewhere.batch.spi.microservice.IBatchOperationsTenantEngine;
import com.sitewhere.grpc.service.BatchManagementGrpc;
import com.sitewhere.microservice.api.batch.IBatchManagement;
import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.multitenant.MicroserviceTenantEngine;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.tenant.ITenant;

import io.sitewhere.k8s.crd.tenant.engine.dataset.TenantEngineDatasetTemplate;

/**
 * Implementation of {@link IMicroserviceTenantEngine} that implements batch
 * operations functionality.
 */
public class BatchOperationsTenantEngine extends MicroserviceTenantEngine<BatchOperationsTenantConfiguration>
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

    public BatchOperationsTenantEngine(ITenant tenant) {
	super(tenant);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantInitialize(com.sitewhere.spi.microservice.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// // Create management interfaces.
	// IBatchManagement implementation = (IBatchManagement) getModuleContext()
	// .getBean(BatchManagementBeans.BEAN_BATCH_MANAGEMENT);
	// this.batchManagement = new BatchManagementTriggers(implementation);
	// this.batchManagementImpl = new
	// BatchManagementImpl((IBatchOperationsMicroservice) getMicroservice(),
	// getBatchManagement());
	//
	// // Load configured batch operation manager.
	// this.batchOperationManager = (IBatchOperationManager) getModuleContext()
	// .getBean(BatchManagementBeans.BEAN_BATCH_OPERATION_MANAGER);

	// Create Kafka components.
	this.unprocessedBatchOperationsProducer = new UnprocessedBatchOperationsProducer();
	this.unprocessedBatchElementsProducer = new UnprocessedBatchElementsProducer();
	this.failedBatchElementsProducer = new FailedBatchElementsProducer();

	// Create step that will initialize components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getComponentName());

	// Initialize discoverable lifecycle components.
	init.addStep(initializeDiscoverableBeans(getModuleContext()));

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
	// Create step that will start components.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getComponentName());

	// Start discoverable lifecycle components.
	start.addStep(startDiscoverableBeans(getModuleContext()));

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
     * tenantBootstrap(io.sitewhere.k8s.crd.tenant.engine.dataset.
     * TenantEngineDatasetTemplate,
     * com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void tenantBootstrap(TenantEngineDatasetTemplate template, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException {
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantStop(com.sitewhere.spi.microservice.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantStop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
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

	// Stop discoverable lifecycle components.
	stop.addStep(stopDiscoverableBeans(getModuleContext()));

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