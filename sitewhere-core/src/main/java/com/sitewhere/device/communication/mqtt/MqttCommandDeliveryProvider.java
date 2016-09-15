/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication.mqtt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fusesource.hawtdispatch.ShutdownException;
import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.QoS;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.communication.ICommandDeliveryProvider;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link ICommandDeliveryProvider} that publishes commands to
 * an MQTT topic so that they can be processed asynchronously by a device
 * listening on the topic.
 * 
 * @author Derek
 */
public class MqttCommandDeliveryProvider extends MqttLifecycleComponent
	implements ICommandDeliveryProvider<byte[], MqttParameters> {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Shared MQTT connection */
    private FutureConnection connection;

    public MqttCommandDeliveryProvider() {
	super(LifecycleComponentType.CommandDeliveryProvider);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
     */
    @Override
    public void start() throws SiteWhereException {
	super.start();

	LOGGER.info("Connecting to MQTT broker at '" + getHostname() + ":" + getPort() + "'...");
	connection = getConnection();
	LOGGER.info("Connected to MQTT broker.");
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
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
     */
    @Override
    public void stop() throws SiteWhereException {
	if (connection != null) {
	    try {
		connection.disconnect().await();
		connection.kill().await();
	    } catch (ShutdownException e) {
		LOGGER.info("Dispatcher has already been shut down.");
	    } catch (Exception e) {
		LOGGER.error("Error shutting down MQTT device event receiver.", e);
	    }
	}
	super.stop();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.communication.ICommandDeliveryProvider#deliver(
     * com. sitewhere .spi.device.IDeviceNestingContext,
     * com.sitewhere.spi.device.IDeviceAssignment,
     * com.sitewhere.spi.device.command.IDeviceCommandExecution,
     * java.lang.Object, java.lang.Object)
     */
    @Override
    public void deliver(IDeviceNestingContext nested, IDeviceAssignment assignment, IDeviceCommandExecution execution,
	    byte[] encoded, MqttParameters params) throws SiteWhereException {
	try {
	    LOGGER.debug("About to publish command message to topic: " + params.getCommandTopic());
	    connection.publish(params.getCommandTopic(), encoded, QoS.AT_LEAST_ONCE, false);
	    LOGGER.debug("Command published.");
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to publish command to MQTT topic.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.ICommandDeliveryProvider#
     * deliverSystemCommand (com.sitewhere.spi.device.IDeviceNestingContext,
     * com.sitewhere.spi.device.IDeviceAssignment, java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public void deliverSystemCommand(IDeviceNestingContext nested, IDeviceAssignment assignment, byte[] encoded,
	    MqttParameters params) throws SiteWhereException {
	try {
	    LOGGER.debug("About to publish system message to topic: " + params.getSystemTopic());
	    connection.publish(params.getSystemTopic(), encoded, QoS.AT_LEAST_ONCE, false);
	    LOGGER.debug("Command published.");
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to publish command to MQTT topic.", e);
	}
    }
}