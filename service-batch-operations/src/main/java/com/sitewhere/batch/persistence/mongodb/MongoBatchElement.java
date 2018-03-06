/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.persistence.mongodb;

import java.util.Date;
import java.util.UUID;

import org.bson.Document;

import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.mongodb.common.MongoMetadataProvider;
import com.sitewhere.rest.model.batch.BatchElement;
import com.sitewhere.spi.batch.ElementProcessingStatus;
import com.sitewhere.spi.batch.IBatchElement;

/**
 * Used to load or save batch element data to MongoDB.
 * 
 * @author Derek
 */
public class MongoBatchElement implements MongoConverter<IBatchElement> {

    /** Property for id */
    public static final String PROP_ID = "_id";

    /** Property for parent batch operation id */
    public static final String PROP_BATCH_OPERATION_ID = "boid";

    /** Property for device id */
    public static final String PROP_DEVICE_ID = "dvid";

    /** Property for processing status */
    public static final String PROP_PROCESSING_STATUS = "stat";

    /** Property for date element was processed */
    public static final String PROP_PROCESSED_DATE = "prdt";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
     */
    @Override
    public Document convert(IBatchElement source) {
	return MongoBatchElement.toDocument(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(org.bson.Document)
     */
    @Override
    public IBatchElement convert(Document source) {
	return MongoBatchElement.fromDocument(source);
    }

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     */
    public static void toDocument(IBatchElement source, Document target) {
	target.append(PROP_ID, source.getId());
	target.append(PROP_BATCH_OPERATION_ID, source.getBatchOperationId());
	target.append(PROP_DEVICE_ID, source.getDeviceId());
	if (source.getProcessingStatus() != null) {
	    target.append(PROP_PROCESSING_STATUS, source.getProcessingStatus().name());
	}
	if (source.getProcessedDate() != null) {
	    target.append(PROP_PROCESSED_DATE, source.getProcessedDate());
	}
	MongoMetadataProvider.toDocument(source, target);
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     */
    public static void fromDocument(Document source, BatchElement target) {
	UUID id = (UUID) source.get(PROP_ID);
	UUID batchOperationId = (UUID) source.get(PROP_BATCH_OPERATION_ID);
	UUID deviceId = (UUID) source.get(PROP_DEVICE_ID);
	String status = (String) source.get(PROP_PROCESSING_STATUS);
	Date procDate = (Date) source.get(PROP_PROCESSED_DATE);

	target.setId(id);
	target.setBatchOperationId(batchOperationId);
	target.setDeviceId(deviceId);
	if (status != null) {
	    target.setProcessingStatus(ElementProcessingStatus.valueOf(status));
	}
	target.setProcessedDate(procDate);
	MongoMetadataProvider.fromDocument(source, target);
    }

    /**
     * Convert SPI object to Mongo {@link Document}.
     * 
     * @param source
     * @return
     */
    public static Document toDocument(IBatchElement source) {
	Document result = new Document();
	MongoBatchElement.toDocument(source, result);
	return result;
    }

    /**
     * Convert a {@link Document} into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static BatchElement fromDocument(Document source) {
	BatchElement result = new BatchElement();
	MongoBatchElement.fromDocument(source, result);
	return result;
    }
}