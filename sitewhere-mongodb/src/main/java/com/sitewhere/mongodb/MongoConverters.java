/*
 * MongoConverters.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb;

import java.util.HashMap;
import java.util.Map;

import com.sitewhere.mongodb.device.MongoDevice;
import com.sitewhere.mongodb.device.MongoDeviceAlert;
import com.sitewhere.mongodb.device.MongoDeviceAssignment;
import com.sitewhere.mongodb.device.MongoDeviceLocation;
import com.sitewhere.mongodb.device.MongoDeviceMeasurements;
import com.sitewhere.mongodb.device.MongoSite;
import com.sitewhere.mongodb.device.MongoZone;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAlert;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceLocation;
import com.sitewhere.spi.device.IDeviceMeasurements;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.IZone;

/**
 * Manages classes used to convert between Mongo and SPI objects.
 * 
 * @author Derek
 */
public class MongoConverters {

	/** Maps interface classes to their associated converters */
	private static Map<Class<?>, MongoConverter<?>> CONVERTERS = new HashMap<Class<?>, MongoConverter<?>>();

	/** Create a list of converters for various types */
	static {
		CONVERTERS.put(IDevice.class, new MongoDevice());
		CONVERTERS.put(IDeviceAssignment.class, new MongoDeviceAssignment());
		CONVERTERS.put(IDeviceMeasurements.class, new MongoDeviceMeasurements());
		CONVERTERS.put(IDeviceAlert.class, new MongoDeviceAlert());
		CONVERTERS.put(IDeviceLocation.class, new MongoDeviceLocation());
		CONVERTERS.put(ISite.class, new MongoSite());
		CONVERTERS.put(IZone.class, new MongoZone());
	}

	/**
	 * Get a converter for the given API type.
	 * 
	 * @param api
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> MongoConverter<T> getConverterFor(Class<T> api) {
		MongoConverter<T> result = (MongoConverter<T>) CONVERTERS.get(api);
		if (result == null) {
			throw new RuntimeException("No Mongo converter registered for " + api.getName());
		}
		return result;
	}
}