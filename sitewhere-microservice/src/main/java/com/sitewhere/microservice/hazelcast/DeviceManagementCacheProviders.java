/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.hazelcast;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.microservice.IMicroservice;

/**
 * Cache providers for device management entities.
 * 
 * @author Derek
 */
public class DeviceManagementCacheProviders {

    /** Cache id for device specification cache */
    public static final String ID_SPECIFICATION_CACHE = "sp";

    /** Cache id for device cache */
    public static final String ID_DEVICE_CACHE = "dv";

    /** Cache id for device assignment cache */
    public static final String ID_ASSIGNMENT_CACHE = "as";

    /**
     * Cache for device specifications.
     * 
     * @author Derek
     */
    public static class DeviceSpecificationCache extends CacheProvider<String, IDeviceSpecification> {

	/** Static logger instance */
	private static Logger LOGGER = LogManager.getLogger();

	public DeviceSpecificationCache(IMicroservice microservice, boolean createOnStartup) {
	    super(microservice, ID_SPECIFICATION_CACHE, createOnStartup);
	}

	/*
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Logger getLogger() {
	    return LOGGER;
	}
    }

    /**
     * Cache for devices.
     * 
     * @author Derek
     */
    public static class DeviceCache extends CacheProvider<String, IDevice> {

	/** Static logger instance */
	private static Logger LOGGER = LogManager.getLogger();

	public DeviceCache(IMicroservice microservice, boolean createOnStartup) {
	    super(microservice, ID_DEVICE_CACHE, createOnStartup);
	}

	/*
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Logger getLogger() {
	    return LOGGER;
	}
    }

    /**
     * Cache for device assignments.
     * 
     * @author Derek
     */
    public static class DeviceAssignmentCache extends CacheProvider<String, IDeviceAssignment> {

	/** Static logger instance */
	private static Logger LOGGER = LogManager.getLogger();

	public DeviceAssignmentCache(IMicroservice microservice, boolean createOnStartup) {
	    super(microservice, ID_ASSIGNMENT_CACHE, createOnStartup);
	}

	/*
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Logger getLogger() {
	    return LOGGER;
	}
    }
}
