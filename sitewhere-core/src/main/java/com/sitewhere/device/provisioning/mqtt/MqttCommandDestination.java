/*
 * MqttCommandDestination.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.provisioning.mqtt;

import com.sitewhere.device.provisioning.CommandDestination;
import com.sitewhere.spi.device.provisioning.ICommandDestination;

/**
 * Implementation of {@link ICommandDestination} that encodes and delivers messages that
 * are byte arrays.
 * 
 * @author Derek
 */
public class MqttCommandDestination extends CommandDestination<byte[], MqttParameters> {

	public MqttCommandDestination() {
		setCommandDeliveryParameterExtractor(new HardwareIdMqttParameterExtractor());
	}
}