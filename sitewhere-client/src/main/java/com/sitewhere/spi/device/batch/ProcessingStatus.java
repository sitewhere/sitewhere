/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.batch;

/**
 * Indicates the processing status of a single {@link IBatchElement}.
 * 
 * @author Derek
 */
public enum ProcessingStatus {

	/** Indicates a batch element has not been processed */
	Unprocessed,

	/** Indicates processing failed for the batch element */
	Failed,

	/** Indicates processing succeeded for the batch element */
	Succeeded;
}