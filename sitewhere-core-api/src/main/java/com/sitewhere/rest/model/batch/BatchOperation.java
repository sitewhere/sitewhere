/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.batch;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.rest.model.datatype.JsonDateSerializer;
import com.sitewhere.spi.batch.BatchOperationStatus;
import com.sitewhere.spi.batch.IBatchOperation;

/**
 * Model object for a batch operation.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class BatchOperation extends MetadataProviderEntity implements IBatchOperation, Serializable {

    /** Serialization version identifier */
    private static final long serialVersionUID = -228183022121018340L;

    /** Unique id */
    private UUID id;

    /** Unqiue token */
    private String token;

    /** Operation type requested */
    private String operationType;

    /** Operation parameters */
    private Map<String, String> parameters = new HashMap<String, String>();

    /** Processing status for operation */
    private BatchOperationStatus processingStatus = BatchOperationStatus.Unprocessed;

    /** Date when operation processing started */
    private Date processingStartedDate;

    /** Date when operation processing ended */
    private Date processingEndedDate;

    /*
     * @see com.sitewhere.spi.common.ISiteWhereEntity#getId()
     */
    @Override
    public UUID getId() {
	return id;
    }

    public void setId(UUID id) {
	this.id = id;
    }

    /*
     * @see com.sitewhere.spi.common.ISiteWhereEntity#getToken()
     */
    @Override
    public String getToken() {
	return token;
    }

    public void setToken(String token) {
	this.token = token;
    }

    /*
     * @see com.sitewhere.spi.batch.IBatchOperation#getOperationType()
     */
    @Override
    public String getOperationType() {
	return operationType;
    }

    public void setOperationType(String operationType) {
	this.operationType = operationType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.batch.IBatchOperation#getParameters()
     */
    @Override
    public Map<String, String> getParameters() {
	return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
	this.parameters = parameters;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.batch.IBatchOperation#getProcessingStatus()
     */
    @Override
    public BatchOperationStatus getProcessingStatus() {
	return processingStatus;
    }

    public void setProcessingStatus(BatchOperationStatus processingStatus) {
	this.processingStatus = processingStatus;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.batch.IBatchOperation#getProcessingStartedDate()
     */
    @Override
    @JsonSerialize(using = JsonDateSerializer.class)
    public Date getProcessingStartedDate() {
	return processingStartedDate;
    }

    public void setProcessingStartedDate(Date processingStartedDate) {
	this.processingStartedDate = processingStartedDate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.batch.IBatchOperation#getProcessingEndedDate()
     */
    @Override
    @JsonSerialize(using = JsonDateSerializer.class)
    public Date getProcessingEndedDate() {
	return processingEndedDate;
    }

    public void setProcessingEndedDate(Date processingEndedDate) {
	this.processingEndedDate = processingEndedDate;
    }
}