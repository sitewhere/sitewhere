/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hazelcast;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hazelcast.core.IMap;
import com.sitewhere.SiteWhere;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.cache.CacheType;
import com.sitewhere.spi.cache.ICache;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagementCacheProvider;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Implements {@link IDeviceManagementCacheProvider} using Hazelcast as a
 * distributed cache.
 * 
 * @author Derek
 */
public class HazelcastDistributedCacheProvider extends TenantLifecycleComponent
	implements IDeviceManagementCacheProvider {

    public HazelcastDistributedCacheProvider() {
	super(LifecycleComponentType.CacheProvider);
    }

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Name of site cache */
    private static final String SITE_CACHE = "siteCache";

    /** Name of device specification cache */
    private static final String SPECIFICATION_CACHE = "specificationCache";

    /** Name of device cache */
    private static final String DEVICE_CACHE = "deviceCache";

    /** Name of assignment cache */
    private static final String ASSIGNMENT_CACHE = "assignmentCache";

    /** Cache for sites */
    private HazelcastCache<ISite> siteCache;

    /** Cache for device specifications */
    private HazelcastCache<IDeviceSpecification> specificationCache;

    /** Cache for devices */
    private HazelcastCache<IDevice> deviceCache;

    /** Cache for device assignments */
    private HazelcastCache<IDeviceAssignment> assignmentCache;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	this.siteCache = new HazelcastCache<ISite>(addTenantPrefix(SITE_CACHE), CacheType.SiteCache);
	this.specificationCache = new HazelcastCache<IDeviceSpecification>(addTenantPrefix(SPECIFICATION_CACHE),
		CacheType.DeviceSpecificationCache);
	this.deviceCache = new HazelcastCache<IDevice>(addTenantPrefix(DEVICE_CACHE), CacheType.DeviceCache);
	this.assignmentCache = new HazelcastCache<IDeviceAssignment>(addTenantPrefix(ASSIGNMENT_CACHE),
		CacheType.DeviceAssignmentCache);
    }

    /**
     * Add prefix so that each tenant has a unique cache.
     * 
     * @param cacheName
     * @return
     */
    protected String addTenantPrefix(String cacheName) {
	return getTenant().getId() + "-" + cacheName;
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
     * @see
     * com.sitewhere.spi.device.IDeviceManagementCacheProvider#getSiteCache()
     */
    @Override
    public ICache<String, ISite> getSiteCache() throws SiteWhereException {
	return siteCache;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagementCacheProvider#
     * getDeviceSpecificationCache ()
     */
    @Override
    public ICache<String, IDeviceSpecification> getDeviceSpecificationCache() throws SiteWhereException {
	return specificationCache;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagementCacheProvider#getDeviceCache()
     */
    @Override
    public ICache<String, IDevice> getDeviceCache() throws SiteWhereException {
	return deviceCache;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagementCacheProvider#
     * getDeviceAssignmentCache()
     */
    @Override
    public ICache<String, IDeviceAssignment> getDeviceAssignmentCache() throws SiteWhereException {
	return assignmentCache;
    }

    @SuppressWarnings({ "rawtypes", "unused", "unchecked" })
    private class HazelcastCache<T> implements ICache<String, T> {

	/** Name of Hazelcast map */
	private String name;

	/** Cache type */
	private CacheType type;

	/** Hazelcast map used as cache */
	private IMap hMap;

	/** Count of total cache requests */
	private AtomicLong requestCount = new AtomicLong();

	/** Count of total cache hits */
	private AtomicLong hitCount = new AtomicLong();

	public HazelcastCache(String name, CacheType type) {
	    this.name = name;
	    this.type = type;
	    this.hMap = SiteWhere.getServer().getHazelcastConfiguration().getHazelcastInstance().getMap(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.cache.ICache#getType()
	 */
	@Override
	public CacheType getType() {
	    return type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.cache.ICache#get(java.lang.Object)
	 */
	@Override
	public T get(String key) throws SiteWhereException {
	    T result = (T) hMap.get(key);
	    requestCount.incrementAndGet();
	    if (result != null) {
		hitCount.incrementAndGet();
	    }
	    return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.cache.ICache#put(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public void put(String key, T value) throws SiteWhereException {
	    hMap.put(key, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.cache.ICache#remove(java.lang.Object)
	 */
	@Override
	public void remove(String key) throws SiteWhereException {
	    hMap.remove(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.cache.ICache#clear()
	 */
	@Override
	public void clear() throws SiteWhereException {
	    hMap.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.cache.ICache#getElementCount()
	 */
	@Override
	public int getElementCount() throws SiteWhereException {
	    return hMap.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.cache.ICache#getRequestCount()
	 */
	@Override
	public long getRequestCount() throws SiteWhereException {
	    return requestCount.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.cache.ICache#getHitCount()
	 */
	@Override
	public long getHitCount() throws SiteWhereException {
	    return hitCount.get();
	}
    }
}