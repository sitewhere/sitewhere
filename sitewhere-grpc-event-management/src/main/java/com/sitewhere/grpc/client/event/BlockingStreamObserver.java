/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.event;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.spi.SiteWhereException;

import io.grpc.stub.StreamObserver;

/**
 * Blocks while waiting for a stream to return a single result or error.
 * 
 * @author Derek
 *
 * @param <T>
 */
public class BlockingStreamObserver<T> implements StreamObserver<T> {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(BlockingStreamObserver.class);

    /** Operation result */
    private T response;

    /** Operation exception */
    private Throwable exception;

    /** Latch for blocking */
    private CountDownLatch latch = new CountDownLatch(1);

    public T getResult() throws SiteWhereException {
	try {
	    getLatch().await();
	    if (getException() != null) {
		throw new SiteWhereException(getException());
	    }
	    if (getResponse() != null) {
		return getResponse();
	    }
	    return null;
	} catch (InterruptedException e) {
	    throw new SiteWhereException("Interrupted while waiting for API result.", e);
	}
    }

    /*
     * @see io.grpc.stub.StreamObserver#onNext(java.lang.Object)
     */
    @Override
    public void onNext(T value) {
	this.response = value;
	LOGGER.debug("Received response value.");
	getLatch().countDown();
    }

    /*
     * @see io.grpc.stub.StreamObserver#onError(java.lang.Throwable)
     */
    @Override
    public void onError(Throwable t) {
	this.exception = t;
	LOGGER.debug("Exception in API implementation.", t);
	getLatch().countDown();
    }

    /*
     * @see io.grpc.stub.StreamObserver#onCompleted()
     */
    @Override
    public void onCompleted() {
	if (getResponse() == null) {
	    LOGGER.debug("Completed called before response.");
	}
	getLatch().countDown();
    }

    protected T getResponse() {
	return response;
    }

    protected Throwable getException() {
	return exception;
    }

    protected CountDownLatch getLatch() {
	return latch;
    }
}