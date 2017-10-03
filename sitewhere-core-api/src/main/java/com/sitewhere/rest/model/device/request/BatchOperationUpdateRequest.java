/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.request;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import com.sitewhere.spi.batch.BatchOperationStatus;
import com.sitewhere.spi.device.request.IBatchOperationUpdateRequest;

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

    public BatchOperationStatus getProcessingStatus() {
	return processingStatus;
    }

    public void setProcessingStatus(BatchOperationStatus processingStatus) {
	this.processingStatus = processingStatus;
    }

    public Date getProcessingStartedDate() {
	return processingStartedDate;
    }

    public void setProcessingStartedDate(Date processingStartedDate) {
	this.processingStartedDate = processingStartedDate;
    }

    public Date getProcessingEndedDate() {
	return processingEndedDate;
    }

    public void setProcessingEndedDate(Date processingEndedDate) {
	this.processingEndedDate = processingEndedDate;
    }

    public Map<String, String> getMetadata() {
	return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
	this.metadata = metadata;
    }
}