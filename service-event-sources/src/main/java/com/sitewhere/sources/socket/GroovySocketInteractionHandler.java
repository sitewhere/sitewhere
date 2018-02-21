/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.socket;

import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.groovy.IGroovyVariables;
import com.sitewhere.microservice.groovy.GroovyComponent;
import com.sitewhere.sources.spi.IInboundEventReceiver;
import com.sitewhere.sources.spi.socket.ISocketInteractionHandler;
import com.sitewhere.sources.spi.socket.ISocketInteractionHandlerFactory;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

import groovy.lang.Binding;

/**
 * Implementation of {@link ISocketInteractionHandler} that defers processing
 * logic to a Groovy script.
 * 
 * @author Derek
 */
public class GroovySocketInteractionHandler implements ISocketInteractionHandler<byte[]> {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(GroovySocketInteractionHandler.class);

    /** Variable that holds the socket */
    public static final String VAR_SOCKET = "socket";

    /** Variable that holds the event receiver */
    public static final String VAR_EVENT_RECEIVER = "receiver";

    /** Injected global Groovy configuration */
    private Factory factory;

    public GroovySocketInteractionHandler(Factory factory) {
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
	    Binding binding = new Binding();
	    binding.setVariable(VAR_SOCKET, socket);
	    binding.setVariable(VAR_EVENT_RECEIVER, receiver);
	    binding.setVariable(IGroovyVariables.VAR_LOGGER, LOGGER);
	    LOGGER.info("About to execute '" + factory.getScriptId() + "' to interact with socket.");
	    factory.run(binding);
	} catch (SiteWhereException e) {
	    throw new SiteWhereException("Unable to run socket interaction handler script.", e);
	}
    }

    /**
     * Factory that produces {@link GroovySocketInteractionHandler} instances.
     * 
     * @author Derek
     */
    public static class Factory extends GroovyComponent implements ISocketInteractionHandlerFactory<byte[]> {

	/** Static logger instance */
	private static Log LOGGER = LogFactory.getLog(Factory.class);

	public Factory() {
	    super(LifecycleComponentType.Other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start(com.
	 * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
	 */
	@Override
	public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop(com.
	 * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
	 */
	@Override
	public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	}

	/*
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Log getLogger() {
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
	    return new GroovySocketInteractionHandler(this);
	}
    }
}