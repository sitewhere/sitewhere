/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.provisioning.socket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import com.sitewhere.spi.SiteWhereException;

/**
 * Implementation of {@link ISocketInteractionHandler} that reads everything from the
 * socket and puts the resulting byte array on the queue.
 * 
 * @author Derek
 */
public class ReadAllInteractionHandler implements ISocketInteractionHandler<byte[]> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.device.provisioning.receivers.socket.ISocketInteractionHandler#process
	 * (java.net.Socket, java.util.concurrent.BlockingQueue)
	 */
	@Override
	public void process(Socket socket, BlockingQueue<byte[]> queue) throws SiteWhereException {
		try {
			InputStream input = socket.getInputStream();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			int value;
			while ((value = input.read()) != -1) {
				output.write(value);
			}
			input.close();
			queue.offer(output.toByteArray());
		} catch (IOException e) {
			throw new SiteWhereException("Exception processing request in socket interaction handler.", e);
		}
	}
}