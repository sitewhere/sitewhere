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
import com.sitewhere.spi.device.request.IBatchCommandInvocationRequest;
import com.sitewhere.spi.search.device.IBatchElementSearchCriteria;

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

				int pageSize = 100;
				int pageNumber = 0;
				while (true) {
					pageNumber++;
					IBatchElementSearchCriteria criteria =
							new BatchElementSearchCriteria(pageNumber, pageSize);
					SearchResults<IBatchElement> matches =
							SiteWhere.getServer().getDeviceManagement().listBatchElements(
									operation.getToken(), criteria);
					if (matches.getNumResults() == 0) {
						break;
					}
					processBatchElements(operation, matches.getResults());
				}
			} catch (SiteWhereException e) {
				LOGGER.error("Error processing batch operation.", e);
			}
		}

		/**
		 * Processes a page of batch elements.
		 * 
		 * @param operation
		 * @param elements
		 * @throws SiteWhereException
		 */
		protected void processBatchElements(IBatchOperation operation, List<IBatchElement> elements)
				throws SiteWhereException {
			for (IBatchElement element : elements) {
				// Only process unprocessed elements.
				if (element.getProcessingStatus() != ElementProcessingStatus.Unprocessed) {
					continue;
				}

				// Indicate element is being processed.
				BatchElementUpdateRequest request = new BatchElementUpdateRequest();
				request.setProcessingStatus(ElementProcessingStatus.Processing);
				SiteWhere.getServer().getDeviceManagement().updateBatchElement(
						element.getBatchOperationToken(), element.getIndex(), request);

				try {
					switch (operation.getOperationType()) {
					case InvokeCommand: {
						processBatchCommandInvocationElement(operation, element);
						break;
					}
					case UpdateFirmware: {
						break;
					}
					}
					// Indicate element succeeded in processing.
					request = new BatchElementUpdateRequest();
					request.setProcessingStatus(ElementProcessingStatus.Succeeded);
					SiteWhere.getServer().getDeviceManagement().updateBatchElement(
							element.getBatchOperationToken(), element.getIndex(), request);
				} catch (SiteWhereException t) {
					// Indicate element failed in processing.
					request = new BatchElementUpdateRequest();
					request.setProcessingStatus(ElementProcessingStatus.Failed);
					SiteWhere.getServer().getDeviceManagement().updateBatchElement(
							element.getBatchOperationToken(), element.getIndex(), request);
				}
			}
		}

		/**
		 * Process a single element from a batch command invocation.
		 * 
		 * @param operation
		 * @param element
		 * @throws SiteWhereException
		 */
		protected void processBatchCommandInvocationElement(IBatchOperation operation, IBatchElement element)
				throws SiteWhereException {
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

			// Invoke the command.
			SiteWhere.getServer().getDeviceManagement().addDeviceCommandInvocation(assignment.getToken(),
					command, request);
		}
	}
}