/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.cache;

import java.util.UUID;

import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceType;

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

	public AreaByTokenCache() {
	    super(CacheIdentifier.AreaByToken, String.class, IArea.class, 1000, 60);
	}
    }

    /**
     * Cache for areas by id.
     * 
     * @author Derek
     */
    public static class AreaByIdCache extends CacheProvider<UUID, IArea> {

	public AreaByIdCache() {
	    super(CacheIdentifier.AreaById, UUID.class, IArea.class, 1000, 60);
	}
    }

    /**
     * Cache for device types.
     * 
     * @author Derek
     */
    public static class DeviceTypeByTokenCache extends CacheProvider<String, IDeviceType> {

	public DeviceTypeByTokenCache() {
	    super(CacheIdentifier.DeviceByToken, String.class, IDeviceType.class, 1000, 60);
	}
    }

    /**
     * Cache for device types by id.
     * 
     * @author Derek
     */
    public static class DeviceTypeByIdCache extends CacheProvider<UUID, IDeviceType> {

	public DeviceTypeByIdCache() {
	    super(CacheIdentifier.DeviceTypeById, UUID.class, IDeviceType.class, 1000, 60);
	}
    }

    /**
     * Cache for devices by token.
     * 
     * @author Derek
     */
    public static class DeviceByTokenCache extends CacheProvider<String, IDevice> {

	public DeviceByTokenCache() {
	    super(CacheIdentifier.DeviceByToken, String.class, IDevice.class, 10000, 30);
	}
    }

    /**
     * Cache for devices by id.
     * 
     * @author Derek
     */
    public static class DeviceByIdCache extends CacheProvider<UUID, IDevice> {

	public DeviceByIdCache() {
	    super(CacheIdentifier.DeviceById, UUID.class, IDevice.class, 10000, 30);
	}
    }

    /**
     * Cache for device assignments by token.
     * 
     * @author Derek
     */
    public static class DeviceAssignmentByTokenCache extends CacheProvider<String, IDeviceAssignment> {

	public DeviceAssignmentByTokenCache() {
	    super(CacheIdentifier.DeviceAssignmentByToken, String.class, IDeviceAssignment.class, 10000, 30);
	}
    }

    /**
     * Cache for device assignments by id.
     * 
     * @author Derek
     */
    public static class DeviceAssignmentByIdCache extends CacheProvider<UUID, IDeviceAssignment> {

	public DeviceAssignmentByIdCache() {
	    super(CacheIdentifier.DeviceAssignmentById, UUID.class, IDeviceAssignment.class, 10000, 30);
	}
    }
}