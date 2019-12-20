/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.microservice;

import com.sitewhere.connectors.configuration.OutboundConnectorsTenantConfiguration;
import com.sitewhere.connectors.configuration.OutboundConnectorsTenantEngineModule;
import com.sitewhere.connectors.spi.IOutboundConnectorsManager;
import com.sitewhere.connectors.spi.microservice.IOutboundConnectorsTenantEngine;
import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.multitenant.MicroserviceTenantEngine;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineModule;

import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;
import io.sitewhere.k8s.crd.tenant.engine.dataset.TenantEngineDatasetTemplate;

/**
 * Implementation of {@link IMicroserviceTenantEngine} that implements outbound
 * connector management.
 */
public class OutboundConnectorsTenantEngine extends MicroserviceTenantEngine<OutboundConnectorsTenantConfiguration>
	implements IOutboundConnectorsTenantEngine {

    /** Manages the outbound connectors for this tenant */
    private IOutboundConnectorsManager outboundConnectorsManager;

    public OutboundConnectorsTenantEngine(SiteWhereTenantEngine engine) {
	super(engine);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getConfigurationClass()
     */
    @Override
    public Class<OutboundConnectorsTenantConfiguration> getConfigurationClass() {
	return OutboundConnectorsTenantConfiguration.class;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * createConfigurationModule()
     */
    @Override
    public ITenantEngineModule<OutboundConnectorsTenantConfiguration> createConfigurationModule() {
	return new OutboundConnectorsTenantEngineModule(getActiveConfiguration());
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantInitialize(com.sitewhere.spi.microservice.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create outbound connectors manager.
	// this.outboundConnectorsManager = (IOutboundConnectorsManager)
	// getModuleContext()
	// .getBean(OutboundConnectorsBeans.BEAN_OUTBOUND_CONNECTORS_MANAGER);

	// Create step that will initialize components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getComponentName());

	// Initialize outbound connectors manager.
	init.addInitializeStep(this, getOutboundConnectorsManager(), true);

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

	// Start outbound connectors manager.
	start.addStartStep(this, getOutboundConnectorsManager(), true);

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
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Stop " + getComponentName());

	// Stop outbound connectors manager.
	start.addStopStep(this, getOutboundConnectorsManager());

	// Execute shutdown steps.
	start.execute(monitor);
    }

    /*
     * @see
     * com.sitewhere.connectors.spi.microservice.IOutboundConnectorsTenantEngine#
     * getOutboundConnectorsManager()
     */
    @Override
    public IOutboundConnectorsManager getOutboundConnectorsManager() {
	return outboundConnectorsManager;
    }

    public void setOutboundConnectorsManager(IOutboundConnectorsManager outboundConnectorsManager) {
	this.outboundConnectorsManager = outboundConnectorsManager;
    }
}