/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication.mqtt;

import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;

import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.communication.ICommandDeliveryProvider;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link ICommandDeliveryProvider} that publishes commands to an MQTT
 * topic so that they can be processed asynchronously by a device listening on the topic.
 * 
 * @author Derek
 */
public class MqttCommandDeliveryProvider extends LifecycleComponent implements
		ICommandDeliveryProvider<byte[], MqttParameters> {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(MqttCommandDeliveryProvider.class);

	/** Default hostname if not set via Spring */
	public static final String DEFAULT_HOSTNAME = "localhost";

	/** Default port if not set from Spring */
	public static final int DEFAULT_PORT = 1883;

	/** Host name */
	private String hostname = DEFAULT_HOSTNAME;

	/** Port */
	private int port = DEFAULT_PORT;

	/** MQTT client */
	private MQTT mqtt;

	/** Shared MQTT connection */
	private BlockingConnection connection;

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
		this.mqtt = new MQTT();
		try {
			mqtt.setHost(getHostname(), getPort());
		} catch (URISyntaxException e) {
			throw new SiteWhereException("Invalid hostname for MQTT server.", e);
		}
		LOGGER.info("Connecting to MQTT broker at '" + getHostname() + ":" + getPort() + "'...");
		connection = mqtt.blockingConnection();
		try {
			connection.connect();
		} catch (Exception e) {
			throw new SiteWhereException("Unable to establish MQTT connection.", e);
		}
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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.communication.ICommandDeliveryProvider#deliver(com.sitewhere
	 * .spi.device.IDeviceNestingContext, com.sitewhere.spi.device.IDeviceAssignment,
	 * com.sitewhere.spi.device.command.IDeviceCommandExecution, java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public void deliver(IDeviceNestingContext nested, IDeviceAssignment assignment,
			IDeviceCommandExecution execution, byte[] encoded, MqttParameters params)
			throws SiteWhereException {
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
	 * @see
	 * com.sitewhere.spi.device.communication.ICommandDeliveryProvider#deliverSystemCommand
	 * (com.sitewhere.spi.device.IDeviceNestingContext,
	 * com.sitewhere.spi.device.IDeviceAssignment, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void deliverSystemCommand(IDeviceNestingContext nested, IDeviceAssignment assignment,
			byte[] encoded, MqttParameters params) throws SiteWhereException {
		try {
			LOGGER.debug("About to publish system message to topic: " + params.getSystemTopic());
			connection.publish(params.getSystemTopic(), encoded, QoS.AT_LEAST_ONCE, false);
			LOGGER.debug("Command published.");
		} catch (Exception e) {
			throw new SiteWhereException("Unable to publish command to MQTT topic.", e);
		}
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}