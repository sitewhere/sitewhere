/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

import com.sitewhere.batch.handler.BatchCommandInvocationHandler;
import com.sitewhere.batch.kafka.UnprocessedBatchElementsConsumer;
import com.sitewhere.batch.kafka.UnprocessedBatchOperationsConsumer;
import com.sitewhere.batch.spi.IBatchOperationHandler;
import com.sitewhere.batch.spi.IBatchOperationManager;
import com.sitewhere.batch.spi.kafka.IFailedBatchElementsProducer;
import com.sitewhere.batch.spi.kafka.IUnprocessedBatchElementsConsumer;
import com.sitewhere.batch.spi.kafka.IUnprocessedBatchElementsProducer;
import com.sitewhere.batch.spi.kafka.IUnprocessedBatchOperationsConsumer;
import com.sitewhere.batch.spi.kafka.IUnprocessedBatchOperationsProducer;
import com.sitewhere.batch.spi.microservice.IBatchOperationsTenantEngine;
import com.sitewhere.grpc.batch.BatchModelConverter;
import com.sitewhere.grpc.batch.BatchModelMarshaler;
import com.sitewhere.microservice.api.batch.IBatchManagement;
import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.lifecycle.SimpleLifecycleStep;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.microservice.security.SystemUserRunnable;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.rest.model.batch.kafka.UnprocessedBatchElement;
import com.sitewhere.rest.model.batch.kafka.UnprocessedBatchOperation;
import com.sitewhere.rest.model.batch.request.BatchElementCreateRequest;
import com.sitewhere.rest.model.batch.request.BatchOperationUpdateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.batch.BatchOperationStatus;
import com.sitewhere.spi.batch.ElementProcessingStatus;
import com.sitewhere.spi.batch.IBatchElement;
import com.sitewhere.spi.batch.IBatchOperation;
import com.sitewhere.spi.batch.kafka.IUnprocessedBatchElement;
import com.sitewhere.spi.batch.kafka.IUnprocessedBatchOperation;
import com.sitewhere.spi.batch.request.IBatchElementCreateRequest;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.microservice.lifecycle.LifecycleStatus;

/**
 * Default implementation of {@link IBatchOperationManager}. Uses multiple
 * threads to process batch operations.
 */
public class BatchOperationManager extends TenantEngineLifecycleComponent implements IBatchOperationManager {

    /** Number of threads used for batch operation processing */
    private static final int BATCH_PROCESSOR_THREAD_COUNT = 10;

    /** Thread pool for processing events */
    private ExecutorService processorPool;

    /** Throttling delay in milliseconds */
    private long throttleDelayMs;

    /** Map of handlers by operation type */
    private Map<String, IBatchOperationHandler> handlersByOperationType = new HashMap<String, IBatchOperationHandler>();

    /** Kafka consumer for unprocessed batch operations */
    private IUnprocessedBatchOperationsConsumer unprocessedBatchOperationsConsumer;

    /** Kafka consumer for unprocessed batch elements */
    private IUnprocessedBatchElementsConsumer unprocessedBatchElementsConsumer;

