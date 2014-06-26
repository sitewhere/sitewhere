/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package com.sitewhere.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.mule.util.StringMessageUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.FileSystemResource;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.sitewhere.device.event.processor.OutboundProcessingStrategyDecorator;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.user.User;
import com.sitewhere.rest.model.user.UserSearchCriteria;
import com.sitewhere.security.SitewhereAuthentication;
import com.sitewhere.security.SitewhereUserDetails;
import com.sitewhere.server.metrics.DeviceManagementMetricsDecorator;
import com.sitewhere.spi.ISiteWhereLifecycle;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetModuleManager;
import com.sitewhere.spi.device.ICachingDeviceManagement;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceManagementCacheProvider;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.event.processor.IInboundEventProcessorChain;
import com.sitewhere.spi.device.event.processor.IOutboundEventProcessorChain;
import com.sitewhere.spi.device.provisioning.IDeviceProvisioning;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.external.ISearchProviderManager;
import com.sitewhere.spi.server.device.IDeviceModelInitializer;
import com.sitewhere.spi.server.user.IUserModelInitializer;
import com.sitewhere.spi.system.IVersion;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.spi.user.IUserManagement;
import com.sitewhere.version.VersionHelper;

/**
 * Singleton SiteWhere server instance.
 * 
 * @author Derek Adams
 */
public class SiteWhereServer implements ISiteWhereLifecycle {

	/** Private logger instance */
	private static Logger LOGGER = Logger.getLogger(SiteWhereServer.class);

	/** Singleton server instance */
	private static SiteWhereServer SINGLETON;

	/** Spring context for server */
	public static ApplicationContext SERVER_SPRING_CONTEXT;

	/** File name for SiteWhere server config file */
	public static final String SERVER_CONFIG_FILE_NAME = "sitewhere-server.xml";

	/** Contains version information */
	private IVersion version;

	/** Interface to user management implementation */
	private IUserManagement userManagement;

	/** Device management cache provider implementation */
	private IDeviceManagementCacheProvider deviceManagementCacheProvider;

	/** Interface to device management implementation */
	private IDeviceManagement deviceManagement;

	/** Interface to inbound event processor chain */
	private IInboundEventProcessorChain inboundEventProcessorChain;

	/** Interface to outbound event processor chain */
	private IOutboundEventProcessorChain outboundEventProcessorChain;

	/** Interface to device provisioning implementation */
	private IDeviceProvisioning deviceProvisioning;

	/** Interface for the asset module manager */
	private IAssetModuleManager assetModuleManager;

	/** Interface for the search provider manager */
	private ISearchProviderManager searchProviderManager;

	/** Metric regsitry */
	private MetricRegistry metricRegistry = new MetricRegistry();

	/** Health check registry */
	private HealthCheckRegistry healthCheckRegistry = new HealthCheckRegistry();

	/**
	 * Get the singleton server instance.
	 * 
	 * @return
	 */
	public static synchronized SiteWhereServer getInstance() {
		if (SINGLETON == null) {
			SINGLETON = new SiteWhereServer();
		}
		return SINGLETON;
	}

	/**
	 * Get Spring application context for Atlas server objects.
	 * 
	 * @return
	 */
	public static ApplicationContext getServerSpringContext() {
		return SERVER_SPRING_CONTEXT;
	}

	/**
	 * Get version information.
	 * 
	 * @return
	 */
	public IVersion getVersion() {
		return version;
	}

	/**
	 * Get the user management implementation.
	 * 
	 * @return
	 */
	public IUserManagement getUserManagement() {
		return userManagement;
	}

	/**
	 * Get the device management implementation.
	 * 
	 * @return
	 */
	public IDeviceManagement getDeviceManagement() {
		return deviceManagement;
	}

	/**
	 * Get the configured device management cache provider implementation.
	 * 
	 * @return
	 */
	public IDeviceManagementCacheProvider getDeviceManagementCacheProvider() {
		return deviceManagementCacheProvider;
	}

