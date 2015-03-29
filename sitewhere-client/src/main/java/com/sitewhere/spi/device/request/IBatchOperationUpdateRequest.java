/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.request;

import java.util.Date;
import java.util.Map;

import com.sitewhere.spi.device.batch.BatchOperationStatus;
import com.sitewhere.spi.device.batch.IBatchOperation;

/**
 * Defines fields that can be updated on an {@link IBatchOperation}.
 * 
 * @author Derek
 */
public interface IBatchOperationUpdateRequest {

	/**
	 * Get updated processing status for the batch operation.
	 * 
	 * @return
	 */
	public BatchOperationStatus getProcessingStatus();

	/**
	 * Get updated processing start date.
	 * 
	 * @return
	 */
	public Date getProcessingStartedDate();

	/**
	 * Get updated processing end date.
	 * 
	 * @return
	 */
	public Date getProcessingEndedDate();

	/**
	 * Get metadata values.
	 * 
	 * @return
	 */
	public Map<String, String> getMetadata();
}