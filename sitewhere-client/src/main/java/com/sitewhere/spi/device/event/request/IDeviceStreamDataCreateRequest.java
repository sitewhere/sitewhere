/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event.request;

/**
 * Interface for arguments needed to create a chunk of stream data for a device.
 * 
 * @author Derek
 */
public interface IDeviceStreamDataCreateRequest extends IDeviceEventCreateRequest {

	/**
	 * Get sequence number for ordering chunks.
	 * 
	 * @return
	 */
	public Long getSequenceNumber();

	/**
	 * Get chunk data.
	 * 
	 * @return
	 */
	public byte[] getData();
}