	/**
	 * Get the inbound event processor chain.
	 * 
	 * @return
	 */
	public IInboundEventProcessorChain getInboundEventProcessorChain() {
		return inboundEventProcessorChain;
	}

	/**
	 * Get the outbound event processor chain.
	 * 
	 * @return
	 */
	public IOutboundEventProcessorChain getOutboundEventProcessorChain() {
		return outboundEventProcessorChain;
	}

	/**
	 * Get the device provisioning implementation.
	 * 
	 * @return
	 */
	public IDeviceProvisioning getDeviceProvisioning() {
		return deviceProvisioning;
	}

	/**
	 * Get the asset modules manager instance.
	 * 
	 * @return
	 */
	public IAssetModuleManager getAssetModuleManager() {
		return assetModuleManager;
	}

	/**
	 * Get the search provider manager implementation.
	 * 
	 * @return
	 */
	public ISearchProviderManager getSearchProviderManager() {
		return searchProviderManager;
	}

	/**
	 * Get the metrics registry.
	 * 
	 * @return
	 */
	public MetricRegistry getMetricRegistry() {
		return metricRegistry;
	}

	/**
	 * Get the health check registry.
	 * 
	 * @return
	 */
	public HealthCheckRegistry getHealthCheckRegistry() {
		return healthCheckRegistry;
	}

	/**
	 * Gets the CATALINA/conf/sitewhere folder where configs are stored.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public static File getSiteWhereConfigFolder() throws SiteWhereException {
		String catalina = System.getProperty("catalina.base");
		if (catalina == null) {
			throw new SiteWhereException("CATALINA_HOME not set.");
		}
		File catFolder = new File(catalina);
		if (!catFolder.exists()) {
			throw new SiteWhereException("CATALINA_HOME folder does not exist.");
		}
		File confDir = new File(catalina, "conf");
		if (!confDir.exists()) {
			throw new SiteWhereException("CATALINA_HOME conf folder does not exist.");
		}
		File sitewhereDir = new File(confDir, "sitewhere");
		if (!confDir.exists()) {
			throw new SiteWhereException("CATALINA_HOME conf/sitewhere folder does not exist.");
		}
		return sitewhereDir;
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
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#start()
	 */
	public void start() throws SiteWhereException {
		// Start core management implementations.
		getDeviceManagement().start();
		if (getDeviceManagementCacheProvider() != null) {
			getDeviceManagementCacheProvider().start();
		}
		getUserManagement().start();
		getAssetModuleManager().start();
		getSearchProviderManager().start();

		// Populate data if requested.
		verifyUserModel();
		verifyDeviceModel();

		// Enable provisioning.
		if (outboundEventProcessorChain != null) {
			outboundEventProcessorChain.start();
			outboundEventProcessorChain.setProcessingEnabled(true);
		}
		if (inboundEventProcessorChain != null) {
			inboundEventProcessorChain.start();
		}
		deviceProvisioning.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		// Disable provisioning.
		outboundEventProcessorChain.setProcessingEnabled(false);
		outboundEventProcessorChain.stop();
		inboundEventProcessorChain.stop();
		deviceProvisioning.stop();

		// Stop core management implementations.
		if (getDeviceManagementCacheProvider() != null) {
			getDeviceManagementCacheProvider().stop();
		}
		getDeviceManagement().stop();
		getUserManagement().stop();
		getAssetModuleManager().stop();
		getSearchProviderManager().stop();
	}

