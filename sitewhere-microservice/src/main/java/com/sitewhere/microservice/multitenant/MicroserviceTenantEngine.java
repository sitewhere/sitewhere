/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.multitenant;

import java.util.Map;

import com.sitewhere.configuration.ConfigurationUtils;
import com.sitewhere.microservice.groovy.GroovyConfiguration;
import com.sitewhere.microservice.scripting.ScriptSynchronizer;
import com.sitewhere.microservice.scripting.TenantEngineScriptContext;
import com.sitewhere.microservice.scripting.TenantEngineScriptManager;
import com.sitewhere.rest.model.microservice.state.TenantEngineState;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.server.lifecycle.SimpleLifecycleStep;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice;
import com.sitewhere.spi.microservice.groovy.IGroovyConfiguration;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice;
import com.sitewhere.spi.microservice.scripting.IScriptContext;
import com.sitewhere.spi.microservice.scripting.IScriptSynchronizer;
import com.sitewhere.spi.microservice.state.ITenantEngineState;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.ILifecycleStep;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;
import com.sitewhere.spi.tenant.ITenant;

import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;
import io.sitewhere.k8s.crd.tenant.engine.configuration.TenantEngineConfigurationTemplate;
import io.sitewhere.k8s.crd.tenant.engine.dataset.TenantEngineDatasetTemplate;

/**
 * Specialized tenant engine that runs within an
 * {@link IMultitenantMicroservice}.
 */
