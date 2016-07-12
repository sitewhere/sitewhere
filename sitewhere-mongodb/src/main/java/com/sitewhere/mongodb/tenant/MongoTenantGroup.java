/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb.tenant;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.mongodb.common.MongoMetadataProvider;
import com.sitewhere.mongodb.common.MongoSiteWhereEntity;
import com.sitewhere.rest.model.tenant.TenantGroup;
import com.sitewhere.spi.tenant.ITenantGroup;

/**
 * Provides methods for loading and storing {@link TenantGroup} data in MongoDB.
 * 
 * @author Derek
 */
public class MongoTenantGroup implements MongoConverter<ITenantGroup> {

	/** Property for group id */
	public static final String PROP_ID = "id";

	/** Property for group name */
	public static final String PROP_NAME = "name";

	/** Property for group image URL */
	public static final String PROP_IMAGE_URL = "imageUrl";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
	 */
	@Override
	public BasicDBObject convert(ITenantGroup source) {
		return MongoTenantGroup.toDBObject(source);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.MongoConverter#convert(com.mongodb.DBObject)
	 */
	@Override
	public ITenantGroup convert(DBObject source) {
		return MongoTenantGroup.fromDBObject(source);
	}

	/**
	 * Copy information from SPI into Mongo DBObject.
	 * 
	 * @param source
	 * @param target
	 */
	public static void toDBObject(ITenantGroup source, BasicDBObject target) {
		target.append(PROP_ID, source.getId());
		target.append(PROP_NAME, source.getName());
		target.append(PROP_IMAGE_URL, source.getImageUrl());

		MongoSiteWhereEntity.toDBObject(source, target);
		MongoMetadataProvider.toDBObject(source, target);
	}

	/**
	 * Copy information from Mongo DBObject to model object.
	 * 
	 * @param source
	 * @param target
	 */
	public static void fromDBObject(DBObject source, TenantGroup target) {
		String id = (String) source.get(PROP_ID);
		String name = (String) source.get(PROP_NAME);
		String imageUrl = (String) source.get(PROP_IMAGE_URL);

		target.setId(id);
		target.setName(name);
		target.setImageUrl(imageUrl);

		MongoSiteWhereEntity.fromDBObject(source, target);
		MongoMetadataProvider.fromDBObject(source, target);
	}

	/**
	 * Convert SPI object to Mongo DBObject.
	 * 
	 * @param source
	 * @return
	 */
	public static BasicDBObject toDBObject(ITenantGroup source) {
		BasicDBObject result = new BasicDBObject();
		MongoTenantGroup.toDBObject(source, result);
		return result;
	}

	/**
	 * Convert a DBObject into the SPI equivalent.
	 * 
	 * @param source
	 * @return
	 */
	public static TenantGroup fromDBObject(DBObject source) {
		TenantGroup result = new TenantGroup();
		MongoTenantGroup.fromDBObject(source, result);
		return result;
	}
}