	/**
	 * Create the server.
	 * 
	 * @throws SiteWhereException
	 */
	public void create() throws SiteWhereException {
		LOGGER.info("Initializing SiteWhere server components.");

		// Initialize Spring.
		initializeSpringContext();

		// Initialize device provisioning.
		initializeDeviceProvisioning();

		// Initialize device management.
		initializeDeviceManagement();

		// Initialize processing chain for inbound events.
		initializeInboundEventProcessorChain();

		// Initialize user management.
		initializeUserManagement();

		// Initialize asset management.
		initializeAssetManagement();

		// Initialize search provider management.
		initializeSearchProviderManagement();

		// Print version information.
		this.version = VersionHelper.getVersion();
		List<String> messages = new ArrayList<String>();
		messages.add("SiteWhere Server");
		messages.add("");
		messages.add("Version: " + version.getVersionIdentifier() + "." + version.getBuildTimestamp());
		messages.add("");
		messages.add("Copyright (c) 2013-2014 Reveal Technologies, LLC");
		String message = StringMessageUtils.getBoilerPlate(messages, '*', 60);
		LOGGER.info("\n" + message + "\n");
	}

	/**
	 * Verifies and loads the Spring configuration file.
	 * 
	 * @throws SiteWhereException
	 */
	protected void initializeSpringContext() throws SiteWhereException {
		LOGGER.info("Loading Spring configuration ...");
		File sitewhereConf = getSiteWhereConfigFolder();
		File serverConfigFile = new File(sitewhereConf, SERVER_CONFIG_FILE_NAME);
		if (!serverConfigFile.exists()) {
			throw new SiteWhereException("SiteWhere server configuration not found: "
					+ serverConfigFile.getAbsolutePath());
		}
		SERVER_SPRING_CONTEXT = loadServerApplicationContext(serverConfigFile);
	}

	/**
	 * Initialize device management implementation and associated decorators.
	 * 
	 * @throws SiteWhereException
	 */
	protected void initializeDeviceManagement() throws SiteWhereException {
		// Load device management cache provider if configured.
		try {
			this.deviceManagementCacheProvider =
					(IDeviceManagementCacheProvider) SERVER_SPRING_CONTEXT.getBean(SiteWhereServerBeans.BEAN_DEVICE_MANAGEMENT_CACHE_PROVIDER);
		} catch (NoSuchBeanDefinitionException e) {
			LOGGER.info("No device management cache provider configured. Caching disabled.");
		}

		// Verify that a device management implementation exists.
		try {
			IDeviceManagement deviceManagementImpl =
					(IDeviceManagement) SERVER_SPRING_CONTEXT.getBean(SiteWhereServerBeans.BEAN_DEVICE_MANAGEMENT);
			deviceManagement = new DeviceManagementMetricsDecorator(deviceManagementImpl);

			// Inject cache provider.
			if (getDeviceManagementCacheProvider() != null) {
				if (deviceManagementImpl instanceof ICachingDeviceManagement) {
					((ICachingDeviceManagement) deviceManagementImpl).setCacheProvider(getDeviceManagementCacheProvider());
					LOGGER.info("Device management implementation is using configured cache provider.");
				} else {
					LOGGER.info("Device management implementation not using cache provider.");
				}
			}
		} catch (NoSuchBeanDefinitionException e) {
			throw new SiteWhereException("No device management implementation configured.");
		}

		// If device event processor chain is defined, use it.
		try {
			outboundEventProcessorChain =
					(IOutboundEventProcessorChain) SERVER_SPRING_CONTEXT.getBean(SiteWhereServerBeans.BEAN_OUTBOUND_PROCESSOR_CHAIN);
			deviceManagement = new OutboundProcessingStrategyDecorator(deviceManagement);
			LOGGER.info("Event processor chain found with "
					+ outboundEventProcessorChain.getProcessors().size() + " processors.");
		} catch (NoSuchBeanDefinitionException e) {
			LOGGER.info("No outbound event processor chain found in configuration file.");
		}
	}

