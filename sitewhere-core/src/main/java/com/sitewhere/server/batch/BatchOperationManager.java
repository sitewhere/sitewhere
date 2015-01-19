/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.batch;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sitewhere.SiteWhere;
import com.sitewhere.rest.model.device.event.request.DeviceCommandInvocationCreateRequest;
import com.sitewhere.rest.model.device.request.BatchElementUpdateRequest;
import com.sitewhere.rest.model.device.request.BatchOperationUpdateRequest;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.device.BatchElementSearchCriteria;
import com.sitewhere.security.SitewhereAuthentication;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.common.IMetadataProvider;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.batch.BatchOperationStatus;
import com.sitewhere.spi.device.batch.ElementProcessingStatus;
import com.sitewhere.spi.device.batch.IBatchElement;
import com.sitewhere.spi.device.batch.IBatchOperation;
import com.sitewhere.spi.device.batch.IBatchOperationManager;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.CommandInitiator;
import com.sitewhere.spi.device.event.CommandTarget;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.request.IBatchCommandInvocationRequest;
import com.sitewhere.spi.device.request.IBatchOperationCreateRequest;
import com.sitewhere.spi.search.device.IBatchElementSearchCriteria;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Default implementation of {@link IBatchOperationManager}. Uses multiple threads to
 * process batch operations.
 * 
 * @author Derek
 */
public class BatchOperationManager extends LifecycleComponent implements IBatchOperationManager {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(BatchOperationManager.class);

	/** Number of threads used for batch operation processing */
	private static final int BATCH_PROCESSOR_THREAD_COUNT = 10;

	/** Thread pool for processing events */
	private ExecutorService processorPool;

	/** Throttling delay in milliseconds */
	private long throttleDelayMs;

	/** Used for naming batch operation processor threads */
	private class ProcessorsThreadFactory implements ThreadFactory {

		/** Counts threads */
		private AtomicInteger counter = new AtomicInteger();

