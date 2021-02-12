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
package com.sitewhere.labels.microservice;

import com.sitewhere.grpc.service.LabelGenerationGrpc;
import com.sitewhere.labels.configuration.LabelGenerationTenantConfiguration;
import com.sitewhere.labels.configuration.LabelGenerationTenantEngineModule;
import com.sitewhere.labels.grpc.LabelGenerationImpl;
import com.sitewhere.labels.spi.manager.ILabelGeneratorManager;
import com.sitewhere.labels.spi.microservice.ILabelGenerationTenantEngine;
import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.multitenant.MicroserviceTenantEngine;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineModule;

import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;

/**
 * Implementation of {@link IMicroserviceTenantEngine} that implements label
 * generation functionality.
 */
public class LabelGenerationTenantEngine extends MicroserviceTenantEngine<LabelGenerationTenantConfiguration>
	implements ILabelGenerationTenantEngine {

    /** Label generation implementation */
    private ILabelGeneratorManager labelGeneratorManager;

    /** Responds to label generation GRPC requests */
    private LabelGenerationGrpc.LabelGenerationImplBase labelGenerationImpl;

    public LabelGenerationTenantEngine(SiteWhereTenantEngine engine) {
	super(engine);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getConfigurationClass()
     */
    @Override
    public Class<LabelGenerationTenantConfiguration> getConfigurationClass() {
	return LabelGenerationTenantConfiguration.class;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * createConfigurationModule()
     */
    @Override
    public ITenantEngineModule<LabelGenerationTenantConfiguration> createConfigurationModule() {
	return new LabelGenerationTenantEngineModule(this, getActiveConfiguration());
    }

    /*
     * @see com.sitewhere.microservice.multitenant.MicroserviceTenantEngine#
     * loadEngineComponents()
     */
    @Override
    public void loadEngineComponents() throws SiteWhereException {
	this.labelGeneratorManager = getInjector().getInstance(ILabelGeneratorManager.class);
	this.labelGenerationImpl = new LabelGenerationImpl(this);
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

	// Initialize label generator manager.
	init.addInitializeStep(this, getLabelGeneratorManager(), true);

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

	// Start label generator manager.
	start.addStartStep(this, getLabelGeneratorManager(), true);

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
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getComponentName());

	// Stop label generator manager.
	stop.addStopStep(this, getLabelGeneratorManager());

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

    /*
     * @see com.sitewhere.labels.spi.microservice.ILabelGenerationTenantEngine#
     * getLabelGenerationImpl()
     */
    @Override
    public LabelGenerationGrpc.LabelGenerationImplBase getLabelGenerationImpl() {
	return labelGenerationImpl;
    }
}