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
package com.sitewhere.inbound.microservice;

import com.sitewhere.inbound.configuration.InboundProcessingTenantConfiguration;
import com.sitewhere.inbound.configuration.InboundProcessingTenantEngineModule;
import com.sitewhere.inbound.kafka.DecodedEventsPipeline;
import com.sitewhere.inbound.spi.kafka.IDecodedEventsPipeline;
import com.sitewhere.inbound.spi.microservice.IInboundProcessingTenantEngine;
import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.multitenant.MicroserviceTenantEngine;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineModule;

import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;

/**
 * Implementation of {@link IMicroserviceTenantEngine} that implements inbound
 * event processing functionality.
 */
public class InboundProcessingTenantEngine extends MicroserviceTenantEngine<InboundProcessingTenantConfiguration>
	implements IInboundProcessingTenantEngine {

    /** Kafka Streams pipeline that handles inbound decoded events */
    private IDecodedEventsPipeline decodedEventsPipeline;

    public InboundProcessingTenantEngine(SiteWhereTenantEngine engine) {
	super(engine);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getConfigurationClass()
     */
    @Override
    public Class<InboundProcessingTenantConfiguration> getConfigurationClass() {
	return InboundProcessingTenantConfiguration.class;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * createConfigurationModule()
     */
    @Override
    public ITenantEngineModule<InboundProcessingTenantConfiguration> createConfigurationModule() {
	return new InboundProcessingTenantEngineModule(this, getActiveConfiguration());
    }

    /*
     * @see com.sitewhere.microservice.multitenant.MicroserviceTenantEngine#
     * loadEngineComponents()
     */
    @Override
    public void loadEngineComponents() throws SiteWhereException {
	this.decodedEventsPipeline = new DecodedEventsPipeline();
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

	// Initialize decoded events pipeline.
	init.addInitializeStep(this, getDecodedEventsPipeline(), true);

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

	// Start decoded events pipeline.
	start.addStartStep(this, getDecodedEventsPipeline(), true);

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

	// Stop decoded events pipeline.
	stop.addStopStep(this, getDecodedEventsPipeline());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /*
     * @see com.sitewhere.inbound.spi.microservice.IInboundProcessingTenantEngine#
     * getDecodedEventsPipeline()
     */
    @Override
    public IDecodedEventsPipeline getDecodedEventsPipeline() {
	return decodedEventsPipeline;
    }
}