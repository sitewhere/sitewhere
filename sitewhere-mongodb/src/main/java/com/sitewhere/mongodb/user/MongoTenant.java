/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb.user;

import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.mongodb.common.MongoMetadataProvider;
import com.sitewhere.mongodb.common.MongoSiteWhereEntity;
import com.sitewhere.rest.model.user.Tenant;
import com.sitewhere.spi.user.ITenant;

/**
 * Provides methods for loading and storing {@link Tenant} data in MongoDB.
 * 
 * @author Derek
 */
public class MongoTenant implements MongoConverter<ITenant> {

	/** Property for tenant id */
	public static final String PROP_ID = "id";

	/** Property for tenant name */
	public static final String PROP_NAME = "name";

	/** Property for authentication token */
	public static final String PROP_AUTH_TOKEN = "auth";

	/** Property for logo URL */
	public static final String PROP_LOGO_URL = "logo";

	/** Property for authorized users */
	public static final String PROP_AUTH_USERS = "users";

	/** Property for engine configuration */
	public static final String PROP_ENGINE_CONF = "conf";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
	 */
	@Override
	public BasicDBObject convert(ITenant source) {
		return MongoTenant.toDBObject(source);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.MongoConverter#convert(com.mongodb.DBObject)
	 */
	@Override
	public ITenant convert(DBObject source) {
		return MongoTenant.fromDBObject(source);
	}

	/**
	 * Copy information from SPI into Mongo DBObject.
	 * 
	 * @param source
	 * @param target
	 */
	public static void toDBObject(ITenant source, BasicDBObject target) {
		target.append(PROP_ID, source.getId());
		target.append(PROP_NAME, source.getName());
		target.append(PROP_AUTH_TOKEN, source.getAuthenticationToken());
		target.append(PROP_LOGO_URL, source.getLogoUrl());
		target.append(PROP_AUTH_USERS, source.getAuthorizedUserIds());
		target.append(PROP_ENGINE_CONF, source.getEngineConfiguration());

		MongoSiteWhereEntity.toDBObject(source, target);
		MongoMetadataProvider.toDBObject(source, target);
	}

	/**
	 * Copy information from Mongo DBObject to model object.
	 * 
	 * @param source
	 * @param target
	 */
	@SuppressWarnings("unchecked")
	public static void fromDBObject(DBObject source, Tenant target) {
		String id = (String) source.get(PROP_ID);
		String name = (String) source.get(PROP_NAME);
		String authToken = (String) source.get(PROP_AUTH_TOKEN);
		String logo = (String) source.get(PROP_LOGO_URL);
		List<String> authUsers = (List<String>) source.get(PROP_AUTH_USERS);
		String conf = (String) source.get(PROP_ENGINE_CONF);

		target.setId(id);
		target.setName(name);
		target.setAuthenticationToken(authToken);
		target.setLogoUrl(logo);
		target.setAuthorizedUserIds(authUsers);
		target.setEngineConfiguration(conf);

		MongoSiteWhereEntity.fromDBObject(source, target);
		MongoMetadataProvider.fromDBObject(source, target);
	}

	/**
	 * Convert SPI object to Mongo DBObject.
	 * 
	 * @param source
	 * @return
	 */
	public static BasicDBObject toDBObject(ITenant source) {
		BasicDBObject result = new BasicDBObject();
		MongoTenant.toDBObject(source, result);
		return result;
	}

	/**
	 * Convert a DBObject into the SPI equivalent.
	 * 
	 * @param source
	 * @return
	 */
	public static Tenant fromDBObject(DBObject source) {
		Tenant result = new Tenant();
		MongoTenant.fromDBObject(source, result);
		return result;
	}
}