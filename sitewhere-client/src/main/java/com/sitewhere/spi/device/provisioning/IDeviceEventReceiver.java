/*
 * ICommandEventReceiver.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.provisioning;

import java.util.concurrent.BlockingQueue;

import com.sitewhere.spi.SiteWhereException;

/**
 * Handles receipt of device event information from an underlying transport.
 * 
 * @author Derek
 */
public interface IDeviceEventReceiver {

	/**
	 * Gets access to a queue of (still encoded) messages becoming available from the
	 * underlying transport.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public BlockingQueue<byte[]> getEncodedMessages() throws SiteWhereException;
}