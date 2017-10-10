/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.sitewhere.SiteWhere;
import com.sitewhere.common.MarshalUtils;
import com.sitewhere.configuration.ResourceManagerGlobalConfigurationResolver;
import com.sitewhere.core.Boilerplate;
import com.sitewhere.rest.model.search.tenant.TenantSearchCriteria;
import com.sitewhere.rest.model.server.SiteWhereServerRuntime;
import com.sitewhere.rest.model.server.SiteWhereServerRuntime.GeneralInformation;
import com.sitewhere.rest.model.server.SiteWhereServerRuntime.JavaInformation;
import com.sitewhere.rest.model.server.SiteWhereServerState;
import com.sitewhere.rest.model.server.TenantPersistentState;
import com.sitewhere.server.jvm.JvmHistoryMonitor;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.server.lifecycle.LifecycleProgressContext;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.server.lifecycle.SimpleLifecycleStep;
import com.sitewhere.server.lifecycle.StartComponentLifecycleStep;
import com.sitewhere.server.lifecycle.StopComponentLifecycleStep;
import com.sitewhere.server.resource.SiteWhereHomeResourceManager;
import com.sitewhere.spi.ServerStartupException;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.asset.IAssetModuleManager;
import com.sitewhere.spi.batch.IBatchManagement;
import com.sitewhere.spi.configuration.IGlobalConfigurationResolver;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.communication.IDeviceCommunication;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.resource.IResource;
import com.sitewhere.spi.resource.IResourceManager;
import com.sitewhere.spi.scheduling.IScheduleManagement;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.external.ISearchProviderManager;
import com.sitewhere.spi.server.ISiteWhereServer;
import com.sitewhere.spi.server.ISiteWhereServerRuntime;
import com.sitewhere.spi.server.ISiteWhereServerState;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.IDiscoverableTenantLifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;
import com.sitewhere.spi.server.tenant.ISiteWhereTenantEngine;
import com.sitewhere.spi.server.tenant.ITenantPersistentState;
import com.sitewhere.spi.system.IVersion;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.ITenantManagement;
import com.sitewhere.spi.user.IUserManagement;

/**
 * Implementation of {@link ISiteWhereServer} for community edition.
 * 
 * @author Derek Adams
 */
public class SiteWhereServer extends LifecycleComponent implements ISiteWhereServer {

    /** Number of threads in pool for starting tenants */
    private static final int TENANT_OPERATION_PARALLELISM = 5;

    /** Private logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Spring context for server */
    public static ApplicationContext SERVER_SPRING_CONTEXT;

    /** Contains version information */
    private IVersion version;

    /** Persistent server state information */
    private ISiteWhereServerState serverState;

    /** Contains runtime information about the server */
    private ISiteWhereServerRuntime serverRuntime;

    /** Server startup error */
    private ServerStartupException serverStartupError;

    /** Bootstrap resource manager implementation */
    protected IResourceManager bootstrapResourceManager;

    /** Runtime resource manager implementation */
    protected IResourceManager runtimeResourceManager;

    /** Allows Spring configuration to be resolved */
    protected IGlobalConfigurationResolver configurationResolver;

    /** Interface to user management implementation */
    protected IUserManagement userManagement;

    /** Interface to tenant management implementation */
    protected ITenantManagement tenantManagement;

    /** Components registered to participate in SiteWhere server lifecycle */
    private List<ILifecycleComponent> registeredLifecycleComponents = new ArrayList<ILifecycleComponent>();

    /** Map of component ids to lifecycle components */
    private Map<String, ILifecycleComponent> lifecycleComponentsById = new HashMap<String, ILifecycleComponent>();

    /** Map of tenants by authentication token */
    private Map<String, ITenant> tenantsByAuthToken = new HashMap<String, ITenant>();

    /** Map of tenant engines by tenant id */
    private Map<String, ISiteWhereTenantEngine> tenantEnginesById = new HashMap<String, ISiteWhereTenantEngine>();

    /** Metric regsitry */
    private MetricRegistry metricRegistry = new MetricRegistry();

