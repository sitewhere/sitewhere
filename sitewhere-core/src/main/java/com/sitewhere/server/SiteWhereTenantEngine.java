/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server;

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
import com.sitewhere.common.MarshalUtils;
import com.sitewhere.configuration.ConfigurationUtils;
import com.sitewhere.configuration.ResourceManagerTenantConfigurationResolver;
import com.sitewhere.device.DeviceEventManagementTriggers;
import com.sitewhere.device.DeviceManagementTriggers;
import com.sitewhere.groovy.asset.GroovyAssetModelInitializer;
import com.sitewhere.groovy.configuration.TenantGroovyConfiguration;
import com.sitewhere.groovy.device.GroovyDeviceModelInitializer;
import com.sitewhere.rest.model.command.CommandResponse;
import com.sitewhere.rest.model.resource.request.ResourceCreateRequest;
import com.sitewhere.rest.model.server.SiteWhereTenantEngineState;
import com.sitewhere.rest.model.server.TenantEngineComponent;
import com.sitewhere.server.asset.AssetManagementTriggers;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.server.lifecycle.SimpleLifecycleStep;
import com.sitewhere.server.lifecycle.StartComponentLifecycleStep;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.server.scheduling.QuartzScheduleManager;
import com.sitewhere.server.scheduling.ScheduleManagementTriggers;
import com.sitewhere.server.search.SearchProviderManager;
import com.sitewhere.server.tenant.SiteWhereTenantEngineCommands;
import com.sitewhere.server.tenant.TenantEngineCommand;
import com.sitewhere.server.tenant.TenantTemplate;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.asset.IAssetModuleManager;
import com.sitewhere.spi.command.CommandResult;
import com.sitewhere.spi.command.ICommandResponse;
import com.sitewhere.spi.configuration.IDefaultResourcePaths;
import com.sitewhere.spi.configuration.IGlobalConfigurationResolver;
import com.sitewhere.spi.configuration.ITenantConfigurationResolver;
import com.sitewhere.spi.device.ICachingDeviceManagement;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceManagementCacheProvider;
import com.sitewhere.spi.device.communication.IDeviceCommunication;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.IEventProcessing;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.error.ResourceExistsException;
import com.sitewhere.spi.resource.IMultiResourceCreateResponse;
import com.sitewhere.spi.resource.IResource;
import com.sitewhere.spi.resource.ResourceCreateMode;
import com.sitewhere.spi.resource.ResourceType;
import com.sitewhere.spi.resource.request.IResourceCreateRequest;
import com.sitewhere.spi.scheduling.IScheduleManagement;
import com.sitewhere.spi.scheduling.IScheduleManager;
import com.sitewhere.spi.search.external.ISearchProviderManager;
import com.sitewhere.spi.server.ISiteWhereTenantEngineState;
import com.sitewhere.spi.server.ITenantEngineComponent;
import com.sitewhere.spi.server.groovy.ITenantGroovyConfiguration;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.IDiscoverableTenantLifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;
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

    /** SiteWhere global application context */
    private ApplicationContext globalContext;

    /** Supports global configuration management */
    private IGlobalConfigurationResolver globalConfigurationResolver;

    /** Supports tenant configuration management */
    private ITenantConfigurationResolver tenantConfigurationResolver;

    /** Tenant-scoped Groovy configuration */
    private ITenantGroovyConfiguration groovyConfiguration;

    /** Components registered to participate in SiteWhere server lifecycle */
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
	// Organizes steps for starting server.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("START TENANT '" + getTenant().getName() + "'");

	// Clear the component list.
	start.addStep(new SimpleLifecycleStep("Preparing tenant") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		getLifecycleComponents().clear();
	    }
	});

	// Start base tenant services.
	startBaseServices(start);

	// Start tenant management API implementations.
	startManagementImplementations(start);

	// Start tenant services.
	startTenantServices(start);

	// Verify data models bootstrapped from tenant template.
	start.addStep(new SimpleLifecycleStep("Verifying bootstrap data") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		verifyTenantBootstrapped();
	    }
	});

	// Execute operation and report progress.
	start.execute(monitor);
    }

    /**
     * Start base tenant services.
     * 
     * @param start
     * @throws SiteWhereException
     */
    protected void startBaseServices(ICompositeLifecycleStep start) throws SiteWhereException {
	// Start Groovy configuration.
	start.addStep(new StartComponentLifecycleStep(this, getGroovyConfiguration(),
		"Starting tenant Groovy script engine", "Groovy configuration startup failed.", true));

	// Start lifecycle components.
	for (ITenantLifecycleComponent component : getRegisteredLifecycleComponents()) {
	    start.addStep(new StartComponentLifecycleStep(this, component, "Starting " + component.getComponentName(),
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
	start.addStep(new StartComponentLifecycleStep(this, getAssetManagement(), "Starting asset management",
		"Asset management startup failed.", true));

	// Start device management.
	start.addStep(new StartComponentLifecycleStep(this, getDeviceManagement(), "Starting device management",
		"Device management startup failed.", true));

	// Start device management.
	start.addStep(new StartComponentLifecycleStep(this, getDeviceEventManagement(),
		"Starting device event management", "Device event management startup failed.", true));

	// Start device management.
	start.addStep(new StartComponentLifecycleStep(this, getScheduleManagement(), "Starting schedule management",
		"Schedule management startup failed.", true));
    }

    /**
     * Start tenant services.
     * 
     * @param start
     * @throws SiteWhereException
     */
    protected void startTenantServices(ICompositeLifecycleStep start) throws SiteWhereException {
	// Start device management cache provider if specificed.
	if (getDeviceManagementCacheProvider() != null) {
	    start.addStep(new StartComponentLifecycleStep(this, getDeviceManagementCacheProvider(),
		    "Starting device management cache provider", "Device management cache provider startup failed.",
		    true));
	}

	// Start asset module manager.
	start.addStep(new StartComponentLifecycleStep(this, getAssetModuleManager(), "Starting asset module manager",
		"Asset module manager startup failed.", true));

	// Start search provider manager.
	start.addStep(new StartComponentLifecycleStep(this, getSearchProviderManager(),
		"Starting search provider manager", "Search provider manager startup failed.", true));

	// Start event processing subsystem.
	start.addStep(new StartComponentLifecycleStep(this, getEventProcessing(), "Starting event processing subsystem",
		"Event processing subsystem startup failed.", true));

	// Start device communication subsystem.
	start.addStep(new StartComponentLifecycleStep(this, getDeviceCommunication(),
		"Starting device communication subsystem", "Device communication subsystem startup failed.", true));

	// Start schedule manager.
	start.addStep(new StartComponentLifecycleStep(this, getScheduleManager(), "Starting schedule manager",
		"Schedule manager startup failed.", true));
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

	// Stop the Groovy configuration.
	getGroovyConfiguration().lifecycleStop(monitor);
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

	    // Initialize the Groovy configuration.
	    initializeGroovyConfiguration();

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
     * Verify that tenant has been bootstrapped by model initializers registered
     * in tenant template.
     * 
     * @throws SiteWhereException
     */
    protected void verifyTenantBootstrapped() throws SiteWhereException {
	IResource templateResource = getTenantConfigurationResolver()
		.getResourceForPath(IDefaultResourcePaths.TEMPLATE_JSON_FILE_NAME);
	if (templateResource == null) {
	    LOGGER.info("Tenant already bootstrapped with tenant template data.");
	    return;
	}

	// Bootstrap process waits on a lock resource to handle case where
	// multiple SiteWhere instances using the same datastore are starting at
	// the same time. Only one instance should bootstrap the data.
	IResource lock = SiteWhere.getServer().getRuntimeResourceManager().getTenantResource(getTenant().getId(),
		IDefaultResourcePaths.TENANT_LOCK_RESOURCE_NAME);
	if (lock != null) {
	    while (true) {
		LOGGER.info("Tenant bootstrap process waiting on lock...");
		try {
		    Thread.sleep(1000);
		    lock = SiteWhere.getServer().getRuntimeResourceManager().getTenantResource(getTenant().getId(),
			    IDefaultResourcePaths.TENANT_LOCK_RESOURCE_NAME);
		    // If another instance created a lock and released it, we
		    // can assume that the tenant has already been bootstrapped.
		    if (lock == null) {
			return;
		    }
		} catch (InterruptedException e) {
		    LOGGER.info("Tenant bootstrap process lock interrupted.");
		    return;
		}
	    }
	}

	// Create lock resource to prevent other instances from bootstrapping.
	createLockResource();

	// Unmarshal template and bootstrap from it.
	TenantTemplate template = MarshalUtils.unmarshalJson(templateResource.getContent(), TenantTemplate.class);
	try {
	    bootstrapFromTemplate(template);
	} catch (Throwable t) {
	    throw new SiteWhereException("Unable to bootstrap tenant from tenant template configuration.", t);
	} finally {
	    SiteWhere.getServer().getRuntimeResourceManager().deleteTenantResource(getTenant().getId(),
		    IDefaultResourcePaths.TENANT_LOCK_RESOURCE_NAME);
	}

	// Delete template file to prevent bootstrapping on future startups.
	SiteWhere.getServer().getRuntimeResourceManager().deleteTenantResource(getTenant().getId(),
		IDefaultResourcePaths.TEMPLATE_JSON_FILE_NAME);
    }

    /**
     * Bootstrap tenant data based on information contained in the tenant
     * template.
     * 
     * @param template
     * @throws SiteWhereException
     */
    protected void bootstrapFromTemplate(TenantTemplate template) throws SiteWhereException {
	if (template.getInitializers() != null) {

	    // Execute device management model initializer if configured.
	    if (template.getInitializers().getDeviceManagement() != null) {
		GroovyDeviceModelInitializer dmInit = new GroovyDeviceModelInitializer(getGroovyConfiguration(),
			template.getInitializers().getDeviceManagement());
		try {
		    dmInit.initialize(getDeviceManagement(), getDeviceEventManagement(), getAssetModuleManager());
		} catch (ResourceExistsException e) {
		    LOGGER.warn("Device management initializer data overlaps existing. "
			    + "Skipping further device management initialization.");
		}
	    }

	    // Execute asset management model initializer if configured.
	    if (template.getInitializers().getAssetManagement() != null) {
		GroovyAssetModelInitializer amInit = new GroovyAssetModelInitializer(getGroovyConfiguration(),
			template.getInitializers().getAssetManagement());
		try {
		    amInit.initialize(getTenantConfigurationResolver(), getAssetManagement());
		} catch (ResourceExistsException e) {
		    LOGGER.warn("Asset management initializer data overlaps existing. "
			    + "Skipping further asset management initialization.");
		}
	    }
	}
    }

    /**
     * Create a lock resource to prevent other SiteWhere instances from trying
     * to bootstrap concurrently with this instance.
     * 
     * @throws SiteWhereException
     */
    protected void createLockResource() throws SiteWhereException {
	ResourceCreateRequest request = new ResourceCreateRequest();
	request.setResourceType(ResourceType.ConfigurationFile);
	request.setContent("LOCK".getBytes());
	request.setPath(IDefaultResourcePaths.TENANT_LOCK_RESOURCE_NAME);
	List<IResourceCreateRequest> requests = new ArrayList<IResourceCreateRequest>();
	requests.add(request);

	IMultiResourceCreateResponse response = SiteWhere.getServer().getRuntimeResourceManager()
		.createTenantResources(getTenant().getId(), requests, ResourceCreateMode.FAIL_IF_EXISTS);
	if (response.getErrors().size() > 0) {
	    throw new SiteWhereException(
		    "Unable to create lock resource. " + response.getErrors().get(0).getReason().toString());
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
     * Initialize the Groovy configuration.
     * 
     * @throws SiteWhereException
     */
    protected void initializeGroovyConfiguration() throws SiteWhereException {
	this.groovyConfiguration = new TenantGroovyConfiguration();
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
     * @see com.sitewhere.spi.server.tenant.ISiteWhereTenantEngine#
     * getGroovyConfiguration()
     */
    public ITenantGroovyConfiguration getGroovyConfiguration() {
	return groovyConfiguration;
    }

    public void setGroovyConfiguration(ITenantGroovyConfiguration groovyConfiguration) {
	this.groovyConfiguration = groovyConfiguration;
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