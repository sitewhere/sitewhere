/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.IInboundEventReceiver;
import com.sitewhere.spi.device.communication.IInboundEventSource;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Abstract base class for event receivers that poll an external source at a given
 * interval.
 * 
 * @author Derek
 *
 * @param <T>
 */
public abstract class PollingInboundEventReceiver<T> extends LifecycleComponent
		implements IInboundEventReceiver<T> {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(PollingInboundEventReceiver.class);

	/** Default polling interval in milliseconds */
	private static final int DEFAULT_POLL_INTERVAL_MS = 10000;

	/** Parent event source */
	private IInboundEventSource<T> eventSource;

	/** Polling interval in milliseconds */
	private int pollIntervalMs = DEFAULT_POLL_INTERVAL_MS;

	/** Handles poller threading */
	private ExecutorService executor;

	public PollingInboundEventReceiver() {
		super(LifecycleComponentType.InboundEventReceiver);
	}

	/**
	 * Implemented in subclass to do work when polling occurs.
	 * 
	 * @throws SiteWhereException
	 */
	public abstract void doPoll() throws SiteWhereException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		this.executor = Executors.newSingleThreadExecutor();
		executor.submit(new Poller());
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
	}

	/**
	 * Class that excutes polling code at a given interval.
	 * 
	 * @author Derek
	 */
	public class Poller implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					doPoll();
				} catch (SiteWhereException e) {
					LOGGER.error("Error executing polling logic.", e);
				}
				try {
					Thread.sleep(getPollIntervalMs());
				} catch (InterruptedException e) {
					LOGGER.warn("Poller thread interrupted.");
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
	 * @see com.sitewhere.spi.device.communication.IInboundEventReceiver#getEventSource()
	 */
	public IInboundEventSource<T> getEventSource() {
		return eventSource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.communication.IInboundEventReceiver#setEventSource(com.
	 * sitewhere.spi.device.communication.IInboundEventSource)
	 */
	public void setEventSource(IInboundEventSource<T> eventSource) {
		this.eventSource = eventSource;
	}

	public int getPollIntervalMs() {
		return pollIntervalMs;
	}

	public void setPollIntervalMs(int pollIntervalMs) {
		this.pollIntervalMs = pollIntervalMs;
	}
}