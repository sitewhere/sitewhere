/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.request;

import java.util.Date;

import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.spi.device.batch.ElementProcessingStatus;
import com.sitewhere.spi.device.request.IBatchElementUpdateRequest;

/**
 * Holds information needed to update a batch operation element.
 * 
 * @author Derek
 */
public class BatchElementUpdateRequest extends MetadataProvider implements IBatchElementUpdateRequest {

	/** Processing status for update */
	private ElementProcessingStatus processingStatus;

	/** Date element was processed */
	private Date processedDate;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.request.IBatchElementUpdateRequest#getProcessingStatus()
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
	 * @see com.sitewhere.spi.device.request.IBatchElementUpdateRequest#getProcessedDate()
	 */
	public Date getProcessedDate() {
		return processedDate;
	}

	public void setProcessedDate(Date processedDate) {
		this.processedDate = processedDate;
	}
}