/*
 * MongoDeviceSpecification.java 
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
import com.sitewhere.mongodb.common.MongoMetadataProvider;
import com.sitewhere.mongodb.common.MongoSiteWhereEntity;
import com.sitewhere.rest.model.device.DeviceSpecification;
import com.sitewhere.spi.device.IDeviceSpecification;

/**
 * Used to load or save device specification data to MongoDB.
 * 
 * @author dadams
 */
public class MongoDeviceSpecification implements MongoConverter<IDeviceSpecification> {

	/** Property for unique token */
	public static final String PROP_TOKEN = "token";

	/** Property for specification name */
	public static final String PROP_NAME = "name";

	/** Property for asset module id */
	public static final String PROP_ASSET_MODULE_ID = "assetModuleId";

	/** Property for asset id */
	public static final String PROP_ASSET_ID = "assetId";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
	 */
	@Override
	public BasicDBObject convert(IDeviceSpecification source) {
		return MongoDeviceSpecification.toDBObject(source);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.MongoConverter#convert(com.mongodb.DBObject)
	 */
	@Override
	public IDeviceSpecification convert(DBObject source) {
		return MongoDeviceSpecification.fromDBObject(source);
	}

	/**
	 * Copy information from SPI into Mongo DBObject.
	 * 
	 * @param source
	 * @param target
	 */
	public static void toDBObject(IDeviceSpecification source, BasicDBObject target) {
		target.append(PROP_TOKEN, source.getToken());
		target.append(PROP_NAME, source.getName());
		target.append(PROP_ASSET_MODULE_ID, source.getAssetModuleId());
		target.append(PROP_ASSET_ID, source.getAssetId());
		MongoSiteWhereEntity.toDBObject(source, target);
		MongoMetadataProvider.toDBObject(source, target);
	}

	/**
	 * Copy information from Mongo DBObject to model object.
	 * 
	 * @param source
	 * @param target
	 */
	public static void fromDBObject(DBObject source, DeviceSpecification target) {
		String token = (String) source.get(PROP_TOKEN);
		String name = (String) source.get(PROP_NAME);
		String assetModuleId = (String) source.get(PROP_ASSET_MODULE_ID);
		String assetId = (String) source.get(PROP_ASSET_ID);

		target.setToken(token);
		target.setName(name);
		target.setAssetModuleId(assetModuleId);
		target.setAssetId(assetId);
		MongoSiteWhereEntity.fromDBObject(source, target);
		MongoMetadataProvider.fromDBObject(source, target);
	}

	/**
	 * Convert SPI object to Mongo DBObject.
	 * 
	 * @param source
	 * @return
	 */
	public static BasicDBObject toDBObject(IDeviceSpecification source) {
		BasicDBObject result = new BasicDBObject();
		MongoDeviceSpecification.toDBObject(source, result);
		return result;
	}

	/**
	 * Convert a DBObject into the SPI equivalent.
	 * 
	 * @param source
	 * @return
	 */
	public static DeviceSpecification fromDBObject(DBObject source) {
		DeviceSpecification result = new DeviceSpecification();
		MongoDeviceSpecification.fromDBObject(source, result);
		return result;
	}
}