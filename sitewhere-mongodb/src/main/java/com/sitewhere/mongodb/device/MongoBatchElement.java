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
import com.sitewhere.rest.model.device.batch.BatchElement;
import com.sitewhere.spi.device.batch.IBatchElement;
import com.sitewhere.spi.device.batch.ElementProcessingStatus;

/**
 * Used to load or save batch element data to MongoDB.
 * 
 * @author Derek
 */
public class MongoBatchElement implements MongoConverter<IBatchElement> {

	/** Property for parent batch operation token */
	public static final String PROP_BATCH_OPERATION_TOKEN = "parent";

	/** Property for hardware id */
	public static final String PROP_HARDWARE_ID = "hardwareId";

	/** Property for index */
	public static final String PROP_INDEX = "index";

	/** Property for processing status */
	public static final String PROP_PROCESSING_STATUS = "status";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
	 */
	@Override
	public BasicDBObject convert(IBatchElement source) {
		return MongoBatchElement.toDBObject(source);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.MongoConverter#convert(com.mongodb.DBObject)
	 */
	@Override
	public IBatchElement convert(DBObject source) {
		return MongoBatchElement.fromDBObject(source);
	}

	/**
	 * Copy information from SPI into Mongo DBObject.
	 * 
	 * @param source
	 * @param target
	 */
	public static void toDBObject(IBatchElement source, BasicDBObject target) {
		target.append(PROP_BATCH_OPERATION_TOKEN, source.getBatchOperationToken());
		target.append(PROP_HARDWARE_ID, source.getHardwareId());
		target.append(PROP_INDEX, source.getIndex());
		if (source.getProcessingStatus() != null) {
			target.append(PROP_PROCESSING_STATUS, source.getProcessingStatus().name());
		}
	}

	/**
	 * Copy information from Mongo DBObject to model object.
	 * 
	 * @param source
	 * @param target
	 */
	public static void fromDBObject(DBObject source, BatchElement target) {
		String parent = (String) source.get(PROP_BATCH_OPERATION_TOKEN);
		String hardwareId = (String) source.get(PROP_HARDWARE_ID);
		Long index = (Long) source.get(PROP_INDEX);
		String status = (String) source.get(PROP_PROCESSING_STATUS);

		target.setBatchOperationToken(parent);
		target.setHardwareId(hardwareId);
		target.setIndex(index);
		if (status != null) {
			target.setProcessingStatus(ElementProcessingStatus.valueOf(status));
		}
	}

	/**
	 * Convert SPI object to Mongo DBObject.
	 * 
	 * @param source
	 * @return
	 */
	public static BasicDBObject toDBObject(IBatchElement source) {
		BasicDBObject result = new BasicDBObject();
		MongoBatchElement.toDBObject(source, result);
		return result;
	}

	/**
	 * Convert a DBObject into the SPI equivalent.
	 * 
	 * @param source
	 * @return
	 */
	public static BatchElement fromDBObject(DBObject source) {
		BatchElement result = new BatchElement();
		MongoBatchElement.fromDBObject(source, result);
		return result;
	}
}