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
	return new OutboundConnectorsTenantEngineModule(this, getActiveConfiguration());
    }

    /*
     * @see com.sitewhere.microservice.multitenant.MicroserviceTenantEngine#
     * loadEngineComponents()
     */
    @Override
    public void loadEngineComponents() throws SiteWhereException {
	this.outboundConnectorsManager = getInjector().getInstance(IOutboundConnectorsManager.class);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantInitialize(com.sitewhere.spi.microservice.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
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
}