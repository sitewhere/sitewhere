/*
 * BlockingQueueInboundProcessingStrategy.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.provisioning;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.processor.IInboundEventProcessorChain;
import com.sitewhere.spi.device.event.request.IDeviceEventCreateRequest;
import com.sitewhere.spi.device.provisioning.IDecodedDeviceEventRequest;
import com.sitewhere.spi.device.provisioning.IInboundEventReceiver;
import com.sitewhere.spi.device.provisioning.IInboundProcessingStrategy;

/**
 * Implementation of {@link IInboundProcessingStrategy} that uses an
 * {@link ArrayBlockingQueue} to hold decoded events that are submitted into the
 * {@link IInboundEventProcessorChain}.
 * 
 * @author Derek
 */
public class BlockingQueueInboundProcessingStrategy implements IInboundProcessingStrategy {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(BlockingQueueInboundProcessingStrategy.class);

	/** Maximum size of pending request queue */
	private static final int MAX_PENDING_REQUEST_QUEUE_SIZE = 1000;

	/** Number of threads used for event processing (for all receivers) */
	private static final int EVENT_PROCESSOR_THREAD_COUNT = 2;

	/** Blocking queue of pending event creation requests from receivers */
	private BlockingQueue<IDecodedDeviceEventRequest> pendingRequests =
			new ArrayBlockingQueue<IDecodedDeviceEventRequest>(MAX_PENDING_REQUEST_QUEUE_SIZE);

	/** Thread pool for submitting decoded events */
	private ExecutorService processorThreadPool;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		LOGGER.info("Starting blocking queue inbound processing strategy...");
		startEventProcessorThreads();
		LOGGER.info("Started blocking queue inbound processing strategy...");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		if (processorThreadPool != null) {
			processorThreadPool.shutdown();
		}
	}

	/**
	 * Create threads that will process {@link IDeviceEventCreateRequest} requests for all
	 * receivers.
	 * 
	 * @throws SiteWhereException
	 */
	protected void startEventProcessorThreads() throws SiteWhereException {
		LOGGER.info("Initializing " + EVENT_PROCESSOR_THREAD_COUNT + " device event processors...");
		processorThreadPool = Executors.newFixedThreadPool(EVENT_PROCESSOR_THREAD_COUNT);
		for (int i = 0; i < EVENT_PROCESSOR_THREAD_COUNT; i++) {
			processorThreadPool.execute(new EventProcessor());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.provisioning.IInboundProcessingStrategy#submit(com.sitewhere
	 * .spi.device.provisioning.IDecodedDeviceEventRequest)
	 */
	@Override
	public void submit(IDecodedDeviceEventRequest event) throws SiteWhereException {
		pendingRequests.offer(event);
	}

	/**
	 * Thread that processes events being decoded from {@link IInboundEventReceiver}
	 * threads.
	 * 
	 * @author Derek
	 */
	private class EventProcessor implements Runnable {

		@Override
		public void run() {
			LOGGER.info("Started inbound event processor thread.");

			// Event creation APIs expect an authenticated user in order to check
			// permissions and log who creates events. When called in this context, the
			// authenticated user will always be 'system'.
			//
			// TODO: Alternatively, we may want the client to authenticate on registration
			// and pass a token on each request.
			try {
				SecurityContextHolder.getContext().setAuthentication(
						SiteWhereServer.getSystemAuthentication());
			} catch (SiteWhereException e) {
				throw new RuntimeException(
						"Unable to use system authentication for inbound event processor thread.", e);
			}
			while (true) {
				try {
					IDecodedDeviceEventRequest decoded = pendingRequests.take();
					LOGGER.debug("Device event processor thread picked up request.");
					SiteWhereServer.getInstance().getInboundEventProcessorChain().onDecodedDeviceEventRequest(
							decoded);
				} catch (SiteWhereException e) {
					LOGGER.error("Error processing inbound event request.", e);
				} catch (InterruptedException e) {
					LOGGER.warn("Device event processor thread interrupted.", e);
				} catch (Throwable e) {
					LOGGER.error("Unhandled exception in device event processing.", e);
				}
			}
		}
	}
}