/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package com.sitewhere.mongodb.device;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.rest.model.device.event.DeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceMeasurements;

/**
 * Used to load or save device measurements data to MongoDB.
 * 
 * @author dadams
 */
public class MongoDeviceMeasurements implements MongoConverter<IDeviceMeasurements> {

	/** Element that holds measurements */
	public static final String PROP_MEASUREMENTS = "measurements";

	/** Attribute name for measurement name */
	public static final String PROP_NAME = "name";

	/** Attribute name for measurement value */
	public static final String PROP_VALUE = "value";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.dao.mongodb.MongoConverter#convert(java.lang.Object)
	 */
	public BasicDBObject convert(IDeviceMeasurements source) {
		return MongoDeviceMeasurements.toDBObject(source);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.dao.mongodb.MongoConverter#convert(com.mongodb.DBObject)
	 */
	public DeviceMeasurements convert(DBObject source) {
		return MongoDeviceMeasurements.fromDBObject(source);
	}

	/**
	 * Copy information from SPI into Mongo DBObject.
	 * 
	 * @param source
	 * @param target
	 */
	public static void toDBObject(IDeviceMeasurements source, BasicDBObject target) {
		MongoDeviceEvent.toDBObject(source, target);

		// Save arbitrary measurements.
		List<BasicDBObject> props = new ArrayList<BasicDBObject>();
		for (String key : source.getMeasurements().keySet()) {
			BasicDBObject prop = new BasicDBObject();
			prop.put(PROP_NAME, key);
			prop.put(PROP_VALUE, source.getMeasurement(key));
			props.add(prop);
		}
		target.append(PROP_MEASUREMENTS, props);
	}

	/**
	 * Copy information from Mongo DBObject to model object.
	 * 
	 * @param source
	 * @param target
	 */
	@SuppressWarnings("unchecked")
	public static void fromDBObject(DBObject source, DeviceMeasurements target) {
		MongoDeviceEvent.fromDBObject(source, target);

		// Load arbitrary measurements.
		List<DBObject> props = (List<DBObject>) source.get(PROP_MEASUREMENTS);
		if (props != null) {
			for (DBObject prop : props) {
				String name = (String) prop.get(PROP_NAME);
				Double value = (Double) prop.get(PROP_VALUE);
				target.addOrReplaceMeasurement(name, value);
			}
		}
	}

	/**
	 * Convert SPI object to Mongo DBObject.
	 * 
	 * @param source
	 * @return
	 */
	public static BasicDBObject toDBObject(IDeviceMeasurements source) {
		BasicDBObject result = new BasicDBObject();
		MongoDeviceMeasurements.toDBObject(source, result);
		return result;
	}

	/**
	 * Convert a DBObject into the SPI equivalent.
	 * 
	 * @param source
	 * @return
	 */
	public static DeviceMeasurements fromDBObject(DBObject source) {
		DeviceMeasurements result = new DeviceMeasurements();
		MongoDeviceMeasurements.fromDBObject(source, result);
		return result;
	}
}