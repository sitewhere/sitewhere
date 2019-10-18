/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.multitenant;

import java.util.Map;

import org.springframework.context.ApplicationContext;

import com.sitewhere.configuration.ConfigurationUtils;
import com.sitewhere.microservice.groovy.GroovyConfiguration;
import com.sitewhere.microservice.scripting.TenantEngineScriptManager;
import com.sitewhere.microservice.scripting.TenantEngineScriptSynchronizer;
import com.sitewhere.rest.model.microservice.state.TenantEngineState;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.server.lifecycle.SimpleLifecycleStep;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice;
import com.sitewhere.spi.microservice.groovy.IGroovyConfiguration;
import com.sitewhere.spi.microservice.multitenant.IDatasetTemplate;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice;
import com.sitewhere.spi.microservice.multitenant.ITenantTemplate;
import com.sitewhere.spi.microservice.state.ITenantEngineState;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.IDiscoverableTenantLifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.ILifecycleStep;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Specialized tenant engine that runs within an
 * {@link IMultitenantMicroservice}.
 */
public abstract class MicroserviceTenantEngine extends TenantEngineLifecycleComponent
	implements IMicroserviceTenantEngine {

    /** Name for lock path */
    public static final String MODULE_LOCK_NAME = "lock";

    /** Suffix appended to module identifier to locate module configuration */
    public static final String MODULE_CONFIGURATION_SUFFIX = ".xml";

    /** Name for tenant dataset bootstrapped indicator */
    public static final String DATASET_BOOTSTRAPPED_NAME = "bootstrapped";

    /** Tenant template path (relative to configuration root) */
    public static final String TENANT_TEMPLATE_PATH = "tenant-template.json";

    /** Dataset template path (relative to configuration root) */
    public static final String DATASET_TEMPLATE_PATH = "dataset-template.json";

    /** Hosted tenant */
    private ITenant tenant;

    /** Tenant script synchronizer */
    private TenantEngineScriptSynchronizer tenantScriptSynchronizer;

    /** Script manager */
    private TenantEngineScriptManager scriptManager;

    /** Dataset bootstrap manager */
    private DatasetBootstrapManager bootstrapManager;

    /** Groovy configuration */
    private IGroovyConfiguration groovyConfiguration;

    /** Module context information */
    private ApplicationContext moduleContext;

    public MicroserviceTenantEngine(ITenant tenant) {
	this.tenant = tenant;
	this.tenantScriptSynchronizer = new TenantEngineScriptSynchronizer(this);
	this.scriptManager = new TenantEngineScriptManager();
	this.bootstrapManager = new DatasetBootstrapManager();
	this.groovyConfiguration = new GroovyConfiguration(getTenantScriptSynchronizer());
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
	init.addInitializeStep(this, getTenantScriptSynchronizer(), true);

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
	    properties.put("tenant.id", getTenant().getId());
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

	// Start tenant script synchronizer.
	start.addStartStep(this, getTenantScriptSynchronizer(), true);

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

	// Stop tenant script synchronizer.
	stop.addStopStep(this, getTenantScriptSynchronizer());

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

	// Terminate tenant script synchronizer.
	stop.addTerminateStep(this, getTenantScriptSynchronizer());

	// Execute terminate steps.
	stop.execute(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * initializeDiscoverableBeans(org.springframework.context.ApplicationContext)
     */
    @Override
    public ILifecycleStep initializeDiscoverableBeans(ApplicationContext context) throws SiteWhereException {
	return new SimpleLifecycleStep("Initialize discoverable beans") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		Map<String, IDiscoverableTenantLifecycleComponent> components = context
			.getBeansOfType(IDiscoverableTenantLifecycleComponent.class);

		for (IDiscoverableTenantLifecycleComponent component : components.values()) {
		    initializeNestedComponent(component, monitor, component.isRequired());
		}
	    }
	};
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * startDiscoverableBeans(org.springframework.context.ApplicationContext)
     */
    @Override
    public ILifecycleStep startDiscoverableBeans(ApplicationContext context) throws SiteWhereException {
	return new SimpleLifecycleStep("Start discoverable beans") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		Map<String, IDiscoverableTenantLifecycleComponent> components = context
			.getBeansOfType(IDiscoverableTenantLifecycleComponent.class);

		for (IDiscoverableTenantLifecycleComponent component : components.values()) {
		    startNestedComponent(component, monitor, component.isRequired());
		}
	    }
	};
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * stopDiscoverableBeans(org.springframework.context.ApplicationContext)
     */
    @Override
    public ILifecycleStep stopDiscoverableBeans(ApplicationContext context) throws SiteWhereException {
	return new SimpleLifecycleStep("Stop discoverable beans") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		Map<String, IDiscoverableTenantLifecycleComponent> components = context
			.getBeansOfType(IDiscoverableTenantLifecycleComponent.class);

		for (IDiscoverableTenantLifecycleComponent component : components.values()) {
		    component.lifecycleStop(monitor);
		}
	    }
	};
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * terminateDiscoverableBeans(org.springframework.context.ApplicationContext)
     */
    @Override
    public ILifecycleStep terminateDiscoverableBeans(ApplicationContext context) throws SiteWhereException {
	return new SimpleLifecycleStep("Terminate discoverable beans") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		Map<String, IDiscoverableTenantLifecycleComponent> components = context
			.getBeansOfType(IDiscoverableTenantLifecycleComponent.class);

		for (IDiscoverableTenantLifecycleComponent component : components.values()) {
		    component.lifecycleTerminate(monitor);
		}
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
	state.setTenantId(getTenant().getId());
	state.setComponentState(getComponentState());
	return state;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.configuration.IConfigurationListener#
     * onConfigurationCacheInitialized()
     */
    @Override
    public void onConfigurationCacheInitialized() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.configuration.IConfigurationListener#
     * onConfigurationAdded(java.lang.String, byte[])
     */
    @Override
    public void onConfigurationAdded(String path, byte[] data) {
	getLogger().debug("Tenant engine configuration path added: " + path);
	try {
	    // Handle updated configuration file.
	    if (getModuleConfigurationName().equals(path)) {
		getLogger().info("Tenant engine configuration added.");
	    }
	} catch (SiteWhereException e) {
	    getLogger().error("Unable to process added configuration file.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.configuration.IConfigurationListener#
     * onConfigurationUpdated(java.lang.String, byte[])
     */
    @Override
    public void onConfigurationUpdated(String path, byte[] data) {
	getLogger().debug("Tenant engine configuration path updated: " + path);
	try {
	    // Handle updated configuration file.
	    if (getModuleConfigurationName().equals(path)) {
		getLogger().info("Tenant engine configuration updated.");
		((IMultitenantMicroservice<?, ?>) getMicroservice()).getTenantEngineManager()
			.restartTenantEngine(getTenant().getId());
	    }
	} catch (SiteWhereException e) {
	    getLogger().error("Unable to process updated configuration file.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.configuration.IConfigurationListener#
     * onConfigurationDeleted(java.lang.String)
     */
    @Override
    public void onConfigurationDeleted(String path) {
	getLogger().debug("Tenant engine configuration path deleted: " + path);
	try {
	    // Handle updated configuration file.
	    if (getModuleConfigurationName().equals(path)) {
		getLogger().info("Tenant engine configuration deleted.");
		((IMultitenantMicroservice<?, ?>) getMicroservice()).getTenantEngineManager()
			.removeTenantEngine(getTenant().getId());
	    }
	} catch (SiteWhereException e) {
	    getLogger().error("Unable to process deleted configuration file.", e);
	}
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
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * getTenantTemplate()
     */
    @Override
    public ITenantTemplate getTenantTemplate() throws SiteWhereException {
	// String templatePath = getTenantConfigurationPath() + "/" +
	// TENANT_TEMPLATE_PATH;
	// CuratorFramework curator =
	// getMicroservice().getZookeeperManager().getCurator();
	// try {
	// byte[] data = curator.getData().forPath(templatePath);
	// return MarshalUtils.unmarshalJson(data, TenantTemplate.class);
	// } catch (Exception e) {
	// throw new SiteWhereException("Unable to load tenant template from Zk.", e);
	// }
	return null;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getDatasetTemplate()
     */
    @Override
    public IDatasetTemplate getDatasetTemplate() throws SiteWhereException {
	// String templatePath = getTenantConfigurationPath() + "/" +
	// DATASET_TEMPLATE_PATH;
	// CuratorFramework curator =
	// getMicroservice().getZookeeperManager().getCurator();
	// try {
	// byte[] data = curator.getData().forPath(templatePath);
	// return MarshalUtils.unmarshalJson(data, DatasetTemplate.class);
	// } catch (Exception e) {
	// throw new SiteWhereException("Unable to load tenant template from Zk.", e);
	// }
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
     * getModuleConfigurationName()
     */
    @Override
    public String getModuleConfigurationName() throws SiteWhereException {
	return getMicroservice().getIdentifier().getPath() + MicroserviceTenantEngine.MODULE_CONFIGURATION_SUFFIX;
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
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * getTenantScriptSynchronizer()
     */
    @Override
    public TenantEngineScriptSynchronizer getTenantScriptSynchronizer() {
	return tenantScriptSynchronizer;
    }

    public void setTenantScriptSynchronizer(TenantEngineScriptSynchronizer tenantScriptSynchronizer) {
	this.tenantScriptSynchronizer = tenantScriptSynchronizer;
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
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * getModuleContext()
     */
    @Override
    public ApplicationContext getModuleContext() {
	return moduleContext;
    }

    public void setModuleContext(ApplicationContext moduleContext) {
	this.moduleContext = moduleContext;
    }
}