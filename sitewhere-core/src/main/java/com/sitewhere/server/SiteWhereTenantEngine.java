/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import com.sitewhere.rest.model.server.TenantEngineComponent;
import com.sitewhere.rest.model.server.TenantRuntimeState;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.server.lifecycle.SimpleLifecycleStep;
import com.sitewhere.server.lifecycle.StartComponentLifecycleStep;
import com.sitewhere.server.lifecycle.StopComponentLifecycleStep;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.server.search.SearchProviderManager;
import com.sitewhere.server.tenant.SiteWhereTenantEngineCommands;
import com.sitewhere.server.tenant.TenantEngineCommand;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.command.ICommandResponse;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.communication.IDeviceCommunication;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.scheduling.IScheduleManagement;
import com.sitewhere.spi.search.external.ISearchProviderManager;
import com.sitewhere.spi.server.ITenantEngineComponent;
import com.sitewhere.spi.server.ITenantRuntimeState;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.IDiscoverableTenantLifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleConstraints;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;
import com.sitewhere.spi.server.tenant.ISiteWhereTenantEngine;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Default implementation of {@link ISiteWhereTenantEngine} for managing
 * processing and data for a SiteWhere tenant.
 * 
 * @author Derek
 */
public class SiteWhereTenantEngine extends TenantLifecycleComponent implements ISiteWhereTenantEngine {

    /** Private logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Spring context for tenant */
    private ApplicationContext tenantContext;

    /** Components registered to participate in SiteWhere server lifecycle */
    private List<ILifecycleComponent> registeredLifecycleComponents = new ArrayList<ILifecycleComponent>();

    /** Map of component ids to lifecycle components */
    private Map<String, ILifecycleComponent> lifecycleComponentsById = new HashMap<String, ILifecycleComponent>();

    /** Interface to device management implementation */
    private IDeviceManagement deviceManagement;

    /** Device management with associated triggers */
    private IDeviceManagement deviceManagementWithTriggers;

    /** Interface to device event management implementation */
    private IDeviceEventManagement deviceEventManagement;

    /** Interface to asset management implementation */
    private IAssetManagement assetManagement;

    /** Interface to schedule management implementation */
    private IScheduleManagement scheduleManagement;

    /** Interface to device communication subsystem implementation */
    private IDeviceCommunication deviceCommunication;

    /** Interface for the search provider manager */
    private ISearchProviderManager searchProviderManager;

    /** Threads used to issue engine commands */
    private ExecutorService commandExecutor = Executors.newSingleThreadExecutor();

