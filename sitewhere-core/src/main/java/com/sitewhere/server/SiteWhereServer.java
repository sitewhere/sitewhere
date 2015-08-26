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

import org.apache.log4j.Logger;
import org.mule.util.StringMessageUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.sitewhere.SiteWhere;
import com.sitewhere.configuration.ExternalConfigurationResolver;
import com.sitewhere.configuration.TomcatConfigurationResolver;
import com.sitewhere.rest.model.search.user.TenantSearchCriteria;
import com.sitewhere.rest.model.user.User;
import com.sitewhere.rest.model.user.UserSearchCriteria;
import com.sitewhere.security.SitewhereAuthentication;
import com.sitewhere.security.SitewhereUserDetails;
import com.sitewhere.server.SiteWhereServerState.GeneralInformation;
import com.sitewhere.server.SiteWhereServerState.JavaInformation;
import com.sitewhere.server.debug.NullTracer;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.ServerStartupException;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.asset.IAssetModuleManager;
import com.sitewhere.spi.configuration.IConfigurationResolver;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.communication.IDeviceCommunication;
import com.sitewhere.spi.device.event.processor.IInboundEventProcessorChain;
import com.sitewhere.spi.device.event.processor.IOutboundEventProcessorChain;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.external.ISearchProviderManager;
import com.sitewhere.spi.server.ISiteWhereServer;
import com.sitewhere.spi.server.ISiteWhereServerEnvironment;
import com.sitewhere.spi.server.ISiteWhereServerState;
import com.sitewhere.spi.server.ISiteWhereTenantEngine;
import com.sitewhere.spi.server.debug.ITracer;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.server.user.IUserModelInitializer;
import com.sitewhere.spi.system.IVersion;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.ITenant;
import com.sitewhere.spi.user.IUser;
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

	/** Contains runtime information about the server */
	private ISiteWhereServerState serverState;

	/** Server startup error */
	private ServerStartupException serverStartupError;

	/** Provides hierarchical tracing for debugging */
	private ITracer tracer = new NullTracer();

	/** Allows Spring configuration to be resolved */
	private IConfigurationResolver configurationResolver = new TomcatConfigurationResolver();

	/** Interface to user management implementation */
	private IUserManagement userManagement;

	/** List of components registered to participate in SiteWhere server lifecycle */
	private List<ILifecycleComponent> registeredLifecycleComponents = new ArrayList<ILifecycleComponent>();

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
	 * @see com.sitewhere.spi.server.ISiteWhereServer#getServerState()
	 */
	public ISiteWhereServerState getServerState() throws SiteWhereException {
		this.serverState = computeServerState();
		return serverState;
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
	public IConfigurationResolver getConfigurationResolver() {
		return configurationResolver;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.server.ISiteWhereServer#getAuthorizedTenants(java.lang.String)
	 */
	@Override
	public List<ITenant> getAuthorizedTenants(String userId) throws SiteWhereException {
		ISearchResults<ITenant> tenants =
				SiteWhere.getServer().getUserManagement().listTenants(new TenantSearchCriteria(1, 0));
		List<ITenant> matches = new ArrayList<ITenant>();
		for (ITenant tenant : tenants.getResults()) {
			if (tenant.getAuthorizedUserIds().contains(userId)) {
				matches.add(tenant);
			}
		}
		return matches;
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
	 * com.sitewhere.spi.server.ISiteWhereServer#getOutboundEventProcessorChain(com.sitewhere
	 * .spi.user.ITenant)
	 */
	@Override
	public IOutboundEventProcessorChain getOutboundEventProcessorChain(ITenant tenant)
			throws SiteWhereException {
		ISiteWhereTenantEngine engine = assureTenantEngine(tenant);
		return engine.getOutboundEventProcessorChain();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.server.ISiteWhereServer#getInboundEventProcessorChain(com.sitewhere
	 * .spi.user.ITenant)
	 */
	@Override
	public IInboundEventProcessorChain getInboundEventProcessorChain(ITenant tenant)
			throws SiteWhereException {
		ISiteWhereTenantEngine engine = assureTenantEngine(tenant);
		return engine.getInboundEventProcessorChain();
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
	public List<ILifecycleComponent> getRegisteredLifecycleComponents() {
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

		// Populate user data if requested.
		verifyUserModel();

		// Initialize and start tenant engines.
		initializeTenantEngines();
		for (ISiteWhereTenantEngine engine : tenantEnginesById.values()) {
			try {
				startNestedComponent(engine, "Tenant engine startup failed.", true);
			} catch (SiteWhereException e) {
				LOGGER.error("Tenant engine (" + engine.getTenant().getId() + ") startup failed.", e);
			}
		}

		// Force refresh on components-by-id map.
		refreshLifecycleComponentMap(this, lifecycleComponentsById);

		// Set uptime timestamp.
		this.uptime = System.currentTimeMillis();
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
	 * @see
	 * com.sitewhere.spi.server.ISiteWhereServer#getLifecycleComponentById(java.lang.String
	 * )
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
		getUserManagement().lifecycleStop();

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
	protected ISiteWhereServerState computeServerState() throws SiteWhereException {
		SiteWhereServerState state = new SiteWhereServerState();

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

		return state;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.ISiteWhereServer#initialize()
	 */
	public void initialize() throws SiteWhereException {
		this.version = VersionHelper.getVersion();

		// Initialize Spring.
		initializeSpringContext();

		// Initialize tracer.
		initializeTracer();

		// Initialize user management.
		initializeUserManagement();

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
		messages.add("");
		messages.add("Version: " + version.getVersionIdentifier() + "." + version.getBuildTimestamp());
		messages.add("Operating System: " + os);
		messages.add("Java Runtime: " + java);
		messages.add("");
		messages.add("Copyright (c) 2009-2015 SiteWhere, LLC");
		String message = StringMessageUtils.getBoilerPlate(messages, '*', 60);
		LOGGER.info("\n" + message + "\n");
	}

	/**
	 * Allows subclasses to add their own banner messages.
	 * 
	 * @param messages
	 */
	protected void addBannerMessages(List<String> messages) {
	}

	/**
	 * Verifies and loads the Spring configuration file.
	 * 
	 * @throws SiteWhereException
	 */
	protected void initializeSpringContext() throws SiteWhereException {
		String extConfig = System.getenv(ISiteWhereServerEnvironment.ENV_EXTERNAL_CONFIGURATION_URL);
		if (extConfig != null) {
			IConfigurationResolver resolver = new ExternalConfigurationResolver(extConfig);
			SERVER_SPRING_CONTEXT = resolver.resolveSiteWhereContext(getVersion());
		} else {
			SERVER_SPRING_CONTEXT = getConfigurationResolver().resolveSiteWhereContext(getVersion());
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
			userManagement =
					(IUserManagement) SERVER_SPRING_CONTEXT.getBean(SiteWhereServerBeans.BEAN_USER_MANAGEMENT);
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
		ISearchResults<ITenant> tenants = getUserManagement().listTenants(criteria);
		for (ITenant tenant : tenants.getResults()) {
			SiteWhereTenantEngine engine = new SiteWhereTenantEngine(tenant);
			engine.setConfigurationResolver(getConfigurationResolver());
			engine.initialize();
			tenantsByAuthToken.put(tenant.getId(), tenant);
			tenantEnginesById.put(tenant.getId(), engine);
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
	 * Check whether user model is populated and offer to bootstrap system if not.
	 */
	protected void verifyUserModel() {
		try {
			IUserModelInitializer init =
					(IUserModelInitializer) SERVER_SPRING_CONTEXT.getBean(SiteWhereServerBeans.BEAN_USER_MODEL_INITIALIZER);
			List<IUser> users = getUserManagement().listUsers(new UserSearchCriteria());
			if (users.size() == 0) {
				List<String> messages = new ArrayList<String>();
				messages.add("User model is currently empty. A default user and permissions can be "
						+ "created automatically so that the admin interface and web services can authenticate. "
						+ "Create default user and permissions now?");
				String message = StringMessageUtils.getBoilerPlate(messages, '*', 60);
				LOGGER.info("\n" + message + "\n");
				System.out.println("Initialize user model? Yes/No (Default is Yes)");
				String response = readLine();
				if ((response == null) && (init.isInitializeIfNoConsole())) {
					response = "Y";
				} else if ((response == null) && (!init.isInitializeIfNoConsole())) {
					response = "N";
				}
				if ((response.length() == 0) || (response.toLowerCase().startsWith("y"))) {
					init.initialize(getUserManagement());
				}
			}
		} catch (NoSuchBeanDefinitionException e) {
			LOGGER.info("No user model initializer found in Spring bean configuration. Skipping.");
			return;
		} catch (SiteWhereException e) {
			LOGGER.warn("Error verifying user model.", e);
		}
	}
}