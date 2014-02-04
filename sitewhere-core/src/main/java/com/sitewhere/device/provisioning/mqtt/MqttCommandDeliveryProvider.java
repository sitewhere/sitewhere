/*
 * MqttDeliveryProvider.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.provisioning.mqtt;

import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.provisioning.ICommandDeliveryProvider;

/**
 * Implementation of {@link ICommandDeliveryProvider} that publishes commands to an MQTT
 * topic so that they can be processed asynchronously by a device listening on the topic.
 * 
 * @author Derek
 */
public class MqttCommandDeliveryProvider implements ICommandDeliveryProvider {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(MqttCommandDeliveryProvider.class);

	/** Default hostname if not set via Spring */
	public static final String DEFAULT_HOSTNAME = "localhost";

	/** Default port if not set from Spring */
	public static final int DEFAULT_PORT = 1883;

	/** Prefix for command topic */
	public static final String DEFAULT_COMMAND_PREFIX = "SiteWhere/command/";

	/** Prefix for system command topic */
	public static final String DEFAULT_SYSTEM_PREFIX = "SiteWhere/system/";

	/** Host name */
	private String hostname = DEFAULT_HOSTNAME;

	/** Port */
	private int port = DEFAULT_PORT;

	/** Command topic prefix */
	private String commandTopicPrefix = DEFAULT_COMMAND_PREFIX;

	/** System topic prefix */
	private String systemTopicPrefix = DEFAULT_SYSTEM_PREFIX;

	/** Indicates whether to use a fallback topic if no 'reply to' found */
	private boolean useFallbackTopic = true;

	/** MQTT client */
	private MQTT mqtt;

	/** Shared MQTT connection */
	private BlockingConnection connection;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#start()
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
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.provisioning.ICommandDeliveryProvider#deliver(com.sitewhere
	 * .spi.device.IDeviceAssignment,
	 * com.sitewhere.spi.device.event.IDeviceCommandInvocation, byte[])
	 */
	@Override
	public void deliver(IDeviceAssignment assignment, IDeviceCommandInvocation invocation, byte[] encoded)
			throws SiteWhereException {
		String commandTopic = getCommandTopicPrefix() + assignment.getDeviceHardwareId();
		try {
			LOGGER.debug("About to publish command message to topic: " + commandTopic);
			connection.publish(commandTopic, encoded, QoS.AT_LEAST_ONCE, false);
			LOGGER.debug("Command published.");
		} catch (Exception e) {
			throw new SiteWhereException("Unable to publish command to MQTT topic.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.provisioning.ICommandDeliveryProvider#deliverSystemCommand
	 * (java.lang.String, byte[])
	 */
	@Override
	public void deliverSystemCommand(String hardwareId, byte[] encoded) throws SiteWhereException {
		String systemTopic = getSystemTopicPrefix() + hardwareId;
		try {
			LOGGER.debug("About to publish system message to topic: " + systemTopic);
			connection.publish(systemTopic, encoded, QoS.AT_LEAST_ONCE, false);
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

	public boolean isUseFallbackTopic() {
		return useFallbackTopic;
	}

	public void setUseFallbackTopic(boolean useFallbackTopic) {
		this.useFallbackTopic = useFallbackTopic;
	}

	public String getCommandTopicPrefix() {
		return commandTopicPrefix;
	}

	public void setCommandTopicPrefix(String commandTopicPrefix) {
		this.commandTopicPrefix = commandTopicPrefix;
	}

	public String getSystemTopicPrefix() {
		return systemTopicPrefix;
	}

	public void setSystemTopicPrefix(String systemTopicPrefix) {
		this.systemTopicPrefix = systemTopicPrefix;
	}
}