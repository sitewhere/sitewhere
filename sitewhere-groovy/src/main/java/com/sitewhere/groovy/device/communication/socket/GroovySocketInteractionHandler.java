/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.groovy.device.communication.socket;

import java.net.Socket;

import org.apache.log4j.Logger;

import com.sitewhere.groovy.GroovyConfiguration;
import com.sitewhere.groovy.device.communication.IGroovyVariables;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.IInboundEventReceiver;
import com.sitewhere.spi.device.communication.socket.ISocketInteractionHandler;
import com.sitewhere.spi.device.communication.socket.ISocketInteractionHandlerFactory;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

import groovy.lang.Binding;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

/**
 * Implementation of {@link ISocketInteractionHandler} that defers processing logic to a
 * Groovy script.
 * 
 * @author Derek
 */
public class GroovySocketInteractionHandler implements ISocketInteractionHandler<byte[]> {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(GroovySocketInteractionHandler.class);

	/** Variable that holds the socket */
	public static final String VAR_SOCKET = "socket";

	/** Variable that holds the event receiver */
	public static final String VAR_EVENT_RECEIVER = "receiver";

	/** Injected global Groovy configuration */
	private GroovyConfiguration configuration;

	/** Path to script used for decoder */
	private String scriptPath;

	public GroovySocketInteractionHandler(GroovyConfiguration configuration, String scriptPath) {
		this.configuration = configuration;
		this.scriptPath = scriptPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.communication.socket.ISocketInteractionHandler#process(
	 * java.net.Socket, com.sitewhere.spi.device.communication.IInboundEventReceiver)
	 */
	@Override
	public void process(Socket socket, IInboundEventReceiver<byte[]> receiver) throws SiteWhereException {
		try {
			Binding binding = new Binding();
			binding.setVariable(VAR_SOCKET, socket);
			binding.setVariable(VAR_EVENT_RECEIVER, receiver);
			binding.setVariable(IGroovyVariables.VAR_LOGGER, LOGGER);
			LOGGER.info("About to execute '" + getScriptPath() + "' to interact with socket.");
			getConfiguration().getGroovyScriptEngine().run(getScriptPath(), binding);
		} catch (ResourceException e) {
			throw new SiteWhereException("Unable to access Groovy decoder script.", e);
		} catch (ScriptException e) {
			throw new SiteWhereException("Unable to run Groovy decoder script.", e);
		}
	}

	public GroovyConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(GroovyConfiguration configuration) {
		this.configuration = configuration;
	}

	public String getScriptPath() {
		return scriptPath;
	}

	public void setScriptPath(String scriptPath) {
		this.scriptPath = scriptPath;
	}

	/**
	 * Factory that produces {@link GroovySocketInteractionHandler} instances.
	 * 
	 * @author Derek
	 */
	public static class Factory extends LifecycleComponent
			implements ISocketInteractionHandlerFactory<byte[]> {

		/** Static logger instance */
		private static Logger LOGGER = Logger.getLogger(Factory.class);

		/** Injected global Groovy configuration */
		private GroovyConfiguration configuration;

		/** Path to script used for decoder */
		private String scriptPath;

		public Factory() {
			super(LifecycleComponentType.Other);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
		 */
		@Override
		public void start() throws SiteWhereException {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
		 */
		@Override
		public void stop() throws SiteWhereException {
		}

		@Override
		public Logger getLogger() {
			return LOGGER;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sitewhere.spi.device.communication.socket.ISocketInteractionHandlerFactory
		 * #newInstance()
		 */
		@Override
		public ISocketInteractionHandler<byte[]> newInstance() {
			return new GroovySocketInteractionHandler(getConfiguration(), getScriptPath());
		}

		public GroovyConfiguration getConfiguration() {
			return configuration;
		}

		public void setConfiguration(GroovyConfiguration configuration) {
			this.configuration = configuration;
		}

		public String getScriptPath() {
			return scriptPath;
		}

		public void setScriptPath(String scriptPath) {
			this.scriptPath = scriptPath;
		}
	}
}