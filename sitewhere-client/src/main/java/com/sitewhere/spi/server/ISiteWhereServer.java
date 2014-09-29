/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.server;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.sitewhere.spi.ISiteWhereLifecycle;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetModuleManager;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceManagementCacheProvider;
import com.sitewhere.spi.device.event.processor.IInboundEventProcessorChain;
import com.sitewhere.spi.device.event.processor.IOutboundEventProcessorChain;
import com.sitewhere.spi.device.provisioning.IDeviceProvisioning;
import com.sitewhere.spi.search.external.ISearchProviderManager;
import com.sitewhere.spi.server.debug.ITracer;
import com.sitewhere.spi.system.IVersion;
import com.sitewhere.spi.user.IUserManagement;

/**
 * Interface for interacting with core SiteWhere server functionality.
 * 
 * @author Derek
 */
public interface ISiteWhereServer extends ISiteWhereLifecycle {

	/**
	 * Initialize the server.
	 * 
	 * @throws SiteWhereException
	 */
	public void initialize() throws SiteWhereException;

	/**
	 * Get version information.
	 * 
	 * @return
	 */
	public IVersion getVersion();

	/**
	 * Get tracer for debug operations.
	 * 
	 * @return
	 */
	public ITracer getTracer();

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
	 * Get the device provisioning implementation.
	 * 
	 * @return
	 */
	public IDeviceProvisioning getDeviceProvisioning();

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