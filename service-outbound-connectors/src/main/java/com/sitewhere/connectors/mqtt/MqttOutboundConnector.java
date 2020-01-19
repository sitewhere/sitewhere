/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.mqtt;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.fusesource.hawtdispatch.Dispatch;
import org.fusesource.hawtdispatch.DispatchQueue;
import org.fusesource.mqtt.client.Future;
import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;

import com.sitewhere.communication.mqtt.IMqttConfiguration;
import com.sitewhere.communication.mqtt.MqttLifecycleComponent;
import com.sitewhere.connectors.SerialOutboundConnector;
import com.sitewhere.connectors.spi.IMulticastingOutboundConnector;
import com.sitewhere.connectors.spi.multicast.IDeviceEventMulticaster;
import com.sitewhere.connectors.spi.routing.IRouteBuilder;
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
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;

/**
 * Outbound connector that sends events to an MQTT topic.
 */
public class MqttOutboundConnector extends SerialOutboundConnector implements IMulticastingOutboundConnector<String> {

    /** Configuration */
    private IMqttConfiguration configuration;

    /** MQTT client */
    private MQTT mqtt;

    /** Hawtdispatch queue */
    private DispatchQueue queue;

    /** Shared MQTT connection */
    private FutureConnection connection;

    /** Multicaster for events */
    private IDeviceEventMulticaster<String> multicaster;

    /** Route builder for generating topics */
    private IRouteBuilder<String> routeBuilder;

    public MqttOutboundConnector(IMqttConfiguration configuration) {
	this.configuration = configuration;
    }

    /*
     * @see
     * com.sitewhere.connectors.FilteredOutboundConnector#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if ((getConfiguration().getTopic() == null) && (getMulticaster() == null) && (getRouteBuilder() == null)) {
	    // this.topic = String.format("SiteWhere/%1$s/outbound/%2$s",
	    // getTenantEngine().getTenantResource().getMetadata().getName(),
	    // getConnectorId());
	    // getLogger().warn(String.format(
	    // "No topic specified and no multicaster or route builder configured.
	    // Defaulting to topic '%s'",
	    // getTopic()));
	}

	// Required for filters.
	super.start(monitor);

	// Start multicaster if configured.
	if (getMulticaster() != null) {
	    startNestedComponent(getMulticaster(), monitor, true);
	}

	// Start route builder if configured.
	if (getRouteBuilder() != null) {
	    startNestedComponent(getRouteBuilder(), monitor, true);
	}

	// Use common MQTT configuration setup.
	this.queue = Dispatch.createQueue(getComponentId().toString());
	// this.mqtt = configure(getConfiguration(), queue);

	getLogger().info(String.format("Connecting to MQTT broker at %s:%s ...", getConfiguration().getHostname(),
		getConfiguration().getPort()));
	connection = mqtt.futureConnection();
	try {
	    Future<Void> future = connection.connect();
	    future.await(MqttLifecycleComponent.DEFAULT_CONNECT_TIMEOUT_SECS, TimeUnit.SECONDS);
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to connect to MQTT broker.", e);
	}
	getLogger().info("Connected to MQTT broker.");
    }

    /*
     * @see
     * com.sitewhere.connectors.FilteredOutboundConnector#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
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

	if (connection != null) {
	    try {
		connection.disconnect();
		connection.kill();
	    } catch (Exception e) {
		getLogger().error("Error shutting down MQTT device event receiver.", e);
	    }
	}
	if (queue != null) {
	    queue.suspend();
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
	if (getMulticaster() != null) {
	    IDeviceAssignment assignment = getDeviceManagement().getDeviceAssignment(event.getDeviceAssignmentId());
	    IDevice device = getDeviceManagement().getDevice(assignment.getDeviceId());
	    List<String> routes = getMulticaster().calculateRoutes(event, device, assignment);
	    for (String route : routes) {
		publish(event, route);
	    }
	} else {
	    if (getRouteBuilder() != null) {
		IDeviceAssignment assignment = getDeviceManagement().getDeviceAssignment(event.getDeviceAssignmentId());
		IDevice device = getDeviceManagement().getDevice(assignment.getDeviceId());
		publish(event, getRouteBuilder().build(event, device, assignment));
	    } else {
		publish(event, getConfiguration().getTopic());
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
	connection.publish(topic, MarshalUtils.marshalJson(event), QoS.AT_LEAST_ONCE, false);
    }

    /*
     * @see
     * com.sitewhere.connectors.spi.IMulticastingOutboundConnector#getMulticaster()
     */
    @Override
    public IDeviceEventMulticaster<String> getMulticaster() {
	return multicaster;
    }

    public void setMulticaster(IDeviceEventMulticaster<String> multicaster) {
	this.multicaster = multicaster;
    }

    /*
     * @see
     * com.sitewhere.connectors.spi.IMulticastingOutboundConnector#getRouteBuilder()
     */
    @Override
    public IRouteBuilder<String> getRouteBuilder() {
	return routeBuilder;
    }

    public void setRouteBuilder(IRouteBuilder<String> routeBuilder) {
	this.routeBuilder = routeBuilder;
    }

    protected IMqttConfiguration getConfiguration() {
	return configuration;
    }
}