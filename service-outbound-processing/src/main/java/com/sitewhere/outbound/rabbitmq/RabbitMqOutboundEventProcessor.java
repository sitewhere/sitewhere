/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.outbound.rabbitmq;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.sitewhere.common.MarshalUtils;
import com.sitewhere.outbound.FilteredOutboundEventProcessor;
import com.sitewhere.outbound.spi.IMulticastingOutboundEventProcessor;
import com.sitewhere.outbound.spi.multicast.IDeviceEventMulticaster;
import com.sitewhere.outbound.spi.routing.IRouteBuilder;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Extension of {@link FilteredOutboundEventProcessor} that sends messages to
 * RabbitMQ via AMQP.
 * 
 * @author Derek
 */
public class RabbitMqOutboundEventProcessor extends FilteredOutboundEventProcessor
	implements IMulticastingOutboundEventProcessor<String> {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Default connection URI */
    private static final String DEFAULT_CONNECTION_URI = "amqp://localhost";

    /** Default exchange name suffix */
    private static final String DEFAULT_EXCHANGE_SUFFIX = "-outbound";

    /** Default topic name */
    private static final String DEFAULT_TOPIC = "sitewhere.output";

    /** Connection URI */
    private String connectionUri = DEFAULT_CONNECTION_URI;

    /** Topic name */
    private String topic = DEFAULT_TOPIC;

    /** Exchange name */
    private String exchange;

    /** RabbitMQ connection */
    private Connection connection;

    /** RabbitMQ channel */
    private Channel channel;

    /** Multicaster for events */
    private IDeviceEventMulticaster<String> multicaster;

    /** Route builder for generating topics */
    private IRouteBuilder<String> routeBuilder;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.device.event.processor.FilteredOutboundEventProcessor#start
     * (com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);

	// Start multicaster if configured.
	if (multicaster != null) {
	    startNestedComponent(multicaster, monitor, true);
	}

	// Start route builder if configured.
	if (routeBuilder != null) {
	    startNestedComponent(routeBuilder, monitor, true);
	}
	try {
	    ConnectionFactory factory = new ConnectionFactory();
	    factory.setUri(getConnectionUri());
	    this.connection = factory.newConnection();
	    this.channel = connection.createChannel();
	    this.exchange = getTenantEngine().getTenant().getId() + DEFAULT_EXCHANGE_SUFFIX;
	    channel.exchangeDeclare(exchange, "topic");
	    LOGGER.info("RabbitMQ outbound processor connected to: " + getConnectionUri());
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to start RabbitMQ event processor.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.device.event.processor.FilteredOutboundEventProcessor#stop(
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Stop multicaster if configured.
	if (multicaster != null) {
	    multicaster.lifecycleStop(monitor);
	}

	// Stop route builder if configured.
	if (routeBuilder != null) {
	    routeBuilder.lifecycleStop(monitor);
	}

	try {
	    if (channel != null) {
		channel.close();
	    }
	    if (connection != null) {
		connection.close();
	    }
	} catch (Exception e) {
	    throw new SiteWhereException("Error stopping RabbitMQ event processor.", e);
	}
	super.stop(monitor);
    }

    /*
     * @see com.sitewhere.outbound.FilteredOutboundEventProcessor#
     * onMeasurementsNotFiltered(com.sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceMeasurements)
     */
    @Override
    public void onMeasurementsNotFiltered(IDeviceEventContext context, IDeviceMeasurements measurements)
	    throws SiteWhereException {
	sendEvent(measurements);
    }

    /*
     * @see
     * com.sitewhere.outbound.FilteredOutboundEventProcessor#onLocationNotFiltered(
     * com.sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceLocation)
     */
    @Override
    public void onLocationNotFiltered(IDeviceEventContext context, IDeviceLocation location) throws SiteWhereException {
	sendEvent(location);
    }

    /*
     * @see
     * com.sitewhere.outbound.FilteredOutboundEventProcessor#onAlertNotFiltered(com.
     * sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceAlert)
     */
    @Override
    public void onAlertNotFiltered(IDeviceEventContext context, IDeviceAlert alert) throws SiteWhereException {
	sendEvent(alert);
    }

    /*
     * @see com.sitewhere.outbound.FilteredOutboundEventProcessor#
     * onStateChangeNotFiltered(com.sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceStateChange)
     */
    @Override
    public void onStateChangeNotFiltered(IDeviceEventContext context, IDeviceStateChange state)
	    throws SiteWhereException {
	sendEvent(state);
    }

    /*
     * @see com.sitewhere.outbound.FilteredOutboundEventProcessor#
     * onCommandInvocationNotFiltered(com.sitewhere.spi.device.event.
     * IDeviceEventContext, com.sitewhere.spi.device.event.IDeviceCommandInvocation)
     */
    @Override
    public void onCommandInvocationNotFiltered(IDeviceEventContext context, IDeviceCommandInvocation invocation)
	    throws SiteWhereException {
	sendEvent(invocation);
    }

    /*
     * @see com.sitewhere.outbound.FilteredOutboundEventProcessor#
     * onCommandResponseNotFiltered(com.sitewhere.spi.device.event.
     * IDeviceEventContext, com.sitewhere.spi.device.event.IDeviceCommandResponse)
     */
    @Override
    public void onCommandResponseNotFiltered(IDeviceEventContext context, IDeviceCommandResponse response)
	    throws SiteWhereException {
	sendEvent(response);
    }

    /**
     * Send an {@link IDeviceEvent} to the configured topic.
     * 
     * @param event
     * @throws SiteWhereException
     */
    protected void sendEvent(IDeviceEvent event) throws SiteWhereException {
	IDeviceManagement dm = getDeviceManagement(getTenantEngine().getTenant());
	IDeviceAssignment assignment = dm.getDeviceAssignmentByToken(event.getDeviceAssignmentToken());
	IDevice device = dm.getDeviceByHardwareId(assignment.getDeviceHardwareId());
	if (getMulticaster() != null) {
	    List<String> routes = getMulticaster().calculateRoutes(event, device, assignment);
	    for (String route : routes) {
		publish(event, route);
	    }
	} else {
	    if (getRouteBuilder() != null) {
		publish(event, getRouteBuilder().build(event, device, assignment));
	    } else {
		publish(event, getTopic());
	    }
	}
    }

    /**
     * Publish an event to an MQTT topic.
     * 
     * @param event
     * @throws SiteWhereException
     */
    protected void publish(IDeviceEvent event, String topic) throws SiteWhereException {
	try {
	    channel.basicPublish(exchange, topic, null, MarshalUtils.marshalJson(event));
	    LOGGER.debug("Publishing event " + event.getId() + " to topic: " + topic);
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to publish to RabbitMQ topic.", e);
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

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.processor.
     * IMulticastingOutboundEventProcessor# getMulticaster()
     */
    @Override
    public IDeviceEventMulticaster<String> getMulticaster() {
	return this.multicaster;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.processor.
     * IMulticastingOutboundEventProcessor# getRouteBuilder()
     */
    @Override
    public IRouteBuilder<String> getRouteBuilder() {
	return this.routeBuilder;
    }

    public void setMulticaster(IDeviceEventMulticaster<String> multicaster) {
	this.multicaster = multicaster;
    }

    public void setRouteBuilder(IRouteBuilder<String> routeBuilder) {
	this.routeBuilder = routeBuilder;
    }

    public String getConnectionUri() {
	return connectionUri;
    }

    public void setConnectionUri(String connectionUri) {
	this.connectionUri = connectionUri;
    }

    public String getTopic() {
	return topic;
    }

    public void setTopic(String topic) {
	this.topic = topic;
    }

    private IDeviceManagement getDeviceManagement(ITenant tenant) {
	return null;
    }
}