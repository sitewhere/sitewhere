/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.event;

import io.grpc.stub.StreamObserver;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Operators;

/**
 * Takes data from a gRPC {@link StreamObserver} and publishes it using project
 * reactor APIs.
 * 
 * @author Derek
 *
 * @param <T>
 */
public class MonoStreamObserver<T> extends Mono<T> implements StreamObserver<T> {

    /** Single subscriber */
    private CoreSubscriber<? super T> subscriber;

    /*
     * @see reactor.core.publisher.Mono#subscribe(reactor.core.CoreSubscriber)
     */
    @Override
    public void subscribe(CoreSubscriber<? super T> actual) {
	Operators.MonoSubscriber<T, T> sds = new Operators.MonoSubscriber<>(actual);
	actual.onSubscribe(sds);
	this.subscriber = actual;
    }

    /*
     * @see io.grpc.stub.StreamObserver#onNext(java.lang.Object)
     */
    @Override
    public void onNext(T value) {
	if (getSubscriber() != null) {
	    getSubscriber().onNext(value);
	}
    }

    /*
     * @see io.grpc.stub.StreamObserver#onError(java.lang.Throwable)
     */
    @Override
    public void onError(Throwable t) {
	if (getSubscriber() != null) {
	    getSubscriber().onError(t);
	}
    }

    /*
     * @see io.grpc.stub.StreamObserver#onCompleted()
     */
    @Override
    public void onCompleted() {
	if (getSubscriber() != null) {
	    getSubscriber().onComplete();
	}
    }

    protected CoreSubscriber<? super T> getSubscriber() {
	return subscriber;
    }

    protected void setSubscriber(CoreSubscriber<? super T> subscriber) {
	this.subscriber = subscriber;
    }
}