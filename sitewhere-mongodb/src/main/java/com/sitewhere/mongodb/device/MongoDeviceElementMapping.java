/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb.device;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.rest.model.device.DeviceElementMapping;
import com.sitewhere.spi.device.IDeviceElementMapping;

/**
 * Used to load or save device element mapping data to MongoDB.
 * 
 * @author dadams
 */
public class MongoDeviceElementMapping implements MongoConverter<IDeviceElementMapping> {

	/** Property for device element schema path */
	public static final String PROP_DEVICE_ELEMENT_SCHEMA_PATH = "deviceElementSchemaPath";

	/** Property for mapped hardware id */
	public static final String PROP_HARDWARE_ID = "hardwareId";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
	 */
	@Override
	public BasicDBObject convert(IDeviceElementMapping source) {
		return MongoDeviceElementMapping.toDBObject(source);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.MongoConverter#convert(com.mongodb.DBObject)
	 */
	@Override
	public IDeviceElementMapping convert(DBObject source) {
		return MongoDeviceElementMapping.fromDBObject(source);
	}

	/**
	 * Copy information from SPI into Mongo DBObject.
	 * 
	 * @param source
	 * @param target
	 */
	public static void toDBObject(IDeviceElementMapping source, BasicDBObject target) {
		target.append(PROP_DEVICE_ELEMENT_SCHEMA_PATH, source.getDeviceElementSchemaPath());
		target.append(PROP_HARDWARE_ID, source.getHardwareId());
	}

	/**
	 * Copy information from Mongo DBObject to model object.
	 * 
	 * @param source
	 * @param target
	 */
	public static void fromDBObject(DBObject source, DeviceElementMapping target) {
		String path = (String) source.get(PROP_DEVICE_ELEMENT_SCHEMA_PATH);
		String hardwareId = (String) source.get(PROP_HARDWARE_ID);

		target.setDeviceElementSchemaPath(path);
		target.setHardwareId(hardwareId);
	}

	/**
	 * Convert SPI object to Mongo DBObject.
	 * 
	 * @param source
	 * @return
	 */
	public static BasicDBObject toDBObject(IDeviceElementMapping source) {
		BasicDBObject result = new BasicDBObject();
		MongoDeviceElementMapping.toDBObject(source, result);
		return result;
	}

	/**
	 * Convert a DBObject into the SPI equivalent.
	 * 
	 * @param source
	 * @return
	 */
	public static DeviceElementMapping fromDBObject(DBObject source) {
		DeviceElementMapping result = new DeviceElementMapping();
		MongoDeviceElementMapping.fromDBObject(source, result);
		return result;
	}
}