/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.batch;

/**
 * Holds information about a single device operation within an {@link IBatchOperation}.
 * 
 * @author Derek
 */
public interface IBatchElement {

	/**
	 * Get token for parent {@link IBatchOperation}.
	 * 
	 * @return
	 */
	public String getBatchOperationToken();

	/**
	 * Get hardware id of device.
	 * 
	 * @return
	 */
	public String getHardwareId();

	/**
	 * Get index that corresponds to this entry.
	 * 
	 * @return
	 */
	public long getIndex();

	/**
	 * Get processing status indicator.
	 * 
	 * @return
	 */
	public ProcessingStatus getProcessingStatus();
}