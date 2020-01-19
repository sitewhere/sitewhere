/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.destination.mqtt;

import java.util.List;

import org.fusesource.hawtdispatch.ShutdownException;
import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.QoS;

import com.sitewhere.commands.spi.ICommandDeliveryProvider;
import com.sitewhere.communication.mqtt.IMqttConfiguration;
import com.sitewhere.communication.mqtt.MqttLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link ICommandDeliveryProvider} that publishes commands to
 * an MQTT topic so that they can be processed asynchronously by a device
 * listening on the topic.
 */
public class MqttCommandDeliveryProvider extends MqttLifecycleComponent
	implements ICommandDeliveryProvider<byte[], MqttParameters> {

    /** Configuration */
    private IMqttConfiguration configuration;

    /** Shared MQTT connection */
    private FutureConnection connection;

    public MqttCommandDeliveryProvider(IMqttConfiguration configuration) {
	super(LifecycleComponentType.CommandDeliveryProvider, configuration);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.device.communication.mqtt.MqttLifecycleComponent#start(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);

	getLogger().info("Connecting to MQTT broker at '" + getConfiguration().getHostname() + ":"
		+ getConfiguration().getPort() + "'...");
	connection = getConnection();
	getLogger().info("Connected to MQTT broker.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.device.communication.mqtt.MqttLifecycleComponent#stop(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (connection != null) {
	    try {
		connection.disconnect().await();
		connection.kill().await();
	    } catch (ShutdownException e) {
		getLogger().info("Dispatcher has already been shut down.");
	    } catch (Exception e) {
		getLogger().error("Error shutting down MQTT device event receiver.", e);
	    }
	}
	super.stop(monitor);
    }

    /*
     * @see
     * com.sitewhere.commands.spi.ICommandDeliveryProvider#deliver(com.sitewhere.spi
     * .device.IDeviceNestingContext, java.util.List,
     * com.sitewhere.spi.device.command.IDeviceCommandExecution, java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public void deliver(IDeviceNestingContext nested, List<? extends IDeviceAssignment> assignments,
	    IDeviceCommandExecution execution, byte[] encoded, MqttParameters params) throws SiteWhereException {
	try {
	    getLogger().info("About to publish command message to topic: " + params.getCommandTopic());
	    connection.publish(params.getCommandTopic(), encoded, QoS.AT_LEAST_ONCE, false);
	    getLogger().info("Command published.");
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to publish command to MQTT topic.", e);
	}
    }

    /*
     * @see
     * com.sitewhere.commands.spi.ICommandDeliveryProvider#deliverSystemCommand(com.
     * sitewhere.spi.device.IDeviceNestingContext, java.util.List, java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public void deliverSystemCommand(IDeviceNestingContext nested, List<? extends IDeviceAssignment> assignments,
	    byte[] encoded, MqttParameters params) throws SiteWhereException {
	try {
	    getLogger().info("About to publish system message to topic: " + params.getSystemTopic());
	    connection.publish(params.getSystemTopic(), encoded, QoS.AT_LEAST_ONCE, false);
	    getLogger().info("Command published.");
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to publish command to MQTT topic.", e);
	}
    }

    protected IMqttConfiguration getConfiguration() {
	return configuration;
    }
}