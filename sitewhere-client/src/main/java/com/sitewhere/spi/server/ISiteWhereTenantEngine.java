/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.server;

import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.asset.IAssetModuleManager;
import com.sitewhere.spi.configuration.IConfigurationResolver;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceManagementCacheProvider;
import com.sitewhere.spi.device.communication.IDeviceCommunication;
import com.sitewhere.spi.device.event.processor.IInboundEventProcessorChain;
import com.sitewhere.spi.device.event.processor.IOutboundEventProcessorChain;
import com.sitewhere.spi.search.external.ISearchProviderManager;
import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

/**
 * A SiteWhere tenant engine wraps up the processing pipeline and data storage for a
 * single SiteWhere tenant.
 * 
 * @author Derek
 */
public interface ISiteWhereTenantEngine extends ITenantLifecycleComponent {

	/**
	 * Get class that can be used to location the Spring configuration context.
	 * 
	 * @return
	 */
	public IConfigurationResolver getConfigurationResolver();

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
	public IDeviceCommunication getDeviceCommunication();

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
	 * Get current runtime state of engine.
	 * 
	 * @return
	 */
	public ISiteWhereTenantEngineState getEngineState();
}