/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.batch;

import java.util.Date;
import java.util.UUID;

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
public class BatchElement extends MetadataProvider implements IBatchElement {

    /** Serialization version identifier */
    private static final long serialVersionUID = 7080873473253195755L;

    /** Unqiue id */
    private UUID id;

    /** Id for parent batch operation */
    private UUID batchOperationId;

    /** Id for device */
    private UUID deviceId;

    /** Processing status */
    private ElementProcessingStatus processingStatus;

    /** Date on which element was processed */
    private Date processedDate;

    /*
     * @see com.sitewhere.spi.batch.IBatchElement#getId()
     */
    @Override
    public UUID getId() {
	return id;
    }

    public void setId(UUID id) {
	this.id = id;
    }

    /*
     * @see com.sitewhere.spi.batch.IBatchElement#getBatchOperationId()
     */
    @Override
    public UUID getBatchOperationId() {
	return batchOperationId;
    }

    public void setBatchOperationId(UUID batchOperationId) {
	this.batchOperationId = batchOperationId;
    }

    /*
     * @see com.sitewhere.spi.batch.IBatchElement#getDeviceId()
     */
    @Override
    public UUID getDeviceId() {
	return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
	this.deviceId = deviceId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.batch.IBatchElement#getProcessingStatus()
     */
    @Override
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
    @Override
    @JsonSerialize(using = JsonDateSerializer.class)
    public Date getProcessedDate() {
	return processedDate;
    }

    public void setProcessedDate(Date processedDate) {
	this.processedDate = processedDate;
    }
}