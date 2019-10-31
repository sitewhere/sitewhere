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
import java.util.concurrent.CountDownLatch;

import com.sitewhere.configuration.ConfigurationUtils;
import com.sitewhere.microservice.Microservice;
import com.sitewhere.microservice.scripting.KubernetesScriptManagement;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.server.lifecycle.LifecycleProgressContext;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.server.lifecycle.SimpleLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice;
import com.sitewhere.spi.microservice.configuration.IInstanceConfigurationListener;
import com.sitewhere.spi.microservice.configuration.IInstanceConfigurationMonitor;
import com.sitewhere.spi.microservice.scripting.IScriptManagement;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.ILifecycleStep;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import io.sitewhere.k8s.crd.instance.SiteWhereInstance;

/**
 * Base class for microservices that monitor the configuration folder for
 * updates.
 * 
 * @author Derek
 */
public abstract class ConfigurableMicroservice<T extends IFunctionIdentifier> extends Microservice<T>
	implements IConfigurableMicroservice<T>, IInstanceConfigurationListener {

    /** Configuration monitor */
    private IInstanceConfigurationMonitor configurationMonitor;

    /** Script management implementation */
    private IScriptManagement scriptManagement;

    /** Global instance application context */
    private Object globalApplicationContext;

    /** Local microservice application context */
    private Object localApplicationContext;

    /** Latest instance configuration */
    private SiteWhereInstance lastInstanceConfiguration;

    /** Latch for instance config availability */
    private CountDownLatch instanceConfigAvailable = new CountDownLatch(1);

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurationListener#
     * onConfigurationAdded(io.sitewhere.k8s.crd.instance.SiteWhereInstance)
     */
    @Override
    public void onConfigurationAdded(SiteWhereInstance instance) {
	onConfigurationUpdated(instance);
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurationListener#
     * onConfigurationUpdated(io.sitewhere.k8s.crd.instance.SiteWhereInstance)
     */
    @Override
    public void onConfigurationUpdated(SiteWhereInstance instance) {
	// Skip partially configured instance.
	if (instance.getSpec().getConfiguration() == null) {
	    return;
	}

	boolean wasConfigured = getLastInstanceConfiguration() != null
		&& getLastInstanceConfiguration().getSpec().getConfiguration() != null;
	boolean configUpdated = wasConfigured && getLastInstanceConfiguration().getSpec().getConfiguration()
		.equals(instance.getSpec().getConfiguration());

	// Save updated instance.
	this.lastInstanceConfiguration = instance;

	// If configuration was not updated, skip context restart.
	if (wasConfigured && !configUpdated) {
	    return;
	}

	getLogger().info("Detected configuration update. Reloading context...");
	getMicroserviceOperationsService().execute(new Runnable() {

	    @Override
	    public void run() {
		try {
		    if (!wasConfigured) {
			initializeAndStart();
		    } else {
			restartConfiguration();
		    }
		    getInstanceConfigAvailable().countDown();
		} catch (SiteWhereException e) {
		    getLogger().error("Unable to restart microservice.", e);
		}
	    }
	});
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurationListener#
     * onConfigurationDeleted(io.sitewhere.k8s.crd.instance.SiteWhereInstance)
     */
    @Override
    public void onConfigurationDeleted(SiteWhereInstance instance) {
	getMicroserviceOperationsService().execute(new Runnable() {

	    @Override
	    public void run() {
		try {
		    stopAndTerminate();
		} catch (SiteWhereException e) {
		    getLogger().error("Unable to stop microservice.", e);
		}
	    }
	});

	this.lastInstanceConfiguration = null;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getLastInstanceConfiguration()
     */
    @Override
    public SiteWhereInstance getLastInstanceConfiguration() {
	return lastInstanceConfiguration;
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

	// Create script management support.
	this.scriptManagement = new KubernetesScriptManagement();

	// Make sure that instance is bootstrapped before configuring.
	waitForInstanceInitialization();

	// Organizes steps for initializing microservice.
	ICompositeLifecycleStep initialize = new CompositeLifecycleStep("Initialize " + getName());

	// Initialize script management.
	initialize.addInitializeStep(this, getScriptManagement(), true);

	// Start script management.
	initialize.addStartStep(this, getScriptManagement(), true);

	// Execute initialization steps.
	initialize.execute(monitor);
    }

    /*
     * @see
     * com.sitewhere.microservice.Microservice#createKubernetesResourceControllers(
     * io.fabric8.kubernetes.client.informers.SharedInformerFactory)
     */
    @Override
    public void createKubernetesResourceControllers(SharedInformerFactory informers) throws SiteWhereException {
	super.createKubernetesResourceControllers(informers);

	// Add shared informer for instance configuration monitoring.
	this.configurationMonitor = new InstanceConfigurationMonitor(getKubernetesClient(), informers);
	getConfigurationMonitor().getListeners().add(this);
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * configurationInitialize(java.lang.Object, java.lang.Object,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void configurationInitialize(Object global, Object local, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException {
	if (local != null) {
	    initializeDiscoverableBeans(local).execute(monitor);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * configurationStart(java.lang.Object, java.lang.Object,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void configurationStart(Object global, Object local, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException {
	if (local != null) {
	    startDiscoverableBeans(local).execute(monitor);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * configurationStop(java.lang.Object, java.lang.Object,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void configurationStop(Object global, Object local, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException {
	if (local != null) {
	    stopDiscoverableBeans(local).execute(monitor);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * configurationTerminate(java.lang.Object, java.lang.Object,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void configurationTerminate(Object global, Object local, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException {
	if (local != null) {
	    terminateDiscoverableBeans(local).execute(monitor);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * initializeDiscoverableBeans(java.lang.Object)
     */
    @Override
    public ILifecycleStep initializeDiscoverableBeans(Object context) throws SiteWhereException {
	return new SimpleLifecycleStep("Initialize discoverable beans") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		getLogger().info("Initializing discoverable beans...");
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
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * startDiscoverableBeans(java.lang.Object)
     */
    @Override
    public ILifecycleStep startDiscoverableBeans(Object context) throws SiteWhereException {
	return new SimpleLifecycleStep("Start discoverable beans") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		getLogger().info("Starting discoverable beans...");
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
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * stopDiscoverableBeans(java.lang.Object)
     */
    @Override
    public ILifecycleStep stopDiscoverableBeans(Object context) throws SiteWhereException {
	return new SimpleLifecycleStep("Stop discoverable beans") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		getLogger().info("Stopping discoverable beans...");
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
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * terminateDiscoverableBeans(java.lang.Object)
     */
    @Override
    public ILifecycleStep terminateDiscoverableBeans(Object context) throws SiteWhereException {
	return new SimpleLifecycleStep("Terminate discoverable beans") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		getLogger().info("Terminating discoverable beans...");
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

	// Terminate script management.
	stop.addTerminateStep(this, getScriptManagement());

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
	    SiteWhereInstance instance = getLastInstanceConfiguration();
	    if (instance == null || instance.getSpec() == null || instance.getSpec().getConfiguration() == null) {
		throw new SiteWhereException("Global instance configuration not set. Unable to start microservice.");
	    }
	    byte[] globalConfig = instance.getSpec().getConfiguration().getBytes();
	    Object globalContext = ConfigurationUtils.buildGlobalContext(this, globalConfig,
		    getMicroservice().getSpringProperties());

	    Object localContext = null;
	    byte[] localConfig = getLocalConfiguration(instance);
	    if (localConfig != null) {
		localContext = ConfigurationUtils.buildSubcontext(localConfig, getMicroservice().getSpringProperties(),
			globalContext);
	    }

	    // Store contexts for later use.
	    setGlobalApplicationContext(globalContext);
	    setLocalApplicationContext(localContext);

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
	} catch (Throwable t) {
	    getLogger().error("Unable to initialize microservice configuration '" + getMicroservice().getName() + "'.",
		    t);
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
	} catch (Throwable t) {
	    getLogger().error("Unable to start microservice configuration '" + getMicroservice().getName() + "'.", t);
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
	} catch (Throwable t) {
	    getLogger().error("Unable to stop microservice configuration '" + getMicroservice().getName() + "'.", t);
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
	} catch (Throwable t) {
	    getLogger().error("Unable to terminate microservice configuration '" + getName() + "'.", t);
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
	if (getInstanceConfigAvailable().getCount() == 0) {
	    return;
	}
	try {
	    getLogger().info("Waiting for configuration to be loaded...");
	    getInstanceConfigAvailable().await();
	} catch (InterruptedException e) {
	    throw new SiteWhereException("Interrupted while waiting for instance configuration to become available.");
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
    public IInstanceConfigurationMonitor getConfigurationMonitor() {
	return configurationMonitor;
    }

    protected void setConfigurationMonitor(IInstanceConfigurationMonitor configurationMonitor) {
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
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getGlobalApplicationContext()
     */
    @Override
    public Object getGlobalApplicationContext() {
	return globalApplicationContext;
    }

    @Override
    public void setGlobalApplicationContext(Object globalApplicationContext) {
	this.globalApplicationContext = globalApplicationContext;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getLocalApplicationContext()
     */
    @Override
    public Object getLocalApplicationContext() {
	return localApplicationContext;
    }

    @Override
    public void setLocalApplicationContext(Object localApplicationContext) {
	this.localApplicationContext = localApplicationContext;
    }

    protected CountDownLatch getInstanceConfigAvailable() {
	return instanceConfigAvailable;
    }
}