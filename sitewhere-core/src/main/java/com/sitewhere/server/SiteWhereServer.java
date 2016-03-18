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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.sitewhere.SiteWhere;
import com.sitewhere.common.MarshalUtils;
import com.sitewhere.configuration.ConfigurationMigrationSupport;
import com.sitewhere.configuration.ConfigurationUtils;
import com.sitewhere.configuration.DefaultGlobalConfigurationResolver;
import com.sitewhere.core.Boilerplate;
import com.sitewhere.rest.model.search.user.TenantSearchCriteria;
import com.sitewhere.rest.model.server.SiteWhereServerRuntime;
import com.sitewhere.rest.model.server.SiteWhereServerRuntime.GeneralInformation;
import com.sitewhere.rest.model.server.SiteWhereServerRuntime.JavaInformation;
import com.sitewhere.rest.model.server.SiteWhereServerState;
import com.sitewhere.rest.model.user.User;
import com.sitewhere.security.SitewhereAuthentication;
import com.sitewhere.security.SitewhereUserDetails;
import com.sitewhere.server.debug.NullTracer;
import com.sitewhere.server.jvm.JvmHistoryMonitor;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.server.tenant.TenantManagementTriggers;
import com.sitewhere.spi.ServerStartupException;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.asset.IAssetModuleManager;
import com.sitewhere.spi.configuration.IGlobalConfigurationResolver;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceManagementCacheProvider;
import com.sitewhere.spi.device.communication.IDeviceCommunication;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.IEventProcessing;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.scheduling.IScheduleManagement;
import com.sitewhere.spi.scheduling.IScheduleManager;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.external.ISearchProviderManager;
import com.sitewhere.spi.server.ISiteWhereServer;
import com.sitewhere.spi.server.ISiteWhereServerRuntime;
import com.sitewhere.spi.server.ISiteWhereServerState;
import com.sitewhere.spi.server.debug.ITracer;
import com.sitewhere.spi.server.lifecycle.IDiscoverableTenantLifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;
import com.sitewhere.spi.server.tenant.ISiteWhereTenantEngine;
import com.sitewhere.spi.server.tenant.ITenantModelInitializer;
import com.sitewhere.spi.server.user.IUserModelInitializer;
import com.sitewhere.spi.system.IVersion;
import com.sitewhere.spi.system.IVersionChecker;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.ITenantManagement;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IUserManagement;
import com.sitewhere.version.VersionHelper;

/**
 * Implementation of {@link ISiteWhereServer} for community edition.
 * 
 * @author Derek Adams
 */
public class SiteWhereServer extends LifecycleComponent implements ISiteWhereServer {

	/** Private logger instance */
	private static Logger LOGGER = Logger.getLogger(SiteWhereServer.class);

	/** Spring context for server */
	public static ApplicationContext SERVER_SPRING_CONTEXT;

	/** Contains version information */
	private IVersion version;

	/** Version checker implementation */
	private IVersionChecker versionChecker;

	/** Persistent server state information */
	private ISiteWhereServerState serverState;

	/** Contains runtime information about the server */
	private ISiteWhereServerRuntime serverRuntime;

	/** Server startup error */
	private ServerStartupException serverStartupError;

	/** Provides hierarchical tracing for debugging */
	private ITracer tracer = new NullTracer();

	/** Allows Spring configuration to be resolved */
	private IGlobalConfigurationResolver configurationResolver = new DefaultGlobalConfigurationResolver();

	/** Interface to user management implementation */
	private IUserManagement userManagement;

	/** Interface to tenant management implementation */
	private ITenantManagement tenantManagement;

	/** List of components registered to participate in SiteWhere server lifecycle */
	private List<ITenantLifecycleComponent> registeredLifecycleComponents =
			new ArrayList<ITenantLifecycleComponent>();

	/** Map of component ids to lifecycle components */
	private Map<String, ILifecycleComponent> lifecycleComponentsById =
			new HashMap<String, ILifecycleComponent>();

	/** Map of tenants by authentication token */
	private Map<String, ITenant> tenantsByAuthToken = new HashMap<String, ITenant>();

	/** Map of tenant engines by tenant id */
	private Map<String, ISiteWhereTenantEngine> tenantEnginesById =
			new HashMap<String, ISiteWhereTenantEngine>();

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

