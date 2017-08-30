/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicemanagement.mongodb;

import java.util.HashMap;
import java.util.Map;

import com.sitewhere.mongodb.IMongoConverterLookup;
import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.IDeviceStatus;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.IZone;
import com.sitewhere.spi.device.batch.IBatchElement;
import com.sitewhere.spi.device.batch.IBatchOperation;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.group.IDeviceGroupElement;
import com.sitewhere.spi.device.streaming.IDeviceStream;

/**
 * Manages classes used to convert between Mongo and SPI objects.
 * 
 * @author Derek
 */
public class MongoConverters implements IMongoConverterLookup {

    /** Maps interface classes to their associated converters */
    private static Map<Class<?>, MongoConverter<?>> CONVERTERS = new HashMap<Class<?>, MongoConverter<?>>();

    /** Create a list of converters for various types */
    static {
	// Converters for device management.
	CONVERTERS.put(IDeviceSpecification.class, new MongoDeviceSpecification());
	CONVERTERS.put(IDeviceCommand.class, new MongoDeviceCommand());
	CONVERTERS.put(IDeviceStatus.class, new MongoDeviceStatus());
	CONVERTERS.put(IDevice.class, new MongoDevice());
	CONVERTERS.put(IDeviceAssignment.class, new MongoDeviceAssignment());
	CONVERTERS.put(IDeviceStream.class, new MongoDeviceStream());
	CONVERTERS.put(ISite.class, new MongoSite());
	CONVERTERS.put(IZone.class, new MongoZone());
	CONVERTERS.put(IDeviceGroup.class, new MongoDeviceGroup());
	CONVERTERS.put(IDeviceGroupElement.class, new MongoDeviceGroupElement());
	CONVERTERS.put(IBatchOperation.class, new MongoBatchOperation());
	CONVERTERS.put(IBatchElement.class, new MongoBatchElement());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.mongodb.IMongoConverterLookup#getConverterFor(java.lang.
     * Class)
     */
    @SuppressWarnings("unchecked")
    public <T> MongoConverter<T> getConverterFor(Class<T> api) {
	MongoConverter<T> result = (MongoConverter<T>) CONVERTERS.get(api);
	if (result == null) {
	    throw new RuntimeException("No Mongo converter registered for " + api.getName());
	}
	return result;
    }
}