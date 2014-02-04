/*
 * DefaultDeviceEventProcessor.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.provisioning;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.request.IDeviceEventCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;
import com.sitewhere.spi.device.provisioning.IDeviceEventDecoder;
import com.sitewhere.spi.device.provisioning.IDeviceEventProcessor;
import com.sitewhere.spi.device.provisioning.IDeviceEventReceiver;

/**
 * Default implementation of {@link IDeviceEventProcessor}.
 * 
 * @author Derek
 */
public class DefaultDeviceEventProcessor implements IDeviceEventProcessor {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(DefaultDeviceEventProcessor.class);

	/** Maximum size of pending request queue */
	private static final int MAX_PENDING_REQUEST_QUEUE_SIZE = 100;

	/** Number of threads used for event processing (for all receivers) */
	private static final int EVENT_PROCESSOR_THREAD_COUNT = 2;

	/** Device event decoder */
	private IDeviceEventDecoder deviceEventDecoder;

	/** List of {@link IDeviceEventReceiver} that supply this processor */
	private List<IDeviceEventReceiver> deviceEventReceivers = new ArrayList<IDeviceEventReceiver>();

	/** Thread pool for event receivers */
	private ExecutorService receiverThreadPool;

	/** Blocking queue of pending event creation requests from receivers */
	private BlockingQueue<IDeviceEventCreateRequest> pendingRequests =
			new ArrayBlockingQueue<IDeviceEventCreateRequest>(MAX_PENDING_REQUEST_QUEUE_SIZE);

	/** Thread pool for event creation */
	private ExecutorService processorThreadPool;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		LOGGER.info("Starting device event processing...");
		startEventReceiverThreads();
		startEventProcessorThreads();
		LOGGER.info("Started device event processing.");
	}

	/**
	 * Create one thread per receiver and have it pull records in blocking mode.
	 * 
	 * @throws SiteWhereException
	 */
	protected void startEventReceiverThreads() throws SiteWhereException {
		if (getDeviceEventReceivers().size() > 0) {
			LOGGER.info("Initializing " + getDeviceEventReceivers().size() + " device event receivers...");
			receiverThreadPool = Executors.newFixedThreadPool(getDeviceEventReceivers().size());
			for (IDeviceEventReceiver receiver : getDeviceEventReceivers()) {
				receiver.start();
				receiverThreadPool.execute(new EventReceiverProcessor(receiver));
			}
		} else {
			LOGGER.warn("No device event receivers configured for processor!");
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

	/**
	 * Thread that processes events being decoded from {@link IDeviceEventReceiver}
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
					IDeviceEventCreateRequest request = pendingRequests.take();
					LOGGER.debug("Device event processor thread picked up request.");
					if (request instanceof IDeviceRegistrationRequest) {
						LOGGER.debug("Processing registration request.");
						SiteWhereServer.getInstance().getInboundEventProcessorChain().onRegistrationRequest(
								(IDeviceRegistrationRequest) request);
					} else {
						throw new SiteWhereException(
								"Unknown request type decoded by inbound receiver. Type was: "
										+ request.getClass().getName());
					}
				} catch (SiteWhereException e) {
					LOGGER.error("Event receiver thread unable to decode event request.", e);
				} catch (InterruptedException e) {
					LOGGER.warn("Device event receiver thread interrupted.", e);
				} catch (Throwable e) {
					LOGGER.error("Unhandled exception in device event decoding.", e);
				}
			}
		}
	}

	/**
	 * Thread that pulls records off the receiver's queue (blocking if necessary).
	 * 
	 * @author Derek
	 */
	private class EventReceiverProcessor implements Runnable {

		/** Receiver being monitored */
		private IDeviceEventReceiver receiver;

		public EventReceiverProcessor(IDeviceEventReceiver receiver) {
			this.receiver = receiver;
		}

		@Override
		public void run() {
			LOGGER.info("Started device provisioning receiver thread.");
			while (true) {
				try {
					byte[] message = receiver.getEncodedMessages().take();
					LOGGER.debug("Device event receiver thread picked up event.");
					IDeviceEventCreateRequest request = getDeviceEventDecoder().decode(message);
					if (request != null) {
						pendingRequests.offer(request);
						LOGGER.debug("Decoded event added to processing queue.");
					}
				} catch (SiteWhereException e) {
					LOGGER.error("Event receiver thread unable to decode event request.", e);
				} catch (InterruptedException e) {
					LOGGER.warn("Device event receiver thread interrupted.", e);
				} catch (Throwable e) {
					LOGGER.error("Unhandled exception in device event decoding.", e);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		LOGGER.info("Stopped MQTT inbound event processing.");
	}

	public IDeviceEventDecoder getDeviceEventDecoder() {
		return deviceEventDecoder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.provisioning.IDeviceEventProcessor#setDeviceEventDecoder
	 * (com.sitewhere.spi.device.provisioning.IDeviceEventDecoder)
	 */
	public void setDeviceEventDecoder(IDeviceEventDecoder deviceEventDecoder) {
		this.deviceEventDecoder = deviceEventDecoder;
	}

	public List<IDeviceEventReceiver> getDeviceEventReceivers() {
		return deviceEventReceivers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.provisioning.IDeviceEventProcessor#setDeviceEventReceivers
	 * (java.util.List)
	 */
	public void setDeviceEventReceivers(List<IDeviceEventReceiver> deviceEventReceivers) {
		this.deviceEventReceivers = deviceEventReceivers;
	}
}