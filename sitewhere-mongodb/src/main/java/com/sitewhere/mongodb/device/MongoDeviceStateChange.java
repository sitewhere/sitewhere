/*
 * MongoDeviceStateChange.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb.device;

import java.util.HashMap;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.rest.model.device.event.DeviceStateChange;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.state.StateChangeCategory;
import com.sitewhere.spi.device.event.state.StateChangeType;

/**
 * Used to load or save device state change data to MongoDB.
 * 
 * @author dadams
 */
public class MongoDeviceStateChange implements MongoConverter<IDeviceStateChange> {

	/** Property for state category */
	public static final String PROP_CATEGORY = "category";

	/** Property for state type */
	public static final String PROP_TYPE = "type";

	/** Property for previous state value */
	public static final String PROP_PREVIOUS_STATE = "previousState";

	/** Property for new state value */
	public static final String PROP_NEW_STATE = "newState";

	/** Property for supporting data map */
	public static final String PROP_DATA = "stateData";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
	 */
	@Override
	public BasicDBObject convert(IDeviceStateChange source) {
		return MongoDeviceStateChange.toDBObject(source);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.MongoConverter#convert(com.mongodb.DBObject)
	 */
	@Override
	public IDeviceStateChange convert(DBObject source) {
		return MongoDeviceStateChange.fromDBObject(source);
	}

	/**
	 * Copy information from SPI into Mongo DBObject.
	 * 
	 * @param source
	 * @param target
	 */
	public static void toDBObject(IDeviceStateChange source, BasicDBObject target) {
		MongoDeviceEvent.toDBObject(source, target, false);

		target.append(PROP_CATEGORY, source.getCategory().name());
		target.append(PROP_TYPE, source.getType().name());
		target.append(PROP_PREVIOUS_STATE, source.getPreviousState());
		target.append(PROP_NEW_STATE, source.getNewState());

		BasicDBObject params = new BasicDBObject();
		for (String key : source.getData().keySet()) {
			params.append(key, source.getData().get(key));
		}
		target.append(PROP_DATA, params);
	}

	/**
	 * Copy information from Mongo DBObject to model object.
	 * 
	 * @param source
	 * @param target
	 */
	public static void fromDBObject(DBObject source, DeviceStateChange target) {
		MongoDeviceEvent.fromDBObject(source, target, false);

		String category = (String) source.get(PROP_CATEGORY);
		String type = (String) source.get(PROP_TYPE);
		String previousState = (String) source.get(PROP_PREVIOUS_STATE);
		String newState = (String) source.get(PROP_NEW_STATE);

		if (category != null) {
			target.setCategory(StateChangeCategory.valueOf(category));
		}
		if (type != null) {
			target.setType(StateChangeType.valueOf(type));
		}
		target.setPreviousState(previousState);
		target.setNewState(newState);

		Map<String, String> data = new HashMap<String, String>();
		DBObject dbdata = (DBObject) source.get(PROP_DATA);
		if (dbdata != null) {
			for (String key : dbdata.keySet()) {
				data.put(key, (String) dbdata.get(key));
			}
		}
		target.setData(data);
	}

	/**
	 * Convert SPI object to Mongo DBObject.
	 * 
	 * @param source
	 * @return
	 */
	public static BasicDBObject toDBObject(IDeviceStateChange source) {
		BasicDBObject result = new BasicDBObject();
		MongoDeviceStateChange.toDBObject(source, result);
		return result;
	}

	/**
	 * Convert a DBObject into the SPI equivalent.
	 * 
	 * @param source
	 * @return
	 */
	public static DeviceStateChange fromDBObject(DBObject source) {
		DeviceStateChange result = new DeviceStateChange();
		MongoDeviceStateChange.fromDBObject(source, result);
		return result;
	}
}