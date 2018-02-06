/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.device;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.client.cache.CacheProvider;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.microservice.IMicroservice;

/**
 * Cache providers for device management entities.
 * 
 * @author Derek
 */
public class DeviceManagementCacheProviders {

    /** Cache id for site cache */
    public static final String ID_SITE_CACHE = "site";

    /** Cache id for site by id cache */
    public static final String ID_SITE_ID_CACHE = "stid";

    /** Cache id for device specification cache */
    public static final String ID_SPECIFICATION_CACHE = "spex";

    /** Cache id for device specification by id cache */
    public static final String ID_SPECIFICATION_ID_CACHE = "spid";

    /** Cache id for device cache */
    public static final String ID_DEVICE_CACHE = "devc";

    /** Cache id for device by id cache */
    public static final String ID_DEVICE_ID_CACHE = "dvid";

    /** Cache id for device assignment cache */
    public static final String ID_ASSIGNMENT_CACHE = "assn";

    /** Cache id for device assignment by id cache */
    public static final String ID_ASSIGNMENT_ID_CACHE = "anid";

    /**
     * Cache for sites.
     * 
     * @author Derek
     */
    public static class SiteCache extends CacheProvider<String, ISite> {

	/** Static logger instance */
	private static Logger LOGGER = LogManager.getLogger();

	public SiteCache(IMicroservice microservice, boolean createOnStartup) {
	    super(microservice, ID_SITE_CACHE, createOnStartup);
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
     * Cache for sites by id.
     * 
     * @author Derek
     */
    public static class SiteByIdCache extends CacheProvider<UUID, ISite> {

	/** Static logger instance */
	private static Logger LOGGER = LogManager.getLogger();

	public SiteByIdCache(IMicroservice microservice, boolean createOnStartup) {
	    super(microservice, ID_SITE_ID_CACHE, createOnStartup);
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
     * Cache for device specifications by id.
     * 
     * @author Derek
     */
    public static class DeviceSpecificationByIdCache extends CacheProvider<UUID, IDeviceSpecification> {

	/** Static logger instance */
	private static Logger LOGGER = LogManager.getLogger();

	public DeviceSpecificationByIdCache(IMicroservice microservice, boolean createOnStartup) {
	    super(microservice, ID_SPECIFICATION_ID_CACHE, createOnStartup);
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
     * Cache for devices by id.
     * 
     * @author Derek
     */
    public static class DeviceByIdCache extends CacheProvider<UUID, IDevice> {

	/** Static logger instance */
	private static Logger LOGGER = LogManager.getLogger();

	public DeviceByIdCache(IMicroservice microservice, boolean createOnStartup) {
	    super(microservice, ID_DEVICE_ID_CACHE, createOnStartup);
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

    /**
     * Cache for device assignments by id.
     * 
     * @author Derek
     */
    public static class DeviceAssignmentByIdCache extends CacheProvider<UUID, IDeviceAssignment> {

	/** Static logger instance */
	private static Logger LOGGER = LogManager.getLogger();

	public DeviceAssignmentByIdCache(IMicroservice microservice, boolean createOnStartup) {
	    super(microservice, ID_ASSIGNMENT_ID_CACHE, createOnStartup);
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
