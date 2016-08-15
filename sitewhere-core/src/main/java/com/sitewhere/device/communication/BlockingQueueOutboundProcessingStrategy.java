/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sitewhere.SiteWhere;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.IOutboundProcessingStrategy;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.processor.IOutboundEventProcessorChain;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link IOutboundProcessingStrategy} that uses an
 * {@link ArrayBlockingQueue} to hold events that are submitted into the
 * {@link IOutboundEventProcessorChain}.
 * 
 * @author Derek
 */
public class BlockingQueueOutboundProcessingStrategy extends TenantLifecycleComponent
		implements IOutboundProcessingStrategy {

	/** Static logger instance */
	private static Logger LOGGER = LogManager.getLogger();

	/** Maximum size of queues */
	private static final int MAX_QUEUE_SIZE = 1000;

	/** Number of threads used for event processing */
	private static final int EVENT_PROCESSOR_THREAD_COUNT = 10;

	/** Number of events added before queue blocks */
	private int maxQueueSize = MAX_QUEUE_SIZE;

	/** Number of thread processing queue */
	private int eventProcessorThreadCount = EVENT_PROCESSOR_THREAD_COUNT;

	/** Blocking queue of events waiting for outbound processing */
	private BlockingQueue<IDeviceEvent> queue;

	/** Thread pool for processing events */
	private ExecutorService processorPool;

	public BlockingQueueOutboundProcessingStrategy() {
		super(LifecycleComponentType.OutboundProcessingStrategy);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		this.queue = new ArrayBlockingQueue<IDeviceEvent>(getMaxQueueSize());
		processorPool = Executors.newFixedThreadPool(getEventProcessorThreadCount(), new ProcessorsThreadFactory());
		for (int i = 0; i < getEventProcessorThreadCount(); i++) {
			processorPool.execute(new BlockingDeviceEventProcessor(queue));
		}
		LOGGER.info("Started blocking queue outbound processing strategy with queue size of " + getMaxQueueSize()
				+ " and " + getEventProcessorThreadCount() + " threads.");
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
			return new Thread(r,
					"SiteWhere BlockingQueueOutboundProcessingStrategy Processor " + counter.incrementAndGet());
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
	 * @see com.sitewhere.spi.device.event.processor.IOutboundEventProcessor#
	 * onMeasurements (com.sitewhere.spi.device.event.IDeviceMeasurements)
	 */
	@Override
	public void onMeasurements(IDeviceMeasurements measurements) throws SiteWhereException {
		queue.offer(measurements);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.processor.IOutboundEventProcessor#
	 * onLocation(com .sitewhere.spi.device.event.IDeviceLocation)
	 */
	@Override
	public void onLocation(IDeviceLocation location) throws SiteWhereException {
		queue.offer(location);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IOutboundEventProcessor#onAlert(
	 * com. sitewhere .spi.device.event.IDeviceAlert)
	 */
	@Override
	public void onAlert(IDeviceAlert alert) throws SiteWhereException {
		queue.offer(alert);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.processor.IOutboundEventProcessor#
	 * onCommandInvocation
	 * (com.sitewhere.spi.device.event.IDeviceCommandInvocation)
	 */
	@Override
	public void onCommandInvocation(IDeviceCommandInvocation invocation) throws SiteWhereException {
		queue.offer(invocation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.processor.IOutboundEventProcessor#
	 * onCommandResponse (com.sitewhere.spi.device.event.IDeviceCommandResponse)
	 */
	@Override
	public void onCommandResponse(IDeviceCommandResponse response) throws SiteWhereException {
		queue.offer(response);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.processor.IOutboundEventProcessor#
	 * onStateChange(com. sitewhere.spi.device.event.IDeviceStateChange)
	 */
	@Override
	public void onStateChange(IDeviceStateChange state) throws SiteWhereException {
		queue.offer(state);
	}

	public int getMaxQueueSize() {
		return maxQueueSize;
	}

	public void setMaxQueueSize(int maxQueueSize) {
		this.maxQueueSize = maxQueueSize;
	}

	public int getEventProcessorThreadCount() {
		return eventProcessorThreadCount;
	}

	public void setEventProcessorThreadCount(int eventProcessorThreadCount) {
		this.eventProcessorThreadCount = eventProcessorThreadCount;
	}

	/**
	 * Blocking thread that processes {@link IDeviceEvent} messages from a
	 * queue.
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
			// Event creation APIs expect an authenticated user in order to
			// check
			// permissions and log who creates events. When called in this
			// context, the
			// authenticated user will always be 'system'.
			//
			// TODO: Alternatively, we may want the client to authenticate on
			// registration
			// and pass a token on each request.
			try {
				SecurityContextHolder.getContext().setAuthentication(SiteWhereServer.getSystemAuthentication());
			} catch (SiteWhereException e) {
				throw new RuntimeException(
						"Unable to use system authentication for outbound device " + " event processor thread.", e);
			}
			while (true) {
				try {
					IDeviceEvent event = queue.take();
					switch (event.getEventType()) {
					case Measurements: {
						getOutboundProcessorChain().onMeasurements((IDeviceMeasurements) event);
						break;
					}
					case Location: {
						getOutboundProcessorChain().onLocation((IDeviceLocation) event);
						break;
					}
					case Alert: {
						getOutboundProcessorChain().onAlert((IDeviceAlert) event);
						break;
					}
					case CommandInvocation: {
						getOutboundProcessorChain().onCommandInvocation((IDeviceCommandInvocation) event);
						break;
					}
					case CommandResponse: {
						getOutboundProcessorChain().onCommandResponse((IDeviceCommandResponse) event);
						break;
					}
					case StateChange: {
						getOutboundProcessorChain().onStateChange((IDeviceStateChange) event);
						break;
					}
					default: {
						throw new RuntimeException(
								"Unknown device event type in outbound processing: " + event.getClass().getName());
					}
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

		/**
		 * Get the outbound processing chain implementation for this tenant.
		 * 
		 * @return
		 * @throws SiteWhereException
		 */
		protected IOutboundEventProcessorChain getOutboundProcessorChain() throws SiteWhereException {
			return SiteWhere.getServer().getEventProcessing(getTenant()).getOutboundEventProcessorChain();
		}
	}
}