		public Thread newThread(Runnable r) {
			return new Thread(r, "Batch Operation Processor " + counter.incrementAndGet());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		processorPool =
				Executors.newFixedThreadPool(BATCH_PROCESSOR_THREAD_COUNT, new ProcessorsThreadFactory());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.server.lifecycle.LifecycleComponent#canPause()
	 */
	public boolean canPause() throws SiteWhereException {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		processorPool.shutdownNow();
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
	 * @see
	 * com.sitewhere.spi.device.batch.IBatchOperationManager#process(com.sitewhere.spi
	 * .device.batch.IBatchOperation)
	 */
	@Override
	public void process(IBatchOperation operation) throws SiteWhereException {
		processorPool.execute(new BatchOperationProcessor(operation));
	}

	public long getThrottleDelayMs() {
		return throttleDelayMs;
	}

	public void setThrottleDelayMs(long throttleDelayMs) {
		this.throttleDelayMs = throttleDelayMs;
	}

	/**
	 * Processes a batch in a separate thread.
	 * 
	 * @author Derek
	 */
	private class BatchOperationProcessor implements Runnable {

		/** Operation being processed */
		private IBatchOperation operation;

		private SitewhereAuthentication systemUser;

		public BatchOperationProcessor(IBatchOperation operation) {
			this.operation = operation;
		}

		@Override
		public void run() {
			LOGGER.debug("Processing batch operation: " + operation.getToken());
			try {
				systemUser = SiteWhereServer.getSystemAuthentication();
				SecurityContextHolder.getContext().setAuthentication(systemUser);

				BatchOperationUpdateRequest request = new BatchOperationUpdateRequest();
				request.setProcessingStatus(BatchOperationStatus.Processing);
				request.setProcessingStartedDate(new Date());
				SiteWhere.getServer().getDeviceManagement().updateBatchOperation(operation.getToken(),
						request);

				// Process all batch elements.
				IBatchElementSearchCriteria criteria = new BatchElementSearchCriteria(1, 0);
				SearchResults<IBatchElement> matches =
						SiteWhere.getServer().getDeviceManagement().listBatchElements(operation.getToken(),
								criteria);
				BatchProcessingResults result = processBatchElements(operation, matches.getResults());

				// Update operation to reflect processing results.
				request = new BatchOperationUpdateRequest();
				request.setProcessingStatus(BatchOperationStatus.FinishedSuccessfully);
				request.setProcessingEndedDate(new Date());
				if (result.getErrorCount() > 0) {
					request.setProcessingStatus(BatchOperationStatus.FinishedWithErrors);
				}
				SiteWhere.getServer().getDeviceManagement().updateBatchOperation(operation.getToken(),
						request);
			} catch (SiteWhereException e) {
				LOGGER.error("Error processing batch operation.", e);
			}
		}

		/**
		 * Handle case where batch operation manager has been paused.
		 */
		protected void handlePauseAndThrottle() {
			while (getLifecycleStatus() == LifecycleStatus.Paused) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
			if (getThrottleDelayMs() > 0) {
				try {
					Thread.sleep(getThrottleDelayMs());
				} catch (InterruptedException e) {
					LOGGER.warn("Throttle timer interrupted.");
				}
			}
		}

		/**
		 * Processes a page of batch elements.
		 * 
		 * @param operation
		 * @param elements
		 * @return
		 * @throws SiteWhereException
		 */
		protected BatchProcessingResults processBatchElements(IBatchOperation operation,
				List<IBatchElement> elements) throws SiteWhereException {
			BatchProcessingResults results = new BatchProcessingResults();
			for (IBatchElement element : elements) {
				// Check whether manager has been paused.
				handlePauseAndThrottle();

				// Only process unprocessed elements.
				if (element.getProcessingStatus() != ElementProcessingStatus.Unprocessed) {
					continue;
				}

				// Indicate element is being processed.
				BatchElementUpdateRequest request = new BatchElementUpdateRequest();
				request.setProcessingStatus(ElementProcessingStatus.Processing);
				SiteWhere.getServer().getDeviceManagement().updateBatchElement(
						element.getBatchOperationToken(), element.getIndex(), request);

				request = new BatchElementUpdateRequest();
				try {
					switch (operation.getOperationType()) {
					case InvokeCommand: {
						processBatchCommandInvocationElement(operation, element, request);
						break;
					}
					case UpdateFirmware: {
						break;
					}
					}
					// Indicate element succeeded in processing.
					request.setProcessingStatus(ElementProcessingStatus.Succeeded);
					request.setProcessedDate(new Date());
				} catch (SiteWhereException t) {
					// Indicate element failed in processing.
					request.setProcessingStatus(ElementProcessingStatus.Failed);
				} finally {
					IBatchElement updated =
							SiteWhere.getServer().getDeviceManagement().updateBatchElement(
									element.getBatchOperationToken(), element.getIndex(), request);
					results.process(updated);
				}
			}
			return results;
		}

		/**
		 * Process a single element from a batch command invocation.
		 * 
		 * @param operation
		 * @param element
		 * @param updated
		 * @throws SiteWhereException
		 */
		protected void processBatchCommandInvocationElement(IBatchOperation operation, IBatchElement element,
				IMetadataProvider updated) throws SiteWhereException {
			LOGGER.info("Processing command invocation: " + element.getHardwareId());

			// Find information about the command to be executed.
			String commandToken =
					operation.getParameters().get(IBatchCommandInvocationRequest.PARAM_COMMAND_TOKEN);
			if (commandToken == null) {
				throw new SiteWhereException("Command token not found in batch command invocation request.");
			}
			IDeviceCommand command =
					SiteWhere.getServer().getDeviceManagement().getDeviceCommandByToken(commandToken);
			if (command == null) {
				throw new SiteWhereException("Invalid command token referenced by batch command invocation.");
			}

			// Find information about the device to execute the command against.
			IDevice device =
					SiteWhere.getServer().getDeviceManagement().getDeviceByHardwareId(element.getHardwareId());
			if (device == null) {
				throw new SiteWhereException("Invalid device hardware id in command invocation.");
			}

			// Find the current assignment information for the device.
			IDeviceAssignment assignment =
					SiteWhere.getServer().getDeviceManagement().getCurrentDeviceAssignment(device);
			if (assignment == null) {
				throw new SiteWhereException("Device is not currently assigned. Command can not be invoked.");
			}

			// Create the request.
			DeviceCommandInvocationCreateRequest request = new DeviceCommandInvocationCreateRequest();
			request.setCommandToken(commandToken);
			request.setInitiator(CommandInitiator.BatchOperation);
			request.setInitiatorId(systemUser.getName());
			request.setTarget(CommandTarget.Assignment);
			request.setTargetId(assignment.getToken());
			request.setParameterValues(operation.getMetadata());
			request.addOrReplaceMetadata(IBatchOperationCreateRequest.META_BATCH_OPERATION_ID,
					operation.getToken());

			// Invoke the command.
			IDeviceCommandInvocation invocation =
					SiteWhere.getServer().getDeviceManagement().addDeviceCommandInvocation(
							assignment.getToken(), command, request);
			updated.addOrReplaceMetadata(IBatchCommandInvocationRequest.META_INVOCATION_EVENT_ID,
					invocation.getId());
		}
	}

	/**
	 * Used to track status of processed elements.
	 * 
	 * @author Derek
	 */
	private class BatchProcessingResults {

		// Count of successfully processed elements.
		private AtomicLong success = new AtomicLong();

		// Count of elements that failed to process.
		private AtomicLong failed = new AtomicLong();

		public void process(IBatchElement element) {
			switch (element.getProcessingStatus()) {
			case Succeeded: {
				success.incrementAndGet();
				break;
			}
			case Failed: {
				failed.incrementAndGet();
				break;
			}
			case Processing:
			case Unprocessed: {
				LOGGER.warn("Batch element was not in an expected state: " + element.getProcessingStatus());
				break;
			}
			}
		}

		public long getErrorCount() {
			return failed.get();
		}
	}
}