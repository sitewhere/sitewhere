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
package com.sitewhere.commands.microservice;

import com.sitewhere.commands.DefaultCommandProcessingStrategy;
import com.sitewhere.commands.configuration.CommandDeliveryTenantConfiguration;
import com.sitewhere.commands.configuration.CommandDeliveryTenantEngineModule;
import com.sitewhere.commands.kafka.EnrichedCommandInvocationsPipeline;
import com.sitewhere.commands.kafka.UndeliveredCommandInvocationsProducer;
import com.sitewhere.commands.spi.ICommandDestinationsManager;
import com.sitewhere.commands.spi.ICommandProcessingStrategy;
import com.sitewhere.commands.spi.IOutboundCommandRouter;
import com.sitewhere.commands.spi.kafka.IEnrichedCommandInvocationsPipeline;
import com.sitewhere.commands.spi.kafka.IUndeliveredCommandInvocationsProducer;
import com.sitewhere.commands.spi.microservice.ICommandDeliveryTenantEngine;
import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.multitenant.MicroserviceTenantEngine;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineModule;

import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;

/**
 * Implementation of {@link IMicroserviceTenantEngine} that implements command
 * delivery functionality.
 */
public class CommandDeliveryTenantEngine extends MicroserviceTenantEngine<CommandDeliveryTenantConfiguration>
	implements ICommandDeliveryTenantEngine {

    /** Configured command processing strategy */
    private ICommandProcessingStrategy commandProcessingStrategy = new DefaultCommandProcessingStrategy();

    /** Configured outbound command router */
    private IOutboundCommandRouter outboundCommandRouter;

    /** Command destinations manager */
    private ICommandDestinationsManager commandDestinationsManager;

    /** Kafka Streams pipeline for enriched command invocations */
    private IEnrichedCommandInvocationsPipeline enrichedCommandInvocationsPipeline;

    /** Kafka producer for undelivered command invocations */
    private IUndeliveredCommandInvocationsProducer undeliveredCommandInvocationsProducer;

    public CommandDeliveryTenantEngine(SiteWhereTenantEngine engine) {
	super(engine);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getConfigurationClass()
     */
    @Override
    public Class<CommandDeliveryTenantConfiguration> getConfigurationClass() {
	return CommandDeliveryTenantConfiguration.class;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * createConfigurationModule()
     */
    @Override
    public ITenantEngineModule<CommandDeliveryTenantConfiguration> createConfigurationModule() {
	return new CommandDeliveryTenantEngineModule(this, getActiveConfiguration());
    }

    /*
     * @see com.sitewhere.microservice.multitenant.MicroserviceTenantEngine#
     * loadEngineComponents()
     */
    @Override
    public void loadEngineComponents() throws SiteWhereException {
	getLogger().info(String.format("Loaded command destinations configuration as:\n%s\n\n",
		MarshalUtils.marshalJsonAsPrettyString(getActiveConfiguration())));

	// Load configured command destinations manager.
	this.commandDestinationsManager = getInjector().getInstance(ICommandDestinationsManager.class);

	// Load configured command router.
	this.outboundCommandRouter = getInjector().getInstance(IOutboundCommandRouter.class);
	if (getOutboundCommandRouter() == null) {
	    throw new SiteWhereException("Outbound command router not properly initialized.");
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantInitialize(com.sitewhere.spi.microservice.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Listener for enriched command invocations.
	this.enrichedCommandInvocationsPipeline = new EnrichedCommandInvocationsPipeline();

	// Producer for storing undelivered command invocations.
	this.undeliveredCommandInvocationsProducer = new UndeliveredCommandInvocationsProducer();

	// Create step that will initialize components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getComponentName());

	// Initialize command destinations manager.
	init.addInitializeStep(this, getCommandDestinationsManager(), true);

	// Initialize outbound command router.
	init.addInitializeStep(this, getOutboundCommandRouter(), true);

	// Initialize command processing strategy.
	init.addInitializeStep(this, getCommandProcessingStrategy(), true);

	// Initialize undelivered command invocations producer.
	init.addInitializeStep(this, getUndeliveredCommandInvocationsProducer(), true);

	// Initialize enriched command invocations pipeline.
	init.addInitializeStep(this, getEnrichedCommandInvocationsPipeline(), true);

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

	// Start command destinations manager.
	start.addStartStep(this, getCommandDestinationsManager(), true);

	// Start outbound command router.
	start.addStartStep(this, getOutboundCommandRouter(), true);

	// Start command processing strategy.
	start.addStartStep(this, getCommandProcessingStrategy(), true);

	// Start undelivered command invocations producer.
	start.addStartStep(this, getUndeliveredCommandInvocationsProducer(), true);

	// Start command invocations pipeline.
	start.addStartStep(this, getEnrichedCommandInvocationsPipeline(), true);

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

	// Stop command invocations pipeline.
	stop.addStopStep(this, getEnrichedCommandInvocationsPipeline());

	// Stop undelivered command invocations producer.
	stop.addStopStep(this, getUndeliveredCommandInvocationsProducer());

	// Stop outbound command router.
	stop.addStopStep(this, getOutboundCommandRouter());

	// Stop command processing strategy.
	stop.addStopStep(this, getCommandProcessingStrategy());

	// Stop command destinations manager.
	stop.addStopStep(this, getCommandDestinationsManager());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /*
     * @see com.sitewhere.commands.spi.microservice.ICommandDeliveryTenantEngine#
     * getCommandProcessingStrategy()
     */
    @Override
    public ICommandProcessingStrategy getCommandProcessingStrategy() {
	return commandProcessingStrategy;
    }

    /*
     * @see com.sitewhere.commands.spi.microservice.ICommandDeliveryTenantEngine#
     * getOutboundCommandRouter()
     */
    @Override
    public IOutboundCommandRouter getOutboundCommandRouter() {
	return outboundCommandRouter;
    }

    /*
     * @see com.sitewhere.commands.spi.microservice.ICommandDeliveryTenantEngine#
     * getCommandDestinationsManager()
     */
    @Override
    public ICommandDestinationsManager getCommandDestinationsManager() {
	return commandDestinationsManager;
    }

    /*
     * @see com.sitewhere.commands.spi.microservice.ICommandDeliveryTenantEngine#
     * getEnrichedCommandInvocationsPipeline()
     */
    @Override
    public IEnrichedCommandInvocationsPipeline getEnrichedCommandInvocationsPipeline() {
	return enrichedCommandInvocationsPipeline;
    }

    /*
     * @see com.sitewhere.commands.spi.microservice.ICommandDeliveryTenantEngine#
     * getUndeliveredCommandInvocationsProducer()
     */
    @Override
    public IUndeliveredCommandInvocationsProducer getUndeliveredCommandInvocationsProducer() {
	return undeliveredCommandInvocationsProducer;
    }
}