    public BatchOperationManager() {
	super(LifecycleComponentType.BatchOperationManager);

	// TODO: Move this into a configuration element.
	getHandlersByOperationType().put(BatchOperationTypes.OPERATION_BATCH_COMMAND_INVOCATION,
		new BatchCommandInvocationHandler());
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	this.unprocessedBatchOperationsConsumer = new UnprocessedBatchOperationsConsumer();
	this.unprocessedBatchElementsConsumer = new UnprocessedBatchElementsConsumer();

	// Create step that will initialize components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getComponentName());

	// Initialize unprocessed batch operations consumer.
	init.addInitializeStep(this, getUnprocessedBatchOperationsConsumer(), true);

	// Initialize unprocessed batch elements consumer.
	init.addInitializeStep(this, getUnprocessedBatchElementsConsumer(), true);

	// Initialize handlers.
	init.addStep(new SimpleLifecycleStep("Initialize Handlers") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		for (String key : getHandlersByOperationType().keySet()) {
		    IBatchOperationHandler handler = getHandlersByOperationType().get(key);
		    initializeNestedComponent(handler, monitor, true);
		}
	    }
	});

	// Execute initialization steps.
	init.execute(monitor);
    }

    /*
     * @see
     * com.sitewhere.microservice.lifecycle.LifecycleComponent#start(com.sitewhere.
     * spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (getProcessorPool() != null) {
	    getProcessorPool().shutdownNow();
	}
	processorPool = Executors.newFixedThreadPool(BATCH_PROCESSOR_THREAD_COUNT, new ProcessorsThreadFactory());

	// Create step that will start components.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getComponentName());

	// Start unprocessed batch operations consumer.
	start.addStartStep(this, getUnprocessedBatchOperationsConsumer(), true);

	// Start unprocessed batch elements consumer.
	start.addStartStep(this, getUnprocessedBatchElementsConsumer(), true);

	// Start handlers.
	start.addStep(new SimpleLifecycleStep("Start Handlers") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		for (String key : getHandlersByOperationType().keySet()) {
		    IBatchOperationHandler handler = getHandlersByOperationType().get(key);
		    startNestedComponent(handler, monitor, true);
		}
	    }
	});

	// Execute startup steps.
	start.execute(monitor);
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
     * @see
     * com.sitewhere.microservice.lifecycle.LifecycleComponent#stop(com.sitewhere.
     * spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (getProcessorPool() != null) {
	    getProcessorPool().shutdownNow();
	}

	// Create step that will stop components.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getComponentName());

	// Stop unprocessed batch elements consumer.
	stop.addStopStep(this, getUnprocessedBatchElementsConsumer());

	// Stop unprocessed batch operations consumer.
	stop.addStopStep(this, getUnprocessedBatchOperationsConsumer());

	// Execute shutdown steps.
	stop.execute(monitor);

	// Stop handlers.
	for (String key : getHandlersByOperationType().keySet()) {
	    IBatchOperationHandler handler = getHandlersByOperationType().get(key);
	    stopNestedComponent(handler, monitor);
	}
    }

    /*
     * @see
     * com.sitewhere.batch.spi.IBatchOperationManager#addUnprocessedBatchOperation(
     * com.sitewhere.spi.batch.IBatchOperation, java.util.List)
     */
    @Override
    public void addUnprocessedBatchOperation(IBatchOperation operation, List<String> deviceTokens)
	    throws SiteWhereException {
	getProcessorPool().execute(new BatchOperationCreator(operation, deviceTokens));
    }

    /*
     * @see
     * com.sitewhere.batch.spi.IBatchOperationManager#initializeBatchOperation(com.
     * sitewhere.spi.microservice.kafka.payload.IUnprocessedBatchOperation)
     */
    @Override
    public void initializeBatchOperation(IUnprocessedBatchOperation operation) throws SiteWhereException {
	getProcessorPool().execute(new BatchOperationInitializer(operation));
    }

    /*
     * @see com.sitewhere.batch.spi.IBatchOperationManager#processBatchElement(com.
     * sitewhere.spi.batch.kafka.IUnprocessedBatchElement)
     */
    @Override
    public void processBatchElement(IUnprocessedBatchElement element) throws SiteWhereException {
	getProcessorPool().execute(new BatchElementProcessor(element));
    }

    /*
     * @see com.sitewhere.batch.spi.IBatchOperationManager#
     * getUnprocessedBatchOperationsConsumer()
     */
    @Override
    public IUnprocessedBatchOperationsConsumer getUnprocessedBatchOperationsConsumer() {
	return unprocessedBatchOperationsConsumer;
    }

    /*
     * @see com.sitewhere.batch.spi.IBatchOperationManager#
     * getUnprocessedBatchElementsConsumer()
     */
    @Override
    public IUnprocessedBatchElementsConsumer getUnprocessedBatchElementsConsumer() {
	return unprocessedBatchElementsConsumer;
    }

    public long getThrottleDelayMs() {
	return throttleDelayMs;
    }

    public void setThrottleDelayMs(long throttleDelayMs) {
	this.throttleDelayMs = throttleDelayMs;
    }

    /**
     * Creates an unprocessed batch operation in a separate thread.
     */
    private class BatchOperationCreator implements Runnable {

	/** Batch operation */
	private IBatchOperation batchOperation;

	/** List of device tokens for operation */
	private List<String> deviceTokens;

	public BatchOperationCreator(IBatchOperation batchOperation, List<String> deviceTokens) {
	    this.batchOperation = batchOperation;
	    this.deviceTokens = deviceTokens;
	}

	/*
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
	    getLogger().debug("Received new batch operation: " + getBatchOperation().getToken());
	    // Create payload to carry unprocessed batch operation information.
	    UnprocessedBatchOperation unprocessed = new UnprocessedBatchOperation();
	    unprocessed.setBatchOperation(getBatchOperation());
	    unprocessed.setDeviceTokens(getDeviceTokens());

	    getLogger().info("Submitting batch operation for processing. " + getBatchOperation().getId().toString());
	    try {
		getUnprocessedBatchOperationsProducer().send(getBatchOperation().getToken(),
			BatchModelMarshaler.buildUnprocessedBatchOperationPayloadMessage(
				BatchModelConverter.asGrpcUnprocessedBatchOperation(unprocessed)));
	    } catch (SiteWhereException e) {
		getLogger().error("Unable to send unprocessed batch operation.", e);
	    }
	}

	protected IBatchOperation getBatchOperation() {
	    return batchOperation;
	}

	protected List<String> getDeviceTokens() {
	    return deviceTokens;
	}
    }

    /**
     * Initializes a batch operation in a separate thread.
     */
    private class BatchOperationInitializer extends SystemUserRunnable {

	/** Operation being processed */
	private IUnprocessedBatchOperation unprocessed;

	public BatchOperationInitializer(IUnprocessedBatchOperation unprocessed) {
	    super(BatchOperationManager.this);
	    this.unprocessed = unprocessed;
	}

	@Override
	public void runAsSystemUser() throws SiteWhereException {
	    getLogger()
		    .info("Initializing batch operation: " + getUnprocessed().getBatchOperation().getId().toString());
	    try {
		BatchOperationUpdateRequest request = new BatchOperationUpdateRequest();
		request.setProcessingStatus(BatchOperationStatus.Initializing);
		request.setProcessingStartedDate(new Date());
		getBatchManagement().updateBatchOperation(getUnprocessed().getBatchOperation().getId(), request);

		int errorCount = 0;
		for (String deviceToken : getUnprocessed().getDeviceTokens()) {
		    BatchElementCreateRequest element = new BatchElementCreateRequest();
		    element.setDeviceToken(deviceToken);
		    element.setProcessingStatus(ElementProcessingStatus.Unprocessed);
		    element.setProcessedDate(null);
		    try {
			IBatchElement created = getBatchManagement()
				.createBatchElement(getUnprocessed().getBatchOperation().getId(), element);
			sendUnprocessedBatchElement(created);
		    } catch (SiteWhereException e) {
			getLogger().error("Unable to create batch element.", e);
			sendFailedBatchElementRequest(element);
			errorCount++;
		    }

		    // Potentially pause or throttle batch element processing.
		    handlePauseAndThrottle();
		}

		// Update operation to reflect processing results.
		request = new BatchOperationUpdateRequest();
		request.setProcessingStatus(BatchOperationStatus.InitializedSuccessfully);
		request.setProcessingEndedDate(new Date());
		if (errorCount > 0) {
		    request.setProcessingStatus(BatchOperationStatus.InitializedWithErrors);
		}
		getBatchManagement().updateBatchOperation(getUnprocessed().getBatchOperation().getId(), request);
	    } catch (SiteWhereException e) {
		getLogger().error("Error processing batch operation.", e);
	    }
	}

	/**
	 * Send unprocessed batch element to Kafka topic.
	 * 
	 * @param element
	 * @throws SiteWhereException
	 */
	protected void sendUnprocessedBatchElement(IBatchElement element) throws SiteWhereException {
	    UnprocessedBatchElement unprocessedElement = new UnprocessedBatchElement();
	    unprocessedElement.setBatchElement(element);
	    getUnprocessedBatchElementsProducer().send(element.getDeviceId().toString(),
		    BatchModelMarshaler.buildUnprocessedBatchElementPayloadMessage(
			    BatchModelConverter.asGrpcUnprocessedBatchElement(unprocessedElement)));
	}

	/**
	 * Send failed batch element request to Kafka topic.
	 * 
	 * @param request
	 * @throws SiteWhereException
	 */
	protected void sendFailedBatchElementRequest(IBatchElementCreateRequest request) throws SiteWhereException {
	    getFailedBatchElementsProducer().send(request.getDeviceToken(), MarshalUtils.marshalJson(request));
	}

	protected IUnprocessedBatchOperation getUnprocessed() {
	    return unprocessed;
	}
    }

    /**
     * Processes a batch element in a separate thread.
     */
    private class BatchElementProcessor extends SystemUserRunnable {

	/** Element being processed */
	private IUnprocessedBatchElement unprocessed;

	public BatchElementProcessor(IUnprocessedBatchElement unprocessed) {
	    super(BatchOperationManager.this);
	    this.unprocessed = unprocessed;
	}

	/*
	 * @see com.sitewhere.microservice.security.SystemUserRunnable#runAsSystemUser()
	 */
	@Override
	public void runAsSystemUser() throws SiteWhereException {
	    getLogger().info("Processing batch element: " + getUnprocessed().getBatchElement().getId().toString());

	    // Check whether manager has been paused.
	    handlePauseAndThrottle();

	    // Only process unprocessed elements.
	    IBatchElement element = getUnprocessed().getBatchElement();
	    IBatchOperation operation = getBatchManagement().getBatchOperation(element.getBatchOperationId());
	    if (element.getProcessingStatus() != ElementProcessingStatus.Unprocessed) {
		return;
	    }

	    // Indicate element is being processed.
	    BatchElementCreateRequest request = new BatchElementCreateRequest();
	    request.setProcessingStatus(ElementProcessingStatus.Processing);
	    getBatchManagement().updateBatchElement(element.getId(), request);

	    request = new BatchElementCreateRequest();
	    request.setMetadata(new HashMap<String, String>());
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
		getLogger().error("Error processing batch invocation element.", t);
		request.setProcessingStatus(ElementProcessingStatus.Failed);
	    } finally {
		getBatchManagement().updateBatchElement(element.getId(), request);
	    }
	}

	protected IUnprocessedBatchElement getUnprocessed() {
	    return unprocessed;
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
		getLogger().warn("Throttle timer interrupted.");
	    }
	}
    }

    public Map<String, IBatchOperationHandler> getHandlersByOperationType() {
	return handlersByOperationType;
    }

    public void setHandlersByOperationType(Map<String, IBatchOperationHandler> handlersByOperationType) {
	this.handlersByOperationType = handlersByOperationType;
    }

    protected ExecutorService getProcessorPool() {
	return processorPool;
    }

    protected IUnprocessedBatchElementsProducer getUnprocessedBatchElementsProducer() {
	return ((IBatchOperationsTenantEngine) getTenantEngine()).getUnprocessedBatchElementsProducer();
    }

    protected IFailedBatchElementsProducer getFailedBatchElementsProducer() {
	return ((IBatchOperationsTenantEngine) getTenantEngine()).getFailedBatchElementsProducer();
    }

    protected IUnprocessedBatchOperationsProducer getUnprocessedBatchOperationsProducer() {
	return ((IBatchOperationsTenantEngine) getTenantEngine()).getUnprocessedBatchOperationsProducer();
    }

    protected IBatchManagement getBatchManagement() {
	return ((IBatchOperationsTenantEngine) getTenantEngine()).getBatchManagement();
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