	/**
	 * Initializes the {@link IInboundEventProcessorChain} that handles events coming into
	 * the system from external devices.
	 * 
	 * @throws SiteWhereException
	 */
	protected void initializeInboundEventProcessorChain() throws SiteWhereException {
		try {
			inboundEventProcessorChain =
					(IInboundEventProcessorChain) SERVER_SPRING_CONTEXT.getBean(SiteWhereServerBeans.BEAN_INBOUND_PROCESSOR_CHAIN);
		} catch (NoSuchBeanDefinitionException e) {
			LOGGER.info("No inbound event processor chain found in configuration file.");
		}
	}

	/**
	 * Verify and initialize device provisioning implementation.
	 * 
	 * @throws SiteWhereException
	 */
	protected void initializeDeviceProvisioning() throws SiteWhereException {
		try {
			deviceProvisioning =
					(IDeviceProvisioning) SERVER_SPRING_CONTEXT.getBean(SiteWhereServerBeans.BEAN_DEVICE_PROVISIONING);
		} catch (NoSuchBeanDefinitionException e) {
			throw new SiteWhereException("No device provisioning implementation configured.");
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
	 * Verify and initialize asset module manager.
	 * 
	 * @throws SiteWhereException
	 */
	protected void initializeAssetManagement() throws SiteWhereException {
		try {
			assetModuleManager =
					(IAssetModuleManager) SERVER_SPRING_CONTEXT.getBean(SiteWhereServerBeans.BEAN_ASSET_MODULE_MANAGER);
		} catch (NoSuchBeanDefinitionException e) {
			throw new SiteWhereException("No asset module manager implementation configured.");
		}
	}

	/**
	 * Verify and initialize search provider manager.
	 * 
	 * @throws SiteWhereException
	 */
	protected void initializeSearchProviderManagement() throws SiteWhereException {
		try {
			searchProviderManager =
					(ISearchProviderManager) SERVER_SPRING_CONTEXT.getBean(SiteWhereServerBeans.BEAN_SEARCH_PROVIDER_MANAGER);
		} catch (NoSuchBeanDefinitionException e) {
			throw new SiteWhereException("No search provider manager implementation configured.");
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

	/**
	 * Check whether device model is populated and offer to bootstrap system if not.
	 */
	protected void verifyDeviceModel() {
		try {
			IDeviceModelInitializer init =
					(IDeviceModelInitializer) SERVER_SPRING_CONTEXT.getBean(SiteWhereServerBeans.BEAN_DEVICE_MODEL_INITIALIZER);
			ISearchResults<ISite> sites = getDeviceManagement().listSites(new SearchCriteria(1, 1));
			if (sites.getNumResults() == 0) {
				List<String> messages = new ArrayList<String>();
				messages.add("There are currently no sites defined in the system. You have the option of loading "
						+ "a default dataset for previewing system functionality. Would you like to load the default "
						+ "dataset?");
				String message = StringMessageUtils.getBoilerPlate(messages, '*', 60);
				LOGGER.info("\n" + message + "\n");
				System.out.println("Load default dataset? Yes/No (Default is Yes)");
				String response = readLine();
				if ((response == null) && (init.isInitializeIfNoConsole())) {
					response = "Y";
				} else if ((response == null) && (!init.isInitializeIfNoConsole())) {
					response = "N";
				}
				if ((response.length() == 0) || (response.toLowerCase().startsWith("y"))) {
					init.initialize(getDeviceManagement());
				}
			}
		} catch (NoSuchBeanDefinitionException e) {
			LOGGER.info("No device model initializer found in Spring bean configuration. Skipping.");
			return;
		} catch (SiteWhereException e) {
			LOGGER.warn("Unable to read from device model.", e);
		}
	}

	/**
	 * Load the springified server configuration.
	 * 
	 * @return
	 */
	protected ApplicationContext loadServerApplicationContext(File configFile) throws SiteWhereException {
		GenericApplicationContext context = new GenericApplicationContext();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(context);
		reader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_XSD);
		reader.loadBeanDefinitions(new FileSystemResource(configFile));
		context.refresh();
		return context;
	}
}