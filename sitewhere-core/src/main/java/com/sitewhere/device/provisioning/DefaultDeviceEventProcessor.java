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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.request.IDeviceEventCreateRequest;
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

	/** Device event decoder */
	private IDeviceEventDecoder deviceEventDecoder;

	/** List of {@link IDeviceEventReceiver} that supply this processor */
	private List<IDeviceEventReceiver> deviceEventReceivers = new ArrayList<IDeviceEventReceiver>();

	/** Used to execute Solr indexing in a separate thread */
	private ExecutorService pool;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		LOGGER.info("Starting device event processing...");
		startEventReceiverThreads();
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
			pool = Executors.newFixedThreadPool(getDeviceEventReceivers().size());
			for (IDeviceEventReceiver receiver : getDeviceEventReceivers()) {
				receiver.start();
				pool.execute(new EventReceiverProcessor(receiver));
			}
		} else {
			LOGGER.warn("No device event receivers configured for processor!");
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
			LOGGER.info("Started device provisioning processor thread.");
			while (true) {
				try {
					byte[] message = receiver.getEncodedMessages().take();
					try {
						LOGGER.debug("Device event processor thread picked up event.");
						IDeviceEventCreateRequest request = getDeviceEventDecoder().decode(message);
						ObjectMapper mapper = new ObjectMapper();
						String output = mapper.writeValueAsString(request);
						LOGGER.info(output);
					} catch (SiteWhereException e) {
						LOGGER.error("Unable to decode inbound device event.", e);
					} catch (Throwable e) {
						LOGGER.error("Unhandled exception in device event decoding.", e);
					}
				} catch (SiteWhereException e) {
					LOGGER.warn("Device event processor interrupted.", e);
				} catch (InterruptedException e) {
					LOGGER.error(e);
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