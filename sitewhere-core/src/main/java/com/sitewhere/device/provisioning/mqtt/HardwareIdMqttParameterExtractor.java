/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.provisioning.mqtt;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.provisioning.ICommandDeliveryParameterExtractor;

/**
 * Implements {@link ICommandDeliveryParameterExtractor} for {@link MqttParameters},
 * allowing expressions to be defined such that the device hardware id may be included in
 * the topic name to target a specific device.
 * 
 * @author Derek
 */
public class HardwareIdMqttParameterExtractor implements ICommandDeliveryParameterExtractor<MqttParameters> {

	/** Default command topic */
	public static final String DEFAULT_COMMAND_TOPIC = "SiteWhere/command/%s";

	/** Default system topic */
	public static final String DEFAULT_SYSTEM_TOPIC = "SiteWhere/system/%s";

	/** Command topic prefix */
	private String commandTopicExpr = DEFAULT_COMMAND_TOPIC;

	/** System topic prefix */
	private String systemTopicExpr = DEFAULT_SYSTEM_TOPIC;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.provisioning.ICommandDeliveryParameterExtractor#
	 * extractDeliveryParameters(com.sitewhere.spi.device.IDevice,
	 * com.sitewhere.spi.device.IDeviceAssignment,
	 * com.sitewhere.spi.device.command.IDeviceCommandExecution)
	 */
	@Override
	public MqttParameters extractDeliveryParameters(IDevice device, IDeviceAssignment assignment,
			IDeviceCommandExecution execution) throws SiteWhereException {
		MqttParameters params = new MqttParameters();

		String commandTopic = String.format(getCommandTopicExpr(), device.getHardwareId());
		params.setCommandTopic(commandTopic);

		String systemTopic = String.format(getSystemTopicExpr(), device.getHardwareId());
		params.setSystemTopic(systemTopic);

		return params;
	}

	public String getCommandTopicExpr() {
		return commandTopicExpr;
	}

	public void setCommandTopicExpr(String commandTopicExpr) {
		this.commandTopicExpr = commandTopicExpr;
	}

	public String getSystemTopicExpr() {
		return systemTopicExpr;
	}

	public void setSystemTopicExpr(String systemTopicExpr) {
		this.systemTopicExpr = systemTopicExpr;
	}
}