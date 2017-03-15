/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb.device;

import java.util.Date;

import org.bson.Document;

import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.mongodb.common.MongoMetadataProvider;
import com.sitewhere.mongodb.common.MongoSiteWhereEntity;
import com.sitewhere.rest.model.device.batch.BatchOperation;
import com.sitewhere.spi.device.batch.BatchOperationStatus;
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
    public static final String PROP_OPERATION_TYPE = "type";

    /** Property for operation parameters */
    public static final String PROP_PARAMETERS = "params";

    /** Property for processing status */
    public static final String PROP_PROC_STATUS = "status";

    /** Property for processing start date */
    public static final String PROP_PROC_START_DATE = "processingStartDate";

    /** Property for processing end date */
    public static final String PROP_PROC_END_DATE = "processingEndDate";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
     */
    @Override
    public Document convert(IBatchOperation source) {
	return MongoBatchOperation.toDocument(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(org.bson.Document)
     */
    @Override
    public IBatchOperation convert(Document source) {
	return MongoBatchOperation.fromDocument(source);
    }

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     */
    public static void toDocument(IBatchOperation source, Document target) {
	target.append(PROP_TOKEN, source.getToken());
	if (source.getOperationType() != null) {
	    target.append(PROP_OPERATION_TYPE, source.getOperationType().name());
	}
	if (source.getProcessingStatus() != null) {
	    target.append(PROP_PROC_STATUS, source.getProcessingStatus().name());
	}
	if (source.getProcessingStartedDate() != null) {
	    target.append(PROP_PROC_START_DATE, source.getProcessingStartedDate());
	}
	if (source.getProcessingEndedDate() != null) {
	    target.append(PROP_PROC_END_DATE, source.getProcessingEndedDate());
	}

	// Set parameters as nested object.
	Document params = new Document();
	for (String key : source.getParameters().keySet()) {
	    params.put(key, source.getParameters().get(key));
	}
	target.put(PROP_PARAMETERS, params);

	MongoSiteWhereEntity.toDocument(source, target);
	MongoMetadataProvider.toDocument(source, target);
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     */
    public static void fromDocument(Document source, BatchOperation target) {
	String token = (String) source.get(PROP_TOKEN);
	String operationType = (String) source.get(PROP_OPERATION_TYPE);
	String procStatus = (String) source.get(PROP_PROC_STATUS);
	Date procStart = (Date) source.get(PROP_PROC_START_DATE);
	Date procEnd = (Date) source.get(PROP_PROC_END_DATE);

	target.setToken(token);
	if (operationType != null) {
	    target.setOperationType(OperationType.valueOf(operationType));
	}
	if (procStatus != null) {
	    target.setProcessingStatus(BatchOperationStatus.valueOf(procStatus));
	}
	target.setProcessingStartedDate(procStart);
	target.setProcessingEndedDate(procEnd);

	// Load parameters from nested object.
	Document params = (Document) source.get(PROP_PARAMETERS);
	if (params != null) {
	    for (String key : params.keySet()) {
		target.getParameters().put(key, (String) params.get(key));
	    }
	}

	MongoSiteWhereEntity.fromDocument(source, target);
	MongoMetadataProvider.fromDocument(source, target);
    }

    /**
     * Convert SPI object to Mongo {@link Document}.
     * 
     * @param source
     * @return
     */
    public static Document toDocument(IBatchOperation source) {
	Document result = new Document();
	MongoBatchOperation.toDocument(source, result);
	return result;
    }

    /**
     * Convert a {@link Document} into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static BatchOperation fromDocument(Document source) {
	BatchOperation result = new BatchOperation();
	MongoBatchOperation.fromDocument(source, result);
	return result;
    }
}