/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.persistence.streaming;

import java.util.UUID;

import org.reactivestreams.Processor;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceAssignmentEventCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceEventCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.device.event.streaming.IEventStreamAck;

/**
 * Handles streaming device assignment event create requests into an
 * {@link IDeviceEventManagement} implementation.
 * 
 * @author Derek
 */
public class DeviceAssignmentEventCreateProcessor
	implements Processor<IDeviceAssignmentEventCreateRequest, IEventStreamAck> {

    /** Subscriber */
    private Subscriber<? super IEventStreamAck> subscriber;

    /** Subscription */
    private Subscription subscription;

    /** Device event management implementation */
    private IDeviceEventManagement deviceEventManagement;

    public DeviceAssignmentEventCreateProcessor(IDeviceEventManagement deviceEventManagement) {
	this.deviceEventManagement = deviceEventManagement;
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
	UUID assignmentId = t.getDeviceAssignmentId();
	IDeviceEventCreateRequest request = t.getRequest();
	try {
	    switch (request.getEventType()) {
	    case Alert: {
		getDeviceEventManagement().addDeviceAlert(assignmentId, (IDeviceAlertCreateRequest) request);
		break;
	    }
	    case CommandInvocation: {
		getDeviceEventManagement().addDeviceCommandInvocation(assignmentId,
			(IDeviceCommandInvocationCreateRequest) request);
		break;
	    }
	    case CommandResponse: {
		getDeviceEventManagement().addDeviceCommandResponse(assignmentId,
			(IDeviceCommandResponseCreateRequest) request);
		break;
	    }
	    case Location: {
		getDeviceEventManagement().addDeviceLocation(assignmentId, (IDeviceLocationCreateRequest) request);
		break;
	    }
	    case Measurements: {
		getDeviceEventManagement().addDeviceMeasurements(assignmentId,
			(IDeviceMeasurementsCreateRequest) request);
		break;
	    }
	    case StateChange: {
		getDeviceEventManagement().addDeviceStateChange(assignmentId,
			(IDeviceStateChangeCreateRequest) request);
		break;
	    }
	    default: {
		throw new SiteWhereException("Unable to process event of type " + request.getEventType().name());
	    }
	    }
	    getSubscription().request(1);
	} catch (SiteWhereException e) {
	    getSubscriber().onError(e);
	}
    }

    /*
     * @see org.reactivestreams.Subscriber#onError(java.lang.Throwable)
     */
    @Override
    public void onError(Throwable t) {
	getSubscriber().onError(t);
    }

    /*
     * @see org.reactivestreams.Subscriber#onComplete()
     */
    @Override
    public void onComplete() {
	getSubscriber().onComplete();
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

    public IDeviceEventManagement getDeviceEventManagement() {
	return deviceEventManagement;
    }

    public void setDeviceEventManagement(IDeviceEventManagement deviceEventManagement) {
	this.deviceEventManagement = deviceEventManagement;
    }
}