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

import org.springframework.context.ApplicationContext;

import com.sitewhere.configuration.ConfigurationUtils;
import com.sitewhere.microservice.Microservice;
import com.sitewhere.microservice.scripting.KubernetesScriptManagement;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.server.lifecycle.LifecycleProgressContext;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
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
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

import io.sitewhere.k8s.crd.instance.SiteWhereInstance;

/**
 * Base class for microservices that monitor the configuration folder for
 * updates.
 * 
 * @author Derek
 */
public abstract class ConfigurableMicroservice<T extends IFunctionIdentifier> extends Microservice<T>
	implements IConfigurableMicroservice<T>, IConfigurationListener {

    /** Configuration monitor */
    private IConfigurationMonitor configurationMonitor;

    /** Script management implementation */
    private IScriptManagement scriptManagement;

    /** Configuration state */
    private ConfigurationState configurationState = ConfigurationState.Unloaded;

    /** Global instance application context */
    private ApplicationContext globalApplicationContext;

    /** Local microservice application context */
    private ApplicationContext localApplicationContext;

    /** Global configuration */
    private SiteWhereInstance globalConfiguration;

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurationListener#
     * onConfigurationAdded(io.sitewhere.k8s.crd.instance.SiteWhereInstance)
     */
    @Override
    public void onConfigurationAdded(SiteWhereInstance instance) {
	this.globalConfiguration = instance;
	try {
	    initializeAndStart();
	} catch (SiteWhereException e) {
	    getLogger().error("Unable to start microservice.", e);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurationListener#
     * onConfigurationUpdated(io.sitewhere.k8s.crd.instance.SiteWhereInstance)
     */
    @Override
    public void onConfigurationUpdated(SiteWhereInstance instance) {
	this.globalConfiguration = instance;
	try {
	    restartConfiguration();
	} catch (SiteWhereException e) {
	    getLogger().error("Unable to restart microservice.", e);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurationListener#
     * onConfigurationDeleted(io.sitewhere.k8s.crd.instance.SiteWhereInstance)
     */
    @Override
    public void onConfigurationDeleted(SiteWhereInstance instance) {
	try {
	    stopAndTerminate();
	} catch (SiteWhereException e) {
	    getLogger().error("Unable to stop microservice.", e);
	}
	this.globalConfiguration = null;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getGlobalConfiguration()
     */
    @Override
    public SiteWhereInstance getGlobalConfiguration() {
	return globalConfiguration;
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
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.initialize(monitor);
	getLogger().info("Shutting down configurable microservice components...");

	// Create configuration monitor.
	this.configurationMonitor = new KubernetesConfigurationMonitor();
	getConfigurationMonitor().getListeners().add(this);

	// Create script management support.
	this.scriptManagement = new KubernetesScriptManagement();

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
		getLogger().info("Initializing discoverable beans...");
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
		getLogger().info("Starting discoverable beans...");
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
		getLogger().info("Stopping discoverable beans...");
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
		getLogger().info("Terminating discoverable beans...");
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
	// Stop and terminate the configuration components.
	getLogger().info("Shutting down configurable microservice components...");
	stopConfiguration();
	terminateConfiguration();

	super.terminate(monitor);

	// Organizes steps for stopping microservice.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getName());

	// Stop script management.
	stop.addStopStep(this, getScriptManagement());

	// Stop configuration monitor.
	stop.addStopStep(this, getConfigurationMonitor());

	// Terminate script management.
	stop.addTerminateStep(this, getScriptManagement());

	// Terminate configuration monitor.
	stop.addTerminateStep(this, getConfigurationMonitor());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * initializeConfiguration()
     */
    @Override
    public void initializeConfiguration() throws SiteWhereException {
	try {
	    // Load microservice configuration.
	    setConfigurationState(ConfigurationState.Loading);
	    SiteWhereInstance global = getGlobalConfiguration();
	    if (global.getSpec() == null || global.getSpec().getConfiguration() == null) {
		throw new SiteWhereException("Global instance configuration not set. Unable to start microservice.");
	    }
	    byte[] globalConfig = global.getSpec().getConfiguration().getBytes();
	    ApplicationContext globalContext = ConfigurationUtils.buildGlobalContext(this, globalConfig,
		    getMicroservice().getSpringProperties());

	    ApplicationContext localContext = null;
	    byte[] localConfig = getLocalConfiguration(global);
	    if (localConfig != null) {
		localContext = ConfigurationUtils.buildSubcontext(localConfig, getMicroservice().getSpringProperties(),
			globalContext);
	    }

	    // Store contexts for later use.
	    setGlobalApplicationContext(globalContext);
	    setLocalApplicationContext(localContext);
	    setConfigurationState(ConfigurationState.Stopped);

	    ILifecycleProgressMonitor monitor = new LifecycleProgressMonitor(
		    new LifecycleProgressContext(1, "Initialize microservice configuration."), getMicroservice());
	    long start = System.currentTimeMillis();
	    getLogger().info("Initializing from updated configuration...");
	    configurationInitialize(getGlobalApplicationContext(), getLocalApplicationContext(), monitor);
	    if (getLifecycleStatus() == LifecycleStatus.LifecycleError) {
		throw getMicroservice().getLifecycleError();
	    }
	    getLogger().info("Microservice configuration '" + getMicroservice().getName() + "' initialized in "
		    + (System.currentTimeMillis() - start) + "ms.");
	    setConfigurationState(ConfigurationState.Initialized);
	} catch (Throwable t) {
	    getLogger().error("Unable to initialize microservice configuration '" + getMicroservice().getName() + "'.",
		    t);
	    setConfigurationState(ConfigurationState.Failed);
	    throw t;
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * startConfiguration()
     */
    @Override
    public void startConfiguration() throws SiteWhereException {
	try {
	    // Start microservice.
	    ILifecycleProgressMonitor monitor = new LifecycleProgressMonitor(
		    new LifecycleProgressContext(1, "Start microservice configuration."), getMicroservice());
	    long start = System.currentTimeMillis();
	    configurationStart(getGlobalApplicationContext(), getLocalApplicationContext(), monitor);
	    if (getMicroservice().getLifecycleStatus() == LifecycleStatus.LifecycleError) {
		throw getMicroservice().getLifecycleError();
	    }
	    getLogger().info("Microservice configuration '" + getMicroservice().getName() + "' started in "
		    + (System.currentTimeMillis() - start) + "ms.");
	    setConfigurationState(ConfigurationState.Started);
	} catch (Throwable t) {
	    getLogger().error("Unable to start microservice configuration '" + getMicroservice().getName() + "'.", t);
	    setConfigurationState(ConfigurationState.Failed);
	    throw t;
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * stopConfiguration()
     */
    @Override
    public void stopConfiguration() throws SiteWhereException {
	try {
	    // Stop microservice.
	    if (getLifecycleStatus() != LifecycleStatus.Stopped) {
		ILifecycleProgressMonitor monitor = new LifecycleProgressMonitor(
			new LifecycleProgressContext(1, "Stop microservice configuration."), getMicroservice());
		long start = System.currentTimeMillis();
		configurationStop(getGlobalApplicationContext(), getLocalApplicationContext(), monitor);
		if (getLifecycleStatus() == LifecycleStatus.LifecycleError) {
		    throw getMicroservice().getLifecycleError();
		}
		getMicroservice().getLogger().info("Microservice configuration '" + getMicroservice().getName()
			+ "' stopped in " + (System.currentTimeMillis() - start) + "ms.");
	    }
	    setConfigurationState(ConfigurationState.Stopped);
	} catch (Throwable t) {
	    getLogger().error("Unable to stop microservice configuration '" + getMicroservice().getName() + "'.", t);
	    setConfigurationState(ConfigurationState.Failed);
	    throw t;
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * terminateConfiguration()
     */
    @Override
    public void terminateConfiguration() throws SiteWhereException {
	try {
	    // Terminate microservice.
	    if (getLifecycleStatus() != LifecycleStatus.Terminated) {
		ILifecycleProgressMonitor monitor = new LifecycleProgressMonitor(
			new LifecycleProgressContext(1, "Terminate microservice configuration."), this);
		long start = System.currentTimeMillis();
		configurationTerminate(getGlobalApplicationContext(), getLocalApplicationContext(), monitor);
		if (getLifecycleStatus() == LifecycleStatus.LifecycleError) {
		    throw getMicroservice().getLifecycleError();
		}
		getLogger().info("Microservice configuration '" + getName() + "' terminated in "
			+ (System.currentTimeMillis() - start) + "ms.");
	    }
	    setConfigurationState(ConfigurationState.Unloaded);
	} catch (Throwable t) {
	    getLogger().error("Unable to terminate microservice configuration '" + getName() + "'.", t);
	    setConfigurationState(ConfigurationState.Failed);
	    throw t;
	}
    }

    /**
     * Initialize and start the microservice components.
     * 
     * @throws SiteWhereException
     */
    protected void initializeAndStart() throws SiteWhereException {
	initializeConfiguration();
	startConfiguration();
    }

    /**
     * Stop and terminate the microservice components.
     * 
     * @throws SiteWhereException
     */
    protected void stopAndTerminate() throws SiteWhereException {
	stopConfiguration();
	terminateConfiguration();
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * restartConfiguration()
     */
    @Override
    public void restartConfiguration() throws SiteWhereException {
	stopAndTerminate();
	initializeAndStart();
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
	while (true) {
	    if (getConfigurationState() == ConfigurationState.Failed) {
		throw new SiteWhereException("Microservice configuration failed.");
	    }
	    if (getConfigurationState() == ConfigurationState.Started) {
		getLogger().info("Configuration started successfully.");
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
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * microserviceInitialize(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void microserviceInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * microserviceStart(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void microserviceStart(ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * microserviceStop(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void microserviceStop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
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
}