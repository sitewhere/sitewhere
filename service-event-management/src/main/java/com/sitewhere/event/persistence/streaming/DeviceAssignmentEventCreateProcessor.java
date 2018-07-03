/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.persistence.streaming;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.reactivestreams.Processor;
import org.reactivestreams.Subscriber;

import com.sitewhere.rest.model.device.event.streaming.EventStreamAck;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceAssignmentEventCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceEventCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.device.event.streaming.IEventStreamAck;

import reactor.core.publisher.BaseSubscriber;

/**
 * Handles streaming device assignment event create requests into an
 * {@link IDeviceEventManagement} implementation.
 * 
 * @author Derek
 */
public class DeviceAssignmentEventCreateProcessor extends BaseSubscriber<IDeviceAssignmentEventCreateRequest>
	implements Processor<IDeviceAssignmentEventCreateRequest, IEventStreamAck> {

    /** Device event management implementation */
    private IDeviceEventManagement deviceEventManagement;

    /** Subscriber */
    private Subscriber<? super IEventStreamAck> subscriber;

    /** Counter for processed events */
    private AtomicLong processedCount = new AtomicLong();

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
     * @see reactor.core.publisher.BaseSubscriber#hookOnNext(java.lang.Object)
     */
    @Override
    protected void hookOnNext(IDeviceAssignmentEventCreateRequest value) {
	UUID assignmentId = value.getDeviceAssignmentId();
	IDeviceEventCreateRequest request = value.getRequest();
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
	    case Measurement: {
		getDeviceEventManagement().addDeviceMeasurement(assignmentId,
			(IDeviceMeasurementCreateRequest) request);
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
	    getProcessedCount().incrementAndGet();
	} catch (SiteWhereException e) {
	    getSubscriber().onError(e);
	}
    }

    /*
     * @see reactor.core.publisher.BaseSubscriber#hookOnComplete()
     */
    @Override
    protected void hookOnComplete() {
	EventStreamAck ack = new EventStreamAck();
	ack.setProcessedEventCount(getProcessedCount().get());
	getSubscriber().onNext(ack);
	getSubscriber().onComplete();
    }

    public IDeviceEventManagement getDeviceEventManagement() {
	return deviceEventManagement;
    }

    public void setDeviceEventManagement(IDeviceEventManagement deviceEventManagement) {
	this.deviceEventManagement = deviceEventManagement;
    }

    public Subscriber<? super IEventStreamAck> getSubscriber() {
	return subscriber;
    }

    public void setSubscriber(Subscriber<? super IEventStreamAck> subscriber) {
	this.subscriber = subscriber;
    }

    public AtomicLong getProcessedCount() {
	return processedCount;
    }

    public void setProcessedCount(AtomicLong processedCount) {
	this.processedCount = processedCount;
    }
}