/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.socket;

import java.net.Socket;

import com.sitewhere.microservice.scripting.Binding;
import com.sitewhere.microservice.scripting.ScriptingComponent;
import com.sitewhere.sources.spi.IInboundEventReceiver;
import com.sitewhere.sources.spi.socket.ISocketInteractionHandler;
import com.sitewhere.sources.spi.socket.ISocketInteractionHandlerFactory;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link ISocketInteractionHandler} that defers processing
 * logic to a script.
 */
public class ScriptedSocketInteractionHandler extends ScriptingComponent<Void>
	implements ISocketInteractionHandler<byte[]> {

    /** Variable that holds the socket */
    public static final String VAR_SOCKET = "socket";

    /** Variable that holds the event receiver */
    public static final String VAR_EVENT_RECEIVER = "receiver";

    /** Injected global Groovy configuration */
    private Factory factory;

    public ScriptedSocketInteractionHandler(Factory factory) {
	this.factory = factory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.socket.ISocketInteractionHandler#
     * process( java.net.Socket,
     * com.sitewhere.spi.device.communication.IInboundEventReceiver)
     */
    @Override
    public void process(Socket socket, IInboundEventReceiver<byte[]> receiver) throws SiteWhereException {
	try {
	    Binding binding = createBindingFor(this);
	    binding.setVariable(VAR_SOCKET, socket);
	    binding.setVariable(VAR_EVENT_RECEIVER, receiver);
	    factory.run(binding);
	} catch (SiteWhereException e) {
	    throw new SiteWhereException("Unable to run socket interaction handler script.", e);
	}
    }

    /**
     * Factory that produces {@link ScriptedSocketInteractionHandler} instances.
     */
    public static class Factory extends ScriptingComponent<Void> implements ISocketInteractionHandlerFactory<byte[]> {

	public Factory() {
	    super(LifecycleComponentType.Other);
	}

	/*
	 * @see
	 * com.sitewhere.sources.spi.socket.ISocketInteractionHandlerFactory#newInstance
	 * ()
	 */
	@Override
	public ISocketInteractionHandler<byte[]> newInstance() {
	    return new ScriptedSocketInteractionHandler(this);
	}
    }
}