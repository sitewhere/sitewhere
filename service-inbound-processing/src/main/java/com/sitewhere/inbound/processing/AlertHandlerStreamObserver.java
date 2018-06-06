/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.inbound.processing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.stub.StreamObserver;

/**
 * Handles event storage error asynchronously.
 * 
 * @author Derek
 *
 * @param <T>
 */
public class AlertHandlerStreamObserver<T> implements StreamObserver<T> {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(AlertHandlerStreamObserver.class);

    /** Get processing logic */
    private InboundPayloadProcessingLogic inboundPayloadProcessingLogic;

    public AlertHandlerStreamObserver(InboundPayloadProcessingLogic inboundPayloadProcessingLogic) {
	this.inboundPayloadProcessingLogic = inboundPayloadProcessingLogic;
    }

    /*
     * @see io.grpc.stub.StreamObserver#onNext(java.lang.Object)
     */
    @Override
    public void onNext(T value) {
	getInboundPayloadProcessingLogic().getProcessedEvents().mark();
    }

    /*
     * @see io.grpc.stub.StreamObserver#onError(java.lang.Throwable)
     */
    @Override
    public void onError(Throwable t) {
	LOGGER.error("Error storing device event.", t);
	getInboundPayloadProcessingLogic().getFailedEvents().mark();
    }

    /*
     * @see io.grpc.stub.StreamObserver#onCompleted()
     */
    @Override
    public void onCompleted() {
    }

    protected InboundPayloadProcessingLogic getInboundPayloadProcessingLogic() {
	return inboundPayloadProcessingLogic;
    }

    protected void setInboundPayloadProcessingLogic(InboundPayloadProcessingLogic inboundPayloadProcessingLogic) {
	this.inboundPayloadProcessingLogic = inboundPayloadProcessingLogic;
    }
}