public abstract class MicroserviceTenantEngine extends TenantEngineLifecycleComponent
	implements IMicroserviceTenantEngine {

    /** Hosted tenant */
    private ITenant tenant;

    /** Script synchronizer */
    private IScriptSynchronizer scriptSynchronizer;

    /** Script context */
    private IScriptContext scriptContext;

    /** Script manager */
    private TenantEngineScriptManager scriptManager;

    /** Dataset bootstrap manager */
    private DatasetBootstrapManager bootstrapManager;

    /** Groovy configuration */
    private IGroovyConfiguration groovyConfiguration;

    /** Module context information */
    private Object moduleContext;

    public MicroserviceTenantEngine(ITenant tenant) {
	this.tenant = tenant;
	this.scriptSynchronizer = new ScriptSynchronizer();
	this.scriptManager = new TenantEngineScriptManager();
	this.scriptContext = new TenantEngineScriptContext(this);
	this.bootstrapManager = new DatasetBootstrapManager();
	this.groovyConfiguration = new GroovyConfiguration(getScriptContext(), getScriptSynchronizer());
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent#getTenantEngine
     * ()
     */
    @Override
    public IMicroserviceTenantEngine getTenantEngine() {
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	loadModuleConfiguration();

	// Create step that will initialize components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize tenant engine " + getTenant().getName());

	// Initialize script synchronizer.
	init.addInitializeStep(this, getScriptSynchronizer(), true);

	// Initialize script manager.
	init.addInitializeStep(this, getScriptManager(), true);

	// Initialize Groovy configuration.
	init.addInitializeStep(this, getGroovyConfiguration(), true);

	// Initialize bootstrap manager.
	init.addInitializeStep(this, getBootstrapManager(), true);

	// Execute initialization steps.
	init.execute(monitor);

	// Allow subclass to execute initialization logic.
	tenantInitialize(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getModuleConfiguration()
     */
    @Override
    public byte[] getModuleConfiguration() throws SiteWhereException {
	return null;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * updateModuleConfiguration(byte[])
     */
    @Override
    public void updateModuleConfiguration(byte[] content) throws SiteWhereException {
    }

    /**
     * Load Spring configuration into module context.
     * 
     * @throws SiteWhereException
     */
    protected void loadModuleConfiguration() throws SiteWhereException {
	try {
	    byte[] data = getModuleConfiguration();
	    Map<String, Object> properties = ((IConfigurableMicroservice<?>) getMicroservice()).getSpringProperties();
	    properties.put("tenant.id", getTenant().getToken());
	    properties.put("tenant.token", getTenant().getToken());
	    this.moduleContext = ConfigurationUtils.buildSubcontext(data, properties,
		    ((IConfigurableMicroservice<?>) getMicroservice()).getGlobalApplicationContext());
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to load module configuration.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {

	// Create step that will start components.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start tenant engine " + getTenant().getName());

	// Start script synchronizer.
	start.addStartStep(this, getScriptSynchronizer(), true);

	// Start tenant script manager.
	start.addStartStep(this, getScriptManager(), true);

	// Start Groovy configuration.
	start.addStartStep(this, getGroovyConfiguration(), true);

	// Execute startup steps.
	start.execute(monitor);

	// Allow subclass to execute startup logic.
	tenantStart(monitor);

	// Execute bootstrap in background
	startNestedComponent(getBootstrapManager(), monitor, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {

	// Stop the bootstrap manager.
	stopNestedComponent(getBootstrapManager(), monitor);

	// Allow subclass to execute shutdown logic.
	tenantStop(monitor);

	// Create step that will stop components.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop tenant engine " + getTenant().getName());

	// Stop Groovy configuration.
	stop.addStopStep(this, getGroovyConfiguration());

	// Stop tenant script manager.
	stop.addStopStep(this, getScriptManager());

	// Stop script synchronizer.
	stop.addStopStep(this, getScriptSynchronizer());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#terminate(com.sitewhere
     * .spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void terminate(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will terminate components.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Terminate tenant engine " + getTenant().getName());

	// Terminate Groovy configuration.
	stop.addTerminateStep(this, getGroovyConfiguration());

	// Terminate tenant script manager.
	stop.addTerminateStep(this, getScriptManager());

	// Terminate script synchronizer.
	stop.addTerminateStep(this, getScriptSynchronizer());

	// Execute terminate steps.
	stop.execute(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * initializeDiscoverableBeans(java.lang.Object)
     */
    @Override
    public ILifecycleStep initializeDiscoverableBeans(Object context) throws SiteWhereException {
	return new SimpleLifecycleStep("Initialize discoverable beans") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		// Map<String, IDiscoverableTenantLifecycleComponent> components = context
		// .getBeansOfType(IDiscoverableTenantLifecycleComponent.class);
		//
		// for (IDiscoverableTenantLifecycleComponent component : components.values()) {
		// initializeNestedComponent(component, monitor, component.isRequired());
		// }
	    }
	};
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * startDiscoverableBeans(java.lang.Object)
     */
    @Override
    public ILifecycleStep startDiscoverableBeans(Object context) throws SiteWhereException {
	return new SimpleLifecycleStep("Start discoverable beans") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		// Map<String, IDiscoverableTenantLifecycleComponent> components = context
		// .getBeansOfType(IDiscoverableTenantLifecycleComponent.class);
		//
		// for (IDiscoverableTenantLifecycleComponent component : components.values()) {
		// startNestedComponent(component, monitor, component.isRequired());
		// }
	    }
	};
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * stopDiscoverableBeans(java.lang.Object)
     */
    @Override
    public ILifecycleStep stopDiscoverableBeans(Object context) throws SiteWhereException {
	return new SimpleLifecycleStep("Stop discoverable beans") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		// Map<String, IDiscoverableTenantLifecycleComponent> components = context
		// .getBeansOfType(IDiscoverableTenantLifecycleComponent.class);
		//
		// for (IDiscoverableTenantLifecycleComponent component : components.values()) {
		// component.lifecycleStop(monitor);
		// }
	    }
	};
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * terminateDiscoverableBeans(java.lang.Object)
     */
    @Override
    public ILifecycleStep terminateDiscoverableBeans(Object context) throws SiteWhereException {
	return new SimpleLifecycleStep("Terminate discoverable beans") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		// Map<String, IDiscoverableTenantLifecycleComponent> components = context
		// .getBeansOfType(IDiscoverableTenantLifecycleComponent.class);
		//
		// for (IDiscoverableTenantLifecycleComponent component : components.values()) {
		// component.lifecycleTerminate(monitor);
		// }
	    }
	};
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#lifecycleStatusChanged(com.
     * sitewhere.spi.server.lifecycle.LifecycleStatus,
     * com.sitewhere.spi.server.lifecycle.LifecycleStatus)
     */
    @Override
    public void lifecycleStatusChanged(LifecycleStatus before, LifecycleStatus after) {
	try {
	    ITenantEngineState state = getCurrentState();
	    getMicroservice().onTenantEngineStateChanged(state);
	} catch (SiteWhereException e) {
	    getLogger().error("Unable to calculate current state.", e);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getCurrentState()
     */
    @Override
    public ITenantEngineState getCurrentState() throws SiteWhereException {
	TenantEngineState state = new TenantEngineState();
	state.setMicroservice(getMicroservice().getMicroserviceDetails());
	state.setTenantId(null);
	state.setComponentState(getComponentState());
	return state;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.
     * ITenantEngineConfigurationListener#onConfigurationAdded(io.sitewhere.k8s.crd.
     * tenant.engine.SiteWhereTenantEngine)
     */
    @Override
    public void onConfigurationAdded(SiteWhereTenantEngine engine) {
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.
     * ITenantEngineConfigurationListener#onConfigurationUpdated(io.sitewhere.k8s.
     * crd.tenant.engine.SiteWhereTenantEngine)
     */
    @Override
    public void onConfigurationUpdated(SiteWhereTenantEngine engine) {
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.
     * ITenantEngineConfigurationListener#onConfigurationDeleted(io.sitewhere.k8s.
     * crd.tenant.engine.SiteWhereTenantEngine)
     */
    @Override
    public void onConfigurationDeleted(SiteWhereTenantEngine engine) {
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * onGlobalConfigurationUpdated()
     */
    @Override
    public void onGlobalConfigurationUpdated() throws SiteWhereException {
	getLogger().debug("Tenant engine detected global configuration update.");
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getConfigurationTemplate()
     */
    @Override
    public TenantEngineConfigurationTemplate getConfigurationTemplate() throws SiteWhereException {
	return null;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getDatasetTemplate()
     */
    @Override
    public TenantEngineDatasetTemplate getDatasetTemplate() throws SiteWhereException {
	return null;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getTenantBootstrapPrerequisites()
     */
    @Override
    public IFunctionIdentifier[] getTenantBootstrapPrerequisites() {
	return new IFunctionIdentifier[0];
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * waitForTenantDatasetBootstrapped(com.sitewhere.spi.microservice.
     * IFunctionIdentifier)
     */
    @Override
    public void waitForTenantDatasetBootstrapped(IFunctionIdentifier identifier) throws SiteWhereException {
	// getLogger().info(String.format("Verifying that module '%s' has been
	// bootstrapped.", identifier.getShortName()));
	// String path = ((IConfigurableMicroservice<?>)
	// getMicroservice()).getInstanceTenantStatePath(getTenant().getId())
	// + "/" + identifier.getPath() + "/" +
	// MicroserviceTenantEngine.DATASET_BOOTSTRAPPED_NAME;
	// Callable<Boolean> bootstrapCheck = () -> {
	// return
	// getMicroservice().getZookeeperManager().getCurator().checkExists().forPath(path)
	// == null ? false
	// : true;
	// };
	// RetryConfig config = new
	// RetryConfigBuilder().retryOnReturnValue(Boolean.FALSE).retryIndefinitely()
	// .withDelayBetweenTries(Duration.ofSeconds(2)).withRandomBackoff().build();
	// RetryListener perFail = new RetryListener<Boolean>() {
	//
	// @Override
	// public void onEvent(Status<Boolean> status) {
	// getLogger().info(String.format(
	// "Unable to locate bootstrap marker for '%s' on attempt %d (total wait so far
	// %dms). Retrying after fallback...",
	// identifier.getShortName(), status.getTotalTries(),
	// status.getTotalElapsedDuration().toMillis()));
	// }
	// };
	// RetryListener success = new RetryListener<Boolean>() {
	//
	// @Override
	// public void onEvent(Status<Boolean> status) {
	// getLogger().info(String.format("Located bootstrap marker for '%s' in %dms.",
	// identifier.getShortName(),
	// status.getTotalElapsedDuration().toMillis()));
	// }
	// };
	// new
	// CallExecutorBuilder().config(config).afterFailedTryListener(perFail).onSuccessListener(success).build()
	// .execute(bootstrapCheck);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getTenant()
     */
    @Override
    public ITenant getTenant() {
	return tenant;
    }

    public void setTenant(ITenant tenant) {
	this.tenant = tenant;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getScriptSynchronizer()
     */
    @Override
    public IScriptSynchronizer getScriptSynchronizer() {
	return scriptSynchronizer;
    }

    public void setScriptSynchronizer(IScriptSynchronizer scriptSynchronizer) {
	this.scriptSynchronizer = scriptSynchronizer;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getScriptManager()
     */
    @Override
    public TenantEngineScriptManager getScriptManager() {
	return scriptManager;
    }

    public void setScriptManager(TenantEngineScriptManager scriptManager) {
	this.scriptManager = scriptManager;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getScriptContext()
     */
    @Override
    public IScriptContext getScriptContext() {
	return scriptContext;
    }

    public void setScriptContext(IScriptContext scriptContext) {
	this.scriptContext = scriptContext;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getBootstrapManager()
     */
    @Override
    public DatasetBootstrapManager getBootstrapManager() {
	return bootstrapManager;
    }

    public void setBootstrapManager(DatasetBootstrapManager bootstrapManager) {
	this.bootstrapManager = bootstrapManager;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getGroovyConfiguration()
     */
    @Override
    public IGroovyConfiguration getGroovyConfiguration() {
	return groovyConfiguration;
    }

    public void setGroovyConfiguration(IGroovyConfiguration groovyConfiguration) {
	this.groovyConfiguration = groovyConfiguration;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getModuleContext()
     */
    @Override
    public Object getModuleContext() {
	return moduleContext;
    }

    public void setModuleContext(Object moduleContext) {
	this.moduleContext = moduleContext;
    }
}