	public SiteWhereServer() {
		super(LifecycleComponentType.System);
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
	 * com.sitewhere.spi.server.ISiteWhereServer#getTenantConfigurationParserClassname()
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
	public ISiteWhereServerRuntime getServerRuntimeInformation(boolean includeHistorical)
			throws SiteWhereException {
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
	 * @see
	 * com.sitewhere.spi.server.ISiteWhereServer#setServerStartupError(com.sitewhere.spi
	 * .ServerStartupException)
	 */
	public void setServerStartupError(ServerStartupException e) {
		this.serverStartupError = e;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.ISiteWhereServer#getTracer()
	 */
	public ITracer getTracer() {
		return tracer;
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
	 * com.sitewhere.spi.server.ISiteWhereServer#getAuthorizedTenants(java.lang.String,
	 * boolean)
	 */
	@Override
	public List<ITenant> getAuthorizedTenants(String userId, boolean requireStarted)
			throws SiteWhereException {
		ISearchResults<ITenant> tenants =
				SiteWhere.getServer().getTenantManagement().listTenants(new TenantSearchCriteria(1, 0));
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
	 * @see com.sitewhere.spi.server.ISiteWhereServer#getTenantEngine(java.lang.String)
	 */
	@Override
	public ISiteWhereTenantEngine getTenantEngine(String tenantId) throws SiteWhereException {
		ISiteWhereTenantEngine engine = tenantEnginesById.get(tenantId);
		if (engine == null) {
			ITenant tenant = getTenantManagement().getTenantById(tenantId);
			if (tenant == null) {
				return null;
			}
			engine = initializeTenantEngine(tenant);
		}
		return engine;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.server.ISiteWhereServer#onTenantInformationUpdated(com.sitewhere
	 * .spi.user.ITenant)
	 */
	@Override
	public void onTenantInformationUpdated(ITenant tenant) throws SiteWhereException {
		// Account for updated authentication token.
		for (ITenant current : tenantsByAuthToken.values()) {
			if (current.getId().equals(tenant.getId())) {
				tenantsByAuthToken.remove(current);
			}
		}
		tenantsByAuthToken.put(tenant.getAuthenticationToken(), tenant);

		// Update tenant information in tenant engine.
		ISiteWhereTenantEngine engine = tenantEnginesById.get(tenant.getId());
		if (engine != null) {
			engine.setTenant(tenant);
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
	 * @see
	 * com.sitewhere.spi.server.ISiteWhereServer#getDeviceManagement(com.sitewhere.spi
	 * .user.ITenant)
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
	 * com.sitewhere.spi.server.ISiteWhereServer#getDeviceEventManagement(com.sitewhere
	 * .spi.user.ITenant)
	 */
	@Override
	public IDeviceEventManagement getDeviceEventManagement(ITenant tenant) throws SiteWhereException {
		ISiteWhereTenantEngine engine = assureTenantEngine(tenant);
		return engine.getDeviceEventManagement();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.server.ISiteWhereServer#getDeviceManagementCacheProvider(com.
	 * sitewhere.spi.user.ITenant)
	 */
	@Override
	public IDeviceManagementCacheProvider getDeviceManagementCacheProvider(ITenant tenant)
			throws SiteWhereException {
		ISiteWhereTenantEngine engine = assureTenantEngine(tenant);
		return engine.getDeviceManagementCacheProvider();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.server.ISiteWhereServer#getAssetManagement(com.sitewhere.spi.
	 * user.ITenant)
	 */
	@Override
	public IAssetManagement getAssetManagement(ITenant tenant) throws SiteWhereException {
		ISiteWhereTenantEngine engine = assureTenantEngine(tenant);
		return engine.getAssetManagement();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.server.ISiteWhereServer#getScheduleManagement(com.sitewhere.spi
	 * .user.ITenant)
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
	 * com.sitewhere.spi.server.ISiteWhereServer#getDeviceCommunication(com.sitewhere.
	 * spi.user.ITenant)
	 */
	@Override
	public IDeviceCommunication getDeviceCommunication(ITenant tenant) throws SiteWhereException {
		ISiteWhereTenantEngine engine = assureTenantEngine(tenant);
		return engine.getDeviceCommunication();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.server.ISiteWhereServer#getEventProcessing(com.sitewhere.spi.
	 * user.ITenant)
	 */
	@Override
	public IEventProcessing getEventProcessing(ITenant tenant) throws SiteWhereException {
		ISiteWhereTenantEngine engine = assureTenantEngine(tenant);
		return engine.getEventProcessing();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.server.ISiteWhereServer#getAssetModuleManager(com.sitewhere.spi
	 * .user.ITenant)
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
	 * com.sitewhere.spi.server.ISiteWhereServer#getSearchProviderManager(com.sitewhere
	 * .spi.user.ITenant)
	 */
	@Override
	public ISearchProviderManager getSearchProviderManager(ITenant tenant) throws SiteWhereException {
		ISiteWhereTenantEngine engine = assureTenantEngine(tenant);
		return engine.getSearchProviderManager();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.server.ISiteWhereServer#getScheduleManager(com.sitewhere.spi.
	 * user.ITenant)
	 */
	@Override
	public IScheduleManager getScheduleManager(ITenant tenant) throws SiteWhereException {
		ISiteWhereTenantEngine engine = assureTenantEngine(tenant);
		return engine.getScheduleManager();
	}

	/**
	 * Get tenant engine for tenant. Throw an exception if not found.
	 * 
	 * @param tenant
	 * @return
	 * @throws SiteWhereException
	 */
	protected ISiteWhereTenantEngine assureTenantEngine(ITenant tenant) throws SiteWhereException {
		ISiteWhereTenantEngine engine = tenantEnginesById.get(tenant.getId());
		if (engine == null) {
			throw new SiteWhereException("No engine registered for tenant.");
		}
		return engine;
	}

	/**
	 * Get a tenant engine by authentication token. Throw and exception if not found.
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
		ISiteWhereTenantEngine engine = tenantEnginesById.get(tenant.getId());
		if (engine == null) {
			throw new SiteWhereException("Tenant found for auth token, but no engine registered for tenant.");
		}
		return engine;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.server.ISiteWhereServer#getTenantByAuthToken(java.lang.String)
	 */
	public ITenant getTenantByAuthToken(String authToken) throws SiteWhereException {
		return tenantsByAuthToken.get(authToken);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.ISiteWhereServer#getRegisteredLifecycleComponents()
	 */
	public List<ITenantLifecycleComponent> getRegisteredLifecycleComponents() {
		return registeredLifecycleComponents;
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
	 * Returns a fake account used for operations on the data model done by the system.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public static SitewhereAuthentication getSystemAuthentication() throws SiteWhereException {
		User fake = new User();
		fake.setUsername("system");
		SitewhereUserDetails details = new SitewhereUserDetails(fake, new ArrayList<IGrantedAuthority>());
		SitewhereAuthentication auth = new SitewhereAuthentication(details, null);
		return auth;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		// Clear the component list.
		getLifecycleComponents().clear();

		// Start all lifecycle components.
		for (ILifecycleComponent component : getRegisteredLifecycleComponents()) {
			startNestedComponent(component, component.getComponentName() + " startup failed.", true);
		}

		// Start user management.
		startNestedComponent(getUserManagement(), "User management startup failed.", true);

		// Start tenant management.
		startNestedComponent(getTenantManagement(), "Tenant management startup failed.", true);

		// Populate user data if requested.
		verifyUserModel();

		// Populate tenant data if requested.
		verifyTenantModel();

		// Initialize and start tenant engines.
		initializeTenantEngines();
		for (ISiteWhereTenantEngine engine : tenantEnginesById.values()) {
			startTenantEngine(engine);
		}

		// Force refresh on components-by-id map.
		refreshLifecycleComponentMap(this, lifecycleComponentsById);

		// Set uptime timestamp.
		this.uptime = System.currentTimeMillis();

		// Schedule JVM monitor.
		executor = Executors.newFixedThreadPool(2);
		executor.execute(jvmHistory);

		// If version checker configured, perform in a separate thread.
		if (versionChecker != null) {
			executor.execute(versionChecker);
		}
	}

	/**
	 * Start a tenant engine.
	 * 
	 * @param engine
	 */
	protected void startTenantEngine(ISiteWhereTenantEngine engine) {
		try {
			if (engine.getLifecycleStatus() != LifecycleStatus.Error) {
				startNestedComponent(engine, "Tenant engine startup failed.", true);
			} else {
				getLifecycleComponents().add(engine);
				LOGGER.info("Skipping startup for tenant engine '" + engine.getTenant().getName()
						+ "' due to initialization errors.");
			}
		} catch (SiteWhereException e) {
			LOGGER.error("Tenant engine (" + engine.getTenant().getId() + ") startup failed.", e);
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
		return "SiteWhere Server " + getVersion().getEditionIdentifier() + " "
				+ getVersion().getVersionIdentifier();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.ISiteWhereServer#getLifecycleComponentById(java.lang.
	 * String )
	 */
	@Override
	public ILifecycleComponent getLifecycleComponentById(String id) {
		return lifecycleComponentsById.get(id);
	}

	/**
	 * Recursively navigates component structure and creates a map of components by id.
	 * 
	 * @param current
	 * @param map
	 */
	protected void refreshLifecycleComponentMap(ILifecycleComponent current,
			Map<String, ILifecycleComponent> map) {
		map.put(current.getComponentId(), current);
		for (ILifecycleComponent sub : current.getLifecycleComponents()) {
			refreshLifecycleComponentMap(sub, map);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		if (executor != null) {
			executor.shutdownNow();
			executor = null;
		}

		// Stop tenant engines.
		for (ISiteWhereTenantEngine engine : tenantEnginesById.values()) {
			if (engine.getLifecycleStatus() == LifecycleStatus.Started) {
				engine.lifecycleStop();
			}
		}

		if (getTenantManagement() != null) {
			getTenantManagement().lifecycleStop();
		}

		if (getUserManagement() != null) {
			getUserManagement().lifecycleStop();
		}

		// Start all lifecycle components.
		for (ILifecycleComponent component : getRegisteredLifecycleComponents()) {
			component.lifecycleStop();
		}
	}

	/**
	 * Compute current server state.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	protected ISiteWhereServerRuntime computeServerRuntime(boolean includeHistorical)
			throws SiteWhereException {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.ISiteWhereServer#initialize()
	 */
	public void initialize() throws SiteWhereException {
		this.version = VersionHelper.getVersion();

		// Migrate old configuration structure if necessary.
		ConfigurationMigrationSupport.migrateProjectStructureIfNecessary(getConfigurationResolver());

		// Initialize persistent state.
		initializeServerState();

		// Initialize Spring.
		initializeSpringContext();

		// Initialize discoverable beans.
		initializeDiscoverableBeans();

		// Initialize version checker.
		initializeVersionChecker();

		// Initialize tracer.
		initializeTracer();

		// Initialize user management.
		initializeUserManagement();

		// Initialize tenant management.
		initializeTenantManagement();

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
		messages.add("Copyright (c) 2009-2015 SiteWhere, LLC");
		String message = Boilerplate.boilerplate(messages, '*', 60);
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
		byte[] stateData = getConfigurationResolver().resolveServerState(getVersion());
		SiteWhereServerState state = null;
		if (stateData == null) {
			state = new SiteWhereServerState();
			state.setNodeId(UUID.randomUUID().toString());
			getConfigurationResolver().storeServerState(version, MarshalUtils.marshalJson(state));
		} else {
			state = MarshalUtils.unmarshalJson(stateData, SiteWhereServerState.class);
		}
		this.serverState = state;
	}

	/**
	 * Verifies and loads the Spring configuration file.
	 * 
	 * @throws SiteWhereException
	 */
	protected void initializeSpringContext() throws SiteWhereException {
		byte[] global = getConfigurationResolver().getGlobalConfiguration(getVersion());
		SERVER_SPRING_CONTEXT = ConfigurationUtils.buildGlobalContext(global, getVersion());
	}

	/**
	 * Initialize beans marked with {@link IDiscoverableTenantLifecycleComponent}
	 * interface and add them as registered components.
	 * 
	 * @throws SiteWhereException
	 */
	protected void initializeDiscoverableBeans() throws SiteWhereException {
		Map<String, IDiscoverableTenantLifecycleComponent> components =
				SERVER_SPRING_CONTEXT.getBeansOfType(IDiscoverableTenantLifecycleComponent.class);
		getRegisteredLifecycleComponents().clear();

		LOGGER.info("Registering " + components.size() + " discoverable components.");
		for (IDiscoverableTenantLifecycleComponent component : components.values()) {
			LOGGER.info("Registering " + component.getComponentName() + ".");
			getRegisteredLifecycleComponents().add(component);
		}
	}

	/**
	 * Initialize debug tracing implementation.
	 * 
	 * @throws SiteWhereException
	 */
	protected void initializeVersionChecker() throws SiteWhereException {
		try {
			this.versionChecker =
					(IVersionChecker) SERVER_SPRING_CONTEXT.getBean(SiteWhereServerBeans.BEAN_VERSION_CHECK);
		} catch (NoSuchBeanDefinitionException e) {
			LOGGER.info("Version checking not enabled.");
		}
	}

	/**
	 * Initialize debug tracing implementation.
	 * 
	 * @throws SiteWhereException
	 */
	protected void initializeTracer() throws SiteWhereException {
		try {
			this.tracer = (ITracer) SERVER_SPRING_CONTEXT.getBean(SiteWhereServerBeans.BEAN_TRACER);
			LOGGER.info("Tracer implementation using: " + tracer.getClass().getName());
		} catch (NoSuchBeanDefinitionException e) {
			LOGGER.info("No Tracer implementation configured.");
			this.tracer = new NullTracer();
		}
	}

	/**
	 * Verify and initialize user management implementation.
	 * 
	 * @throws SiteWhereException
	 */
	protected void initializeUserManagement() throws SiteWhereException {
		try {
			this.userManagement =
					(IUserManagement) SERVER_SPRING_CONTEXT.getBean(
							SiteWhereServerBeans.BEAN_USER_MANAGEMENT);
		} catch (NoSuchBeanDefinitionException e) {
			throw new SiteWhereException("No user management implementation configured.");
		}
	}

	/**
	 * Verify and initialize tenant management implementation.
	 * 
	 * @throws SiteWhereException
	 */
	protected void initializeTenantManagement() throws SiteWhereException {
		try {
			ITenantManagement implementation =
					(ITenantManagement) SERVER_SPRING_CONTEXT.getBean(
							SiteWhereServerBeans.BEAN_TENANT_MANAGEMENT);
			this.tenantManagement = new TenantManagementTriggers(implementation);
		} catch (NoSuchBeanDefinitionException e) {
			throw new SiteWhereException("No user management implementation configured.");
		}
	}

	/**
	 * Create and initialize all tenant engines.
	 * 
	 * @throws SiteWhereException
	 */
	protected void initializeTenantEngines() throws SiteWhereException {
		TenantSearchCriteria criteria = new TenantSearchCriteria(1, 0);
		ISearchResults<ITenant> tenants = getTenantManagement().listTenants(criteria);
		for (ITenant tenant : tenants.getResults()) {
			initializeTenantEngine(tenant);
		}
	}

	/**
	 * Initialize a tenant engine.
	 * 
	 * @param tenant
	 * @return
	 * @throws SiteWhereException
	 */
	protected ISiteWhereTenantEngine initializeTenantEngine(ITenant tenant) throws SiteWhereException {
		ISiteWhereTenantEngine engine =
				createTenantEngine(tenant, SERVER_SPRING_CONTEXT, getConfigurationResolver());
		if (!engine.initialize()) {
			LOGGER.error("Tenant engine initialization for '" + tenant.getName() + "' failed.",
					engine.getLifecycleError());
		}
		registerTenant(tenant, engine);
		return engine;
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
		tenantEnginesById.put(tenant.getId(), engine);
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
	 * Check whether user model is populated and bootstrap system if not.
	 */
	protected void verifyUserModel() {
		try {
			IUserModelInitializer init =
					(IUserModelInitializer) SERVER_SPRING_CONTEXT.getBean(
							SiteWhereServerBeans.BEAN_USER_MODEL_INITIALIZER);
			init.initialize(getUserManagement());
		} catch (NoSuchBeanDefinitionException e) {
			LOGGER.info("No user model initializer found in Spring bean configuration. Skipping.");
			return;
		} catch (SiteWhereException e) {
			LOGGER.warn("Error verifying user model.", e);
		}
	}

	/**
	 * Check whether tenant model is populated and bootstrap system if not.
	 */
	protected void verifyTenantModel() {
		try {
			ITenantModelInitializer init =
					(ITenantModelInitializer) SERVER_SPRING_CONTEXT.getBean(
							SiteWhereServerBeans.BEAN_TENANT_MODEL_INITIALIZER);
			init.initialize(getTenantManagement());
		} catch (NoSuchBeanDefinitionException e) {
			LOGGER.info("No tenant model initializer found in Spring bean configuration. Skipping.");
			return;
		} catch (SiteWhereException e) {
			LOGGER.warn("Error verifying tenant model.", e);
		}
	}
}