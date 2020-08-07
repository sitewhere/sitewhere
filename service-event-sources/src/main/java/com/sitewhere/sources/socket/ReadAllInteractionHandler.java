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

import com.sitewhere.microservice.lifecycle.LifecycleComponent;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.sources.spi.IInboundEventReceiver;
import com.sitewhere.sources.spi.socket.ISocketInteractionHandler;
import com.sitewhere.sources.spi.socket.ISocketInteractionHandlerFactory;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link ISocketInteractionHandler} that reads everything
 * from the socket and sends the resulting byte array to the parent event
 * source.
 */
public class ReadAllInteractionHandler extends TenantEngineLifecycleComponent
	implements ISocketInteractionHandler<byte[]> {

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.socket.ISocketInteractionHandler#
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
	    receiver.onEventPayloadReceived(output.toByteArray(), null);
	} catch (IOException e) {
	    throw new SiteWhereException("Exception processing request in socket interaction handler.", e);
	}
    }

    /**
     * Factory class that produces {@link ReadAllInteractionHandler} instances.
     */
    public static class Factory extends LifecycleComponent implements ISocketInteractionHandlerFactory<byte[]> {

	public Factory() {
	    super(LifecycleComponentType.Other);
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