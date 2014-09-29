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

	/** Number of threads used for event processing (for each event type) */
	private static final int EVENT_PROCESSOR_THREAD_COUNT = 2;

	/** Blocking queue of pending registration requests from receivers */
	private BlockingQueue<IDecodedDeviceEventRequest> registrations =
			new ArrayBlockingQueue<IDecodedDeviceEventRequest>(MAX_QUEUE_SIZE);

	/** Blocking queue of pending device command responses from receivers */
	private BlockingQueue<IDecodedDeviceEventRequest> commandResponses =
			new ArrayBlockingQueue<IDecodedDeviceEventRequest>(MAX_QUEUE_SIZE);

	/** Blocking queue of pending device measurements from receivers */
	private BlockingQueue<IDecodedDeviceEventRequest> measurements =
			new ArrayBlockingQueue<IDecodedDeviceEventRequest>(MAX_QUEUE_SIZE);

	/** Blocking queue of pending device locations from receivers */
	private BlockingQueue<IDecodedDeviceEventRequest> locations =
			new ArrayBlockingQueue<IDecodedDeviceEventRequest>(MAX_QUEUE_SIZE);

	/** Blocking queue of pending device locations from receivers */
	private BlockingQueue<IDecodedDeviceEventRequest> alerts =
			new ArrayBlockingQueue<IDecodedDeviceEventRequest>(MAX_QUEUE_SIZE);

	/** Thread pools for processors */
	private ExecutorService registrationsPool;
	private ExecutorService commandResponsesPool;
	private ExecutorService measurementsPool;
	private ExecutorService locationsPool;
	private ExecutorService alertsPool;

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
		if (registrationsPool != null) {
			registrationsPool.shutdown();
		}
		if (commandResponsesPool != null) {
			commandResponsesPool.shutdown();
		}
		if (measurementsPool != null) {
			measurementsPool.shutdown();
		}
		if (locationsPool != null) {
			locationsPool.shutdown();
		}
		if (alertsPool != null) {
			alertsPool.shutdown();
		}
	}

	/**
	 * Create thread pools for processing inbound messages.
	 * 
	 * @throws SiteWhereException
	 */
	protected void startEventProcessorThreads() throws SiteWhereException {
		registrationsPool = Executors.newFixedThreadPool(EVENT_PROCESSOR_THREAD_COUNT);
		for (int i = 0; i < EVENT_PROCESSOR_THREAD_COUNT; i++) {
			registrationsPool.execute(new RegistrationMessageProcessor(registrations));
		}
		commandResponsesPool = Executors.newFixedThreadPool(EVENT_PROCESSOR_THREAD_COUNT);
		for (int i = 0; i < EVENT_PROCESSOR_THREAD_COUNT; i++) {
			commandResponsesPool.execute(new CommandResponseMessageProcessor(commandResponses));
		}
		measurementsPool = Executors.newFixedThreadPool(EVENT_PROCESSOR_THREAD_COUNT);
		for (int i = 0; i < EVENT_PROCESSOR_THREAD_COUNT; i++) {
			measurementsPool.execute(new MeasurementsMessageProcessor(measurements));
		}
		locationsPool = Executors.newFixedThreadPool(EVENT_PROCESSOR_THREAD_COUNT);
		for (int i = 0; i < EVENT_PROCESSOR_THREAD_COUNT; i++) {
			locationsPool.execute(new LocationsMessageProcessor(locations));
		}
		alertsPool = Executors.newFixedThreadPool(EVENT_PROCESSOR_THREAD_COUNT);
		for (int i = 0; i < EVENT_PROCESSOR_THREAD_COUNT; i++) {
			alertsPool.execute(new AlertsMessageProcessor(alerts));
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
		registrations.offer(request);
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
		commandResponses.offer(request);
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
		measurements.offer(request);
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
		locations.offer(request);
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
		alerts.offer(request);
	}

	/**
	 * Processor for {@link IDeviceRegistrationRequest}.
	 * 
	 * @author Derek
	 */
	private class RegistrationMessageProcessor extends BlockingMessageProcessor {

		public RegistrationMessageProcessor(BlockingQueue<IDecodedDeviceEventRequest> queue) {
			super(queue, "registration");
		}

		@Override
		public void sendToInboundChain(IDecodedDeviceEventRequest request) throws SiteWhereException {
			SiteWhere.getServer().getInboundEventProcessorChain().onRegistrationRequest(
					request.getHardwareId(), request.getOriginator(),
					((IDeviceRegistrationRequest) request.getRequest()));
		}
	}

	/**
	 * Processor for {@link IDeviceCommandResponseCreateRequest}.
	 * 
	 * @author Derek
	 */
	private class CommandResponseMessageProcessor extends BlockingMessageProcessor {

		public CommandResponseMessageProcessor(BlockingQueue<IDecodedDeviceEventRequest> queue) {
			super(queue, "command response");
		}

		@Override
		public void sendToInboundChain(IDecodedDeviceEventRequest request) throws SiteWhereException {
			SiteWhere.getServer().getInboundEventProcessorChain().onDeviceCommandResponseRequest(
					request.getHardwareId(), request.getOriginator(),
					((IDeviceCommandResponseCreateRequest) request.getRequest()));
		}
	}

	/**
	 * Processor for {@link IDeviceMeasurementsCreateRequest}.
	 * 
	 * @author Derek
	 */
	private class MeasurementsMessageProcessor extends BlockingMessageProcessor {

		public MeasurementsMessageProcessor(BlockingQueue<IDecodedDeviceEventRequest> queue) {
			super(queue, "measurements");
		}

		@Override
		public void sendToInboundChain(IDecodedDeviceEventRequest request) throws SiteWhereException {
			SiteWhere.getServer().getInboundEventProcessorChain().onDeviceMeasurementsCreateRequest(
					request.getHardwareId(), request.getOriginator(),
					((IDeviceMeasurementsCreateRequest) request.getRequest()));
		}
	}

	/**
	 * Processor for {@link IDeviceLocationCreateRequest}.
	 * 
	 * @author Derek
	 */
	private class LocationsMessageProcessor extends BlockingMessageProcessor {

		public LocationsMessageProcessor(BlockingQueue<IDecodedDeviceEventRequest> queue) {
			super(queue, "location");
		}

		@Override
		public void sendToInboundChain(IDecodedDeviceEventRequest request) throws SiteWhereException {
			SiteWhere.getServer().getInboundEventProcessorChain().onDeviceLocationCreateRequest(
					request.getHardwareId(), request.getOriginator(),
					((IDeviceLocationCreateRequest) request.getRequest()));
		}
	}

	/**
	 * Processor for {@link IDeviceAlertCreateRequest}.
	 * 
	 * @author Derek
	 */
	private class AlertsMessageProcessor extends BlockingMessageProcessor {

		public AlertsMessageProcessor(BlockingQueue<IDecodedDeviceEventRequest> queue) {
			super(queue, "alert");
		}

		@Override
		public void sendToInboundChain(IDecodedDeviceEventRequest request) throws SiteWhereException {
			SiteWhere.getServer().getInboundEventProcessorChain().onDeviceAlertCreateRequest(
					request.getHardwareId(), request.getOriginator(),
					((IDeviceAlertCreateRequest) request.getRequest()));
		}
	}

	/**
	 * Blocking thread that processes messages of a given type from a queue.
	 * 
	 * @author Derek
	 * 
	 * @param <T>
	 */
	private abstract class BlockingMessageProcessor implements Runnable {

		/** Queue where messages are placed */
		private BlockingQueue<IDecodedDeviceEventRequest> queue;

		/** Label used for messages */
		private String label;

		public BlockingMessageProcessor(BlockingQueue<IDecodedDeviceEventRequest> queue, String label) {
			this.queue = queue;
			this.label = label;
		}

		@Override
		public void run() {
			LOGGER.info("Started " + label + " message processor thread.");

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
				throw new RuntimeException("Unable to use system authentication for inbound device " + label
						+ " event processor thread.", e);
			}
			while (true) {
				try {
					IDecodedDeviceEventRequest message = queue.take();
					LOGGER.debug("Device " + label + " event processor thread picked up request.");
					sendToInboundChain(message);
				} catch (SiteWhereException e) {
					LOGGER.error("Error processing device " + label + " event request.", e);
				} catch (InterruptedException e) {
					LOGGER.warn("Device " + label + " event processor thread interrupted.", e);
				} catch (Throwable e) {
					LOGGER.error("Unhandled exception in device " + label + " event processing.", e);
				}
			}
		}

		public abstract void sendToInboundChain(IDecodedDeviceEventRequest message) throws SiteWhereException;
	}
}