/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hazelcast;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
public class DeviceManagementCacheProvider extends TenantLifecycleComponent implements IDeviceManagementCacheProvider {

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

    public DeviceManagementCacheProvider() {
	super(LifecycleComponentType.CacheProvider);
    }

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
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	this.siteCache = new HazelcastCache<ISite>(this, HazelcastCache.getNameForTenantCache(getTenant(), SITE_CACHE),
		CacheType.SiteCache, false);
	this.specificationCache = new HazelcastCache<IDeviceSpecification>(this,
		HazelcastCache.getNameForTenantCache(getTenant(), SPECIFICATION_CACHE),
		CacheType.DeviceSpecificationCache, false);
	this.deviceCache = new HazelcastCache<IDevice>(this,
		HazelcastCache.getNameForTenantCache(getTenant(), DEVICE_CACHE), CacheType.DeviceCache, false);
	this.assignmentCache = new HazelcastCache<IDeviceAssignment>(this,
		HazelcastCache.getNameForTenantCache(getTenant(), ASSIGNMENT_CACHE), CacheType.DeviceAssignmentCache,
		false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
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
}