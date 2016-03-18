/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.server;

import java.util.List;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.sitewhere.spi.ServerStartupException;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.asset.IAssetModuleManager;
import com.sitewhere.spi.configuration.IGlobalConfigurationResolver;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceManagementCacheProvider;
import com.sitewhere.spi.device.communication.IDeviceCommunication;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.IEventProcessing;
import com.sitewhere.spi.scheduling.IScheduleManagement;
import com.sitewhere.spi.scheduling.IScheduleManager;
import com.sitewhere.spi.search.external.ISearchProviderManager;
import com.sitewhere.spi.server.debug.ITracer;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;
import com.sitewhere.spi.server.tenant.ISiteWhereTenantEngine;
import com.sitewhere.spi.system.IVersion;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.ITenantManagement;
import com.sitewhere.spi.user.IUserManagement;

/**
 * Interface for interacting with core SiteWhere server functionality.
 * 
 * @author Derek
 */
public interface ISiteWhereServer extends ILifecycleComponent {

	/**
	 * Get version information.
	 * 
	 * @return
	 */
	public IVersion getVersion();

	/**
	 * Get Spring parser class name for handling bean definitions.
	 * 
	 * @return
	 */
	public String getTenantConfigurationParserClassname();

	/**
	 * Get persistent server state information.
	 * 
	 * @return
	 */
	public ISiteWhereServerState getServerState();

	/**
	 * Gets runtime information about the server.
	 * 
	 * @param includeHistorical
	 * @return
	 * @throws SiteWhereException
	 */
	public ISiteWhereServerRuntime getServerRuntimeInformation(boolean includeHistorical)
			throws SiteWhereException;

	/**
	 * Initialize the server.
	 * 
	 * @throws SiteWhereException
	 */
	public void initialize() throws SiteWhereException;

	/**
	 * Returns exception if one was thrown on startup.
	 * 
	 * @return
	 */
	public ServerStartupException getServerStartupError();

	/**
	 * Set server startup error reason.
	 * 
	 * @param e
	 */
	public void setServerStartupError(ServerStartupException e);

	/**
	 * Get tracer for debug operations.
	 * 
	 * @return
	 */
	public ITracer getTracer();

	/**
	 * Get class that can be used to location the Spring configuration context.
	 * 
	 * @return
	 */
	public IGlobalConfigurationResolver getConfigurationResolver();

	/**
	 * Get a tenant based on its authentication token.
	 * 
	 * @param authToken
	 * @return
	 * @throws SiteWhereException
	 */
	public ITenant getTenantByAuthToken(String authToken) throws SiteWhereException;

	/**
	 * Get list of tenants a given user can access.
	 * 
	 * @param userId
	 * @param requireStarted
	 * @return
	 * @throws SiteWhereException
	 */
	public List<ITenant> getAuthorizedTenants(String userId, boolean requireStarted)
			throws SiteWhereException;

	/**
	 * Get a tenant engine by tenant id. If a tenant exists but the engine has not been
	 * initialized, the tenant engine will be initialized and started.
	 * 
	 * @param tenantId
	 * @return
	 * @throws SiteWhereException
	 */
	public ISiteWhereTenantEngine getTenantEngine(String tenantId) throws SiteWhereException;

	/**
	 * Called when tenant information has been updated so that cached data is kept
	 * current.
	 * 
	 * @param tenant
	 * @throws SiteWhereException
	 */
	public void onTenantInformationUpdated(ITenant tenant) throws SiteWhereException;

	/**
	 * Get the user management implementation.
	 * 
	 * @return
	 */
	public IUserManagement getUserManagement();

	/**
	 * Get the tenant management implementation.
	 * 
	 * @return
	 */
	public ITenantManagement getTenantManagement();

	/**
	 * Get device management implementation for tenant.
	 * 
	 * @param tenant
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceManagement getDeviceManagement(ITenant tenant) throws SiteWhereException;

	/**
	 * Get device event management implementation for tenant.
	 * 
	 * @param tenant
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceEventManagement getDeviceEventManagement(ITenant tenant) throws SiteWhereException;

	/**
	 * Get device management cache provider for tenant.
	 * 
	 * @param tenant
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceManagementCacheProvider getDeviceManagementCacheProvider(ITenant tenant)
			throws SiteWhereException;

	/**
	 * Get asset management implementation for the given tenant.
	 * 
	 * @param tenant
	 * @return
	 * @throws SiteWhereException
	 */
	public IAssetManagement getAssetManagement(ITenant tenant) throws SiteWhereException;

	/**
	 * Get schedule management implementation for the given tenant.
	 * 
	 * @param tenant
	 * @return
	 * @throws SiteWhereException
	 */
	public IScheduleManagement getScheduleManagement(ITenant tenant) throws SiteWhereException;

	/**
	 * Get device communication subsystem for the given tenant.
	 * 
	 * @param tenant
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceCommunication getDeviceCommunication(ITenant tenant) throws SiteWhereException;

	/**
	 * Get the event processing subsystem for the given tenant.
	 * 
	 * @param tenant
	 * @return
	 * @throws SiteWhereException
	 */
	public IEventProcessing getEventProcessing(ITenant tenant) throws SiteWhereException;

	/**
	 * Get asset module manager for tenant.
	 * 
	 * @param tenant
	 * @return
	 * @throws SiteWhereException
	 */
	public IAssetModuleManager getAssetModuleManager(ITenant tenant) throws SiteWhereException;

	/**
	 * Get search provider manager for tenant.
	 * 
	 * @param tenant
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchProviderManager getSearchProviderManager(ITenant tenant) throws SiteWhereException;

	/**
	 * Get schedule manager for tenant.
	 * 
	 * @param tenant
	 * @return
	 * @throws SiteWhereException
	 */
	public IScheduleManager getScheduleManager(ITenant tenant) throws SiteWhereException;

	/**
	 * Get list of components that have registered to participate in the server component
	 * lifecycle.
	 * 
	 * @return
	 */
	public List<ITenantLifecycleComponent> getRegisteredLifecycleComponents();

	/**
	 * Gets an {@link ILifecycleComponent} by unique id.
	 * 
	 * @param id
	 * @return
	 */
	public ILifecycleComponent getLifecycleComponentById(String id);

	/**
	 * Get the metrics registry.
	 * 
	 * @return
	 */
	public MetricRegistry getMetricRegistry();

	/**
	 * Get the health check registry.
	 * 
	 * @return
	 */
	public HealthCheckRegistry getHealthCheckRegistry();
}