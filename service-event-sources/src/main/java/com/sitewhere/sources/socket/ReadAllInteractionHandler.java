/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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