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
import com.sitewhere.rest.model.device.network.DeviceNetworkElement;
import com.sitewhere.spi.device.network.IDeviceNetworkElement;
import com.sitewhere.spi.device.network.NetworkElementType;

/**
 * Used to load or save device network data to MongoDB.
 * 
 * @author Derek
 */
public class MongoDeviceNetworkElement implements MongoConverter<IDeviceNetworkElement> {

	/** Property for element network token */
	public static final String PROP_NETWORK_TOKEN = "networkToken";

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
	public BasicDBObject convert(IDeviceNetworkElement source) {
		return MongoDeviceNetworkElement.toDBObject(source);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.MongoConverter#convert(com.mongodb.DBObject)
	 */
	@Override
	public IDeviceNetworkElement convert(DBObject source) {
		return MongoDeviceNetworkElement.fromDBObject(source);
	}

	/**
	 * Copy information from SPI into Mongo DBObject.
	 * 
	 * @param source
	 * @param target
	 */
	public static void toDBObject(IDeviceNetworkElement source, BasicDBObject target) {
		target.append(PROP_NETWORK_TOKEN, source.getNetworkToken());
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
	public static void fromDBObject(DBObject source, DeviceNetworkElement target) {
		String network = (String) source.get(PROP_NETWORK_TOKEN);
		Long index = (Long) source.get(PROP_INDEX);
		String type = (String) source.get(PROP_TYPE);
		String elementId = (String) source.get(PROP_ELEMENT_ID);

		if (type == null) {
			throw new RuntimeException("Network element type not stored.");
		}
		target.setNetworkToken(network);
		target.setType(NetworkElementType.valueOf(type));
		target.setElementId(elementId);
		target.setIndex(index);
	}

	/**
	 * Convert SPI object to Mongo DBObject.
	 * 
	 * @param source
	 * @return
	 */
	public static BasicDBObject toDBObject(IDeviceNetworkElement source) {
		BasicDBObject result = new BasicDBObject();
		MongoDeviceNetworkElement.toDBObject(source, result);
		return result;
	}

	/**
	 * Convert a DBObject into the SPI equivalent.
	 * 
	 * @param source
	 * @return
	 */
	public static DeviceNetworkElement fromDBObject(DBObject source) {
		DeviceNetworkElement result = new DeviceNetworkElement();
		MongoDeviceNetworkElement.fromDBObject(source, result);
		return result;
	}
}