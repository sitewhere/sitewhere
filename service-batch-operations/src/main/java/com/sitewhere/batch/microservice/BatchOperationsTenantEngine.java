/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.microservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.batch.grpc.BatchManagementImpl;
import com.sitewhere.batch.spi.microservice.IBatchOperationsTenantEngine;
import com.sitewhere.grpc.service.BatchManagementGrpc;
import com.sitewhere.microservice.multitenant.MicroserviceTenantEngine;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.batch.IBatchManagement;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice;
import com.sitewhere.spi.microservice.multitenant.ITenantTemplate;
import com.sitewhere.spi.microservice.spring.BatchManagementBeans;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Implementation of {@link IMicroserviceTenantEngine} that implements batch
 * operations functionality.
 * 
 * @author Derek
 */
public class BatchOperationsTenantEngine extends MicroserviceTenantEngine implements IBatchOperationsTenantEngine {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Batch management persistence implementation */
    private IBatchManagement batchManagement;

    /** Responds to batch management GRPC requests */
    private BatchManagementGrpc.BatchManagementImplBase batchManagementImpl;

    public BatchOperationsTenantEngine(IMultitenantMicroservice<?> microservice, ITenant tenant) {
	super(microservice, tenant);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantInitialize(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create management interfaces.
	this.batchManagement = (IBatchManagement) getModuleContext()
		.getBean(BatchManagementBeans.BEAN_BATCH_MANAGEMENT);
	this.batchManagementImpl = new BatchManagementImpl(getBatchManagement());

	// Create step that will initialize components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getComponentName());

	// Initialize discoverable lifecycle components.
	init.addStep(getMicroservice().initializeDiscoverableBeans(getModuleContext(), monitor));

	// Initialize batch management persistence.
	init.addInitializeStep(this, getBatchManagement(), true);

	// Execute initialization steps.
	init.execute(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantStart(com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void tenantStart(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will start components.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getComponentName());

	// Start batch management persistence.
	start.addStartStep(this, getBatchManagement(), true);

	// Execute startup steps.
	start.execute(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantBootstrap(com.sitewhere.spi.microservice.multitenant.ITenantTemplate,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void tenantBootstrap(ITenantTemplate template, ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantStop(com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void tenantStop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will stop components.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getComponentName());

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
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }
}