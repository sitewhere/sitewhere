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
import com.sitewhere.rest.model.tenant.TenantGroupElement;
import com.sitewhere.spi.tenant.ITenantGroupElement;

/**
 * Provides methods for loading and storing {@link TenantGroupElement} data in MongoDB.
 * 
 * @author Derek
 */
public class MongoTenantGroupElement implements MongoConverter<ITenantGroupElement> {

	/** Property for tenant group id */
	public static final String PROP_GROUP_ID = "groupId";

	/** Property for tenant id */
	public static final String PROP_TENANT_ID = "tenantId";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
	 */
	@Override
	public BasicDBObject convert(ITenantGroupElement source) {
		return MongoTenantGroupElement.toDBObject(source);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.MongoConverter#convert(com.mongodb.DBObject)
	 */
	@Override
	public ITenantGroupElement convert(DBObject source) {
		return MongoTenantGroupElement.fromDBObject(source);
	}

	/**
	 * Copy information from SPI into Mongo DBObject.
	 * 
	 * @param source
	 * @param target
	 */
	public static void toDBObject(ITenantGroupElement source, BasicDBObject target) {
		target.append(PROP_GROUP_ID, source.getTenantGroupId());
		target.append(PROP_TENANT_ID, source.getTenantId());
	}

	/**
	 * Copy information from Mongo DBObject to model object.
	 * 
	 * @param source
	 * @param target
	 */
	public static void fromDBObject(DBObject source, TenantGroupElement target) {
		String groupId = (String) source.get(PROP_GROUP_ID);
		String tenantId = (String) source.get(PROP_TENANT_ID);

		target.setTenantGroupId(groupId);
		target.setTenantId(tenantId);
	}

	/**
	 * Convert SPI object to Mongo DBObject.
	 * 
	 * @param source
	 * @return
	 */
	public static BasicDBObject toDBObject(ITenantGroupElement source) {
		BasicDBObject result = new BasicDBObject();
		MongoTenantGroupElement.toDBObject(source, result);
		return result;
	}

	/**
	 * Convert a DBObject into the SPI equivalent.
	 * 
	 * @param source
	 * @return
	 */
	public static TenantGroupElement fromDBObject(DBObject source) {
		TenantGroupElement result = new TenantGroupElement();
		MongoTenantGroupElement.fromDBObject(source, result);
		return result;
	}
}