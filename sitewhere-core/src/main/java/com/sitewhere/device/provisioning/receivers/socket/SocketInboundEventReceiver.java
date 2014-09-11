/* 
 * Copyright (C) Reveal Technologies, LLC - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */
package com.sitewhere.device.provisioning.receivers.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.provisioning.IInboundEventReceiver;

/**
 * Implementation of {@link IInboundEventReceiver} that creates a server socket and spawns
 * threads to service requests.
 * 
 * @author Derek
 */
public class SocketInboundEventReceiver<T> implements IInboundEventReceiver<T> {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(SocketInboundEventReceiver.class);

	/** Default number of threads used to service requests */
	private static final int DEFAULT_NUM_THREADS = 5;

	/** Default port for server socket */
	private static final int DEFAULT_PORT = 8484;

	/** Default max requests waiting for system to process */
	private static final int DEFAULT_MAX_QUEUED_REQUESTS = 100;

	/** Number of threads used to service requests */
	private int numThreads = DEFAULT_NUM_THREADS;

	/** Port used for server socket */
	private int port = DEFAULT_PORT;

	/** Classname for handler implementation */
	private String handler = ReadAllInteractionHandler.class.getName();

	/** Pool of threads used to service requests */
	private ExecutorService processingService;

	/** Pool of threads used to service requests */
	private ExecutorService pool;

	/** Server socket that processes requests */
	private ServerSocket server;

	/** Handles processing of server requests */
	private ServerProcessingThread processing;

	/** Queue used to hold messages that are ready for processing */
	private BlockingQueue<T> queue = new ArrayBlockingQueue<T>(DEFAULT_MAX_QUEUED_REQUESTS);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		try {
			LOGGER.info("Receiver creating server socket on port " + getPort() + ".");
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
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#stop()
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.provisioning.IInboundEventReceiver#getEncodedMessages()
	 */
	@Override
	public BlockingQueue<T> getEncodedMessages() throws SiteWhereException {
		return queue;
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
					LOGGER.error("Exception while accepting request in event receiver server socket.", e);
				}
			}
		}

		public void setTerminate(boolean terminate) {
			this.terminate = terminate;
		}
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
				createHandlerInstance().process(socket, queue);
				LOGGER.debug("Processing complete.");
			} catch (SiteWhereException e) {
				LOGGER.error("Exception processing request in event receiver server socket.", e);
			}
		}
	}

	/**
	 * Create an instance of the handler class.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	@SuppressWarnings("unchecked")
	protected ISocketInteractionHandler<T> createHandlerInstance() throws SiteWhereException {
		try {
			Class<?> clazz = Class.forName(getHandler());
			if (!(ISocketInteractionHandler.class.isAssignableFrom(clazz))) {
				throw new SiteWhereException(
						"Socket interaction handler does not implement required interface.");
			}
			return (ISocketInteractionHandler<T>) clazz.newInstance();
		} catch (ClassNotFoundException e) {
			throw new SiteWhereException(e);
		} catch (InstantiationException e) {
			throw new SiteWhereException(e);
		} catch (IllegalAccessException e) {
			throw new SiteWhereException(e);
		}
	}

	public int getNumThreads() {
		return numThreads;
	}

	public void setNumThreads(int numThreads) {
		this.numThreads = numThreads;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHandler() {
		return handler;
	}

	public void setHandler(String handler) {
		this.handler = handler;
	}
}