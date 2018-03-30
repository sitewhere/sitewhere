/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.event.streaming;

import org.reactivestreams.Processor;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.sitewhere.grpc.client.event.DeviceEventManagementGrpcChannel;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceAssignmentEventCreateRequest;
import com.sitewhere.grpc.model.DeviceEventModel.GEventStreamAck;
import com.sitewhere.grpc.model.converter.EventModelConverter;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.request.IDeviceAssignmentEventCreateRequest;
import com.sitewhere.spi.device.event.streaming.IEventStreamAck;

import io.grpc.stub.StreamObserver;

/**
 * Handles streaming device assignment event create requests via GRPC channel.
 * 
 * @author Derek
 */
public class DeviceAssignmentEventCreateProcessor
	implements Processor<IDeviceAssignmentEventCreateRequest, IEventStreamAck> {

    /** Subscriber */
    private Subscriber<? super IEventStreamAck> subscriber;

    /** Subscription */
    private Subscription subscription;

    /** Handles GRPC stream interaction */
    private StreamObserver<GDeviceAssignmentEventCreateRequest> grpcObserver;

    public DeviceAssignmentEventCreateProcessor(DeviceEventManagementGrpcChannel grpc) {
	StreamObserver<GEventStreamAck> grpcOutput = new StreamObserver<GEventStreamAck>() {

	    @Override
	    public void onNext(GEventStreamAck value) {
		try {
		    getSubscriber().onNext(EventModelConverter.asApiEventStreamAck(value));
		} catch (SiteWhereException e) {
		    onError(e);
		}
	    }

	    @Override
	    public void onError(Throwable t) {
		getSubscriber().onError(t);
	    }

	    @Override
	    public void onCompleted() {
		getSubscriber().onComplete();
	    }
	};
	this.grpcObserver = grpc.getAsyncStub().streamDeviceAssignmentEventCreateRequests(grpcOutput);
    }

    /*
     * @see org.reactivestreams.Publisher#subscribe(org.reactivestreams.Subscriber)
     */
    @Override
    public void subscribe(Subscriber<? super IEventStreamAck> s) {
	this.subscriber = s;
    }

    /*
     * @see
     * org.reactivestreams.Subscriber#onSubscribe(org.reactivestreams.Subscription)
     */
    @Override
    public void onSubscribe(Subscription s) {
	this.subscription = s;
	s.request(1);
    }

    /*
     * @see org.reactivestreams.Subscriber#onNext(java.lang.Object)
     */
    @Override
    public void onNext(IDeviceAssignmentEventCreateRequest t) {
	try {
	    getGrpcObserver().onNext(EventModelConverter.asGrpcDeviceAssignmentEventCreateRequest(t));
	    getSubscription().request(1);
	} catch (SiteWhereException e) {
	    getGrpcObserver().onError(e);
	}
    }

    /*
     * @see org.reactivestreams.Subscriber#onError(java.lang.Throwable)
     */
    @Override
    public void onError(Throwable t) {
	getGrpcObserver().onError(t);
    }

    /*
     * @see org.reactivestreams.Subscriber#onComplete()
     */
    @Override
    public void onComplete() {
	getGrpcObserver().onCompleted();
    }

    public Subscriber<? super IEventStreamAck> getSubscriber() {
	return subscriber;
    }

    public void setSubscriber(Subscriber<? super IEventStreamAck> subscriber) {
	this.subscriber = subscriber;
    }

    protected Subscription getSubscription() {
	return subscription;
    }

    protected void setSubscription(Subscription subscription) {
	this.subscription = subscription;
    }

    protected StreamObserver<GDeviceAssignmentEventCreateRequest> getGrpcObserver() {
	return grpcObserver;
    }

    protected void setGrpcObserver(StreamObserver<GDeviceAssignmentEventCreateRequest> grpcObserver) {
	this.grpcObserver = grpcObserver;
    }
}