    /** Health check registry */
    private HealthCheckRegistry healthCheckRegistry = new HealthCheckRegistry();

    /** Timestamp when server was started */
    private Long uptime;

    /** Records historical values for JVM */
    private JvmHistoryMonitor jvmHistory = new JvmHistoryMonitor(this);

    /** Thread for executing JVM history monitor */
    private ExecutorService executor;

    /** Thread pool for starting tenants in parallel */
    private ExecutorService tenantOperations;

    public SiteWhereServer() {
	super(LifecycleComponentType.System);

	// Set version information.
	this.version = SiteWhere.getVersion();
    }

    /**
     * Get Spring application context for Atlas server objects.
     * 
     * @return
     */
    public static ApplicationContext getServerSpringContext() {
	return SERVER_SPRING_CONTEXT;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#getVersion()
     */
    public IVersion getVersion() {
	return version;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.ISiteWhereServer#getConfigurationParserClassname
     * ()
     */
    @Override
    public String getConfigurationParserClassname() {
	return "com.sitewhere.spring.handler.ConfigurationParser";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#
     * getTenantConfigurationParserClassname()
     */
    @Override
    public String getTenantConfigurationParserClassname() {
	return "com.sitewhere.spring.handler.TenantConfigurationParser";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#getServerState()
     */
    @Override
    public ISiteWhereServerState getServerState() {
	return serverState;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#getServerState(boolean)
     */
    public ISiteWhereServerRuntime getServerRuntimeInformation(boolean includeHistorical) throws SiteWhereException {
	this.serverRuntime = computeServerRuntime(includeHistorical);
	return serverRuntime;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#getServerStartupError()
     */
    public ServerStartupException getServerStartupError() {
	return serverStartupError;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#setServerStartupError(com.
     * sitewhere.spi .ServerStartupException)
     */
    public void setServerStartupError(ServerStartupException e) {
	this.serverStartupError = e;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.ISiteWhereServer#getBootstrapResourceManager()
     */
    @Override
    public IResourceManager getBootstrapResourceManager() {
	return bootstrapResourceManager;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.ISiteWhereServer#getRuntimeResourceManager()
     */
    @Override
    public IResourceManager getRuntimeResourceManager() {
	return runtimeResourceManager;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#getConfigurationResolver()
     */
    public IGlobalConfigurationResolver getConfigurationResolver() {
	return configurationResolver;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.ISiteWhereServer#getAuthorizedTenants(java.lang.
     * String, boolean)
     */
    @Override
    public List<ITenant> getAuthorizedTenants(String userId, boolean requireStarted) throws SiteWhereException {
	ISearchResults<ITenant> tenants = SiteWhere.getServer().getTenantManagement()
		.listTenants(new TenantSearchCriteria(1, 0));
	List<ITenant> matches = new ArrayList<ITenant>();
	for (ITenant tenant : tenants.getResults()) {
	    if (tenant.getAuthorizedUserIds().contains(userId)) {
		if (requireStarted) {
		    ISiteWhereTenantEngine engine = getTenantEngine(tenant.getId());
		    if ((engine == null) || (engine.getLifecycleStatus() != LifecycleStatus.Started)) {
			continue;
		    }
		}
		matches.add(tenant);
	    }
	}
	return matches;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#getTenantEnginesById()
     */
    @Override
    public Map<String, ISiteWhereTenantEngine> getTenantEnginesById() {
	return tenantEnginesById;
    }

    public void setTenantEnginesById(Map<String, ISiteWhereTenantEngine> tenantEnginesById) {
	this.tenantEnginesById = tenantEnginesById;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#getTenantEngine(java.lang.
     * String)
     */
    @Override
    public ISiteWhereTenantEngine getTenantEngine(String tenantId) throws SiteWhereException {
	ISiteWhereTenantEngine engine = getTenantEnginesById().get(tenantId);
	if (engine == null) {
	    ITenant tenant = getTenantManagement().getTenantById(tenantId);
	    if (tenant == null) {
		return null;
	    }
	    engine = initializeTenantEngine(tenant);
	}
	return engine;
    }

    /**
     * If we have an auth token entry for the tenant, remove it.
     * 
     * @param tenant
     */
    protected void removeTenantForAuthToken(ITenant tenant) {
	for (ITenant current : tenantsByAuthToken.values()) {
	    if (current.getId().equals(tenant.getId())) {
		tenantsByAuthToken.remove(current);
	    }
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.ISiteWhereServer#onTenantUpdated(com.sitewhere.
     * spi.tenant.ITenant)
     */
    @Override
    public void onTenantUpdated(ITenant tenant) throws SiteWhereException {
	// Account for updated authentication token.
	removeTenantForAuthToken(tenant);
	tenantsByAuthToken.put(tenant.getAuthenticationToken(), tenant);

	// Update tenant information in tenant engine.
	ISiteWhereTenantEngine engine = getTenantEnginesById().get(tenant.getId());
	if (engine != null) {
	    engine.setTenant(tenant);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.ISiteWhereServer#onTenantDeleted(com.sitewhere.
     * spi.tenant.ITenant)
     */
    @Override
    public void onTenantDeleted(ITenant tenant) throws SiteWhereException {
	// Remove from auth token cache.
	removeTenantForAuthToken(tenant);

	// Shutdown tenant engine and remove it.
	ISiteWhereTenantEngine engine = getTenantEnginesById().get(tenant.getId());
	if (engine != null) {
	    if (engine.getLifecycleStatus() == LifecycleStatus.Started) {
		stopAndTerminateTenantEngine(engine, new LifecycleProgressMonitor(
			new LifecycleProgressContext(1, "Shut down deleted tenant engine.")));
	    }
	    getTenantEnginesById().remove(tenant.getId());
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#getUserManagement()
     */
    public IUserManagement getUserManagement() {
	return userManagement;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#getTenantManagement()
     */
    @Override
    public ITenantManagement getTenantManagement() {
	return tenantManagement;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#getDeviceManagement(com.
     * sitewhere.spi .user.ITenant)
     */
    @Override
    public IDeviceManagement getDeviceManagement(ITenant tenant) throws SiteWhereException {
	ISiteWhereTenantEngine engine = assureTenantEngine(tenant);
	return engine.getDeviceManagement();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.ISiteWhereServer#getDeviceEventManagement(com.
     * sitewhere .spi.user.ITenant)
     */
    @Override
    public IDeviceEventManagement getDeviceEventManagement(ITenant tenant) throws SiteWhereException {
	ISiteWhereTenantEngine engine = assureTenantEngine(tenant);
	return engine.getDeviceEventManagement();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#getAssetManagement(com.
     * sitewhere.spi. user.ITenant)
     */
    @Override
    public IAssetManagement getAssetManagement(ITenant tenant) throws SiteWhereException {
	ISiteWhereTenantEngine engine = assureTenantEngine(tenant);
	return engine.getAssetManagement();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#getBatchManagement(com.
     * sitewhere.spi.tenant.ITenant)
     */
    @Override
    public IBatchManagement getBatchManagement(ITenant tenant) throws SiteWhereException {
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#getScheduleManagement(com.
     * sitewhere.spi .user.ITenant)
     */
    @Override
    public IScheduleManagement getScheduleManagement(ITenant tenant) throws SiteWhereException {
	ISiteWhereTenantEngine engine = assureTenantEngine(tenant);
	return engine.getScheduleManagement();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.ISiteWhereServer#getDeviceCommunication(com.
     * sitewhere. spi.user.ITenant)
     */
    @Override
    public IDeviceCommunication getDeviceCommunication(ITenant tenant) throws SiteWhereException {
	ISiteWhereTenantEngine engine = assureTenantEngine(tenant);
	return engine.getDeviceCommunication();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#getAssetModuleManager(com.
     * sitewhere.spi .user.ITenant)
     */
    @Override
    public IAssetModuleManager getAssetModuleManager(ITenant tenant) throws SiteWhereException {
	ISiteWhereTenantEngine engine = assureTenantEngine(tenant);
	return engine.getAssetModuleManager();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.ISiteWhereServer#getSearchProviderManager(com.
     * sitewhere .spi.user.ITenant)
     */
    @Override
    public ISearchProviderManager getSearchProviderManager(ITenant tenant) throws SiteWhereException {
	ISiteWhereTenantEngine engine = assureTenantEngine(tenant);
	return engine.getSearchProviderManager();
    }

    /**
     * Get tenant engine for tenant. Throw an exception if not found.
     * 
     * @param tenant
     * @return
     * @throws SiteWhereException
     */
    protected ISiteWhereTenantEngine assureTenantEngine(ITenant tenant) throws SiteWhereException {
	ISiteWhereTenantEngine engine = getTenantEnginesById().get(tenant.getId());
	if (engine == null) {
	    throw new SiteWhereException("No engine registered for tenant.");
	}
	return engine;
    }

    /**
     * Get a tenant engine by authentication token. Throw and exception if not
     * found.
     * 
     * @param tenantAuthToken
     * @return
     * @throws SiteWhereException
     */
    protected ISiteWhereTenantEngine assureTenantEngine(String tenantAuthToken) throws SiteWhereException {
	ITenant tenant = tenantsByAuthToken.get(tenantAuthToken);
	if (tenant == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidTenantAuthToken, ErrorLevel.ERROR);
	}
	ISiteWhereTenantEngine engine = getTenantEnginesById().get(tenant.getId());
	if (engine == null) {
	    throw new SiteWhereException("Tenant found for auth token, but no engine registered for tenant.");
	}
	return engine;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.ISiteWhereServer#getTenantByAuthToken(java.lang.
     * String)
     */
    public ITenant getTenantByAuthToken(String authToken) throws SiteWhereException {
	return tenantsByAuthToken.get(authToken);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#getMetricRegistry()
     */
    public MetricRegistry getMetricRegistry() {
	return metricRegistry;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#getHealthCheckRegistry()
     */
    public HealthCheckRegistry getHealthCheckRegistry() {
	return healthCheckRegistry;
    }

    /**
     * Access folder pointed to by SiteWhere home environment variable.
     * 
     * @return
     * @throws SiteWhereException
     */
    public static File getSiteWhereHomeFolder() throws SiteWhereException {
	String sitewhere = System.getProperty(ISiteWhereServer.ENV_SITEWHERE_HOME);
	if (sitewhere == null) {
	    // Support fallback environment variable name.
	    sitewhere = System.getProperty("SITEWHERE_HOME");
	    if (sitewhere == null) {
		throw new SiteWhereException(
			"SiteWhere home environment variable (" + ISiteWhereServer.ENV_SITEWHERE_HOME + ") not set.");
	    }
	}
	File swFolder = new File(sitewhere);
	if (!swFolder.exists()) {
	    throw new SiteWhereException(
		    "SiteWhere home folder does not exist. Looking in: " + swFolder.getAbsolutePath());
	}
	return swFolder;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.ISiteWhereServer#initialize(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Initialize bootstrap resource manager.
	initializeBootstrapResourceManager();
	LOGGER.info("Bootstrap resources loading from: " + getBootstrapResourceManager().getClass().getCanonicalName());
	getBootstrapResourceManager().start(monitor);

	// Initialize persistent state.
	initializeServerState();

	// Initialize discoverable beans.
	initializeDiscoverableBeans(monitor);

	// Initialize management implementations.
	initializeManagementImplementations();

	// Initialize runtime resource manager.
	initializeRuntimeResourceManager();
	LOGGER.info("Runtime resources loading from: " + getRuntimeResourceManager().getClass().getCanonicalName());
	getRuntimeResourceManager().start(monitor);

	// Show banner containing server information.
	showServerBanner();
    }

    /**
     * Displays the server information banner in the log.
     */
    protected void showServerBanner() {
	String os = System.getProperty("os.name") + " (" + System.getProperty("os.version") + ")";
	String java = System.getProperty("java.vendor") + " (" + System.getProperty("java.version") + ")";

	// Print version information.
	List<String> messages = new ArrayList<String>();
	messages.add("SiteWhere Server " + version.getEdition());
	messages.add("Version: " + version.getVersionIdentifier() + "." + version.getBuildTimestamp());
	messages.add("Node id: " + serverState.getNodeId());
	addBannerMessages(messages);
	messages.add("Operating System: " + os);
	messages.add("Java Runtime: " + java);
	messages.add("");
	messages.add("Copyright (c) 2009-2016 SiteWhere, LLC");
	String message = Boilerplate.boilerplate(messages, "*");
	LOGGER.info("\n" + message + "\n");
    }

    /**
     * Allows subclasses to add their own banner messages.
     * 
     * @param messages
     */
    protected void addBannerMessages(List<String> messages) {
	messages.add("");
    }

    /**
     * Initialize the server state information.
     * 
     * @throws SiteWhereException
     */
    protected void initializeServerState() throws SiteWhereException {
	IResource stateResource = getConfigurationResolver().resolveServerState(getVersion());
	SiteWhereServerState state = null;
	if (stateResource == null) {
	    state = new SiteWhereServerState();
	    state.setNodeId(UUID.randomUUID().toString());
	    getConfigurationResolver().storeServerState(version, MarshalUtils.marshalJson(state));
	} else {
	    state = MarshalUtils.unmarshalJson(stateResource.getContent(), SiteWhereServerState.class);
	}
	this.serverState = state;
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
	Map<String, IDiscoverableTenantLifecycleComponent> components = SERVER_SPRING_CONTEXT
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
     * Initialize management implementations.
     * 
     * @throws SiteWhereException
     */
    protected void initializeManagementImplementations() throws SiteWhereException {

	// Initialize user management.
	initializeUserManagement();
    }

    /**
     * Verify and initialize user management implementation.
     * 
     * @throws SiteWhereException
     */
    protected void initializeUserManagement() throws SiteWhereException {
	try {
	    this.userManagement = (IUserManagement) SERVER_SPRING_CONTEXT
		    .getBean(SiteWhereServerBeans.BEAN_USER_MANAGEMENT);
	} catch (NoSuchBeanDefinitionException e) {
	    throw new SiteWhereException("No user management implementation configured.");
	}
    }

    /**
     * Initialize bootstrap resource manager.
     * 
     * @throws SiteWhereException
     */
    protected void initializeBootstrapResourceManager() throws SiteWhereException {
	this.bootstrapResourceManager = new SiteWhereHomeResourceManager();
	this.configurationResolver = new ResourceManagerGlobalConfigurationResolver(bootstrapResourceManager);
    }

    /**
     * Initialize runtime resource manager and swap configuration resolver to
     * use it.
     * 
     * @throws SiteWhereException
     */
    protected void initializeRuntimeResourceManager() throws SiteWhereException {
	this.runtimeResourceManager = new SiteWhereHomeResourceManager();
	this.configurationResolver = new ResourceManagerGlobalConfigurationResolver(runtimeResourceManager);
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
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Started Server");

	// Initialize tenant engines.
	start.addStep(new SimpleLifecycleStep("Prepared server") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		// Clear the component list.
		getLifecycleComponents().clear();
	    }
	});

	// Start base service services.
	startBaseServices(start);

	// Start management implementations.
	startManagementImplementations(start);

	// Initialize tenant engines.
	initializeTenantEngines(start);

	// Start tenant engines.
	startTenantEngines(start);

	// Finish operations for starting server.
	start.addStep(new SimpleLifecycleStep("Finished server startup") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		// Force refresh on components-by-id map.
		lifecycleComponentsById = buildComponentMap();

		// Set uptime timestamp.
		SiteWhereServer.this.uptime = System.currentTimeMillis();

		// Schedule JVM monitor.
		executor = Executors.newFixedThreadPool(2);
		executor.execute(jvmHistory);
	    }
	});

	// Start the operation and report progress.
	start.execute(monitor);
    }

    /**
     * Start basic services required by other components.
     * 
     * @param start
     * @throws SiteWhereException
     */
    protected void startBaseServices(ICompositeLifecycleStep start) throws SiteWhereException {
	// Organizes steps for starting base services.
	ICompositeLifecycleStep base = new CompositeLifecycleStep("Started Base Services");

	// Start all lifecycle components.
	for (ILifecycleComponent component : getRegisteredLifecycleComponents()) {
	    base.addStep(new StartComponentLifecycleStep(this, component, "Started " + component.getComponentName(),
		    component.getComponentName() + " startup failed.", true));
	}

	start.addStep(base);
    }

    /**
     * Add management implementation startup to composite operation.
     * 
     * @param start
     * @throws SiteWhereException
     */
    protected void startManagementImplementations(ICompositeLifecycleStep start) throws SiteWhereException {
	// Organizes steps for starting management implementations.
	ICompositeLifecycleStep mgmt = new CompositeLifecycleStep("Started Management Implementations");

	// Start user management.
	mgmt.addStep(new StartComponentLifecycleStep(this, getUserManagement(),
		"Started user management implementation", "User management startup failed.", true));

	// Start tenant management.
	mgmt.addStep(new StartComponentLifecycleStep(this, getTenantManagement(),
		"Started tenant management implementation", "Tenant management startup failed", true));

	start.addStep(mgmt);
    }

    /**
     * Initialize tenant engines.
     * 
     * @param start
     * @throws SiteWhereException
     */
    protected void initializeTenantEngines(ICompositeLifecycleStep start) throws SiteWhereException {
	// Initialize tenant engines.
	start.addStep(new SimpleLifecycleStep("Initialized tenant engines") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		// Create thread pool for initializing tenants in parallel.
		if (tenantOperations != null) {
		    tenantOperations.shutdownNow();
		}
		tenantOperations = Executors.newFixedThreadPool(TENANT_OPERATION_PARALLELISM,
			new TenantOperationsThreadFactory());

		TenantSearchCriteria criteria = new TenantSearchCriteria(1, 0);
		ISearchResults<ITenant> tenants = getTenantManagement().listTenants(criteria);
		LOGGER.info("About to initialize " + tenants.getNumResults() + " tenant engines.");
		for (ITenant tenant : tenants.getResults()) {
		    tenantOperations.execute(new Runnable() {

			@Override
			public void run() {
			    try {
				initializeTenantEngine(tenant);
			    } catch (SiteWhereException e) {
				LOGGER.error("Tenant engine initialization failed.", e);
			    }
			}
		    });
		}

		// Wait for tenant initialization operations to finish.
		tenantOperations.shutdown();
		try {
		    tenantOperations.awaitTermination(5, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
		    LOGGER.info("Interrupted while waiting for tenants to start.");
		}
	    }
	});
    }

    /**
     * Initialize a tenant engine.
     * 
     * @param tenant
     * @return
     * @throws SiteWhereException
     */
    protected ISiteWhereTenantEngine initializeTenantEngine(ITenant tenant) throws SiteWhereException {
	ISiteWhereTenantEngine engine = createTenantEngine(tenant, SERVER_SPRING_CONTEXT, getConfigurationResolver());
	initializeNestedComponent(engine,
		new LifecycleProgressMonitor(
			new LifecycleProgressContext(1, "Initializing tenant engine '" + tenant.getName() + "'")),
		"Tenant engine initialization failed.", false);
	if (engine.getLifecycleStatus() != LifecycleStatus.InitializationError) {
	    registerTenant(tenant, engine);
	    return engine;
	} else {
	    LOGGER.warn("Tenant engine initialization failed for '" + tenant.getId() + "'", engine.getLifecycleError());
	    return null;
	}
    }

    /**
     * Start tenant engines.
     * 
     * @param start
     * @throws SiteWhereException
     */
    protected void startTenantEngines(ICompositeLifecycleStep start) throws SiteWhereException {
	// Initialize tenant engines.
	start.addStep(new SimpleLifecycleStep("Started tenant engines") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		// Create thread pool for starting tenants in parallel.
		if (tenantOperations != null) {
		    tenantOperations.shutdownNow();
		}
		tenantOperations = Executors.newFixedThreadPool(TENANT_OPERATION_PARALLELISM,
			new TenantOperationsThreadFactory());

		// Start tenant engines.
		for (ISiteWhereTenantEngine engine : getTenantEnginesById().values()) {

		    // Find state or create initial state as needed.
		    ITenantPersistentState state = engine.getPersistentState();
		    if (state == null) {
			TenantPersistentState newState = new TenantPersistentState();
			newState.setDesiredState(LifecycleStatus.Started);
			newState.setLastKnownState(LifecycleStatus.Starting);
			engine.persistState(newState);
			state = newState;
		    }

		    // Do not start if desired state is 'Stopped'.
		    if (state.getDesiredState() != LifecycleStatus.Stopped) {
			tenantOperations.execute(new Runnable() {

			    @Override
			    public void run() {
				try {
				    startTenantEngine(engine);
				} catch (SiteWhereException e) {
				    LOGGER.error("Tenant engine startup failed.", e);
				}
			    }
			});
		    }
		}
		tenantOperations.shutdown();
		try {
		    tenantOperations.awaitTermination(5, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
		    LOGGER.info("Interrupted while waiting for tenants to start.");
		}
	    }
	});
    }

    /**
     * Start a tenant engine.
     * 
     * @param engine
     * @throws SiteWhereException
     */
    protected void startTenantEngine(ISiteWhereTenantEngine engine) throws SiteWhereException {
	if (engine.getLifecycleStatus() != LifecycleStatus.LifecycleError) {
	    LifecycleProgressMonitor monitor = new LifecycleProgressMonitor(
		    new LifecycleProgressContext(1, "Starting tenant engine '" + engine.getTenant().getName() + "'"));
	    startNestedComponent(engine, monitor,
		    "Tenant engine '" + engine.getTenant().getName() + "' startup failed.", false);
	    engine.logState();
	} else {
	    getLifecycleComponents().put(engine.getComponentId(), engine);
	    LOGGER.info("Skipping startup for tenant engine '" + engine.getTenant().getName()
		    + "' due to initialization errors.");
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
	return "SiteWhere Server " + getVersion().getEditionIdentifier() + " " + getVersion().getVersionIdentifier();
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
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Organizes steps for stopping server.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stopped Server");

	// Shut down services.
	stop.addStep(new SimpleLifecycleStep("Stopped monitoring services") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		if (executor != null) {
		    executor.shutdownNow();
		    executor = null;
		}
	    }
	});

	// Stop tenant engines.
	stopTenantEngines(stop);

	// Stop management implementations.
	stopManagementImplementations(stop);

	// Execute stop operation and report progress.
	stop.execute(monitor);
    }

    /**
     * Stop management implementations.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    protected void stopManagementImplementations(ICompositeLifecycleStep stop) throws SiteWhereException {
	// Stop tenant management implementation.
	stop.addStep(new StopComponentLifecycleStep(this, getTenantManagement(), "Stopped tenant management"));

	// Stop user management implementation.
	stop.addStep(new StopComponentLifecycleStep(this, getUserManagement(), "Stopped user management"));
    }

    /**
     * Stop tenant engines.
     * 
     * @param stop
     * @throws SiteWhereException
     */
    protected void stopTenantEngines(ICompositeLifecycleStep stop) throws SiteWhereException {
	stop.addStep(new SimpleLifecycleStep("Stopped tenant engines") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {

		// Create thread pool for stopping tenants in parallel.
		if (tenantOperations != null) {
		    tenantOperations.shutdownNow();
		}
		tenantOperations = Executors.newFixedThreadPool(TENANT_OPERATION_PARALLELISM,
			new TenantOperationsThreadFactory());

		for (ISiteWhereTenantEngine engine : getTenantEnginesById().values()) {
		    tenantOperations.execute(new Runnable() {

			@Override
			public void run() {
			    try {
				LifecycleProgressMonitor threadMonitor = new LifecycleProgressMonitor(
					new LifecycleProgressContext(1,
						"Stopping tenant engine '" + engine.getTenant().getName() + "'"));
				stopAndTerminateTenantEngine(engine, threadMonitor);
			    } catch (SiteWhereException e) {
				LOGGER.error("Tenant engine shutdown failed.", e);
			    }
			}
		    });
		}
		tenantOperations.shutdown();
		try {
		    tenantOperations.awaitTermination(5, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
		    LOGGER.info("Interrupted while waiting for tenants to shut down.");
		}
	    }
	});
    }

    /**
     * Stop and terminate a tenant engine.
     * 
     * @param engine
     * @param monitor
     * @throws SiteWhereException
     */
    protected void stopAndTerminateTenantEngine(ISiteWhereTenantEngine engine, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException {
	// TODO: Revisit tenant shutdown logic.
	if (engine.getLifecycleStatus() == LifecycleStatus.Started) {
	    engine.lifecycleStop(monitor);
	}
	engine.lifecycleTerminate(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#terminate(com.sitewhere
     * .spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    public void terminate(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Stop runtime resource manager.
	getRuntimeResourceManager().lifecycleStop(monitor);

	// Stop bootstrap resource manager.
	getBootstrapResourceManager().lifecycleStop(monitor);
    }

    /**
     * Compute current server state.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected ISiteWhereServerRuntime computeServerRuntime(boolean includeHistorical) throws SiteWhereException {
	SiteWhereServerRuntime state = new SiteWhereServerRuntime();

	String osName = System.getProperty("os.name");
	String osVersion = System.getProperty("os.version");
	String javaVendor = System.getProperty("java.vendor");
	String javaVersion = System.getProperty("java.version");

	GeneralInformation general = new GeneralInformation();
	general.setEdition(getVersion().getEdition());
	general.setEditionIdentifier(getVersion().getEditionIdentifier());
	general.setVersionIdentifier(getVersion().getVersionIdentifier());
	general.setBuildTimestamp(getVersion().getBuildTimestamp());
	general.setUptime(System.currentTimeMillis() - uptime);
	general.setOperatingSystemName(osName);
	general.setOperatingSystemVersion(osVersion);
	state.setGeneral(general);

	JavaInformation java = new JavaInformation();
	java.setJvmVendor(javaVendor);
	java.setJvmVersion(javaVersion);
	state.setJava(java);

	Runtime runtime = Runtime.getRuntime();
	java.setJvmFreeMemory(runtime.freeMemory());
	java.setJvmTotalMemory(runtime.totalMemory());
	java.setJvmMaxMemory(runtime.maxMemory());

	if (includeHistorical) {
	    java.setJvmTotalMemoryHistory(jvmHistory.getTotalMemory());
	    java.setJvmFreeMemoryHistory(jvmHistory.getFreeMemory());
	}

	return state;
    }

    /**
     * Create a tenant engine.
     * 
     * @param tenant
     * @param parent
     * @param resolver
     * @return
     * @throws SiteWhereException
     */
    protected ISiteWhereTenantEngine createTenantEngine(ITenant tenant, ApplicationContext parent,
	    IGlobalConfigurationResolver resolver) throws SiteWhereException {
	return new SiteWhereTenantEngine(tenant, SERVER_SPRING_CONTEXT, getConfigurationResolver());
    }

    /**
     * Registers an initialized tenant engine with the server.
     * 
     * @param tenant
     * @param engine
     * @throws SiteWhereException
     */
    protected void registerTenant(ITenant tenant, ISiteWhereTenantEngine engine) throws SiteWhereException {
	tenantsByAuthToken.put(tenant.getAuthenticationToken(), tenant);
	getTenantEnginesById().put(tenant.getId(), engine);
    }

    /** Used for naming tenant operation threads */
    private class TenantOperationsThreadFactory implements ThreadFactory {

	/** Counts threads */
	private AtomicInteger counter = new AtomicInteger();

	public Thread newThread(Runnable r) {
	    return new Thread(r, "Tenant Lifecycle " + counter.incrementAndGet());
	}
    }
}