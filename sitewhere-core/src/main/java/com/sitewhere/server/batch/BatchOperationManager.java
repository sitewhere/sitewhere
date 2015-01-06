/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.batch;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sitewhere.SiteWhere;
import com.sitewhere.rest.model.device.request.BatchOperationUpdateRequest;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.device.BatchElementSearchCriteria;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.batch.BatchOperationStatus;
import com.sitewhere.spi.device.batch.IBatchElement;
import com.sitewhere.spi.device.batch.IBatchOperation;
import com.sitewhere.spi.device.batch.IBatchOperationManager;
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

		public BatchOperationProcessor(IBatchOperation operation) {
			this.operation = operation;
		}

		@Override
		public void run() {
			LOGGER.debug("Processing batch operation: " + operation.getToken());
			try {
				SecurityContextHolder.getContext().setAuthentication(
						SiteWhereServer.getSystemAuthentication());

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
					for (IBatchElement element : matches.getResults()) {
						switch (operation.getOperationType()) {
						case InvokeCommand: {
							processBatchCommandInvocationElement(element);
							break;
						}
						case UpdateFirmware: {
							break;
						}
						}
					}
				}
			} catch (SiteWhereException e) {
				LOGGER.error("Error processing batch operation.", e);
			}
		}

		/**
		 * Process a single element from a batch command invocation.
		 * 
		 * @param element
		 * @throws SiteWhereException
		 */
		protected void processBatchCommandInvocationElement(IBatchElement element) throws SiteWhereException {
			LOGGER.info("Processing command invocation: " + element.getHardwareId());
		}
	}
}