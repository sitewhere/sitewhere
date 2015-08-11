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
import com.sitewhere.spi.configuration.IConfigurationResolver;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceManagementCacheProvider;
import com.sitewhere.spi.device.communication.IDeviceCommunication;
import com.sitewhere.spi.device.event.processor.IInboundEventProcessorChain;
import com.sitewhere.spi.device.event.processor.IOutboundEventProcessorChain;
import com.sitewhere.spi.search.external.ISearchProviderManager;
import com.sitewhere.spi.server.debug.ITracer;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.system.IVersion;
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
	public IConfigurationResolver getConfigurationResolver();

	/**
	 * Get the user management implementation.
	 * 
	 * @return
	 */
	public IUserManagement getUserManagement();

	/**
	 * Get the device management implementation.
	 * 
	 * @return
	 */
	public IDeviceManagement getDeviceManagement();

	/**
	 * Get the asset management implementation.
	 * 
	 * @return
	 */
	public IAssetManagement getAssetManagement();

	/**
	 * Get the configured device management cache provider implementation.
	 * 
	 * @return
	 */
	public IDeviceManagementCacheProvider getDeviceManagementCacheProvider();

	/**
	 * Get the inbound event processor chain.
	 * 
	 * @return
	 */
	public IInboundEventProcessorChain getInboundEventProcessorChain();

	/**
	 * Get the outbound event processor chain.
	 * 
	 * @return
	 */
	public IOutboundEventProcessorChain getOutboundEventProcessorChain();

	/**
	 * Get the device communication subsystem implementation.
	 * 
	 * @return
	 */
	public IDeviceCommunication getDeviceCommunicationSubsystem();

	/**
	 * Get the asset modules manager instance.
	 * 
	 * @return
	 */
	public IAssetModuleManager getAssetModuleManager();

	/**
	 * Get the search provider manager implementation.
	 * 
	 * @return
	 */
	public ISearchProviderManager getSearchProviderManager();

	/**
	 * Get list of components that have registered to participate in the server component
	 * lifecycle.
	 * 
	 * @return
	 */
	public List<ILifecycleComponent> getRegisteredLifecycleComponents();

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