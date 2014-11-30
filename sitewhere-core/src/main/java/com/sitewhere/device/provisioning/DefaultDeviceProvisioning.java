/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.provisioning;

import com.sitewhere.spi.device.provisioning.IDeviceProvisioning;

/**
 * Default implementation of the {@link IDeviceProvisioning} interface.
 * 
 * @author Derek
 */
public class DefaultDeviceProvisioning extends DeviceProvisioning {

	public DefaultDeviceProvisioning() {
		setRegistrationManager(new RegistrationManager());
		setInboundProcessingStrategy(new BlockingQueueInboundProcessingStrategy());
		setCommandProcessingStrategy(new DefaultCommandProcessingStrategy());
		setOutboundProcessingStrategy(new BlockingQueueOutboundProcessingStrategy());
	}
}