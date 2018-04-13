/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.labels.microservice;

import com.sitewhere.grpc.service.LabelGenerationGrpc;
import com.sitewhere.labels.grpc.LabelGenerationImpl;
import com.sitewhere.labels.spi.microservice.ILabelGenerationTenantEngine;
import com.sitewhere.microservice.multitenant.MicroserviceTenantEngine;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.label.ILabelGeneratorManager;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice;
import com.sitewhere.spi.microservice.multitenant.ITenantTemplate;
import com.sitewhere.spi.microservice.spring.LabelGenerationBeans;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Implementation of {@link IMicroserviceTenantEngine} that implements label
 * generation functionality.
 * 
 * @author Derek
 */
public class LabelGenerationTenantEngine extends MicroserviceTenantEngine implements ILabelGenerationTenantEngine {

    /** Label generation implementation */
    private ILabelGeneratorManager labelGeneratorManager;

    /** Responds to label generation GRPC requests */
    private LabelGenerationGrpc.LabelGenerationImplBase labelGenerationImpl;

    public LabelGenerationTenantEngine(IMultitenantMicroservice<?> microservice, ITenant tenant) {
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
	labelGeneratorManager = (ILabelGeneratorManager) getModuleContext()
		.getBean(LabelGenerationBeans.BEAN_LABEL_GENERATOR_MANAGER);

	this.labelGenerationImpl = new LabelGenerationImpl(this);

	// Create step that will initialize components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getComponentName());

	// Initialize discoverable lifecycle components.
	init.addStep(initializeDiscoverableBeans(getModuleContext()));

	// Initialize label generator manager.
	init.addInitializeStep(this, getLabelGeneratorManager(), true);

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

	// Start discoverable lifecycle components.
	start.addStep(startDiscoverableBeans(getModuleContext()));

	// Start label generator manager.
	start.addStartStep(this, getLabelGeneratorManager(), true);

	// Execute startup steps.
	start.execute(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantBootstrap(com.sitewhere.spi.microservice.multitenant. ITenantTemplate,
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

	// Stop label generator manager.
	stop.addStopStep(this, getLabelGeneratorManager());

	// Stop discoverable lifecycle components.
	stop.addStep(stopDiscoverableBeans(getModuleContext()));

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /*
     * @see com.sitewhere.labels.spi.microservice.ILabelGenerationTenantEngine#
     * getLabelGeneratorManager()
     */
    @Override
    public ILabelGeneratorManager getLabelGeneratorManager() {
	return labelGeneratorManager;
    }

    public void setLabelGeneratorManager(ILabelGeneratorManager labelGeneratorManager) {
	this.labelGeneratorManager = labelGeneratorManager;
    }

    /*
     * @see com.sitewhere.labels.spi.microservice.ILabelGenerationTenantEngine#
     * getLabelGenerationImpl()
     */
    @Override
    public LabelGenerationGrpc.LabelGenerationImplBase getLabelGenerationImpl() {
	return labelGenerationImpl;
    }

    public void setLabelGenerationImpl(LabelGenerationGrpc.LabelGenerationImplBase labelGenerationImpl) {
	this.labelGenerationImpl = labelGenerationImpl;
    }
}