    public SiteWhereTenantEngine(ITenant tenant, ApplicationContext parent) {
	super(LifecycleComponentType.TenantEngine);
	setTenant(tenant);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#canInitialize()
     */
    @Override
    public boolean canInitialize() throws SiteWhereException {
	if (getLifecycleStatus() == LifecycleStatus.Started) {
	    throw new SiteWhereSystemException(ErrorCode.TenantAlreadyStarted, ErrorLevel.ERROR);
	}
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereTenantEngine#initialize()
     */
    public void initialize(ILifecycleProgressMonitor monitor) {
	try {
	    // Register discoverable beans.
	    initializeDiscoverableBeans(monitor);

	    // Initialize device communication subsystem.
	    setDeviceCommunication(initializeDeviceCommunicationSubsystem());

	    // Initialize all management implementations.
	    initializeManagementImplementations(monitor);

	    // Initialize search provider management.
	    setSearchProviderManager(initializeSearchProviderManagement());

	    // Start core functions that must run regardless of whether the
	    // tenant is considered 'started'.
	    startCoreFunctions(monitor);

	    setLifecycleStatus(LifecycleStatus.Stopped);
	} catch (SiteWhereException e) {
	    setLifecycleError(e);
	    setLifecycleStatus(LifecycleStatus.InitializationError);
	} catch (Throwable e) {
	    setLifecycleError(new SiteWhereException("Unhandled exception in tenant engine initialization.", e));
	    setLifecycleStatus(LifecycleStatus.InitializationError);
	    LOGGER.error("Unhandled exception in tenant engine initialization.", e);
	}
    }

    /**
     * Start core functionality that must run regardless of whether the tenant
     * is truly 'started'.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    private void startCoreFunctions(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Organizes steps for starting server.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Started tenant '" + getTenant().getName() + "'");

	// Start base tenant services.
	startBaseServices(start);

	// Start tenant management API implementations.
	startManagementImplementations(start);

	// Execute all operations.
	start.execute(monitor);
    }

    /**
     * Initialize beans marked with
     * {@link IDiscoverableTenantLifecycleComponent} interface and add them as
     * registered components.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    protected void initializeDiscoverableBeans(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	Map<String, IDiscoverableTenantLifecycleComponent> components = tenantContext
		.getBeansOfType(IDiscoverableTenantLifecycleComponent.class);
	getRegisteredLifecycleComponents().clear();

	LOGGER.info("Registering " + components.size() + " discoverable components.");
	for (IDiscoverableTenantLifecycleComponent component : components.values()) {
	    LOGGER.info("Registering " + component.getComponentName() + ".");
	    initializeNestedComponent(component, monitor, "Unable to initialize discoverable component.", false);
	    getRegisteredLifecycleComponents().add(component);
	}
    }

    /**
     * Initialize all management implementations.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    protected void initializeManagementImplementations(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Initialize device management.
	setDeviceManagement(initializeDeviceManagement(monitor));

	// Initialize device event management.
	setDeviceEventManagement(initializeDeviceEventManagement());
    }

    /**
     * Initialize device management implementation and associated decorators.
     * 
     * @param monitor
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceManagement initializeDeviceManagement(ILifecycleProgressMonitor monitor)
	    throws SiteWhereException {
	// Verify that a device management implementation exists.
	try {
	    deviceManagement = (IDeviceManagement) tenantContext.getBean(SiteWhereServerBeans.BEAN_DEVICE_MANAGEMENT);
	    LOGGER.info("Device management implementation using: " + deviceManagement.getClass().getName());

	    return deviceManagement;
	} catch (NoSuchBeanDefinitionException e) {
	    throw new SiteWhereException("No device management implementation configured.");
	}
    }

    /**
     * Initialize device event management implementation.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceEventManagement initializeDeviceEventManagement() throws SiteWhereException {
	// Verify that a device event management implementation exists.
	try {
	    IDeviceEventManagement management = (IDeviceEventManagement) tenantContext
		    .getBean(SiteWhereServerBeans.BEAN_DEVICE_EVENT_MANAGEMENT);
	    LOGGER.info("Device event management implementation using: " + management.getClass().getName());
	    return management;

	} catch (NoSuchBeanDefinitionException e) {
	    throw new SiteWhereException("No device event management implementation configured.");
	}
    }

    /**
     * Verify and initialize device communication subsystem implementation.
     * 
     * @throws SiteWhereException
     */
    protected IDeviceCommunication initializeDeviceCommunicationSubsystem() throws SiteWhereException {
	try {
	    return (IDeviceCommunication) tenantContext.getBean(SiteWhereServerBeans.BEAN_DEVICE_COMMUNICATION);
	} catch (NoSuchBeanDefinitionException e) {
	    throw new SiteWhereException("No device communication subsystem implementation configured.");
	}
    }

    /**
     * Start base tenant services.
     * 
     * @param start
     * @throws SiteWhereException
     */
    protected void startBaseServices(ICompositeLifecycleStep start) throws SiteWhereException {
	// Start lifecycle components.
	for (ILifecycleComponent component : getRegisteredLifecycleComponents()) {
	    start.addStep(new StartComponentLifecycleStep(this, component, "Started " + component.getComponentName(),
		    component.getComponentName() + " startup failed.", true));
	}
    }

    /**
     * Start tenant management API implementations.
     * 
     * @param start
     * @throws SiteWhereException
     */
    protected void startManagementImplementations(ICompositeLifecycleStep start) throws SiteWhereException {
	// Start asset management.
	start.addStep(new StartComponentLifecycleStep(this, getAssetManagement(), "Started asset management",
		"Asset management startup failed.", true));

	// Start device management.
	start.addStep(new StartComponentLifecycleStep(this, getDeviceManagement(), "Started device management",
		"Device management startup failed.", true));

	// Start device management.
	start.addStep(new StartComponentLifecycleStep(this, getDeviceEventManagement(),
		"Started device event management", "Device event management startup failed.", true));

	// Start device management.
	start.addStep(new StartComponentLifecycleStep(this, getScheduleManagement(), "Started schedule management",
		"Schedule management startup failed.", true));
    }

    /**
     * Verify and initialize search provider manager.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected ISearchProviderManager initializeSearchProviderManagement() throws SiteWhereException {
	try {
	    return (ISearchProviderManager) tenantContext.getBean(SiteWhereServerBeans.BEAN_SEARCH_PROVIDER_MANAGER);
	} catch (NoSuchBeanDefinitionException e) {
	    return new SearchProviderManager();
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#canStart()
     */
    @Override
    public boolean canStart() throws SiteWhereException {
	if (getLifecycleStatus() == LifecycleStatus.Started) {
	    throw new SiteWhereSystemException(ErrorCode.TenantAlreadyStarted, ErrorLevel.ERROR);
	}
	return true;
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
	// Organizes steps for starting server.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Started tenant '" + getTenant().getName() + "'");

	// Clear the component list.
	start.addStep(new SimpleLifecycleStep("Prepared tenant") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		getLifecycleComponents().clear();
	    }
	});

	// Start tenant services.
	startTenantServices(start);

	// Finish tenant startup.
	start.addStep(new SimpleLifecycleStep("Verified bootstrap data") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		// Force refresh on components-by-id map.
		lifecycleComponentsById = buildComponentMap();
	    }
	});

	// Execute operation and report progress.
	start.execute(monitor);
    }

    /**
     * Start tenant services.
     * 
     * @param start
     * @throws SiteWhereException
     */
    protected void startTenantServices(ICompositeLifecycleStep start) throws SiteWhereException {
	// Start search provider manager.
	start.addStep(new StartComponentLifecycleStep(this, getSearchProviderManager(),
		"Started search provider manager", "Search provider manager startup failed.", true));

	// Start device communication subsystem.
	start.addStep(new StartComponentLifecycleStep(this, getDeviceCommunication(),
		"Started device communication subsystem", "Device communication subsystem startup failed.", true));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#canStop()
     */
    @Override
    public boolean canStop() throws SiteWhereException {
	if (getLifecycleStatus() == LifecycleStatus.Stopped) {
	    throw new SiteWhereSystemException(ErrorCode.TenantAlreadyStopped, ErrorLevel.ERROR);
	}
	return true;
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
	stop(monitor, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor,
     * com.sitewhere.spi.server.lifecycle.ILifecycleConstraints)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor, ILifecycleConstraints constraints) throws SiteWhereException {
	boolean persist = ((constraints != null)
		&& (constraints instanceof SiteWhereTenantEngineCommands.PersistentShutdownConstraint));
	stop(monitor, persist);
    }

    /**
     * Stops components, but differentiates between server shutdown and an
     * explicit stop request for the tenant.
     * 
     * @param monitor
     * @param persist
     * @throws SiteWhereException
     */
    protected void stop(ILifecycleProgressMonitor monitor, boolean persist) throws SiteWhereException {
	// Organizes steps for stopping tenant.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stopped tenant '" + getTenant().getName() + "'");

	// Stop tenant services.
	stopTenantServices(stop);

	// Execute operation with progress monitoring.
	stop.execute(monitor);
    }

    /**
     * Stop tenant services.
     * 
     * @param stop
     * @throws SiteWhereException
     */
    protected void stopTenantServices(ICompositeLifecycleStep stop) throws SiteWhereException {
	// Disable device communications.
	stop.addStep(new StopComponentLifecycleStep(this, getDeviceCommunication(),
		"Stopped device communication subsystem"));

	// Stop search provider manager.
	stop.addStep(
		new StopComponentLifecycleStep(this, getSearchProviderManager(), "Stopped search provider manager"));
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
	stopCoreFunctions(monitor);
    }

    /**
     * Stop core functionality. This should only happen if the tenant is
     * completely terminated and not in the standard lifecycle loop.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    protected void stopCoreFunctions(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Organizes steps for stopping tenant.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stopped tenant '" + getTenant().getName() + "'");

	// Stop lifecycle components.
	stop.addStep(new SimpleLifecycleStep("Stopped registered components") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		for (ILifecycleComponent component : getRegisteredLifecycleComponents()) {
		    component.lifecycleStop(monitor);
		}
	    }
	});

	// Stop core management implementations.
	stopManagementServices(stop);

	// Execute operation with progress monitoring.
	stop.execute(monitor);
    }

    /**
     * Stop tenant management services.
     * 
     * @param stop
     * @throws SiteWhereException
     */
    protected void stopManagementServices(ICompositeLifecycleStep stop) throws SiteWhereException {
	// Stop schedule management.
	stop.addStep(new StopComponentLifecycleStep(this, getScheduleManagement(),
		"Stopped schedule management implementation"));

	// Stop device event management.
	stop.addStep(new StopComponentLifecycleStep(this, getDeviceEventManagement(),
		"Stopped device event management implementation"));

	// Stop device management.
	stop.addStep(new StopComponentLifecycleStep(this, getDeviceManagement(),
		"Stopped device management implementation"));

	// Stop asset management.
	stop.addStep(
		new StopComponentLifecycleStep(this, getAssetManagement(), "Stopped asset management implementation"));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleHierarchyRoot#
     * getRegisteredLifecycleComponents()
     */
    public List<ILifecycleComponent> getRegisteredLifecycleComponents() {
	return registeredLifecycleComponents;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleHierarchyRoot#
     * getLifecycleComponentById(java.lang.String)
     */
    @Override
    public ILifecycleComponent getLifecycleComponentById(String id) {
	return lifecycleComponentsById.get(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereTenantEngine#getEngineState()
     */
    @Override
    public ITenantRuntimeState getEngineState() {
	TenantRuntimeState state = new TenantRuntimeState();
	state.setLifecycleStatus(getLifecycleStatus());
	if (getLifecycleStatus() == LifecycleStatus.Started) {
	    state.setComponentHierarchyState(getComponentHierarchyState());
	}
	return state;
    }

    /**
     * Use recursion to get state of hierarchy of lifecycle components.
     * 
     * @return
     */
    protected List<ITenantEngineComponent> getComponentHierarchyState() {
	List<ITenantEngineComponent> results = new ArrayList<ITenantEngineComponent>();

	TenantEngineComponent engine = new TenantEngineComponent();
	engine.setId(getComponentId());
	engine.setName(getComponentName());
	engine.setStatus(getLifecycleStatus());
	engine.setType(getComponentType());
	engine.setParentId(null);
	results.add(engine);

	getComponentHierarchyState(this, results);
	return results;
    }

    /**
     * Recursive call to capture hierarchy of components.
     * 
     * @param parent
     * @param results
     */
    protected void getComponentHierarchyState(ILifecycleComponent parent, List<ITenantEngineComponent> results) {
	Map<String, ILifecycleComponent> children = parent.getLifecycleComponents();
	for (ILifecycleComponent child : children.values()) {
	    TenantEngineComponent component = new TenantEngineComponent();
	    component.setId(child.getComponentId());
	    component.setName(child.getComponentName());
	    component.setStatus(child.getLifecycleStatus());
	    component.setType(child.getComponentType());
	    component.setParentId(parent.getComponentId());
	    results.add(component);
	    getComponentHierarchyState(child, results);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.tenant.ISiteWhereTenantEngine#issueCommand(java.
     * lang.String,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public ICommandResponse issueCommand(String command, ILifecycleProgressMonitor monitor) throws SiteWhereException {
	Class<? extends TenantEngineCommand> commandClass = SiteWhereTenantEngineCommands.Command
		.getCommandClass(command);
	if (commandClass == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidTenantEngineCommand, ErrorLevel.ERROR);
	}
	try {
	    TenantEngineCommand cmd = commandClass.newInstance();
	    cmd.setEngine(this);
	    cmd.setProgressMonitor(monitor);
	    Future<ICommandResponse> response = commandExecutor.submit(cmd);
	    return response.get();
	} catch (InstantiationException e) {
	    throw new SiteWhereException(e);
	} catch (IllegalAccessException e) {
	    throw new SiteWhereException(e);
	} catch (InterruptedException e) {
	    throw new SiteWhereException(e);
	} catch (ExecutionException e) {
	    throw new SiteWhereException(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#getComponentName()
     */
    @Override
    public String getComponentName() {
	return getClass().getSimpleName() + " '" + getTenant().getName() + "' (" + getTenant().getId() + ")";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.ISiteWhereTenantEngine#getDeviceManagement()
     */
    public IDeviceManagement getDeviceManagement() {
	return deviceManagementWithTriggers;
    }

    public void setDeviceManagement(IDeviceManagement deviceManagement) {
	this.deviceManagementWithTriggers = deviceManagement;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.ISiteWhereTenantEngine#getDeviceEventManagement(
     * )
     */
    public IDeviceEventManagement getDeviceEventManagement() {
	return deviceEventManagement;
    }

    public void setDeviceEventManagement(IDeviceEventManagement deviceEventManagement) {
	this.deviceEventManagement = deviceEventManagement;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereTenantEngine#getAssetManagement()
     */
    public IAssetManagement getAssetManagement() {
	return assetManagement;
    }

    public void setAssetManagement(IAssetManagement assetManagement) {
	this.assetManagement = assetManagement;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.ISiteWhereTenantEngine#getScheduleManagement()
     */
    public IScheduleManagement getScheduleManagement() {
	return scheduleManagement;
    }

    public void setScheduleManagement(IScheduleManagement scheduleManagement) {
	this.scheduleManagement = scheduleManagement;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.ISiteWhereTenantEngine#getDeviceCommunication()
     */
    public IDeviceCommunication getDeviceCommunication() {
	return deviceCommunication;
    }

    public void setDeviceCommunication(IDeviceCommunication deviceCommunication) {
	this.deviceCommunication = deviceCommunication;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.ISiteWhereTenantEngine#getSearchProviderManager(
     * )
     */
    public ISearchProviderManager getSearchProviderManager() {
	return searchProviderManager;
    }

    public void setSearchProviderManager(ISearchProviderManager searchProviderManager) {
	this.searchProviderManager = searchProviderManager;
    }
}