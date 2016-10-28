/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import com.sitewhere.SiteWhere;
import com.sitewhere.configuration.ConfigurationUtils;
import com.sitewhere.configuration.ResourceManagerTenantConfigurationResolver;
import com.sitewhere.device.DeviceEventManagementTriggers;
import com.sitewhere.device.DeviceManagementTriggers;
import com.sitewhere.rest.model.command.CommandResponse;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.server.SiteWhereTenantEngineState;
import com.sitewhere.rest.model.server.TenantEngineComponent;
import com.sitewhere.server.asset.AssetManagementTriggers;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.server.scheduling.QuartzScheduleManager;
import com.sitewhere.server.scheduling.ScheduleManagementTriggers;
import com.sitewhere.server.search.SearchProviderManager;
import com.sitewhere.server.tenant.SiteWhereTenantEngineCommands;
import com.sitewhere.server.tenant.TenantEngineCommand;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.asset.IAssetCategory;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.asset.IAssetModuleManager;
import com.sitewhere.spi.command.CommandResult;
import com.sitewhere.spi.command.ICommandResponse;
import com.sitewhere.spi.configuration.IGlobalConfigurationResolver;
import com.sitewhere.spi.configuration.ITenantConfigurationResolver;
import com.sitewhere.spi.device.ICachingDeviceManagement;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceManagementCacheProvider;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.communication.IDeviceCommunication;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.IEventProcessing;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.resource.IResource;
import com.sitewhere.spi.scheduling.ISchedule;
import com.sitewhere.spi.scheduling.IScheduleManagement;
import com.sitewhere.spi.scheduling.IScheduleManager;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.external.ISearchProviderManager;
import com.sitewhere.spi.server.ISiteWhereTenantEngineState;
import com.sitewhere.spi.server.ITenantEngineComponent;
import com.sitewhere.spi.server.asset.IAssetModelInitializer;
import com.sitewhere.spi.server.device.IDeviceModelInitializer;
import com.sitewhere.spi.server.lifecycle.IDiscoverableTenantLifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;
import com.sitewhere.spi.server.scheduling.IScheduleModelInitializer;
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

    /** SiteWhere global application context */
    private ApplicationContext globalContext;

    /** Supports global configuration management */
    private IGlobalConfigurationResolver globalConfigurationResolver;

    /** Supports tenant configuration management */
    private ITenantConfigurationResolver tenantConfigurationResolver;

    /**
     * List of components registered to participate in SiteWhere server
     * lifecycle
     */
    private List<ITenantLifecycleComponent> registeredLifecycleComponents = new ArrayList<ITenantLifecycleComponent>();

    /** Device management cache provider implementation */
    private IDeviceManagementCacheProvider deviceManagementCacheProvider;

    /** Interface to device management implementation */
    private IDeviceManagement deviceManagement;

    /** Interface to device event management implementation */
    private IDeviceEventManagement deviceEventManagement;

    /** Interface to asset management implementation */
    private IAssetManagement assetManagement;

    /** Interface to schedule management implementation */
    private IScheduleManagement scheduleManagement;

    /** Interface to device communication subsystem implementation */
    private IDeviceCommunication deviceCommunication;

    /** Interface to event processing subsystem implementation */
    private IEventProcessing eventProcessing;

    /** Interface for the asset module manager */
    private IAssetModuleManager assetModuleManager;

    /** Interface for the search provider manager */
    private ISearchProviderManager searchProviderManager;

    /** Interface for the schedule manager */
    private IScheduleManager scheduleManager;

    /** Threads used to issue engine commands */
    private ExecutorService commandExecutor = Executors.newSingleThreadExecutor();

    public SiteWhereTenantEngine(ITenant tenant, ApplicationContext parent, IGlobalConfigurationResolver global) {
	super(LifecycleComponentType.TenantEngine);
	setTenant(tenant);
	this.globalContext = parent;
	this.globalConfigurationResolver = global;
	this.tenantConfigurationResolver = new ResourceManagerTenantConfigurationResolver(tenant,
		SiteWhere.getServer().getVersion(), global);
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
	// Clear the component list.
	getLifecycleComponents().clear();

	// Start lifecycle components.
	for (ITenantLifecycleComponent component : getRegisteredLifecycleComponents()) {
	    startNestedComponent(component, monitor, component.getComponentName() + " startup failed.", true);
	}

	// Start asset management.
	startNestedComponent(getAssetManagement(), monitor, "Asset management startup failed.", true);

	// Start device management.
	startNestedComponent(getDeviceManagement(), monitor, "Device management startup failed.", true);

	// Start device management.
	startNestedComponent(getDeviceEventManagement(), monitor, "Device event management startup failed.", true);

	// Start device management.
	startNestedComponent(getScheduleManagement(), monitor, "Schedule management startup failed.", true);

	// Populate schedule data if requested.
	verifyScheduleModel();

	// Start device management cache provider if specificed.
	if (getDeviceManagementCacheProvider() != null) {
	    startNestedComponent(getDeviceManagementCacheProvider(), monitor,
		    "Device management cache provider startup failed.", true);
	}

	// Populate asset data if requested.
	verifyAssetModel();

	// Start asset module manager.
	startNestedComponent(getAssetModuleManager(), monitor, "Asset module manager startup failed.", true);

	// Start search provider manager.
	startNestedComponent(getSearchProviderManager(), monitor, "Search provider manager startup failed.", true);
	verifyDeviceModel();

	// Start event processing subsystem.
	startNestedComponent(getEventProcessing(), monitor, "Event processing subsystem startup failed.", true);

	// Start device communication subsystem.
	startNestedComponent(getDeviceCommunication(), monitor, "Device communication subsystem startup failed.", true);

	// Start schedule manager.
	startNestedComponent(getScheduleManager(), monitor, "Schedule manager startup failed.", true);
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
	// Stop scheduling new jobs.
	getScheduleManager().lifecycleStop(monitor);
	getScheduleManagement().lifecycleStop(monitor);

	// Disable device communications.
	getDeviceCommunication().lifecycleStop(monitor);
	getEventProcessing().lifecycleStop(monitor);

	// Stop core management implementations.
	if (getDeviceManagementCacheProvider() != null) {
	    getDeviceManagementCacheProvider().lifecycleStop(monitor);
	}

	// Stop lifecycle components.
	for (ITenantLifecycleComponent component : getRegisteredLifecycleComponents()) {
	    component.lifecycleStop(monitor);
	}

	getDeviceEventManagement().lifecycleStop(monitor);
	getDeviceManagement().lifecycleStop(monitor);
	getAssetModuleManager().lifecycleStop(monitor);
	getAssetManagement().lifecycleStop(monitor);
	getSearchProviderManager().lifecycleStop(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereTenantEngine#
     * getRegisteredLifecycleComponents()
     */
    public List<ITenantLifecycleComponent> getRegisteredLifecycleComponents() {
	return registeredLifecycleComponents;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereTenantEngine#getEngineState()
     */
    @Override
    public ISiteWhereTenantEngineState getEngineState() {
	SiteWhereTenantEngineState state = new SiteWhereTenantEngineState();
	state.setLifecycleStatus(getLifecycleStatus());
	if (getLifecycleStatus() == LifecycleStatus.Started) {
	    state.setComponentHierarchyState(getComponentHierarchyState());
	}
	state.setStaged(getTenantConfigurationResolver().hasStagedConfiguration());
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
	List<ILifecycleComponent> children = parent.getLifecycleComponents();
	for (ILifecycleComponent child : children) {
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
     * com.sitewhere.spi.server.ISiteWhereTenantEngine#issueCommand(java.lang.
     * String, int)
     */
    @Override
    public ICommandResponse issueCommand(String command, int maxWaitSeconds) throws SiteWhereException {
	Class<? extends TenantEngineCommand> commandClass = SiteWhereTenantEngineCommands.Command
		.getCommandClass(command);
	if (commandClass == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidTenantEngineCommand, ErrorLevel.ERROR);
	}
	try {
	    LifecycleProgressMonitor monitor = new LifecycleProgressMonitor();
	    TenantEngineCommand cmd = commandClass.newInstance();
	    cmd.setEngine(this);
	    cmd.setProgressMonitor(monitor);
	    Future<ICommandResponse> response = commandExecutor.submit(cmd);
	    return response.get(maxWaitSeconds, TimeUnit.SECONDS);
	} catch (InstantiationException e) {
	    throw new SiteWhereException(e);
	} catch (IllegalAccessException e) {
	    throw new SiteWhereException(e);
	} catch (InterruptedException e) {
	    throw new SiteWhereException(e);
	} catch (ExecutionException e) {
	    throw new SiteWhereException(e);
	} catch (TimeoutException e) {
	    return new CommandResponse(CommandResult.Successful, "Command submitted.");
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereTenantEngine#initialize()
     */
    public boolean initialize() {
	try {
	    // Verify that tenant configuration exists.
	    verifyTenantConfigured();

	    // Initialize the tenant Spring context.
	    initializeSpringContext();

	    // Register discoverable beans.
	    initializeDiscoverableBeans();

	    // Initialize event processing subsystem.
	    setEventProcessing(initializeEventProcessingSubsystem());

	    // Initialize device communication subsystem.
	    setDeviceCommunication(initializeDeviceCommunicationSubsystem());

	    // Initialize device management.
	    setDeviceManagement(initializeDeviceManagement());

	    // Initialize device event management.
	    setDeviceEventManagement(initializeDeviceEventManagement());

	    // Initialize asset management.
	    setAssetManagement(initializeAssetManagement());

	    // Initialize schedule management.
	    setScheduleManagement(initializeScheduleManagement());

	    // Initialize search provider management.
	    setSearchProviderManager(initializeSearchProviderManagement());

	    setLifecycleStatus(LifecycleStatus.Stopped);
	    return true;
	} catch (SiteWhereException e) {
	    setLifecycleError(e);
	    setLifecycleStatus(LifecycleStatus.Error);
	    return false;
	} catch (Throwable e) {
	    setLifecycleError(new SiteWhereException("Unhandled exception in tenant engine initialization.", e));
	    setLifecycleStatus(LifecycleStatus.Error);
	    LOGGER.error("Unhandled exception in tenant engine initialization.", e);
	    return false;
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereTenantEngine#getSpringContext()
     */
    @Override
    public ApplicationContext getSpringContext() {
	return tenantContext;
    }

    /**
     * Verifies that a configuration exists for the tenant. If not, one is
     * created based on the default template.
     * 
     * @throws SiteWhereException
     */
    protected void verifyTenantConfigured() throws SiteWhereException {
	if (!getTenantConfigurationResolver().hasValidConfiguration()) {
	    getTenantConfigurationResolver().copyTenantTemplateResources();
	}
    }

    /**
     * Loads the tenant configuration file. If a new configuration is staged, it
     * is transitioned into the active configuration. If no configuration is
     * found for a tenant, a new one is created from the tenant template.
     * 
     * @throws SiteWhereException
     */
    protected void initializeSpringContext() throws SiteWhereException {
	// Handle staged configuration if available.
	LOGGER.info("Checking for staged tenant configuration.");
	IResource config = getTenantConfigurationResolver().getStagedTenantConfiguration();
	if (config != null) {
	    LOGGER.info(
		    "Staged tenant configuration found for '" + getTenant().getName() + "'. Transitioning to active.");
	    getTenantConfigurationResolver().transitionStagedToActiveTenantConfiguration();
	} else {
	    LOGGER.info("No staged tenant configuration found.");
	}

	// Load the active configuration and copy the default if necessary.
	LOGGER.info("Loading active tenant configuration for '" + getTenant().getName() + "'.");
	config = getTenantConfigurationResolver().getActiveTenantConfiguration();
	if (config == null) {
	    LOGGER.info("No active configuration found. Copying configuration from template.");
	    config = getTenantConfigurationResolver().copyTenantTemplateResources();
	}
	this.tenantContext = ConfigurationUtils.buildTenantContext(config, getTenant(),
		SiteWhere.getServer().getVersion(), globalContext);
    }

    /**
     * Initialize beans marked with
     * {@link IDiscoverableTenantLifecycleComponent} interface and add them as
     * registered components.
     * 
     * @throws SiteWhereException
     */
    protected void initializeDiscoverableBeans() throws SiteWhereException {
	Map<String, IDiscoverableTenantLifecycleComponent> components = tenantContext
		.getBeansOfType(IDiscoverableTenantLifecycleComponent.class);
	getRegisteredLifecycleComponents().clear();

	LOGGER.info("Registering " + components.size() + " discoverable components.");
	for (IDiscoverableTenantLifecycleComponent component : components.values()) {
	    LOGGER.info("Registering " + component.getComponentName() + ".");
	    getRegisteredLifecycleComponents().add(component);
	}
    }

    /**
     * Initialize device management implementation and associated decorators.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceManagement initializeDeviceManagement() throws SiteWhereException {
	// Load device management cache provider if configured.
	try {
	    this.deviceManagementCacheProvider = (IDeviceManagementCacheProvider) tenantContext
		    .getBean(SiteWhereServerBeans.BEAN_DEVICE_MANAGEMENT_CACHE_PROVIDER);
	    LOGGER.info(
		    "Device management cache provider using: " + deviceManagementCacheProvider.getClass().getName());
	} catch (NoSuchBeanDefinitionException e) {
	    LOGGER.info("No device management cache provider configured. Caching disabled.");
	}

	// Verify that a device management implementation exists.
	try {
	    IDeviceManagement deviceManagementImpl = (IDeviceManagement) tenantContext
		    .getBean(SiteWhereServerBeans.BEAN_DEVICE_MANAGEMENT);
	    LOGGER.info("Device management implementation using: " + deviceManagementImpl.getClass().getName());

	    return configureDeviceManagement(deviceManagementImpl);
	} catch (NoSuchBeanDefinitionException e) {
	    throw new SiteWhereException("No device management implementation configured.");
	}
    }

    /**
     * Configure device management implementation by injecting configured
     * options or wrapping to add functionality.
     * 
     * @param management
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceManagement configureDeviceManagement(IDeviceManagement management) throws SiteWhereException {
	// Inject cache provider if available.
	if (getDeviceManagementCacheProvider() != null) {
	    if (management instanceof ICachingDeviceManagement) {
		((ICachingDeviceManagement) management).setCacheProvider(getDeviceManagementCacheProvider());
		LOGGER.info("Device management implementation is using configured cache provider.");
	    } else {
		LOGGER.info("Device management implementation not using cache provider.");
	    }
	}

	return new DeviceManagementTriggers(management);
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
	    IDeviceEventManagement configured = configureDeviceEventManagement(management);
	    LOGGER.info("Device event management implementation using: " + configured.getClass().getName());
	    return configured;

	} catch (NoSuchBeanDefinitionException e) {
	    throw new SiteWhereException("No device event management implementation configured.");
	}
    }

    /**
     * Configure device event management implementation by injecting configured
     * options or wrapping to add functionality.
     * 
     * @param management
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceEventManagement configureDeviceEventManagement(IDeviceEventManagement management)
	    throws SiteWhereException {
	// Add reference to device management implementation.
	management.setDeviceManagement(getDeviceManagement());

	// Routes stored events to outbound processing strategy.
	return new DeviceEventManagementTriggers(management);
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
     * Verify and initialize event processing subsystem implementation.
     * 
     * @throws SiteWhereException
     */
    protected IEventProcessing initializeEventProcessingSubsystem() throws SiteWhereException {
	try {
	    return (IEventProcessing) tenantContext.getBean(SiteWhereServerBeans.BEAN_EVENT_PROCESSING);
	} catch (NoSuchBeanDefinitionException e) {
	    throw new SiteWhereException("No event processing subsystem implementation configured.");
	}
    }

    /**
     * Verify and initialize asset module manager.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected IAssetManagement initializeAssetManagement() throws SiteWhereException {
	try {
	    IAssetManagement implementation = (IAssetManagement) tenantContext
		    .getBean(SiteWhereServerBeans.BEAN_ASSET_MANAGEMENT);
	    IAssetManagement withTriggers = new AssetManagementTriggers(implementation);
	    assetModuleManager = (IAssetModuleManager) tenantContext
		    .getBean(SiteWhereServerBeans.BEAN_ASSET_MODULE_MANAGER);
	    return withTriggers;
	} catch (NoSuchBeanDefinitionException e) {
	    throw new SiteWhereException("No asset module manager implementation configured.");
	}
    }

    /**
     * Verify and initialize schedule manager.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected IScheduleManagement initializeScheduleManagement() throws SiteWhereException {
	try {
	    IScheduleManagement implementation = (IScheduleManagement) tenantContext
		    .getBean(SiteWhereServerBeans.BEAN_SCHEDULE_MANAGEMENT);
	    IScheduleManagement withTriggers = new ScheduleManagementTriggers(implementation);
	    scheduleManager = (IScheduleManager) new QuartzScheduleManager(withTriggers);
	    return withTriggers;
	} catch (NoSuchBeanDefinitionException e) {
	    throw new SiteWhereException("No schedule manager implementation configured.");
	}
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

    /**
     * Read a line from standard in.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected String readLine() throws SiteWhereException {
	try {
	    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    return br.readLine();
	} catch (IOException e) {
	    throw new SiteWhereException(e);
	}
    }

    /**
     * Check whether device model is populated and offer to bootstrap system if
     * not.
     * 
     * @throws SiteWhereException
     */
    protected void verifyDeviceModel() throws SiteWhereException {
	try {
	    IDeviceModelInitializer init = (IDeviceModelInitializer) tenantContext
		    .getBean(SiteWhereServerBeans.BEAN_DEVICE_MODEL_INITIALIZER);
	    ISearchResults<ISite> sites = getDeviceManagement().listSites(new SearchCriteria(1, 1));
	    if (sites.getNumResults() == 0) {
		init.initialize(getDeviceManagement(), getDeviceEventManagement(), getAssetModuleManager());
	    }
	} catch (NoSuchBeanDefinitionException e) {
	    LOGGER.info("No device model initializer found in Spring bean configuration. Skipping.");
	    return;
	}
    }

    /**
     * Check whether asset model is populated and offer to bootstrap system if
     * not.
     * 
     * @throws SiteWhereException
     */
    protected void verifyAssetModel() throws SiteWhereException {
	try {
	    IAssetModelInitializer init = (IAssetModelInitializer) tenantContext
		    .getBean(SiteWhereServerBeans.BEAN_ASSET_MODEL_INITIALIZER);
	    ISearchResults<IAssetCategory> categories = getAssetManagement()
		    .listAssetCategories(new SearchCriteria(1, 1));
	    if (categories.getNumResults() == 0) {
		init.initialize(getTenantConfigurationResolver(), getAssetManagement());
	    }
	} catch (NoSuchBeanDefinitionException e) {
	    LOGGER.info("No asset model initializer found in Spring bean configuration. Skipping.");
	    return;
	}
    }

    /**
     * Check whether schedule model is populated and offer to bootstrap system
     * if not.
     * 
     * @throws SiteWhereException
     */
    protected void verifyScheduleModel() throws SiteWhereException {
	try {
	    IScheduleModelInitializer init = (IScheduleModelInitializer) tenantContext
		    .getBean(SiteWhereServerBeans.BEAN_SCHEDULE_MODEL_INITIALIZER);
	    ISearchResults<ISchedule> schedules = getScheduleManagement().listSchedules(new SearchCriteria(1, 1));
	    if (schedules.getNumResults() == 0) {
		init.initialize(getScheduleManagement());
	    }
	} catch (NoSuchBeanDefinitionException e) {
	    LOGGER.info("No schedule model initializer found in Spring bean configuration. Skipping.");
	    return;
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
     * @see com.sitewhere.spi.server.ISiteWhereTenantEngine#
     * getGlobalConfigurationResolver()
     */
    public IGlobalConfigurationResolver getGlobalConfigurationResolver() {
	return globalConfigurationResolver;
    }

    public void setConfigurationResolver(IGlobalConfigurationResolver configurationResolver) {
	this.globalConfigurationResolver = configurationResolver;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereTenantEngine#
     * getTenantConfigurationResolver()
     */
    public ITenantConfigurationResolver getTenantConfigurationResolver() {
	return tenantConfigurationResolver;
    }

    public void setTenantConfigurationResolver(ITenantConfigurationResolver tenantConfigurationResolver) {
	this.tenantConfigurationResolver = tenantConfigurationResolver;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereTenantEngine#
     * getDeviceManagementCacheProvider()
     */
    public IDeviceManagementCacheProvider getDeviceManagementCacheProvider() {
	return deviceManagementCacheProvider;
    }

    public void setDeviceManagementCacheProvider(IDeviceManagementCacheProvider deviceManagementCacheProvider) {
	this.deviceManagementCacheProvider = deviceManagementCacheProvider;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.ISiteWhereTenantEngine#getDeviceManagement()
     */
    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    public void setDeviceManagement(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
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
     * @see com.sitewhere.spi.server.ISiteWhereTenantEngine#getEventProcessing()
     */
    public IEventProcessing getEventProcessing() {
	return eventProcessing;
    }

    public void setEventProcessing(IEventProcessing eventProcessing) {
	this.eventProcessing = eventProcessing;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.ISiteWhereTenantEngine#getAssetModuleManager()
     */
    public IAssetModuleManager getAssetModuleManager() {
	return assetModuleManager;
    }

    public void setAssetModuleManager(IAssetModuleManager assetModuleManager) {
	this.assetModuleManager = assetModuleManager;
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

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereTenantEngine#getScheduleManager()
     */
    public IScheduleManager getScheduleManager() {
	return scheduleManager;
    }

    public void setScheduleManager(IScheduleManager scheduleManager) {
	this.scheduleManager = scheduleManager;
    }
}