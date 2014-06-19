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

import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.provisioning.IDecodedDeviceEventRequest;
import com.sitewhere.spi.device.provisioning.IDeviceEventDecoder;
import com.sitewhere.spi.device.provisioning.IInboundEventProcessor;
import com.sitewhere.spi.device.provisioning.IInboundEventReceiver;
import com.sitewhere.spi.device.provisioning.IInboundProcessingStrategy;

/**
 * Default implementation of {@link IInboundEventProcessor}.
 * 
 * @author Derek
 */
public class DefaultInboundEventProcessor implements IInboundEventProcessor {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(DefaultInboundEventProcessor.class);

	/** Device event decoder */
	private IDeviceEventDecoder deviceEventDecoder;

	/** Inbound event processing strategy */
	private IInboundProcessingStrategy inboundProcessingStrategy;

	/** List of {@link IInboundEventReceiver} that supply this processor */
	private List<IInboundEventReceiver> inboundEventReceivers = new ArrayList<IInboundEventReceiver>();

	/** Thread pool for event receivers */
	private ExecutorService receiverThreadPool;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		LOGGER.info("Starting device event processing...");
		if (getInboundProcessingStrategy() == null) {
			setInboundProcessingStrategy(SiteWhereServer.getInstance().getDeviceProvisioning().getInboundProcessingStrategy());
		}
		if ((getInboundEventReceivers() == null) || (getInboundEventReceivers().size() == 0)) {
			throw new SiteWhereException("No inbound event receivers registered for event processor.");
		}
		startEventReceiverThreads();
		LOGGER.info("Started device event processing.");
	}

	/**
	 * Create one thread per receiver and have it pull records in blocking mode.
	 * 
	 * @throws SiteWhereException
	 */
	protected void startEventReceiverThreads() throws SiteWhereException {
		if (getInboundEventReceivers().size() > 0) {
			LOGGER.info("Initializing " + getInboundEventReceivers().size() + " device event receivers...");
			receiverThreadPool = Executors.newFixedThreadPool(getInboundEventReceivers().size());
			for (IInboundEventReceiver receiver : getInboundEventReceivers()) {
				receiver.start();
				receiverThreadPool.execute(new EventReceiverProcessor(receiver));
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
		private IInboundEventReceiver receiver;

		public EventReceiverProcessor(IInboundEventReceiver receiver) {
			this.receiver = receiver;
		}

		@Override
		public void run() {
			LOGGER.info("Started device provisioning receiver thread.");
			while (true) {
				try {
					byte[] message = receiver.getEncodedMessages().take();
					LOGGER.debug("Device event receiver thread picked up event.");
					List<IDecodedDeviceEventRequest> requests = getDeviceEventDecoder().decode(message);
					if (requests != null) {
						for (IDecodedDeviceEventRequest request : requests) {
							getInboundProcessingStrategy().submit(request);
						}
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.provisioning.IInboundEventProcessor#setDeviceEventDecoder
	 * (com.sitewhere.spi.device.provisioning.IDeviceEventDecoder)
	 */
	public void setDeviceEventDecoder(IDeviceEventDecoder deviceEventDecoder) {
		this.deviceEventDecoder = deviceEventDecoder;
	}

	public IDeviceEventDecoder getDeviceEventDecoder() {
		return deviceEventDecoder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.provisioning.IInboundEventProcessor#
	 * setInboundProcessingStrategy
	 * (com.sitewhere.spi.device.provisioning.IInboundProcessingStrategy)
	 */
	public void setInboundProcessingStrategy(IInboundProcessingStrategy inboundProcessingStrategy) {
		this.inboundProcessingStrategy = inboundProcessingStrategy;
	}

	public IInboundProcessingStrategy getInboundProcessingStrategy() {
		return inboundProcessingStrategy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.provisioning.IInboundEventProcessor#setInboundEventReceivers
	 * (java.util.List)
	 */
	public void setInboundEventReceivers(List<IInboundEventReceiver> inboundEventReceivers) {
		this.inboundEventReceivers = inboundEventReceivers;
	}

	public List<IInboundEventReceiver> getInboundEventReceivers() {
		return inboundEventReceivers;
	}
}