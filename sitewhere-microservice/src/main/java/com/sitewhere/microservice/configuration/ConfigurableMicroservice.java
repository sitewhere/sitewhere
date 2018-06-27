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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.sitewhere.microservice.Microservice;
import com.sitewhere.microservice.operations.InitializeConfigurationOperation;
import com.sitewhere.microservice.operations.StartConfigurationOperation;
import com.sitewhere.microservice.operations.StopConfigurationOperation;
import com.sitewhere.microservice.operations.TerminateConfigurationOperation;
import com.sitewhere.microservice.scripting.ZookeeperScriptManagement;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.server.lifecycle.SimpleLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.configuration.ConfigurationState;
import com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice;
import com.sitewhere.spi.microservice.configuration.IConfigurationListener;
import com.sitewhere.spi.microservice.configuration.IConfigurationMonitor;
import com.sitewhere.spi.microservice.scripting.IScriptManagement;
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
public abstract class ConfigurableMicroservice<T extends IFunctionIdentifier> extends Microservice<T>
	implements IConfigurableMicroservice<T>, IConfigurationListener {

    /** Relative path to instance management configuration file */
    private static final String INSTANCE_MANAGEMENT_CONFIGURATION_PATH = "/instance-management.xml";

    /** Relative path for tenant-specific data */
    private static final String INSTANCE_TENANTS_SUBPATH = "/tenants";

    /** Relative path for script data */
    private static final String SCRIPTS_SUBPATH = "/scripts";

    /** Relative path to tenant bootstrapped indicator data */
    private static final String INSTANCE_TENANT_BOOTSTRAPPED_INDICATOR = "bootstrapped";

    /** Injected Spring context for microservice */
    @Autowired
    private ApplicationContext microserviceApplicationContext;

    /** Configuration monitor */
    private IConfigurationMonitor configurationMonitor;

    /** Script management implementation */
    private IScriptManagement scriptManagement;

    /** Configuration state */
    private ConfigurationState configurationState = ConfigurationState.Unloaded;

    /** Indicates if configuration cache is ready to use */
    private boolean configurationCacheReady = false;

    /** Global instance application context */
    private ApplicationContext globalApplicationContext;

    /** Local microservice application context */
    private ApplicationContext localApplicationContext;

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
	restartConfiguration();
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
     * @see com.sitewhere.spi.microservice.IMicroservice#getSpringProperties()
     */
    @Override
    public Map<String, Object> getSpringProperties() {
	Map<String, Object> properties = new HashMap<>();
	properties.put("sitewhere.edition", getVersion().getEditionIdentifier().toLowerCase());
	return properties;
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
	return getInstanceConfigurationPath() + INSTANCE_TENANTS_SUBPATH;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getInstanceTenantConfigurationPath(java.util.UUID)
     */
    @Override
    public String getInstanceTenantConfigurationPath(UUID tenantId) throws SiteWhereException {
	return getInstanceTenantsConfigurationPath() + "/" + tenantId;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getInstanceTenantScriptsPath(java.util.UUID)
     */
    @Override
    public String getInstanceTenantScriptsPath(UUID tenantId) throws SiteWhereException {
	return getInstanceTenantConfigurationPath(tenantId) + SCRIPTS_SUBPATH;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getInstanceTenantsStatePath()
     */
    @Override
    public String getInstanceTenantsStatePath() throws SiteWhereException {
	return getInstanceStatePath() + INSTANCE_TENANTS_SUBPATH;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getInstanceTenantStatePath(java.util.UUID)
     */
    @Override
    public String getInstanceTenantStatePath(UUID tenantId) throws SiteWhereException {
	return getInstanceTenantsStatePath() + "/" + tenantId;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getInstanceTenantBootstrappedIndicatorPath(java.util.UUID)
     */
    @Override
    public String getInstanceTenantBootstrappedIndicatorPath(UUID tenantId) throws SiteWhereException {
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

	// Create configuration monitor.
	this.configurationMonitor = new ConfigurationMonitor(getZookeeperManager(), getInstanceConfigurationPath());
	getConfigurationMonitor().getListeners().add(this);

	// Create script management support.
	this.scriptManagement = new ZookeeperScriptManagement();

	// Make sure that instance is bootstrapped before configuring.
	waitForInstanceInitialization();

	// Organizes steps for initializing microservice.
	ICompositeLifecycleStep initialize = new CompositeLifecycleStep("Initialize " + getName());

	// Initialize configuration monitor.
	initialize.addInitializeStep(this, getConfigurationMonitor(), true);

	// Start configuration monitor.
	initialize.addStartStep(this, getConfigurationMonitor(), true);

	// Initialize script management.
	initialize.addInitializeStep(this, getScriptManagement(), true);

	// Start script management.
	initialize.addStartStep(this, getScriptManagement(), true);

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

	// Stop script management.
	stop.addStopStep(this, getScriptManagement());

	// Stop configuration monitor.
	stop.addStopStep(this, getConfigurationMonitor());

	// Execute shutdown steps.
	stop.execute(monitor);

	// Terminate script management.
	getScriptManagement().lifecycleTerminate(monitor);

	// Terminate configuration monitor.
	getConfigurationMonitor().lifecycleTerminate(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * restartConfiguration()
     */
    @Override
    public CompletableFuture<?> restartConfiguration() {
	return StopConfigurationOperation.createCompletableFuture(this, getMicroserviceOperationsService())
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
	getLogger().debug("Waiting for configuration to be loaded...");
	while (true) {
	    if (getConfigurationState() == ConfigurationState.Failed) {
		throw new SiteWhereException("Microservice configuration failed.");
	    }
	    if (getConfigurationState() == ConfigurationState.Started) {
		getLogger().debug("Configuration started successfully.");
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
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getScriptManagement()
     */
    @Override
    public IScriptManagement getScriptManagement() {
	return scriptManagement;
    }

    public void setScriptManagement(IScriptManagement scriptManagement) {
	this.scriptManagement = scriptManagement;
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

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * setConfigurationState(com.sitewhere.spi.microservice.configuration.
     * ConfigurationState)
     */
    @Override
    public void setConfigurationState(ConfigurationState configurationState) {
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

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * setGlobalApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
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

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * setLocalApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setLocalApplicationContext(ApplicationContext localApplicationContext) {
	this.localApplicationContext = localApplicationContext;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getMicroserviceApplicationContext()
     */
    @Override
    public ApplicationContext getMicroserviceApplicationContext() {
	return microserviceApplicationContext;
    }

    protected void setMicroserviceApplicationContext(ApplicationContext microserviceApplicationContext) {
	this.microserviceApplicationContext = microserviceApplicationContext;
    }
}