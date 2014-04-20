/*
 * DeviceManagementCacheProvider.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.ehcache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;

import org.apache.log4j.Logger;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.cache.ICache;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagementCacheProvider;

/**
 * Implementation of {@link IDeviceManagementCacheProvider} that uses EHCache for caching.
 * 
 * @author Derek
 */
public class DeviceManagementCacheProvider implements IDeviceManagementCacheProvider {

	/** Static logger instance */
	private static final Logger LOGGER = Logger.getLogger(DeviceManagementCacheProvider.class);

	/** Cache id for device cache */
	public static final String DEVICE_CACHE_ID = "sitewhere-device-cache";

	/** Cache id for device assignment cache */
	public static final String DEVICE_ASSIGNMENT_CACHE_ID = "sitewhere-device-assignment-cache";

	/** Default max cache entries */
	public static final int DEFAULT_MAX_ENTRIES = 500;

	/** Default max time to live */
	public static final int DEFAULT_MAX_TTL = 30;

	/** Cache for device data */
	private CacheAdapter<String, IDevice> deviceCache;

	/** Cache for device assignment data */
	private CacheAdapter<String, IDeviceAssignment> deviceAssignmentCache;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		LOGGER.info("Starting EHCache device management cache provider...");

		Configuration config = new Configuration();
		CacheConfiguration cacheConfig = new CacheConfiguration();
		cacheConfig.setMaxEntriesLocalHeap(DEFAULT_MAX_ENTRIES);
		cacheConfig.setTimeToLiveSeconds(DEFAULT_MAX_TTL);
		config.setDefaultCacheConfiguration(cacheConfig);
		CacheManager manager = CacheManager.create(config);

		// Create device cache.
		Ehcache dc = manager.addCacheIfAbsent(DEVICE_CACHE_ID);
		deviceCache = new CacheAdapter<String, IDevice>(dc);

		// Create device assignment cache.
		Ehcache dac = manager.addCacheIfAbsent(DEVICE_ASSIGNMENT_CACHE_ID);
		deviceAssignmentCache = new CacheAdapter<String, IDeviceAssignment>(dac);
		LOGGER.info("Started EHCache device management cache provider.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagementCacheProvider#getDeviceCache()
	 */
	@Override
	public ICache<String, IDevice> getDeviceCache() throws SiteWhereException {
		return deviceCache;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagementCacheProvider#getDeviceAssignmentCache()
	 */
	@Override
	public ICache<String, IDeviceAssignment> getDeviceAssignmentCache() throws SiteWhereException {
		return deviceAssignmentCache;
	}
}