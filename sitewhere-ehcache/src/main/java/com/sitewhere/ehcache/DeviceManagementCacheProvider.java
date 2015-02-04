/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
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

import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.cache.CacheType;
import com.sitewhere.spi.cache.ICache;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagementCacheProvider;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link IDeviceManagementCacheProvider} that uses EHCache for caching.
 * 
 * @author Derek
 */
public class DeviceManagementCacheProvider extends LifecycleComponent implements
		IDeviceManagementCacheProvider {

	/** Static logger instance */
	private static final Logger LOGGER = Logger.getLogger(DeviceManagementCacheProvider.class);

	/** Cache id for site cache */
	public static final String SITE_CACHE_ID = "sitewhere-site-cache";

	/** Cache id for device specification cache */
	public static final String DEVICE_SPECIFICATION_CACHE_ID = "sitewhere-device-specification-cache";

	/** Cache id for device cache */
	public static final String DEVICE_CACHE_ID = "sitewhere-device-cache";

	/** Cache id for device assignment cache */
	public static final String DEVICE_ASSIGNMENT_CACHE_ID = "sitewhere-device-assignment-cache";

	/** Max number of entries in site cache */
	public long siteCacheMaxEntries = 50;

	/** Max number of entries in specification cache */
	public long deviceSpecificationCacheMaxEntries = 100;

	/** Max number of entries in device cache */
	public long deviceCacheMaxEntries = 1000;

	/** Max number of entries in assignment cache */
	public long deviceAssignmentCacheMaxEntries = 1000;

	/** Time to live (seconds) for site entries */
	public long siteCacheTtl = 300;

	/** Time to live (seconds) for specification entries */
	public long deviceSpecificationCacheTtl = 300;

	/** Time to live (seconds) for device entries */
	public long deviceCacheTtl = 60;

	/** Time to live (seconds) for assignment entries */
	public long deviceAssignmentCacheTtl = 60;

	/** Cache for site data */
	private CacheAdapter<String, ISite> siteCache;

	/** Cache for device data */
	private CacheAdapter<String, IDeviceSpecification> deviceSpecificationCache;

	/** Cache for device data */
	private CacheAdapter<String, IDevice> deviceCache;

	/** Cache for device assignment data */
	private CacheAdapter<String, IDeviceAssignment> deviceAssignmentCache;

	public DeviceManagementCacheProvider() {
		super(LifecycleComponentType.CacheProvider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		LOGGER.info("Starting EHCache device management cache provider...");

		Configuration config = new Configuration();
		CacheConfiguration cacheConfig = new CacheConfiguration();
		cacheConfig.setMaxEntriesLocalHeap(100);
		cacheConfig.setTimeToLiveSeconds(60);
		config.setDefaultCacheConfiguration(cacheConfig);
		CacheManager manager = CacheManager.create(config);

		// Create site cache.
		siteCache =
				createCache(manager, ISite.class, SITE_CACHE_ID, CacheType.SiteCache,
						getSiteCacheMaxEntries(), getSiteCacheTtl());

		// Create device specification cache.
		deviceSpecificationCache =
				createCache(manager, IDeviceSpecification.class, DEVICE_SPECIFICATION_CACHE_ID,
						CacheType.DeviceSpecificationCache, getDeviceSpecificationCacheMaxEntries(),
						getDeviceSpecificationCacheTtl());

		// Create device cache.
		deviceCache =
				createCache(manager, IDevice.class, DEVICE_CACHE_ID, CacheType.DeviceCache,
						getDeviceCacheMaxEntries(), getDeviceCacheTtl());

		// Create device assignment cache.
		deviceAssignmentCache =
				createCache(manager, IDeviceAssignment.class, DEVICE_ASSIGNMENT_CACHE_ID,
						CacheType.DeviceAssignmentCache, getDeviceAssignmentCacheMaxEntries(),
						getDeviceAssignmentCacheTtl());
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

	/**
	 * Create a cache with the given characteristics.
	 * 
	 * @param manager
	 * @param clazz
	 * @param cacheId
	 * @param type
	 * @param maxEntries
	 * @param ttl
	 * @return
	 */
	protected <T> CacheAdapter<String, T> createCache(CacheManager manager, Class<T> clazz, String cacheId,
			CacheType type, long maxEntries, long ttl) {
		Ehcache ehcache = manager.addCacheIfAbsent(cacheId);
		ehcache.getCacheConfiguration().setMaxEntriesLocalHeap(maxEntries);
		ehcache.getCacheConfiguration().setTimeToLiveSeconds(ttl);
		CacheAdapter<String, T> cache = new CacheAdapter<String, T>(type, ehcache);
		LOGGER.info(type.name() + " created (entries: " + maxEntries + ", ttl: " + ttl + ").");
		return cache;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagementCacheProvider#getSiteCache()
	 */
	@Override
	public ICache<String, ISite> getSiteCache() throws SiteWhereException {
		return siteCache;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagementCacheProvider#getDeviceSpecificationCache
	 * ()
	 */
	@Override
	public ICache<String, IDeviceSpecification> getDeviceSpecificationCache() throws SiteWhereException {
		return deviceSpecificationCache;
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

	public long getSiteCacheMaxEntries() {
		return siteCacheMaxEntries;
	}

	public void setSiteCacheMaxEntries(long siteCacheMaxEntries) {
		this.siteCacheMaxEntries = siteCacheMaxEntries;
	}

	public long getDeviceSpecificationCacheMaxEntries() {
		return deviceSpecificationCacheMaxEntries;
	}

	public void setDeviceSpecificationCacheMaxEntries(long deviceSpecificationCacheMaxEntries) {
		this.deviceSpecificationCacheMaxEntries = deviceSpecificationCacheMaxEntries;
	}

	public long getDeviceCacheMaxEntries() {
		return deviceCacheMaxEntries;
	}

	public void setDeviceCacheMaxEntries(long deviceCacheMaxEntries) {
		this.deviceCacheMaxEntries = deviceCacheMaxEntries;
	}

	public long getDeviceAssignmentCacheMaxEntries() {
		return deviceAssignmentCacheMaxEntries;
	}

	public void setDeviceAssignmentCacheMaxEntries(long deviceAssignmentCacheMaxEntries) {
		this.deviceAssignmentCacheMaxEntries = deviceAssignmentCacheMaxEntries;
	}

	public long getSiteCacheTtl() {
		return siteCacheTtl;
	}

	public void setSiteCacheTtl(long siteCacheTtl) {
		this.siteCacheTtl = siteCacheTtl;
	}

	public long getDeviceSpecificationCacheTtl() {
		return deviceSpecificationCacheTtl;
	}

	public void setDeviceSpecificationCacheTtl(long deviceSpecificationCacheTtl) {
		this.deviceSpecificationCacheTtl = deviceSpecificationCacheTtl;
	}

	public long getDeviceCacheTtl() {
		return deviceCacheTtl;
	}

	public void setDeviceCacheTtl(long deviceCacheTtl) {
		this.deviceCacheTtl = deviceCacheTtl;
	}

	public long getDeviceAssignmentCacheTtl() {
		return deviceAssignmentCacheTtl;
	}

	public void setDeviceAssignmentCacheTtl(long deviceAssignmentCacheTtl) {
		this.deviceAssignmentCacheTtl = deviceAssignmentCacheTtl;
	}
}