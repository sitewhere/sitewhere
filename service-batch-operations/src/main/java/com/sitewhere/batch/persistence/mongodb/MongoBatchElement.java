/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.persistence.mongodb;

import java.util.Date;

import org.bson.Document;

import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.mongodb.common.MongoMetadataProvider;
import com.sitewhere.rest.model.device.batch.BatchElement;
import com.sitewhere.spi.batch.ElementProcessingStatus;
import com.sitewhere.spi.batch.IBatchElement;

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

    /** Property for date element was processed */
    public static final String PROP_PROCESSED_DATE = "processedDate";

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
	target.append(PROP_BATCH_OPERATION_TOKEN, source.getBatchOperationToken());
	target.append(PROP_HARDWARE_ID, source.getHardwareId());
	target.append(PROP_INDEX, source.getIndex());
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
	String parent = (String) source.get(PROP_BATCH_OPERATION_TOKEN);
	String hardwareId = (String) source.get(PROP_HARDWARE_ID);
	Long index = (Long) source.get(PROP_INDEX);
	String status = (String) source.get(PROP_PROCESSING_STATUS);
	Date procDate = (Date) source.get(PROP_PROCESSED_DATE);

	target.setBatchOperationToken(parent);
	target.setHardwareId(hardwareId);
	target.setIndex(index);
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