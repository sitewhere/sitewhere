/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.sitewhere.SiteWhere;
import com.sitewhere.configuration.ConfigurationUtils;
import com.sitewhere.microservice.Microservice;
import com.sitewhere.microservice.spi.configuration.ConfigurationState;
import com.sitewhere.microservice.spi.configuration.IConfigurableMicroservice;
import com.sitewhere.microservice.spi.configuration.IConfigurationListener;
import com.sitewhere.microservice.spi.configuration.IConfigurationMonitor;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.server.lifecycle.InitializeComponentLifecycleStep;
import com.sitewhere.server.lifecycle.SimpleLifecycleStep;
import com.sitewhere.server.lifecycle.StartComponentLifecycleStep;
import com.sitewhere.server.lifecycle.StopComponentLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
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

    /** Relative path to instance global configuration file */
    private static final String INSTANCE_GLOBAL_CONFIGURATION_PATH = "/instance-global.xml";

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

    /** Instance global context information */
    private ApplicationContext instanceGlobalContext;

    /** Get map of global contexts by path */
    private Map<String, ApplicationContext> globalContexts;

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
     * @see com.sitewhere.microservice.spi.configuration.IConfigurationListener#
     * onConfigurationAdded(java.lang.String, byte[])
     */
    @Override
    public void onConfigurationAdded(String path, byte[] data) {
	if (isConfigurationCacheReady()) {
	    getLogger().debug("Configuration added for '" + path + "'.");
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
	if (isConfigurationCacheReady()) {
	    getLogger().debug("Configuration updated for '" + path + "'.");
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
	if (isConfigurationCacheReady()) {
	    getLogger().debug("Configuration deleted for '" + path + "'.");
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.configuration.IConfigurableMicroservice#
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
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.configuration.IConfigurableMicroservice#
     * getInstanceGlobalConfigurationPath()
     */
    @Override
    public String getInstanceGlobalConfigurationPath() {
	return getInstanceConfigurationPath() + INSTANCE_GLOBAL_CONFIGURATION_PATH;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.configuration.IConfigurableMicroservice#
     * getInstanceGlobalConfigurationData()
     */
    @Override
    public byte[] getInstanceGlobalConfigurationData() throws SiteWhereException {
	return getConfigurationMonitor().getConfigurationDataFor(getInstanceGlobalConfigurationPath());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.configuration.IConfigurableMicroservice#
     * getInstanceTenantsConfigurationPath()
     */
    @Override
    public String getInstanceTenantsConfigurationPath() throws SiteWhereException {
	return getInstanceConfigurationPath() + INSTANCE_TENANTS_CONFIGURATION_PATH;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.configuration.IConfigurableMicroservice#
     * getInstanceTenantConfigurationPath(java.lang.String)
     */
    @Override
    public String getInstanceTenantConfigurationPath(String tenantId) throws SiteWhereException {
	return getInstanceTenantsConfigurationPath() + "/" + tenantId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.configuration.IConfigurableMicroservice#
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
	initialize.addStep(new InitializeComponentLifecycleStep(this, getConfigurationMonitor(),
		"Configuration Monitor", "Unable to initialize configuration monitor", true));

	// Start configuration monitor.
	initialize.addStep(new StartComponentLifecycleStep(this, getConfigurationMonitor(), "Configuration Monitor",
		"Unable to start configuration monitor", true));

	// Execute initialization steps.
	initialize.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.configuration.IConfigurableMicroservice#
     * initializeDiscoverableBeans(org.springframework.context.
     * ApplicationContext,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public ILifecycleStep initializeDiscoverableBeans(ApplicationContext context, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException {
	return new SimpleLifecycleStep("Initialize discoverable beans") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		Map<String, IDiscoverableTenantLifecycleComponent> components = context
			.getBeansOfType(IDiscoverableTenantLifecycleComponent.class);

		for (IDiscoverableTenantLifecycleComponent component : components.values()) {
		    initializeNestedComponent(component, monitor,
			    "Unable to initialize " + component.getComponentName() + ".", component.isRequired());
		}
	    }
	};
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.configuration.IConfigurableMicroservice#
     * startDiscoverableBeans(org.springframework.context.ApplicationContext,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public ILifecycleStep startDiscoverableBeans(ApplicationContext context, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException {
	return new SimpleLifecycleStep("Start discoverable beans") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		Map<String, IDiscoverableTenantLifecycleComponent> components = context
			.getBeansOfType(IDiscoverableTenantLifecycleComponent.class);

		for (IDiscoverableTenantLifecycleComponent component : components.values()) {
		    startNestedComponent(component, monitor, "Unable to start " + component.getComponentName() + ".",
			    component.isRequired());
		}
	    }
	};
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.configuration.IConfigurableMicroservice#
     * stopDiscoverableBeans(org.springframework.context.ApplicationContext,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public ILifecycleStep stopDiscoverableBeans(ApplicationContext context, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException {
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
	stop.addStep(new StopComponentLifecycleStep(this, getConfigurationMonitor(), "Configuration Monitor"));

	// Execute shutdown steps.
	stop.execute(monitor);

	// Terminate configuration monitor.
	getConfigurationMonitor().lifecycleTerminate(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.configuration.IConfigurableMicroservice#
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
     * @see
     * com.sitewhere.microservice.spi.configuration.IConfigurableMicroservice#
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
     * @see
     * com.sitewhere.microservice.spi.configuration.IConfigurableMicroservice#
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
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.configuration.IConfigurableMicroservice#
     * getInstanceGlobalContext()
     */
    @Override
    public ApplicationContext getInstanceGlobalContext() {
	return instanceGlobalContext;
    }

    protected void setInstanceGlobalContext(ApplicationContext instanceGlobalContext) {
	this.instanceGlobalContext = instanceGlobalContext;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.configuration.IConfigurableMicroservice#
     * getGlobalContexts()
     */
    @Override
    public Map<String, ApplicationContext> getGlobalContexts() {
	return globalContexts;
    }

    protected void setGlobalContexts(Map<String, ApplicationContext> globalContexts) {
	this.globalContexts = globalContexts;
    }

    protected ApplicationContext getMicroserviceContext() {
	return microserviceContext;
    }

    protected void setMicroserviceContext(ApplicationContext microserviceContext) {
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
		byte[] global = getInstanceGlobalConfigurationData();
		if (global == null) {
		    throw new SiteWhereException("Global instance configuration file not found.");
		}
		ApplicationContext globalContext = ConfigurationUtils.buildGlobalContext(global, SiteWhere.getVersion(),
			getMicroserviceContext());

		Map<String, ApplicationContext> contexts = new HashMap<String, ApplicationContext>();
		for (String path : getConfigurationPaths()) {
		    String fullPath = getInstanceConfigurationPath() + "/" + path;
		    getLogger().info("Loading configuration at path: " + fullPath);
		    byte[] data = getConfigurationMonitor().getConfigurationDataFor(fullPath);
		    if (data != null) {
			ApplicationContext subcontext = ConfigurationUtils.buildSubcontext(data, SiteWhere.getVersion(),
				globalContext);
			contexts.put(path, subcontext);
		    } else {
			throw new SiteWhereException("Required microservice configuration not found: " + fullPath);
		    }
		}

		// Store contexts for later use.
		setInstanceGlobalContext(globalContext);
		setGlobalContexts(contexts);

		// Allow components depending on configuration to proceed.
		initializeFromSpringContexts(globalContext, contexts);
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