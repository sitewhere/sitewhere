/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.grpc.streaming;

import org.reactivestreams.Processor;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.sitewhere.grpc.model.DeviceEventModel.GDeviceAssignmentEventCreateRequest;
import com.sitewhere.grpc.model.DeviceEventModel.GEventStreamAck;
import com.sitewhere.grpc.model.converter.EventModelConverter;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.request.IDeviceAssignmentEventCreateRequest;
import com.sitewhere.spi.device.event.streaming.IEventStreamAck;

import io.grpc.stub.StreamObserver;

public class DeviceAssignmentEventCreateStreamObserver implements StreamObserver<GDeviceAssignmentEventCreateRequest> {

    /** Device event management */
    private IDeviceEventManagement deviceEventManagement;

    /** Get underlying processor */
    private Processor<IDeviceAssignmentEventCreateRequest, IEventStreamAck> processor;

    /** Response observer */
    private StreamObserver<GEventStreamAck> responseObserver;

    /** Subscription */
    private Subscription subscription;

    public DeviceAssignmentEventCreateStreamObserver(IDeviceEventManagement deviceEventManagement,
	    StreamObserver<GEventStreamAck> responseObserver) throws SiteWhereException {
	this.deviceEventManagement = deviceEventManagement;
    }

    /**
     * Create underlying processor via event management API.
     * 
     * @throws SiteWhereException
     */
    public void start() throws SiteWhereException {
	Processor<IDeviceAssignmentEventCreateRequest, IEventStreamAck> processor = getDeviceEventManagement()
		.streamDeviceAssignmentCreateEvents();
	processor.subscribe(new Subscriber<IEventStreamAck>() {

	    @Override
	    public void onSubscribe(Subscription s) {
		subscription = s;
	    }

	    @Override
	    public void onNext(IEventStreamAck t) {
		try {
		    getResponseObserver().onNext(EventModelConverter.asGrpcEventStreamAck(t));
		} catch (SiteWhereException e) {
		    getResponseObserver().onError(e);
		}
	    }

	    @Override
	    public void onError(Throwable t) {
		getResponseObserver().onError(t);
	    }

	    @Override
	    public void onComplete() {
		getResponseObserver().onCompleted();
	    }
	});
    }

    /*
     * @see io.grpc.stub.StreamObserver#onNext(java.lang.Object)
     */
    @Override
    public void onNext(GDeviceAssignmentEventCreateRequest value) {
	try {
	    getProcessor().onNext(EventModelConverter.asApiDeviceAssignmentEventCreateRequest(value));
	} catch (SiteWhereException e) {
	    onError(e);
	}
    }

    /*
     * @see io.grpc.stub.StreamObserver#onError(java.lang.Throwable)
     */
    @Override
    public void onError(Throwable t) {
	getProcessor().onError(t);
    }

    /*
     * @see io.grpc.stub.StreamObserver#onCompleted()
     */
    @Override
    public void onCompleted() {
	getProcessor().onComplete();
    }

    public IDeviceEventManagement getDeviceEventManagement() {
	return deviceEventManagement;
    }

    public void setDeviceEventManagement(IDeviceEventManagement deviceEventManagement) {
	this.deviceEventManagement = deviceEventManagement;
    }

    public Processor<IDeviceAssignmentEventCreateRequest, IEventStreamAck> getProcessor() {
	return processor;
    }

    public void setProcessor(Processor<IDeviceAssignmentEventCreateRequest, IEventStreamAck> processor) {
	this.processor = processor;
    }

    public StreamObserver<GEventStreamAck> getResponseObserver() {
	return responseObserver;
    }

    public void setResponseObserver(StreamObserver<GEventStreamAck> responseObserver) {
	this.responseObserver = responseObserver;
    }

    public Subscription getSubscription() {
	return subscription;
    }

    public void setSubscription(Subscription subscription) {
	this.subscription = subscription;
    }
}