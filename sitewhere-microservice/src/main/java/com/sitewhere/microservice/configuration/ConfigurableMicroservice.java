/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.configuration;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.sitewhere.configuration.ConfigurationUtils;
import com.sitewhere.microservice.Microservice;
import com.sitewhere.microservice.operations.InitializeConfigurationOperation;
import com.sitewhere.microservice.operations.StartConfigurationOperation;
import com.sitewhere.microservice.operations.StopConfigurationOperation;
import com.sitewhere.microservice.operations.TerminateConfigurationOperation;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.server.lifecycle.LifecycleProgressContext;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.server.lifecycle.SimpleLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.configuration.ConfigurationState;
import com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice;
import com.sitewhere.spi.microservice.configuration.IConfigurationListener;
import com.sitewhere.spi.microservice.configuration.IConfigurationMonitor;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.IDiscoverableTenantLifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.ILifecycleStep;

/**
 * Base class for microservices that monitor the configuration folder for
 * updates.
 * 
 * @author Derek
 */
public abstract class ConfigurableMicroservice extends Microservice
	implements IConfigurableMicroservice, IConfigurationListener {

    /** Relative path to instance management configuration file */
    private static final String INSTANCE_MANAGEMENT_CONFIGURATION_PATH = "/instance-management.xml";

    /** Relative path to configuration data for tenants */
    private static final String INSTANCE_TENANTS_CONFIGURATION_PATH = "/tenants";

    /** Relative path to tenant bootstrapped indicator data */
    private static final String INSTANCE_TENANT_BOOTSTRAPPED_INDICATOR = "bootstrapped";

    /** Max wait time for configuration in seconds */
    private static final int MAX_CONFIGURATION_WAIT_SEC = 30;

    /** Injected Spring context for microservice */
    @Autowired
    private ApplicationContext microserviceContext;

    /** Configuration monitor */
    private IConfigurationMonitor configurationMonitor;

    /** Configuration state */
    private ConfigurationState configurationState = ConfigurationState.NotStarted;

    /** Indicates if configuration cache is ready to use */
    private boolean configurationCacheReady = false;

    /** Global instance application context */
    private ApplicationContext globalApplicationContext;

    /** Local microservice application context */
    private ApplicationContext localApplicationContext;

    /** Executor for loading/parsing configuration updates */
    private ExecutorService executor = Executors.newSingleThreadExecutor(new ConfigurationLoaderThreadFactory());

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.configuration.IConfigurationListener#
     * onConfigurationCacheInitialized()
     */
    @Override
    public void onConfigurationCacheInitialized() {
	getLogger().info("Configuration cache initialized.");
	setConfigurationCacheReady(true);

	// Load and parse configuration in separate thread.
	executor.execute(new ConfigurationLoader());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.configuration.IConfigurableMicroservice#
     * getConfigurationDataFor(java.lang.String)
     */
    @Override
    public byte[] getConfigurationDataFor(String path) throws SiteWhereException {
	if (!isConfigurationCacheReady()) {
	    throw new SiteWhereException("Configuration cache not initialized.");
	}
	return getConfigurationMonitor().getConfigurationDataFor(path);
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getInstanceManagementConfigurationPath()
     */
    @Override
    public String getInstanceManagementConfigurationPath() {
	return getInstanceConfigurationPath() + INSTANCE_MANAGEMENT_CONFIGURATION_PATH;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getInstanceManagementConfigurationData()
     */
    @Override
    public byte[] getInstanceManagementConfigurationData() throws SiteWhereException {
	return getConfigurationMonitor().getConfigurationDataFor(getInstanceManagementConfigurationPath());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.configuration.IConfigurableMicroservice#
     * getInstanceTenantsConfigurationPath()
     */
    @Override
    public String getInstanceTenantsConfigurationPath() throws SiteWhereException {
	return getInstanceConfigurationPath() + INSTANCE_TENANTS_CONFIGURATION_PATH;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.configuration.IConfigurableMicroservice#
     * getInstanceTenantConfigurationPath(java.lang.String)
     */
    @Override
    public String getInstanceTenantConfigurationPath(String tenantId) throws SiteWhereException {
	return getInstanceTenantsConfigurationPath() + "/" + tenantId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.configuration.IConfigurableMicroservice#
     * getInstanceTenantBootstrappedIndicatorPath(java.lang.String)
     */
    @Override
    public String getInstanceTenantBootstrappedIndicatorPath(String tenantId) throws SiteWhereException {
	return getInstanceTenantConfigurationPath(tenantId) + "/" + INSTANCE_TENANT_BOOTSTRAPPED_INDICATOR;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.initialize(monitor);

	// Make sure that instance is bootstrapped before configuring.
	waitForInstanceInitialization();

	// Organizes steps for initializing microservice.
	ICompositeLifecycleStep initialize = new CompositeLifecycleStep("Initialize " + getName());

	// Create and initialize configuration monitor.
	createConfigurationMonitor();
	initialize.addInitializeStep(this, getConfigurationMonitor(), true);

	// Start configuration monitor.
	initialize.addStartStep(this, getConfigurationMonitor(), true);

	// Execute initialization steps.
	initialize.execute(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * configurationInitialize(org.springframework.context.ApplicationContext,
     * org.springframework.context.ApplicationContext,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void configurationInitialize(ApplicationContext global, ApplicationContext local,
	    ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (local != null) {
	    initializeDiscoverableBeans(local).execute(monitor);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * configurationStart(org.springframework.context.ApplicationContext,
     * org.springframework.context.ApplicationContext,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void configurationStart(ApplicationContext global, ApplicationContext local,
	    ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (local != null) {
	    startDiscoverableBeans(local).execute(monitor);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * configurationStop(org.springframework.context.ApplicationContext,
     * org.springframework.context.ApplicationContext,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void configurationStop(ApplicationContext global, ApplicationContext local,
	    ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (local != null) {
	    stopDiscoverableBeans(local).execute(monitor);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * configurationTerminate(org.springframework.context.ApplicationContext,
     * org.springframework.context.ApplicationContext,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void configurationTerminate(ApplicationContext global, ApplicationContext local,
	    ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (local != null) {
	    terminateDiscoverableBeans(local).execute(monitor);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
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
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
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
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
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
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
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

    /**
     * Create configuration monitor for microservice.
     * 
     * @throws SiteWhereException
     */
    protected void createConfigurationMonitor() throws SiteWhereException {
	this.configurationMonitor = new ConfigurationMonitor(getZookeeperManager(), getInstanceConfigurationPath());
	getConfigurationMonitor().getListeners().add(this);
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
	super.terminate(monitor);

	// Organizes steps for stopping microservice.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getName());

	// Stop configuration monitor.
	stop.addStopStep(this, getConfigurationMonitor());

	// Execute shutdown steps.
	stop.execute(monitor);

	// Terminate configuration monitor.
	getConfigurationMonitor().lifecycleTerminate(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * restartConfiguration()
     */
    @Override
    public void restartConfiguration() throws SiteWhereException {
	StopConfigurationOperation.createCompletableFuture(this, getMicroserviceOperationsService())
		.thenCompose(m1 -> TerminateConfigurationOperation
			.createCompletableFuture(this, getMicroserviceOperationsService())
			.thenCompose(m2 -> InitializeConfigurationOperation
				.createCompletableFuture(this, getMicroserviceOperationsService())
				.thenCompose(m3 -> StartConfigurationOperation
					.createCompletableFuture(this, getMicroserviceOperationsService())
					.exceptionally(t -> {
					    getLogger().error("Unable to restart microservice.", t);
					    return null;
					}))));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.configuration.IConfigurableMicroservice#
     * waitForConfigurationReady()
     */
    @Override
    public void waitForConfigurationReady() throws SiteWhereException {
	getLogger().info("Waiting for configuration to be loaded...");
	long deadline = System.currentTimeMillis() + (1000 * MAX_CONFIGURATION_WAIT_SEC);
	while (true) {
	    if ((deadline - System.currentTimeMillis()) < 0) {
		throw new SiteWhereException("Microservice not configured within allowable timeframe.");
	    }
	    if (getConfigurationState() == ConfigurationState.Failed) {
		throw new SiteWhereException("Microservice configuration failed.");
	    }
	    if (getConfigurationState() == ConfigurationState.Succeeded) {
		getLogger().info("Configuration loaded successfully.");
		return;
	    }
	    try {
		Thread.sleep(1000);
	    } catch (InterruptedException e) {
		return;
	    }
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.Microservice#getConfigurationMonitor()
     */
    @Override
    public IConfigurationMonitor getConfigurationMonitor() {
	return configurationMonitor;
    }

    protected void setConfigurationMonitor(IConfigurationMonitor configurationMonitor) {
	this.configurationMonitor = configurationMonitor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.configuration.IConfigurableMicroservice#
     * getConfigurationState()
     */
    @Override
    public ConfigurationState getConfigurationState() {
	return configurationState;
    }

    protected void setConfigurationState(ConfigurationState configurationState) {
	this.configurationState = configurationState;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.configuration.IConfigurableMicroservice#
     * isConfigurationCacheReady()
     */
    @Override
    public boolean isConfigurationCacheReady() {
	return configurationCacheReady;
    }

    protected void setConfigurationCacheReady(boolean configurationCacheReady) {
	this.configurationCacheReady = configurationCacheReady;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getGlobalApplicationContext()
     */
    @Override
    public ApplicationContext getGlobalApplicationContext() {
	return globalApplicationContext;
    }

    public void setGlobalApplicationContext(ApplicationContext globalApplicationContext) {
	this.globalApplicationContext = globalApplicationContext;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getLocalApplicationContext()
     */
    @Override
    public ApplicationContext getLocalApplicationContext() {
	return localApplicationContext;
    }

    public void setLocalApplicationContext(ApplicationContext localApplicationContext) {
	this.localApplicationContext = localApplicationContext;
    }

    public ApplicationContext getMicroserviceContext() {
	return microserviceContext;
    }

    public void setMicroserviceContext(ApplicationContext microserviceContext) {
	this.microserviceContext = microserviceContext;
    }

    /**
     * Allow configurations to be loaded and parsed in a separate thread.
     * 
     * @author Derek
     */
    private class ConfigurationLoader implements Runnable {

	@Override
	public void run() {
	    try {
		setConfigurationState(ConfigurationState.Loading);
		byte[] global = getInstanceManagementConfigurationData();
		if (global == null) {
		    throw new SiteWhereException("Global instance management file not found.");
		}
		ApplicationContext globalContext = ConfigurationUtils.buildGlobalContext(global, getVersion(),
			getMicroserviceContext());

		String path = getConfigurationPath();
		ApplicationContext localContext = null;
		if (path != null) {
		    String fullPath = getInstanceConfigurationPath() + "/" + path;
		    getLogger().debug("Loading configuration at path: " + fullPath);
		    byte[] data = getConfigurationMonitor().getConfigurationDataFor(fullPath);
		    if (data != null) {
			localContext = ConfigurationUtils.buildSubcontext(data, getVersion(), globalContext);
		    } else {
			throw new SiteWhereException("Required microservice configuration not found: " + fullPath);
		    }
		}

		// Store contexts for later use.
		setGlobalApplicationContext(globalContext);
		setLocalApplicationContext(localContext);

		// Allow components depending on configuration to proceed.
		ILifecycleProgressMonitor monitor = new LifecycleProgressMonitor(
			new LifecycleProgressContext(1, "Configure microservice"), ConfigurableMicroservice.this);
		configurationInitialize(globalContext, localContext, monitor);
		configurationStart(globalContext, localContext, monitor);
		setConfigurationState(ConfigurationState.Succeeded);
	    } catch (SiteWhereException e) {
		getLogger().error("Unable to load configuration data.", e);
		setConfigurationState(ConfigurationState.Failed);
	    } catch (Throwable e) {
		getLogger().error("Unhandled exception while loading configuration data.", e);
		setConfigurationState(ConfigurationState.Failed);
	    }
	}
    }

    /** Used for naming configuration loader threads */
    private class ConfigurationLoaderThreadFactory implements ThreadFactory {

	/** Counts threads */
	private AtomicInteger counter = new AtomicInteger();

	public Thread newThread(Runnable r) {
	    return new Thread(r, "Configuration Loader " + counter.incrementAndGet());
	}
    }
}