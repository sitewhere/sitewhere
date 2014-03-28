/*
 * MongoDeviceNetwork.java 
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
import com.sitewhere.mongodb.SiteWhereMongoClient;
import com.sitewhere.mongodb.common.MongoMetadataProvider;
import com.sitewhere.mongodb.common.MongoSiteWhereEntity;
import com.sitewhere.rest.model.device.network.DeviceNetwork;
import com.sitewhere.spi.device.network.IDeviceNetwork;

/**
 * Used to load or save device network data to MongoDB.
 * 
 * @author dadams
 */
public class MongoDeviceNetwork implements MongoConverter<IDeviceNetwork> {

	/** Property for unique token */
	public static final String PROP_TOKEN = "token";

	/** Property for name */
	public static final String PROP_NAME = "name";

	/** Property for description */
	public static final String PROP_DESCRIPTION = "description";

	/** Property for element list */
	public static final String PROP_LAST_INDEX = "lastIndex";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
	 */
	@Override
	public BasicDBObject convert(IDeviceNetwork source) {
		return MongoDeviceNetwork.toDBObject(source);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.MongoConverter#convert(com.mongodb.DBObject)
	 */
	@Override
	public IDeviceNetwork convert(DBObject source) {
		return MongoDeviceNetwork.fromDBObject(source);
	}

	/**
	 * Copy information from SPI into Mongo DBObject.
	 * 
	 * @param source
	 * @param target
	 */
	public static void toDBObject(IDeviceNetwork source, BasicDBObject target) {
		target.append(PROP_TOKEN, source.getToken());
		target.append(PROP_NAME, source.getName());
		target.append(PROP_DESCRIPTION, source.getDescription());
		MongoSiteWhereEntity.toDBObject(source, target);
		MongoMetadataProvider.toDBObject(source, target);
	}

	/**
	 * Copy information from Mongo DBObject to model object.
	 * 
	 * @param source
	 * @param target
	 */
	public static void fromDBObject(DBObject source, DeviceNetwork target) {
		String token = (String) source.get(PROP_TOKEN);
		String name = (String) source.get(PROP_NAME);
		String desc = (String) source.get(PROP_DESCRIPTION);

		target.setToken(token);
		target.setName(name);
		target.setDescription(desc);
		MongoSiteWhereEntity.fromDBObject(source, target);
		MongoMetadataProvider.fromDBObject(source, target);
	}

	/**
	 * Convert SPI object to Mongo DBObject.
	 * 
	 * @param source
	 * @return
	 */
	public static BasicDBObject toDBObject(IDeviceNetwork source) {
		BasicDBObject result = new BasicDBObject();
		MongoDeviceNetwork.toDBObject(source, result);
		return result;
	}

	/**
	 * Convert a DBObject into the SPI equivalent.
	 * 
	 * @param source
	 * @return
	 */
	public static DeviceNetwork fromDBObject(DBObject source) {
		DeviceNetwork result = new DeviceNetwork();
		MongoDeviceNetwork.fromDBObject(source, result);
		return result;
	}

	/**
	 * Get the next available index for the network.
	 * 
	 * @param mongo
	 * @param token
	 * @return
	 */
	public static long getNextNetworkIndex(SiteWhereMongoClient mongo, String token) {
		BasicDBObject query = new BasicDBObject(MongoDeviceNetwork.PROP_TOKEN, token);
		BasicDBObject update = new BasicDBObject(MongoDeviceNetwork.PROP_LAST_INDEX, (long) 1);
		BasicDBObject increment = new BasicDBObject("$inc", update);
		DBObject updated =
				mongo.getNetworksCollection().findAndModify(query, new BasicDBObject(), new BasicDBObject(),
						false, increment, true, true);
		return (Long) updated.get(PROP_LAST_INDEX);
	}
}