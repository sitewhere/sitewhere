/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
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
import com.sitewhere.rest.model.device.batch.BatchOperation;
import com.sitewhere.spi.device.batch.IBatchOperation;
import com.sitewhere.spi.device.batch.OperationType;

/**
 * Used to load or save batch operation data to MongoDB.
 * 
 * @author Derek
 */
public class MongoBatchOperation implements MongoConverter<IBatchOperation> {

	/** Property for token */
	public static final String PROP_TOKEN = "token";

	/** Property for operation type */
	public static final String PROP_OPERATION_TYPE = "opType";

	/** Property for operation parameters */
	public static final String PROP_PARAMETERS = "params";

	/** Property for element list */
	public static final String PROP_LAST_INDEX = "lastIndex";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
	 */
	@Override
	public BasicDBObject convert(IBatchOperation source) {
		return MongoBatchOperation.toDBObject(source);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.MongoConverter#convert(com.mongodb.DBObject)
	 */
	@Override
	public IBatchOperation convert(DBObject source) {
		return MongoBatchOperation.fromDBObject(source);
	}

	/**
	 * Copy information from SPI into Mongo DBObject.
	 * 
	 * @param source
	 * @param target
	 */
	public static void toDBObject(IBatchOperation source, BasicDBObject target) {
		target.append(PROP_TOKEN, source.getToken());
		if (source.getOperationType() != null) {
			target.append(PROP_OPERATION_TYPE, source.getOperationType().name());
		}

		// Set parameters as nested object.
		BasicDBObject params = new BasicDBObject();
		for (String key : source.getParameters().keySet()) {
			params.put(key, source.getParameters().get(key));
		}
		target.put(PROP_PARAMETERS, params);

		MongoSiteWhereEntity.toDBObject(source, target);
		MongoMetadataProvider.toDBObject(source, target);
	}

	/**
	 * Copy information from Mongo DBObject to model object.
	 * 
	 * @param source
	 * @param target
	 */
	public static void fromDBObject(DBObject source, BatchOperation target) {
		String token = (String) source.get(PROP_TOKEN);
		String operationType = (String) source.get(PROP_OPERATION_TYPE);

		target.setToken(token);
		if (operationType != null) {
			target.setOperationType(OperationType.valueOf(operationType));
		}

		// Load parameters from nested object.
		DBObject params = (DBObject) source.get(PROP_PARAMETERS);
		if (params != null) {
			for (String key : params.keySet()) {
				target.getParameters().put(key, (String) params.get(key));
			}
		}

		MongoSiteWhereEntity.fromDBObject(source, target);
		MongoMetadataProvider.fromDBObject(source, target);
	}

	/**
	 * Convert SPI object to Mongo DBObject.
	 * 
	 * @param source
	 * @return
	 */
	public static BasicDBObject toDBObject(IBatchOperation source) {
		BasicDBObject result = new BasicDBObject();
		MongoBatchOperation.toDBObject(source, result);
		return result;
	}

	/**
	 * Convert a DBObject into the SPI equivalent.
	 * 
	 * @param source
	 * @return
	 */
	public static BatchOperation fromDBObject(DBObject source) {
		BatchOperation result = new BatchOperation();
		MongoBatchOperation.fromDBObject(source, result);
		return result;
	}
}