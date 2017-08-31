/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.socket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.device.communication.EventProcessingLogic;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.IInboundEventReceiver;
import com.sitewhere.spi.device.communication.socket.ISocketInteractionHandler;
import com.sitewhere.spi.device.communication.socket.ISocketInteractionHandlerFactory;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link ISocketInteractionHandler} that reads everything
 * from the socket and sends the resulting byte array to the parent event
 * source.
 * 
 * @author Derek
 */
public class ReadAllInteractionHandler implements ISocketInteractionHandler<byte[]> {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.communication.socket.ISocketInteractionHandler#
     * process (java.net.Socket,
     * com.sitewhere.spi.device.communication.IInboundEventReceiver)
     */
    @Override
    public void process(Socket socket, IInboundEventReceiver<byte[]> receiver) throws SiteWhereException {
	try {
	    InputStream input = socket.getInputStream();
	    ByteArrayOutputStream output = new ByteArrayOutputStream();
	    int value;
	    while ((value = input.read()) != -1) {
		output.write(value);
	    }
	    input.close();
	    EventProcessingLogic.processRawPayload(receiver, output.toByteArray(), null);
	} catch (IOException e) {
	    throw new SiteWhereException("Exception processing request in socket interaction handler.", e);
	}
    }

    /**
     * Factory class that produces {@link ReadAllInteractionHandler} instances.
     * 
     * @author Derek
     */
    public static class Factory extends LifecycleComponent implements ISocketInteractionHandlerFactory<byte[]> {

	/** Static logger instance */
	private static Logger LOGGER = LogManager.getLogger();

	public Factory() {
	    super(LifecycleComponentType.Other);
	}

	@Override
	public Logger getLogger() {
	    return LOGGER;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.communication.socket.
	 * ISocketInteractionHandlerFactory #newInstance()
	 */
	@Override
	public ISocketInteractionHandler<byte[]> newInstance() {
	    return new ReadAllInteractionHandler();
	}
    }
}