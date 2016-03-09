/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.IInboundEventReceiver;
import com.sitewhere.spi.device.communication.IInboundEventSource;
import com.sitewhere.spi.device.communication.socket.ISocketInteractionHandler;
import com.sitewhere.spi.device.communication.socket.ISocketInteractionHandlerFactory;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link IInboundEventReceiver} that creates a server socket and spawns
 * threads to service requests.
 * 
 * @author Derek
 */
public class SocketInboundEventReceiver<T> extends LifecycleComponent implements IInboundEventReceiver<T> {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(SocketInboundEventReceiver.class);

	/** Default number of threads used to service requests */
	private static final int DEFAULT_NUM_THREADS = 5;

	/** Default ip binding for server socket */
	private static final String DEFAULT_BIND_ADDRESS = "localhost";

	/** Default port for server socket */
	private static final int DEFAULT_PORT = 8484;

	/** Number of threads used to service requests */
	private int numThreads = DEFAULT_NUM_THREADS;

	/** Bind address used for server socket */
	private String bindAddress = DEFAULT_BIND_ADDRESS;

	/** Port used for server socket */
	private int port = DEFAULT_PORT;

	/** Parent event source */
	private IInboundEventSource<T> eventSource;

	/** Factory that produces {@link ISocketInteractionHandler} instances */
	private ISocketInteractionHandlerFactory<T> handlerFactory;

	/** Pool of threads used to service requests */
	private ExecutorService processingService;

	/** Pool of threads used to service requests */
	private ExecutorService pool;

	/** Server socket that processes requests */
	private ServerSocket server;

	/** Handles processing of server requests */
	private ServerProcessingThread processing;

	public SocketInboundEventReceiver() {
		super(LifecycleComponentType.InboundEventReceiver);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		try {
			if (getHandlerFactory() == null) {
				throw new SiteWhereException(
						"No socket interaction handler factory configured for socket event source.");
			}
			LOGGER.info("Receiver creating server socket on " + getBindAddress() + ":" + getPort() + ".");
			this.server = new ServerSocket(getPort());
			this.processing = new ServerProcessingThread();
			this.processingService = Executors.newSingleThreadExecutor();
			this.pool = Executors.newFixedThreadPool(getNumThreads());
			LOGGER.info("Socket receiver creating processing pool of " + getNumThreads() + " threads.");
			processingService.execute(processing);
			LOGGER.info("Socket receiver processing started.");
		} catch (IOException e) {
			throw new SiteWhereException("Unable to bind server socket for event receiver.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Logger getLogger() {
		return LOGGER;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.communication.IInboundEventReceiver#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return getBindAddress() + ":" + getPort();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		if (processing != null) {
			processing.setTerminate(true);
		}
		if (processingService != null) {
			processingService.shutdown();
		}
		if (pool != null) {
			pool.shutdown();
		}
		if (server != null) {
			try {
				server.close();
			} catch (IOException e) {
				throw new SiteWhereException("Error shutting down server socket for event receiver.", e);
			}
		}
		LOGGER.info("Socket receiver processing stopped.");
	}

	/**
	 * Handles loop that processes server requests.
	 * 
	 * @author Derek
	 */
	private class ServerProcessingThread implements Runnable {

		/** Indicates if processing should continue */
		private boolean terminate = false;

		@Override
		public void run() {
			while (!terminate) {
				try {
					Socket socket = server.accept();
					RequestProcessingThread processor = new RequestProcessingThread(socket);
					pool.submit(processor);
				} catch (IOException e) {
					if (!terminate) {
						LOGGER.error("Exception while accepting request in event receiver server socket.", e);
					}
				}
			}
		}

		public void setTerminate(boolean terminate) {
			this.terminate = terminate;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.communication.IInboundEventReceiver#onEventPayloadReceived
	 * (java.lang.Object, java.util.Map)
	 */
	@Override
	public void onEventPayloadReceived(T payload, Map<String, String> metadata) {
		getEventSource().onEncodedEventReceived(SocketInboundEventReceiver.this, payload, metadata);
	}

	/**
	 * Handles processing for a single request.
	 * 
	 * @author Derek
	 */
	private class RequestProcessingThread implements Runnable {

		/** Socket for processing */
		private Socket socket;

		public RequestProcessingThread(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			try {
				LOGGER.debug("About to process request received on port " + getPort() + ".");
				getHandlerFactory().newInstance().process(socket, SocketInboundEventReceiver.this);
				LOGGER.debug("Processing complete.");
			} catch (SiteWhereException e) {
				LOGGER.error("Exception processing request in event receiver server socket.", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.communication.IInboundEventReceiver#getEventSource()
	 */
	public IInboundEventSource<T> getEventSource() {
		return eventSource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.communication.IInboundEventReceiver#setEventSource(com
	 * .sitewhere.spi.device.communication.IInboundEventSource)
	 */
	public void setEventSource(IInboundEventSource<T> eventSource) {
		this.eventSource = eventSource;
	}

	public int getNumThreads() {
		return numThreads;
	}

	public void setNumThreads(int numThreads) {
		this.numThreads = numThreads;
	}

	public String getBindAddress() {
		return bindAddress;
	}

	public void setBindAddress(String bindAddress) {
		this.bindAddress = bindAddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public ISocketInteractionHandlerFactory<T> getHandlerFactory() {
		return handlerFactory;
	}

	public void setHandlerFactory(ISocketInteractionHandlerFactory<T> handlerFactory) {
		this.handlerFactory = handlerFactory;
	}
}