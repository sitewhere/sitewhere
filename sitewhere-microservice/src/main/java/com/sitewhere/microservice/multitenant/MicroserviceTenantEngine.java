/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.multitenant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.context.ApplicationContext;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.configuration.ConfigurationUtils;
import com.sitewhere.microservice.groovy.GroovyConfiguration;
import com.sitewhere.microservice.scripting.TenantEngineScriptManager;
import com.sitewhere.microservice.scripting.TenantEngineScriptSynchronizer;
import com.sitewhere.rest.model.microservice.state.TenantEngineState;
import com.sitewhere.rest.model.tenant.TenantTemplate;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.server.lifecycle.SimpleLifecycleStep;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.groovy.IGroovyConfiguration;
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
 * 
 * @author Derek
 */
public abstract class MicroserviceTenantEngine extends TenantEngineLifecycleComponent
	implements IMicroserviceTenantEngine {

    /** Name for lock path */
    public static final String MODULE_LOCK_NAME = "lock";

    /** Suffix appended to module identifier to locate module configuration */
    public static final String MODULE_CONFIGURATION_SUFFIX = ".xml";

    /** Name for tenant bootstrapped indicator */
    public static final String MODULE_BOOTSTRAPPED_NAME = "bootstrapped";

    /** Tenant template path (relative to configuration root) */
    public static final String TENANT_TEMPLATE_PATH = "/template.json";

    /** Parent microservice */
    private IMultitenantMicroservice<?> microservice;

    /** Hosted tenant */
    private ITenant tenant;

    /** Tenant script synchronizer */
    private TenantEngineScriptSynchronizer tenantScriptSynchronizer;

    /** Script manager */
    private TenantEngineScriptManager scriptManager;

    /** Groovy configuration */
    private IGroovyConfiguration groovyConfiguration;

    /** Module context information */
    private ApplicationContext moduleContext;

    public MicroserviceTenantEngine(IMultitenantMicroservice<?> microservice, ITenant tenant) {
	this.microservice = microservice;
	this.tenant = tenant;
	this.tenantScriptSynchronizer = new TenantEngineScriptSynchronizer(this);
	this.scriptManager = new TenantEngineScriptManager();
	this.groovyConfiguration = new GroovyConfiguration(getTenantScriptSynchronizer());
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
	try {
	    CuratorFramework curator = getMicroservice().getZookeeperManager().getCurator();
	    if (curator.checkExists().forPath(getModuleConfigurationPath()) == null) {
		throw new SiteWhereException("Module configuration '" + getModuleConfigurationPath()
			+ "' does not exist for '" + getTenant().getName() + "'.");
	    }
	    byte[] data = curator.getData().forPath(getModuleConfigurationPath());
	    return data;
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to load module configuration.", e);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * updateModuleConfiguration(byte[])
     */
    @Override
    public void updateModuleConfiguration(byte[] content) throws SiteWhereException {
	try {
	    CuratorFramework curator = getMicroservice().getZookeeperManager().getCurator();
	    if (curator.checkExists().forPath(getModuleConfigurationPath()) == null) {
		curator.create().forPath(getModuleConfigurationPath(), content);
	    } else {
		curator.setData().forPath(getModuleConfigurationPath(), content);
	    }
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to update module configuration.", e);
	}
    }

    /**
     * Load Spring configuration into module context.
     * 
     * @throws SiteWhereException
     */
    protected void loadModuleConfiguration() throws SiteWhereException {
	try {
	    byte[] data = getModuleConfiguration();
	    Map<String, Object> properties = getMicroservice().getSpringProperties();
	    properties.put("tenant.id", getTenant().getId());
	    this.moduleContext = ConfigurationUtils.buildSubcontext(data, properties,
		    getMicroservice().getGlobalApplicationContext());
	    getLogger().info("Successfully loaded module configuration from '" + getModuleConfigurationPath() + "'.");
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
	state.setLifecycleStatus(getLifecycleStatus());
	state.setLifecycleErrorStack(getLifecycleError() != null ? parseErrors(getLifecycleError()) : null);
	state.setTenantToken(getTenant().getToken());
	return state;
    }

    protected List<String> parseErrors(SiteWhereException e) {
	List<String> errors = new ArrayList<>();
	Throwable current = e;
	while (current != null) {
	    errors.add(current.getLocalizedMessage());
	    current = current.getCause();
	}
	return errors;
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
		getMicroservice().restartTenantEngine(getTenant().getId());
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
		getMicroservice().removeTenantEngine(getTenant().getId());
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
	getLogger().debug("Global configuration updated.");
	try {
	    getMicroservice().restartTenantEngine(getTenant().getId());
	} catch (SiteWhereException e) {
	    getLogger().error("Unable to restart after global configuration update.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * getTenantTemplate()
     */
    @Override
    public ITenantTemplate getTenantTemplate() throws SiteWhereException {
	String templatePath = getTenantConfigurationPath() + TENANT_TEMPLATE_PATH;
	CuratorFramework curator = getMicroservice().getZookeeperManager().getCurator();
	try {
	    byte[] data = curator.getData().forPath(templatePath);
	    return MarshalUtils.unmarshalJson(data, TenantTemplate.class);
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to load tenant template from Zk.", e);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * waitForModuleBootstrapped(java.lang.String, long,
     * java.util.concurrent.TimeUnit)
     */
    @Override
    public void waitForModuleBootstrapped(String identifier, long time, TimeUnit unit) throws SiteWhereException {
	String bspath = getMicroservice().getInstanceTenantStatePath(getTenant().getId()) + "/" + identifier + "/"
		+ MicroserviceTenantEngine.MODULE_BOOTSTRAPPED_NAME;
	CuratorFramework curator = getMicroservice().getZookeeperManager().getCurator();
	long deadline = System.currentTimeMillis() + unit.toMillis(time);
	while ((deadline - System.currentTimeMillis()) > 0) {
	    try {
		if (curator.checkExists().forPath(bspath) != null) {
		    return;
		}
		getLogger().info("Waiting for '" + identifier + "' to be bootstrapped before continuing...");
		Thread.sleep(3000);
	    } catch (InterruptedException e) {
		throw new SiteWhereException("Interrupted while waiting for module to be bootstrapped.", e);
	    } catch (Exception e) {
		throw new SiteWhereException("Error checking for module bootstrapped.", e);
	    }
	}
	throw new SiteWhereException("Time limit exceeded for '" + identifier + "' to bootstrap.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * getTenantConfigurationPath()
     */
    @Override
    public String getTenantConfigurationPath() throws SiteWhereException {
	return getMicroservice().getInstanceTenantConfigurationPath(getTenant().getId());
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getTenantStatePath()
     */
    @Override
    public String getTenantStatePath() throws SiteWhereException {
	return getMicroservice().getInstanceTenantStatePath(getTenant().getId()) + "/"
		+ getMicroservice().getIdentifier();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * getModuleLockPath()
     */
    @Override
    public String getModuleLockPath() throws SiteWhereException {
	return getTenantStatePath() + "/" + MicroserviceTenantEngine.MODULE_LOCK_NAME;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getModuleConfigurationName()
     */
    @Override
    public String getModuleConfigurationName() throws SiteWhereException {
	return getMicroservice().getIdentifier() + MicroserviceTenantEngine.MODULE_CONFIGURATION_SUFFIX;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * getModuleConfigurationPath()
     */
    @Override
    public String getModuleConfigurationPath() throws SiteWhereException {
	return getTenantConfigurationPath() + "/" + getModuleConfigurationName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * getModuleBootstrappedPath()
     */
    @Override
    public String getModuleBootstrappedPath() throws SiteWhereException {
	return getTenantStatePath() + "/" + MicroserviceTenantEngine.MODULE_BOOTSTRAPPED_NAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * getMicroservice()
     */
    @Override
    public IMultitenantMicroservice<?> getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IMultitenantMicroservice<?> microservice) {
	this.microservice = microservice;
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