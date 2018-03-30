/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.cache;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.microservice.hazelcast.IHazelcastProvider;

/**
 * Cache providers for device management entities.
 * 
 * @author Derek
 */
public class DeviceManagementCacheProviders {

    /**
     * Cache for areas.
     * 
     * @author Derek
     */
    public static class AreaByTokenCache extends CacheProvider<String, IArea> {

	/** Static logger instance */
	private static Log LOGGER = LogFactory.getLog(AreaByTokenCache.class);

	public AreaByTokenCache(IHazelcastProvider hazelcastProvider) {
	    super(hazelcastProvider, CacheIdentifier.AreaByToken, 1000);
	}

	/*
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Log getLogger() {
	    return LOGGER;
	}
    }

    /**
     * Cache for areas by id.
     * 
     * @author Derek
     */
    public static class AreaByIdCache extends CacheProvider<UUID, IArea> {

	/** Static logger instance */
	private static Log LOGGER = LogFactory.getLog(AreaByIdCache.class);

	public AreaByIdCache(IHazelcastProvider hazelcastProvider) {
	    super(hazelcastProvider, CacheIdentifier.AreaById, 1000);
	}

	/*
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Log getLogger() {
	    return LOGGER;
	}
    }

    /**
     * Cache for device types.
     * 
     * @author Derek
     */
    public static class DeviceTypeByTokenCache extends CacheProvider<String, IDeviceType> {

	/** Static logger instance */
	private static Log LOGGER = LogFactory.getLog(DeviceTypeByTokenCache.class);

	public DeviceTypeByTokenCache(IHazelcastProvider hazelcastProvider) {
	    super(hazelcastProvider, CacheIdentifier.DeviceByToken, 100);
	}

	/*
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Log getLogger() {
	    return LOGGER;
	}
    }

    /**
     * Cache for device types by id.
     * 
     * @author Derek
     */
    public static class DeviceTypeByIdCache extends CacheProvider<UUID, IDeviceType> {

	/** Static logger instance */
	private static Log LOGGER = LogFactory.getLog(DeviceTypeByIdCache.class);

	public DeviceTypeByIdCache(IHazelcastProvider hazelcastProvider) {
	    super(hazelcastProvider, CacheIdentifier.DeviceTypeById, 100);
	}

	/*
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Log getLogger() {
	    return LOGGER;
	}
    }

    /**
     * Cache for devices by token.
     * 
     * @author Derek
     */
    public static class DeviceByTokenCache extends CacheProvider<String, IDevice> {

	/** Static logger instance */
	private static Log LOGGER = LogFactory.getLog(DeviceByTokenCache.class);

	public DeviceByTokenCache(IHazelcastProvider hazelcastProvider) {
	    super(hazelcastProvider, CacheIdentifier.DeviceByToken, 5000);
	}

	/*
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Log getLogger() {
	    return LOGGER;
	}
    }

    /**
     * Cache for devices by id.
     * 
     * @author Derek
     */
    public static class DeviceByIdCache extends CacheProvider<UUID, IDevice> {

	/** Static logger instance */
	private static Log LOGGER = LogFactory.getLog(DeviceByIdCache.class);

	public DeviceByIdCache(IHazelcastProvider hazelcastProvider) {
	    super(hazelcastProvider, CacheIdentifier.DeviceById, 5000);
	}

	/*
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Log getLogger() {
	    return LOGGER;
	}
    }

    /**
     * Cache for device assignments by token.
     * 
     * @author Derek
     */
    public static class DeviceAssignmentByTokenCache extends CacheProvider<String, IDeviceAssignment> {

	/** Static logger instance */
	private static Log LOGGER = LogFactory.getLog(DeviceAssignmentByTokenCache.class);

	public DeviceAssignmentByTokenCache(IHazelcastProvider hazelcastProvider) {
	    super(hazelcastProvider, CacheIdentifier.DeviceAssignmentByToken, 5000);
	}

	/*
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Log getLogger() {
	    return LOGGER;
	}
    }

    /**
     * Cache for device assignments by id.
     * 
     * @author Derek
     */
    public static class DeviceAssignmentByIdCache extends CacheProvider<UUID, IDeviceAssignment> {

	/** Static logger instance */
	private static Log LOGGER = LogFactory.getLog(DeviceAssignmentByIdCache.class);

	public DeviceAssignmentByIdCache(IHazelcastProvider hazelcastProvider) {
	    super(hazelcastProvider, CacheIdentifier.DeviceAssignmentById, 5000);
	}

	/*
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Log getLogger() {
	    return LOGGER;
	}
    }
}