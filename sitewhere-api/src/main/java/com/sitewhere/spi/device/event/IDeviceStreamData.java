/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event;

/**
 * A single chunk of data that is part of a stream coming from a device.
 * 
 * @author Derek
 */
public interface IDeviceStreamData extends IDeviceEvent {

	/**
	 * Get id that ties all related chunks together.
	 * 
	 * @return
	 */
	public String getStreamId();

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