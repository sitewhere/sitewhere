/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.cache;

import java.util.UUID;

import com.sitewhere.grpc.client.spi.cache.ICacheConfiguration;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceType;

/**
 * Cache providers for device management entities.
 */
public class DeviceManagementCacheProviders {

    /**
     * Cache for areas.
     * 
     * @author Derek
     */
    public static class AreaByTokenCache extends CacheProvider<String, IArea> {

	public AreaByTokenCache(ICacheConfiguration configuration) {
	    super(CacheIdentifier.AreaByToken, String.class, IArea.class, configuration);
	}
    }

    /**
     * Cache for areas by id.
     * 
     * @author Derek
     */
    public static class AreaByIdCache extends CacheProvider<UUID, IArea> {

	public AreaByIdCache(ICacheConfiguration configuration) {
	    super(CacheIdentifier.AreaById, UUID.class, IArea.class, configuration);
	}
    }

    /**
     * Cache for device types.
     * 
     * @author Derek
     */
    public static class DeviceTypeByTokenCache extends CacheProvider<String, IDeviceType> {

	public DeviceTypeByTokenCache(ICacheConfiguration configuration) {
	    super(CacheIdentifier.DeviceByToken, String.class, IDeviceType.class, configuration);
	}
    }

    /**
     * Cache for device types by id.
     * 
     * @author Derek
     */
    public static class DeviceTypeByIdCache extends CacheProvider<UUID, IDeviceType> {

	public DeviceTypeByIdCache(ICacheConfiguration configuration) {
	    super(CacheIdentifier.DeviceTypeById, UUID.class, IDeviceType.class, configuration);
	}
    }

    /**
     * Cache for devices by token.
     * 
     * @author Derek
     */
    public static class DeviceByTokenCache extends CacheProvider<String, IDevice> {

	public DeviceByTokenCache(ICacheConfiguration configuration) {
	    super(CacheIdentifier.DeviceByToken, String.class, IDevice.class, configuration);
	}
    }

    /**
     * Cache for devices by id.
     * 
     * @author Derek
     */
    public static class DeviceByIdCache extends CacheProvider<UUID, IDevice> {

	public DeviceByIdCache(ICacheConfiguration configuration) {
	    super(CacheIdentifier.DeviceById, UUID.class, IDevice.class, configuration);
	}
    }

    /**
     * Cache for device assignments by token.
     * 
     * @author Derek
     */
    public static class DeviceAssignmentByTokenCache extends CacheProvider<String, IDeviceAssignment> {

	public DeviceAssignmentByTokenCache(ICacheConfiguration configuration) {
	    super(CacheIdentifier.DeviceAssignmentByToken, String.class, IDeviceAssignment.class, configuration);
	}
    }

    /**
     * Cache for device assignments by id.
     * 
     * @author Derek
     */
    public static class DeviceAssignmentByIdCache extends CacheProvider<UUID, IDeviceAssignment> {

	public DeviceAssignmentByIdCache(ICacheConfiguration configuration) {
	    super(CacheIdentifier.DeviceAssignmentById, UUID.class, IDeviceAssignment.class, configuration);
	}
    }
}