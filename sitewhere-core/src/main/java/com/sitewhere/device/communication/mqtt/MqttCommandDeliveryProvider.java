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
import org.eclipse.paho.client.mqttv3.MqttException;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.communication.ICommandDeliveryProvider;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
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

    public MqttCommandDeliveryProvider() {
	super(LifecycleComponentType.CommandDeliveryProvider);
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
	LOGGER.info("Connected to MQTT broker at '" + getHostname() + ":" + getPort() + "'...");
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
     * @see
     * com.sitewhere.device.communication.mqtt.MqttLifecycleComponent#stop(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if ((getMqttClient() != null) && (getMqttClient().isConnected())) {
	    try {
		getMqttClient().disconnect();
	    } catch (MqttException e) {
		LOGGER.error("Error shutting down MQTT device event receiver.", e);
	    }
	}
	super.stop(monitor);
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
	    getMqttClient().publish(params.getCommandTopic(), encoded, getQos(), false);
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
	    getMqttClient().publish(params.getSystemTopic(), encoded, getQos(), false);
	    LOGGER.debug("Command published.");
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to publish command to MQTT topic.", e);
	}
    }
}