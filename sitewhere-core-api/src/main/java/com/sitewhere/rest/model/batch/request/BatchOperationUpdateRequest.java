/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.batch.request;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import com.sitewhere.spi.batch.BatchOperationStatus;
import com.sitewhere.spi.batch.request.IBatchOperationUpdateRequest;

/**
 * Holds information needed to update a batch operation.
 * 
 * @author Derek
 */
public class BatchOperationUpdateRequest implements IBatchOperationUpdateRequest, Serializable {

    /** Serialization version identifier */
    private static final long serialVersionUID = 7636526750514669256L;

    /** Processing status for operation */
    private BatchOperationStatus processingStatus;

    /** Date when operation processing started */
    private Date processingStartedDate;

    /** Date when operation processing ended */
    private Date processingEndedDate;

    /** Metadata values */
    private Map<String, String> metadata;

    /*
     * @see com.sitewhere.spi.batch.request.IBatchOperationUpdateRequest#
     * getProcessingStatus()
     */
    @Override
    public BatchOperationStatus getProcessingStatus() {
	return processingStatus;
    }

    public void setProcessingStatus(BatchOperationStatus processingStatus) {
	this.processingStatus = processingStatus;
    }

    /*
     * @see com.sitewhere.spi.batch.request.IBatchOperationUpdateRequest#
     * getProcessingStartedDate()
     */
    @Override
    public Date getProcessingStartedDate() {
	return processingStartedDate;
    }

    public void setProcessingStartedDate(Date processingStartedDate) {
	this.processingStartedDate = processingStartedDate;
    }

    /*
     * @see com.sitewhere.spi.batch.request.IBatchOperationUpdateRequest#
     * getProcessingEndedDate()
     */
    @Override
    public Date getProcessingEndedDate() {
	return processingEndedDate;
    }

    public void setProcessingEndedDate(Date processingEndedDate) {
	this.processingEndedDate = processingEndedDate;
    }

    /*
     * @see
     * com.sitewhere.spi.batch.request.IBatchOperationUpdateRequest#getMetadata()
     */
    @Override
    public Map<String, String> getMetadata() {
	return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
	this.metadata = metadata;
    }
}