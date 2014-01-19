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

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.spi.device.event.AlertLevel;
import com.sitewhere.spi.device.event.AlertSource;
import com.sitewhere.spi.device.event.IDeviceAlert;

/**
 * Used to load or save device alert data to MongoDB.
 * 
 * @author dadams
 */
public class MongoDeviceAlert implements MongoConverter<IDeviceAlert> {

	/** Property for source */
	public static final String PROP_SOURCE = "source";

	/** Property for alert level */
	public static final String PROP_LEVEL = "level";

	/** Property for type */
	public static final String PROP_TYPE = "type";

	/** Property for message */
	public static final String PROP_MESSAGE = "message";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.dao.mongodb.MongoConverter#convert(java.lang.Object)
	 */
	public BasicDBObject convert(IDeviceAlert source) {
		return MongoDeviceAlert.toDBObject(source);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.dao.mongodb.MongoConverter#convert(com.mongodb.DBObject)
	 */
	public IDeviceAlert convert(DBObject source) {
		return MongoDeviceAlert.fromDBObject(source);
	}

	/**
	 * Copy information from SPI into Mongo DBObject.
	 * 
	 * @param source
	 * @param target
	 */
	public static void toDBObject(IDeviceAlert source, BasicDBObject target) {
		MongoDeviceEvent.toDBObject(source, target);

		target.append(PROP_SOURCE, source.getSource().name());
		target.append(PROP_LEVEL, source.getLevel().name());
		target.append(PROP_TYPE, source.getType());
		target.append(PROP_MESSAGE, source.getMessage());
	}

	/**
	 * Copy information from Mongo DBObject to model object.
	 * 
	 * @param source
	 * @param target
	 */
	public static void fromDBObject(DBObject source, DeviceAlert target) {
		MongoDeviceEvent.fromDBObject(source, target);

		String sourceName = (String) source.get(PROP_SOURCE);
		String levelName = (String) source.get(PROP_LEVEL);
		String type = (String) source.get(PROP_TYPE);
		String message = (String) source.get(PROP_MESSAGE);

		if (sourceName != null) {
			target.setSource(AlertSource.valueOf(sourceName));
		}
		if (levelName != null) {
			target.setLevel(AlertLevel.valueOf(levelName));
		}
		target.setType(type);
		target.setMessage(message);
	}

	/**
	 * Convert SPI object to Mongo DBObject.
	 * 
	 * @param source
	 * @return
	 */
	public static BasicDBObject toDBObject(IDeviceAlert source) {
		BasicDBObject result = new BasicDBObject();
		MongoDeviceAlert.toDBObject(source, result);
		return result;
	}

	/**
	 * Convert a DBObject into the SPI equivalent.
	 * 
	 * @param source
	 * @return
	 */
	public static DeviceAlert fromDBObject(DBObject source) {
		DeviceAlert result = new DeviceAlert();
		MongoDeviceAlert.fromDBObject(source, result);
		return result;
	}
}