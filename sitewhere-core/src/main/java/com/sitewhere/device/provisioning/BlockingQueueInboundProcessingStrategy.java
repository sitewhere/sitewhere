/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
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

import com.sitewhere.SiteWhere;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.processor.IInboundEventProcessorChain;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;
import com.sitewhere.spi.device.provisioning.IDecodedDeviceEventRequest;
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

	/** Maximum size of queues */
	private static final int MAX_QUEUE_SIZE = 1000;

	/** Number of threads used for event processing */
	private static final int EVENT_PROCESSOR_THREAD_COUNT = 10;

	/** Blocking queue of pending event create requests from receivers */
	private BlockingQueue<IDecodedDeviceEventRequest> queue =
			new ArrayBlockingQueue<IDecodedDeviceEventRequest>(MAX_QUEUE_SIZE);

	/** Thread pool for processing events */
	private ExecutorService processorPool;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		processorPool = Executors.newFixedThreadPool(EVENT_PROCESSOR_THREAD_COUNT);
		for (int i = 0; i < EVENT_PROCESSOR_THREAD_COUNT; i++) {
			processorPool.execute(new BlockingMessageProcessor(queue));
		}
		LOGGER.info("Started blocking queue inbound processing strategy with queue size of " + MAX_QUEUE_SIZE
				+ " and " + EVENT_PROCESSOR_THREAD_COUNT + " threads.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		if (processorPool != null) {
			processorPool.shutdown();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.provisioning.IInboundProcessingStrategy#processRegistration
	 * (com.sitewhere.spi.device.provisioning.IDecodedDeviceEventRequest)
	 */
	@Override
	public void processRegistration(IDecodedDeviceEventRequest request) throws SiteWhereException {
		queue.offer(request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.provisioning.IInboundProcessingStrategy#
	 * processDeviceCommandResponse
	 * (com.sitewhere.spi.device.provisioning.IDecodedDeviceEventRequest)
	 */
	@Override
	public void processDeviceCommandResponse(IDecodedDeviceEventRequest request) throws SiteWhereException {
		queue.offer(request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.provisioning.IInboundProcessingStrategy#
	 * processDeviceMeasurements
	 * (com.sitewhere.spi.device.provisioning.IDecodedDeviceEventRequest)
	 */
	@Override
	public void processDeviceMeasurements(IDecodedDeviceEventRequest request) throws SiteWhereException {
		queue.offer(request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.provisioning.IInboundProcessingStrategy#processDeviceLocation
	 * (com.sitewhere.spi.device.provisioning.IDecodedDeviceEventRequest)
	 */
	@Override
	public void processDeviceLocation(IDecodedDeviceEventRequest request) throws SiteWhereException {
		queue.offer(request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.provisioning.IInboundProcessingStrategy#processDeviceAlert
	 * (com.sitewhere.spi.device.provisioning.IDecodedDeviceEventRequest)
	 */
	@Override
	public void processDeviceAlert(IDecodedDeviceEventRequest request) throws SiteWhereException {
		queue.offer(request);
	}

	/**
	 * Blocking thread that processes {@link IDecodedDeviceEventRequest} from a queue.
	 * 
	 * @author Derek
	 * 
	 * @param <T>
	 */
	private class BlockingMessageProcessor implements Runnable {

		/** Queue where messages are placed */
		private BlockingQueue<IDecodedDeviceEventRequest> queue;

		public BlockingMessageProcessor(BlockingQueue<IDecodedDeviceEventRequest> queue) {
			this.queue = queue;
		}

		@Override
		public void run() {
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
				throw new RuntimeException("Unable to use system authentication for inbound device "
						+ " event processor thread.", e);
			}
			while (true) {
				try {
					IDecodedDeviceEventRequest decoded = queue.take();
					if (decoded.getRequest() instanceof IDeviceRegistrationRequest) {
						SiteWhere.getServer().getInboundEventProcessorChain().onRegistrationRequest(
								decoded.getHardwareId(), decoded.getOriginator(),
								((IDeviceRegistrationRequest) decoded.getRequest()));
					} else if (decoded.getRequest() instanceof IDeviceCommandResponseCreateRequest) {
						SiteWhere.getServer().getInboundEventProcessorChain().onDeviceCommandResponseRequest(
								decoded.getHardwareId(), decoded.getOriginator(),
								((IDeviceCommandResponseCreateRequest) decoded.getRequest()));
					} else if (decoded.getRequest() instanceof IDeviceMeasurementsCreateRequest) {
						SiteWhere.getServer().getInboundEventProcessorChain().onDeviceMeasurementsCreateRequest(
								decoded.getHardwareId(), decoded.getOriginator(),
								((IDeviceMeasurementsCreateRequest) decoded.getRequest()));
					} else if (decoded.getRequest() instanceof IDeviceLocationCreateRequest) {
						SiteWhere.getServer().getInboundEventProcessorChain().onDeviceLocationCreateRequest(
								decoded.getHardwareId(), decoded.getOriginator(),
								((IDeviceLocationCreateRequest) decoded.getRequest()));
					} else if (decoded.getRequest() instanceof IDeviceAlertCreateRequest) {
						SiteWhere.getServer().getInboundEventProcessorChain().onDeviceAlertCreateRequest(
								decoded.getHardwareId(), decoded.getOriginator(),
								((IDeviceAlertCreateRequest) decoded.getRequest()));
					} else {
						throw new RuntimeException("Unknown device event type: "
								+ decoded.getRequest().getClass().getName());
					}
				} catch (SiteWhereException e) {
					LOGGER.error("Error processing inbound device event.", e);
				} catch (InterruptedException e) {
					LOGGER.warn("Inbound event processing thread interrupted.", e);
				} catch (Throwable e) {
					LOGGER.error("Unhandled exception in inbound event processing.", e);
				}
			}
		}
	}
}