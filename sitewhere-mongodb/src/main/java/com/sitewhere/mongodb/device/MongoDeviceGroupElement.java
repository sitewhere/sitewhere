/*
 * MongoDeviceNetworkEntry.java 
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
import com.sitewhere.rest.model.device.group.DeviceGroupElement;
import com.sitewhere.spi.device.group.GroupElementType;
import com.sitewhere.spi.device.group.IDeviceGroupElement;

/**
 * Used to load or save device group element data to MongoDB.
 * 
 * @author Derek
 */
public class MongoDeviceGroupElement implements MongoConverter<IDeviceGroupElement> {

	/** Property for element group token */
	public static final String PROP_GROUP_TOKEN = "groupToken";

	/** Property for element type */
	public static final String PROP_TYPE = "type";

	/** Property for element id */
	public static final String PROP_ELEMENT_ID = "elementId";

	/** Property for element index */
	public static final String PROP_INDEX = "index";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
	 */
	@Override
	public BasicDBObject convert(IDeviceGroupElement source) {
		return MongoDeviceGroupElement.toDBObject(source);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.MongoConverter#convert(com.mongodb.DBObject)
	 */
	@Override
	public IDeviceGroupElement convert(DBObject source) {
		return MongoDeviceGroupElement.fromDBObject(source);
	}

	/**
	 * Copy information from SPI into Mongo DBObject.
	 * 
	 * @param source
	 * @param target
	 */
	public static void toDBObject(IDeviceGroupElement source, BasicDBObject target) {
		target.append(PROP_GROUP_TOKEN, source.getGroupToken());
		target.append(PROP_INDEX, source.getIndex());
		target.append(PROP_TYPE, source.getType().name());
		target.append(PROP_ELEMENT_ID, source.getElementId());
	}

	/**
	 * Copy information from Mongo DBObject to model object.
	 * 
	 * @param source
	 * @param target
	 */
	public static void fromDBObject(DBObject source, DeviceGroupElement target) {
		String group = (String) source.get(PROP_GROUP_TOKEN);
		Long index = (Long) source.get(PROP_INDEX);
		String type = (String) source.get(PROP_TYPE);
		String elementId = (String) source.get(PROP_ELEMENT_ID);

		if (type == null) {
			throw new RuntimeException("Group element type not stored.");
		}
		target.setGroupToken(group);
		target.setType(GroupElementType.valueOf(type));
		target.setElementId(elementId);
		target.setIndex(index);
	}

	/**
	 * Convert SPI object to Mongo DBObject.
	 * 
	 * @param source
	 * @return
	 */
	public static BasicDBObject toDBObject(IDeviceGroupElement source) {
		BasicDBObject result = new BasicDBObject();
		MongoDeviceGroupElement.toDBObject(source, result);
		return result;
	}

	/**
	 * Convert a DBObject into the SPI equivalent.
	 * 
	 * @param source
	 * @return
	 */
	public static DeviceGroupElement fromDBObject(DBObject source) {
		DeviceGroupElement result = new DeviceGroupElement();
		MongoDeviceGroupElement.fromDBObject(source, result);
		return result;
	}
}