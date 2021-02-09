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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sitewhere.sources.InboundEventReceiver;
import com.sitewhere.sources.spi.IInboundEventReceiver;
import com.sitewhere.sources.spi.socket.ISocketInteractionHandler;
import com.sitewhere.sources.spi.socket.ISocketInteractionHandlerFactory;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;

/**
 * Implementation of {@link IInboundEventReceiver} that creates a server socket
 * and spawns threads to service requests.
 */
public class SocketInboundEventReceiver<T> extends InboundEventReceiver<T> {

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

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	try {
	    // Verify handler factory is set, then start it.
	    if (getHandlerFactory() == null) {
		throw new SiteWhereException(
			"No socket interaction handler factory configured for socket event source.");
	    }
	    startNestedComponent(getHandlerFactory(), monitor, true);

	    getLogger().info("Receiver creating server socket on " + getBindAddress() + ":" + getPort() + ".");
	    this.server = new ServerSocket(getPort());
	    this.processing = new ServerProcessingThread();
	    this.processingService = Executors.newSingleThreadExecutor();
	    this.pool = Executors.newFixedThreadPool(getNumThreads());
	    getLogger().info("Socket receiver creating processing pool of " + getNumThreads() + " threads.");
	    processingService.execute(processing);
	    getLogger().info("Socket receiver processing started.");
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to bind server socket for event receiver.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IInboundEventReceiver#
     * getDisplayName()
     */
    @Override
    public String getDisplayName() {
	return getBindAddress() + ":" + getPort();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
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
	if (getHandlerFactory() != null) {
	    getHandlerFactory().stop(monitor);
	}

	getLogger().info("Socket receiver processing stopped.");
    }

    /**
     * Handles loop that processes server requests.
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
			getLogger().error("Exception while accepting request in event receiver server socket.", e);
		    }
		}
	    }
	}

	public void setTerminate(boolean terminate) {
	    this.terminate = terminate;
	}
    }

    /**
     * Handles processing for a single request.
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
		getLogger().debug("About to process request received on port " + getPort() + ".");
		getHandlerFactory().newInstance().process(socket, SocketInboundEventReceiver.this);
		getLogger().debug("Processing complete.");
	    } catch (SiteWhereException e) {
		getLogger().error("Exception processing request in event receiver server socket.", e);
	    }
	}
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