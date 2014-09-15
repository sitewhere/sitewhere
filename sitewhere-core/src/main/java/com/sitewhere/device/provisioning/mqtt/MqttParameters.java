/*
 * NullParameters.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
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
 * Parameters that are needed for interacting with MQTT.
 * 
 * @author Derek
 */
public class MqttParameters {

	/**
	 * Implementation of {@link ICommandDeliveryParameterExtractor} that returns
	 * {@link MqttParameters}.
	 * 
	 * @author Derek
	 */
	public static class Extractor implements ICommandDeliveryParameterExtractor<MqttParameters> {

		/** Value to be returned */
		private MqttParameters parameters = new MqttParameters();

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
			return parameters;
		}
	}
}