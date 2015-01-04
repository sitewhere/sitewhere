/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.batch;

import com.sitewhere.spi.device.batch.IBatchElement;
import com.sitewhere.spi.device.batch.ElementProcessingStatus;

/**
 * Model object for a batch element.
 * 
 * @author Derek
 */
public class BatchElement implements IBatchElement {

	/** Token for parent batch operation */
	private String batchOperationToken;

	/** Hardware id */
	private String hardwareId;

	/** Element index */
	private long index;

	/** Processing status */
	private ElementProcessingStatus processingStatus;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.batch.IBatchElement#getBatchOperationToken()
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
}