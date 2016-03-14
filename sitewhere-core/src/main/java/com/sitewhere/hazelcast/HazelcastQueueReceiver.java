/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hazelcast;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.hazelcast.core.IQueue;
import com.sitewhere.rest.model.device.communication.DecodedDeviceRequest;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.IInboundEventReceiver;
import com.sitewhere.spi.device.communication.IInboundEventSource;
import com.sitewhere.spi.server.hazelcast.ISiteWhereHazelcast;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.server.tenant.ITenantHazelcastAware;
import com.sitewhere.spi.server.tenant.ITenantHazelcastConfiguration;

/**
 * Implementation of {@link IInboundEventReceiver} that reads events from a Hazelcast
 * queue and pushes them into the processing pipeline.
 * 
 * @author Derek
 */
public class HazelcastQueueReceiver extends LifecycleComponent
		implements IInboundEventReceiver<DecodedDeviceRequest<?>>, ITenantHazelcastAware {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(HazelcastQueueReceiver.class);

	/** Parent event source */
	private IInboundEventSource<DecodedDeviceRequest<?>> eventSource;

	/** Queue of events to be processed */
	private IQueue<DecodedDeviceRequest<?>> eventQueue;

	/** Injected Hazelcast configuration */
	private ITenantHazelcastConfiguration hazelcastConfiguration;

	/** Used to queue processing in a separate thread */
	private ExecutorService executor;

	/** Name of Hazelcast queue to listen on */
	private String queueName = ISiteWhereHazelcast.QUEUE_ALL_EVENTS;

	public HazelcastQueueReceiver() {
		super(LifecycleComponentType.InboundEventReceiver);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		if (getHazelcastConfiguration() == null) {
			throw new SiteWhereException("No Hazelcast configuration provided.");
		}
		this.eventQueue = getHazelcastConfiguration().getHazelcastInstance().getQueue(getQueueName());
		LOGGER.info("Receiver listening for events on Hazelcast queue: " + getQueueName());
		this.executor = Executors.newSingleThreadExecutor(new ProcessorsThreadFactory());
		executor.submit(new HazelcastQueueProcessor());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		if (executor != null) {
			executor.shutdownNow();
		}
		this.executor = null;
	}

	/**
	 * Handles Hazelcast queue processing in a separate thread.
	 * 
	 * @author Derek
	 */
	private class HazelcastQueueProcessor implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					DecodedDeviceRequest<?> payload = getEventQueue().take();
					onEventPayloadReceived(payload, null);
					LOGGER.debug("Processed event from " + payload.getHardwareId()
							+ " from Hazelcast event queue.");
				} catch (InterruptedException e) {
					LOGGER.warn("Hazelcast queue processor interrupted.");
					return;
				}
			}
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
		return "Hazelcast";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.communication.IInboundEventReceiver#onEventPayloadReceived
	 * (java.lang.Object, java.util.Map)
	 */
	@Override
	public void onEventPayloadReceived(DecodedDeviceRequest<?> payload, Map<String, String> metadata) {
		getEventSource().onEncodedEventReceived(this, payload, metadata);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.communication.IInboundEventReceiver#setEventSource(com
	 * .sitewhere.spi.device.communication.IInboundEventSource)
	 */
	@Override
	public void setEventSource(IInboundEventSource<DecodedDeviceRequest<?>> source) {
		this.eventSource = source;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.communication.IInboundEventReceiver#getEventSource()
	 */
	@Override
	public IInboundEventSource<DecodedDeviceRequest<?>> getEventSource() {
		return eventSource;
	}

	/** Used for naming processor threads */
	private class ProcessorsThreadFactory implements ThreadFactory {

		/** Counts threads */
		private AtomicInteger counter = new AtomicInteger();

		public Thread newThread(Runnable r) {
			return new Thread(r, "SiteWhere Hazelcast(" + getEventSource().getSourceId() + ") Receiver "
					+ counter.incrementAndGet());
		}
	}

	public IQueue<DecodedDeviceRequest<?>> getEventQueue() {
		return eventQueue;
	}

	public void setEventQueue(IQueue<DecodedDeviceRequest<?>> eventQueue) {
		this.eventQueue = eventQueue;
	}

	public ITenantHazelcastConfiguration getHazelcastConfiguration() {
		return hazelcastConfiguration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.server.tenant.ITenantHazelcastAware#setHazelcastConfiguration(com
	 * .sitewhere.spi.server.tenant.ITenantHazelcastConfiguration)
	 */
	public void setHazelcastConfiguration(ITenantHazelcastConfiguration hazelcastConfiguration) {
		this.hazelcastConfiguration = hazelcastConfiguration;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
}