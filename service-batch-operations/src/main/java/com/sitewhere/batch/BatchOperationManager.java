/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.batch.handler.BatchCommandInvocationHandler;
import com.sitewhere.batch.spi.IBatchOperationHandler;
import com.sitewhere.batch.spi.IBatchOperationManager;
import com.sitewhere.batch.spi.microservice.IBatchOperationsTenantEngine;
import com.sitewhere.rest.model.batch.request.BatchElementUpdateRequest;
import com.sitewhere.rest.model.batch.request.BatchOperationUpdateRequest;
import com.sitewhere.rest.model.search.device.BatchElementSearchCriteria;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.batch.BatchOperationStatus;
import com.sitewhere.spi.batch.ElementProcessingStatus;
import com.sitewhere.spi.batch.IBatchElement;
import com.sitewhere.spi.batch.IBatchManagement;
import com.sitewhere.spi.batch.IBatchOperation;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.device.IBatchElementSearchCriteria;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Default implementation of {@link IBatchOperationManager}. Uses multiple
 * threads to process batch operations.
 * 
 * @author Derek
 */
public class BatchOperationManager extends TenantEngineLifecycleComponent implements IBatchOperationManager {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(BatchOperationManager.class);

    /** Number of threads used for batch operation processing */
    private static final int BATCH_PROCESSOR_THREAD_COUNT = 10;

    /** Thread pool for processing events */
    private ExecutorService processorPool;

    /** Throttling delay in milliseconds */
    private long throttleDelayMs;

    /** Map of handlers by operation type */
    private Map<String, IBatchOperationHandler> handlersByOperationType = new HashMap<String, IBatchOperationHandler>();

    public BatchOperationManager() {
	super(LifecycleComponentType.BatchOperationManager);

	// TODO: Move this into a configuration element.
	getHandlersByOperationType().put(BatchOperationTypes.OPERATION_BATCH_COMMAND_INVOCATION,
		new BatchCommandInvocationHandler());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	processorPool = Executors.newFixedThreadPool(BATCH_PROCESSOR_THREAD_COUNT, new ProcessorsThreadFactory());

	// Start handlers.
	for (String key : getHandlersByOperationType().keySet()) {
	    IBatchOperationHandler handler = getHandlersByOperationType().get(key);
	    startNestedComponent(handler, monitor, true);
	}
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
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop(com.sitewhere
     * .spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	processorPool.shutdownNow();

	// Stop handlers.
	for (String key : getHandlersByOperationType().keySet()) {
	    IBatchOperationHandler handler = getHandlersByOperationType().get(key);
	    stopNestedComponent(handler, monitor);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Log getLogger() {
	return LOGGER;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.batch.IBatchOperationManager#process(com.
     * sitewhere.spi .device.batch.IBatchOperation)
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

    public IBatchManagement getBatchManagement() {
	return ((IBatchOperationsTenantEngine) getTenantEngine()).getBatchManagement();
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
		BatchOperationUpdateRequest request = new BatchOperationUpdateRequest();
		request.setProcessingStatus(BatchOperationStatus.Processing);
		request.setProcessingStartedDate(new Date());
		getBatchManagement().updateBatchOperation(operation.getId(), request);

		// Process all batch elements.
		IBatchElementSearchCriteria criteria = new BatchElementSearchCriteria(1, 0);
		ISearchResults<IBatchElement> matches = getBatchManagement().listBatchElements(operation.getId(),
			criteria);
		BatchProcessingResults result = processBatchElements(operation, matches.getResults());

		// Update operation to reflect processing results.
		request = new BatchOperationUpdateRequest();
		request.setProcessingStatus(BatchOperationStatus.FinishedSuccessfully);
		request.setProcessingEndedDate(new Date());
		if (result.getErrorCount() > 0) {
		    request.setProcessingStatus(BatchOperationStatus.FinishedWithErrors);
		}
		getBatchManagement().updateBatchOperation(operation.getId(), request);
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
	protected BatchProcessingResults processBatchElements(IBatchOperation operation, List<IBatchElement> elements)
		throws SiteWhereException {
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
		getBatchManagement().updateBatchElement(element.getBatchOperationId(), request);

		request = new BatchElementUpdateRequest();
		ElementProcessingStatus status = ElementProcessingStatus.Succeeded;
		try {
		    IBatchOperationHandler handler = getHandlersByOperationType().get(operation.getOperationType());
		    if (handler != null) {
			status = handler.process(operation, element, request);
		    } else {
			status = ElementProcessingStatus.Failed;
		    }

		    // Indicate element succeeded in processing.
		    request.setProcessingStatus(status);
		    request.setProcessedDate(new Date());
		} catch (SiteWhereException t) {
		    // Indicate element failed in processing.
		    LOGGER.error("Error processing batch invocation element.", t);
		    request.setProcessingStatus(ElementProcessingStatus.Failed);
		} finally {
		    IBatchElement updated = getBatchManagement().updateBatchElement(element.getBatchOperationId(),
			    request);
		    results.process(updated);
		}
	    }
	    return results;
	}
    }

    public Map<String, IBatchOperationHandler> getHandlersByOperationType() {
	return handlersByOperationType;
    }

    public void setHandlersByOperationType(Map<String, IBatchOperationHandler> handlersByOperationType) {
	this.handlersByOperationType = handlersByOperationType;
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

    /** Used for naming batch operation processor threads */
    private class ProcessorsThreadFactory implements ThreadFactory {

	/** Counts threads */
	private AtomicInteger counter = new AtomicInteger();

	public Thread newThread(Runnable r) {
	    return new Thread(r, "Batch Operation Processor " + counter.incrementAndGet());
	}
    }
}