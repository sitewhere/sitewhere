package com.sitewhere.microservice.ignite;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.microservice.IMicroservice;

/**
 * Cache providers for device management entities.
 * 
 * @author Derek
 */
public class DeviceManagementCacheProviders {

    /** Cache id for device cache */
    public static final String ID_DEVICE_CACHE = "device";

    /** Cache id for device assignment cache */
    public static final String ID_ASSIGNMENT_CACHE = "assignment";

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
