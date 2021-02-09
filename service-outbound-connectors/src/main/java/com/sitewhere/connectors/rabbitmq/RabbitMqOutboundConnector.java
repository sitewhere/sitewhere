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
package com.sitewhere.connectors.rabbitmq;

import java.io.IOException;
import java.util.List;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.sitewhere.connectors.FilteredOutboundConnector;
import com.sitewhere.connectors.SerialOutboundConnector;
import com.sitewhere.connectors.spi.IMulticastingOutboundConnector;
import com.sitewhere.connectors.spi.multicast.IDeviceEventMulticaster;
import com.sitewhere.connectors.spi.routing.IRouteBuilder;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;

/**
 * Extension of {@link FilteredOutboundConnector} that sends messages to
 * RabbitMQ via AMQP.
 */
public class RabbitMqOutboundConnector extends SerialOutboundConnector
	implements IMulticastingOutboundConnector<String> {

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
     * @see
     * com.sitewhere.connectors.FilteredOutboundConnector#start(com.sitewhere.spi.
     * microservice.lifecycle.ILifecycleProgressMonitor)
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
	    this.exchange = getTenantEngine().getTenantResource().getMetadata().getName() + DEFAULT_EXCHANGE_SUFFIX;
	    channel.exchangeDeclare(exchange, "topic");
	    getLogger().info("RabbitMQ outbound processor connected to: " + getConnectionUri());
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to start RabbitMQ event processor.", e);
	}
    }

    /*
     * @see
     * com.sitewhere.connectors.FilteredOutboundConnector#stop(com.sitewhere.spi.
     * microservice.lifecycle.ILifecycleProgressMonitor)
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
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onMeasurement(com.sitewhere.
     * spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceMeasurement)
     */
    @Override
    public void onMeasurement(IDeviceEventContext context, IDeviceMeasurement mx) throws SiteWhereException {
	sendEvent(mx);
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onLocation(com.sitewhere.spi
     * .device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceLocation)
     */
    @Override
    public void onLocation(IDeviceEventContext context, IDeviceLocation location) throws SiteWhereException {
	sendEvent(location);
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onAlert(com.sitewhere.spi.
     * device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceAlert)
     */
    @Override
    public void onAlert(IDeviceEventContext context, IDeviceAlert alert) throws SiteWhereException {
	sendEvent(alert);
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onStateChange(com.sitewhere.
     * spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceStateChange)
     */
    @Override
    public void onStateChange(IDeviceEventContext context, IDeviceStateChange state) throws SiteWhereException {
	sendEvent(state);
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onCommandInvocation(com.
     * sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceCommandInvocation)
     */
    @Override
    public void onCommandInvocation(IDeviceEventContext context, IDeviceCommandInvocation invocation)
	    throws SiteWhereException {
	sendEvent(invocation);
    }

    /*
     * @see com.sitewhere.connectors.SerialOutboundConnector#onCommandResponse(com.
     * sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceCommandResponse)
     */
    @Override
    public void onCommandResponse(IDeviceEventContext context, IDeviceCommandResponse response)
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
	IDeviceManagement dm = getDeviceManagement();
	IDeviceAssignment assignment = dm.getDeviceAssignment(event.getDeviceAssignmentId());
	IDevice device = dm.getDevice(assignment.getDeviceId());
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
	    getLogger().debug("Publishing event " + event.getId() + " to topic: " + topic);
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to publish to RabbitMQ topic.", e);
	}
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
}