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
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sitewhere.SiteWhere;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.processor.IOutboundEventProcessorChain;
import com.sitewhere.spi.device.provisioning.IOutboundProcessingStrategy;

/**
 * Implementation of {@link IOutboundProcessingStrategy} that uses an
 * {@link ArrayBlockingQueue} to hold events that are submitted into the
 * {@link IOutboundEventProcessorChain}.
 * 
 * @author Derek
 */
public class BlockingQueueOutboundProcessingStrategy extends LifecycleComponent implements
		IOutboundProcessingStrategy {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(BlockingQueueOutboundProcessingStrategy.class);

	/** Maximum size of queues */
	private static final int MAX_QUEUE_SIZE = 1000;

	/** Number of threads used for event processing */
	private static final int EVENT_PROCESSOR_THREAD_COUNT = 10;

	/** Blocking queue of pending event create requests from receivers */
	private BlockingQueue<IDeviceEvent> queue = new ArrayBlockingQueue<IDeviceEvent>(MAX_QUEUE_SIZE);

	/** Thread pool for processing events */
	private ExecutorService processorPool;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		processorPool =
				Executors.newFixedThreadPool(EVENT_PROCESSOR_THREAD_COUNT, new ProcessorsThreadFactory());
		for (int i = 0; i < EVENT_PROCESSOR_THREAD_COUNT; i++) {
			processorPool.execute(new BlockingDeviceEventProcessor(queue));
		}
		LOGGER.info("Started blocking queue outbound processing strategy with queue size of "
				+ MAX_QUEUE_SIZE + " and " + EVENT_PROCESSOR_THREAD_COUNT + " threads.");
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

	/** Used for naming processor threads */
	private class ProcessorsThreadFactory implements ThreadFactory {

		/** Counts threads */
		private AtomicInteger counter = new AtomicInteger();

		public Thread newThread(Runnable r) {
			return new Thread(r, "SiteWhere BlockingQueueOutboundProcessingStrategy Processor "
					+ counter.incrementAndGet());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		if (processorPool != null) {
			processorPool.shutdownNow();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IOutboundEventProcessor#onMeasurements
	 * (com.sitewhere.spi.device.event.IDeviceMeasurements)
	 */
	@Override
	public void onMeasurements(IDeviceMeasurements measurements) throws SiteWhereException {
		queue.offer(measurements);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IOutboundEventProcessor#onLocation(com
	 * .sitewhere.spi.device.event.IDeviceLocation)
	 */
	@Override
	public void onLocation(IDeviceLocation location) throws SiteWhereException {
		queue.offer(location);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IOutboundEventProcessor#onAlert(com.sitewhere
	 * .spi.device.event.IDeviceAlert)
	 */
	@Override
	public void onAlert(IDeviceAlert alert) throws SiteWhereException {
		queue.offer(alert);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IOutboundEventProcessor#onCommandInvocation
	 * (com.sitewhere.spi.device.event.IDeviceCommandInvocation)
	 */
	@Override
	public void onCommandInvocation(IDeviceCommandInvocation invocation) throws SiteWhereException {
		queue.offer(invocation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IOutboundEventProcessor#onCommandResponse
	 * (com.sitewhere.spi.device.event.IDeviceCommandResponse)
	 */
	@Override
	public void onCommandResponse(IDeviceCommandResponse response) throws SiteWhereException {
		queue.offer(response);
	}

	/**
	 * Blocking thread that processes {@link IDeviceEvent} messages from a queue.
	 * 
	 * @author Derek
	 * 
	 * @param <T>
	 */
	private class BlockingDeviceEventProcessor implements Runnable {

		/** Queue where messages are placed */
		private BlockingQueue<IDeviceEvent> queue;

		public BlockingDeviceEventProcessor(BlockingQueue<IDeviceEvent> queue) {
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
				throw new RuntimeException("Unable to use system authentication for outbound device "
						+ " event processor thread.", e);
			}
			while (true) {
				try {
					IDeviceEvent event = queue.take();
					if (event instanceof IDeviceMeasurements) {
						SiteWhere.getServer().getOutboundEventProcessorChain().onMeasurements(
								(IDeviceMeasurements) event);
					} else if (event instanceof IDeviceLocation) {
						SiteWhere.getServer().getOutboundEventProcessorChain().onLocation(
								(IDeviceLocation) event);
					} else if (event instanceof IDeviceAlert) {
						SiteWhere.getServer().getOutboundEventProcessorChain().onAlert((IDeviceAlert) event);
					} else if (event instanceof IDeviceCommandInvocation) {
						SiteWhere.getServer().getOutboundEventProcessorChain().onCommandInvocation(
								(IDeviceCommandInvocation) event);
					} else if (event instanceof IDeviceCommandResponse) {
						SiteWhere.getServer().getOutboundEventProcessorChain().onCommandResponse(
								(IDeviceCommandResponse) event);
					} else {
						throw new RuntimeException("Unknown device event type in outbound processing: "
								+ event.getClass().getName());
					}
				} catch (SiteWhereException e) {
					LOGGER.error("Error processing outbound device event.", e);
				} catch (InterruptedException e) {
					break;
				} catch (Throwable e) {
					LOGGER.error("Unhandled exception in outbound event processing.", e);
				}
			}
		}
	}
}