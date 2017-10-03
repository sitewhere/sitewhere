/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.batch;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.rest.model.datatype.JsonDateSerializer;
import com.sitewhere.spi.batch.ElementProcessingStatus;
import com.sitewhere.spi.batch.IBatchElement;

/**
 * Model object for a batch element.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class BatchElement extends MetadataProvider implements IBatchElement, Serializable {

    /** Serialization version identifier */
    private static final long serialVersionUID = 7080873473253195755L;

    /** Token for parent batch operation */
    private String batchOperationToken;

    /** Hardware id */
    private String hardwareId;

    /** Element index */
    private long index;

    /** Processing status */
    private ElementProcessingStatus processingStatus;

    /** Date on which element was processed */
    private Date processedDate;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.batch.IBatchElement#getBatchOperationToken()
     */
    public String getBatchOperationToken() {
	return batchOperationToken;
    }

    public void setBatchOperationToken(String batchOperationToken) {
	this.batchOperationToken = batchOperationToken;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.batch.IBatchElement#getHardwareId()
     */
    public String getHardwareId() {
	return hardwareId;
    }

    public void setHardwareId(String hardwareId) {
	this.hardwareId = hardwareId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.batch.IBatchElement#getIndex()
     */
    public long getIndex() {
	return index;
    }

    public void setIndex(long index) {
	this.index = index;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.batch.IBatchElement#getProcessingStatus()
     */
    public ElementProcessingStatus getProcessingStatus() {
	return processingStatus;
    }

    public void setProcessingStatus(ElementProcessingStatus processingStatus) {
	this.processingStatus = processingStatus;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.batch.IBatchElement#getProcessedDate()
     */
    @JsonSerialize(using = JsonDateSerializer.class)
    public Date getProcessedDate() {
	return processedDate;
    }

    public void setProcessedDate(Date processedDate) {
	this.processedDate = processedDate;
    }
}