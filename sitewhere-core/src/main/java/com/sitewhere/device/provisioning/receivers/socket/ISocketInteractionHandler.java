/*
 * ISocketInteractionHandler.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.provisioning.receivers.socket;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import com.sitewhere.spi.SiteWhereException;

/**
 * Interface for handling socket communication with a remote device.
 * 
 * @author Derek
 */
public interface ISocketInteractionHandler {

	/**
	 * Delegates processing of socket information. Commands parsed from the socket should
	 * be added to the queue via offer().
	 * 
	 * @param socket
	 * @param queue
	 * @throws SiteWhereException
	 */
	public void process(Socket socket, BlockingQueue<byte[]> queue) throws SiteWhereException;
}