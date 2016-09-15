/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hazelcast;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hazelcast.core.IQueue;
import com.sitewhere.device.event.processor.InboundEventProcessor;
import com.sitewhere.rest.model.device.communication.DecodedDeviceRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest;
import com.sitewhere.spi.device.event.request.ISendDeviceStreamDataRequest;
import com.sitewhere.spi.server.hazelcast.ISiteWhereHazelcast;
import com.sitewhere.spi.server.tenant.ITenantHazelcastAware;
import com.sitewhere.spi.server.tenant.ITenantHazelcastConfiguration;

/**
 * Sends all events to a Hazelcast queue.
 * 
 * @author Derek
 */
public class HazelcastQueueSender extends InboundEventProcessor implements ITenantHazelcastAware {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Injected Hazelcast configuration */
    private ITenantHazelcastConfiguration hazelcastConfiguration;

    /** Queue of events to be processed */
    private IQueue<DecodedDeviceRequest<?>> eventQueue;

    /** Name of Hazelcast queue receiving events */
    private String queueName = ISiteWhereHazelcast.QUEUE_ALL_EVENTS;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.device.event.processor.InboundEventProcessor#start()
     */
    @Override
    public void start() throws SiteWhereException {
	if (getHazelcastConfiguration() == null) {
	    throw new SiteWhereException("No Hazelcast configuration provided.");
	}
	this.eventQueue = getHazelcastConfiguration().getHazelcastInstance().getQueue(getQueueName());
	LOGGER.info("Sender posting events to Hazelcast queue: " + getQueueName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.device.event.processor.InboundEventProcessor#
     * onRegistrationRequest (java.lang.String, java.lang.String,
     * com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest)
     */
    @Override
    public void onRegistrationRequest(String hardwareId, String originator, IDeviceRegistrationRequest request)
	    throws SiteWhereException {
	queueEvent(new DecodedDeviceRequest<IDeviceRegistrationRequest>(hardwareId, originator, request));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.device.event.processor.InboundEventProcessor#
     * onDeviceCommandResponseRequest(java.lang.String, java.lang.String,
     * com.sitewhere.spi.device.event.request.
     * IDeviceCommandResponseCreateRequest)
     */
    @Override
    public void onDeviceCommandResponseRequest(String hardwareId, String originator,
	    IDeviceCommandResponseCreateRequest request) throws SiteWhereException {
	queueEvent(new DecodedDeviceRequest<IDeviceCommandResponseCreateRequest>(hardwareId, originator, request));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.device.event.processor.InboundEventProcessor#
     * onDeviceMeasurementsCreateRequest(java.lang.String, java.lang.String,
     * com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest)
     */
    @Override
    public void onDeviceMeasurementsCreateRequest(String hardwareId, String originator,
	    IDeviceMeasurementsCreateRequest request) throws SiteWhereException {
	queueEvent(new DecodedDeviceRequest<IDeviceMeasurementsCreateRequest>(hardwareId, originator, request));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.device.event.processor.InboundEventProcessor#
     * onDeviceLocationCreateRequest(java.lang.String, java.lang.String,
     * com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest)
     */
    @Override
    public void onDeviceLocationCreateRequest(String hardwareId, String originator,
	    IDeviceLocationCreateRequest request) throws SiteWhereException {
	queueEvent(new DecodedDeviceRequest<IDeviceLocationCreateRequest>(hardwareId, originator, request));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.device.event.processor.InboundEventProcessor#
     * onDeviceAlertCreateRequest (java.lang.String, java.lang.String,
     * com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest)
     */
    @Override
    public void onDeviceAlertCreateRequest(String hardwareId, String originator, IDeviceAlertCreateRequest request)
	    throws SiteWhereException {
	queueEvent(new DecodedDeviceRequest<IDeviceAlertCreateRequest>(hardwareId, originator, request));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.device.event.processor.InboundEventProcessor#
     * onDeviceStreamCreateRequest (java.lang.String, java.lang.String,
     * com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest)
     */
    @Override
    public void onDeviceStreamCreateRequest(String hardwareId, String originator, IDeviceStreamCreateRequest request)
	    throws SiteWhereException {
	queueEvent(new DecodedDeviceRequest<IDeviceStreamCreateRequest>(hardwareId, originator, request));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.device.event.processor.InboundEventProcessor#
     * onDeviceStreamDataCreateRequest(java.lang.String, java.lang.String,
     * com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest)
     */
    @Override
    public void onDeviceStreamDataCreateRequest(String hardwareId, String originator,
	    IDeviceStreamDataCreateRequest request) throws SiteWhereException {
	queueEvent(new DecodedDeviceRequest<IDeviceStreamDataCreateRequest>(hardwareId, originator, request));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.device.event.processor.InboundEventProcessor#
     * onSendDeviceStreamDataRequest(java.lang.String, java.lang.String,
     * com.sitewhere.spi.device.event.request.ISendDeviceStreamDataRequest)
     */
    @Override
    public void onSendDeviceStreamDataRequest(String hardwareId, String originator,
	    ISendDeviceStreamDataRequest request) throws SiteWhereException {
	queueEvent(new DecodedDeviceRequest<ISendDeviceStreamDataRequest>(hardwareId, originator, request));
    }

    /**
     * Queue a decoded event.
     * 
     * @param decoded
     * @throws SiteWhereException
     */
    protected void queueEvent(DecodedDeviceRequest<?> decoded) throws SiteWhereException {
	try {
	    getEventQueue().put(decoded);
	} catch (InterruptedException e) {
	    LOGGER.warn("Interrupted while putting event on queue.", e);
	}
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

    public ITenantHazelcastConfiguration getHazelcastConfiguration() {
	return hazelcastConfiguration;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.tenant.ITenantHazelcastAware#
     * setHazelcastConfiguration(com
     * .sitewhere.spi.server.tenant.ITenantHazelcastConfiguration)
     */
    public void setHazelcastConfiguration(ITenantHazelcastConfiguration hazelcastConfiguration) {
	this.hazelcastConfiguration = hazelcastConfiguration;
    }

    public IQueue<DecodedDeviceRequest<?>> getEventQueue() {
	return eventQueue;
    }

    public void setEventQueue(IQueue<DecodedDeviceRequest<?>> eventQueue) {
	this.eventQueue = eventQueue;
    }

    public String getQueueName() {
	return queueName;
    }

    public void setQueueName(String queueName) {
	this.queueName = queueName;
    }
}