/*
 * MongoDeviceMeasurement.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb.device;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.rest.model.device.event.DeviceMeasurement;
import com.sitewhere.spi.device.event.IDeviceMeasurement;

/**
 * Used to load or save device measurement data to MongoDB.
 * 
 * @author dadams
 */
public class MongoDeviceMeasurement implements MongoConverter<IDeviceMeasurement> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
	 */
	@Override
	public BasicDBObject convert(IDeviceMeasurement source) {
		return MongoDeviceMeasurement.toDBObject(source);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.MongoConverter#convert(com.mongodb.DBObject)
	 */
	@Override
	public IDeviceMeasurement convert(DBObject source) {
		return MongoDeviceMeasurement.fromDBObject(source);
	}

	/**
	 * Copy information from SPI into Mongo DBObject.
	 * 
	 * @param source
	 * @param target
	 */
	public static void toDBObject(IDeviceMeasurement source, BasicDBObject target) {
		MongoDeviceEvent.toDBObject(source, target);

		target.put(MongoDeviceMeasurements.PROP_NAME, source.getName());
		target.put(MongoDeviceMeasurements.PROP_VALUE, source.getValue());
	}

	/**
	 * Copy information from Mongo DBObject to model object.
	 * 
	 * @param source
	 * @param target
	 */
	public static void fromDBObject(DBObject source, DeviceMeasurement target) {
		MongoDeviceEvent.fromDBObject(source, target);

		String name = (String) source.get(MongoDeviceMeasurements.PROP_NAME);
		Double value = (Double) source.get(MongoDeviceMeasurements.PROP_VALUE);
		target.setName(name);
		target.setValue(value);
	}

	/**
	 * Convert SPI object to Mongo DBObject.
	 * 
	 * @param source
	 * @return
	 */
	public static BasicDBObject toDBObject(IDeviceMeasurement source) {
		BasicDBObject result = new BasicDBObject();
		MongoDeviceMeasurement.toDBObject(source, result);
		return result;
	}

	/**
	 * Convert a DBObject into the SPI equivalent.
	 * 
	 * @param source
	 * @return
	 */
	public static DeviceMeasurement fromDBObject(DBObject source) {
		DeviceMeasurement result = new DeviceMeasurement();
		MongoDeviceMeasurement.fromDBObject(source, result);
		return result;
	}
}