/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication;

import org.apache.log4j.Logger;

import com.sitewhere.spi.device.communication.IDeviceCommunication;

/**
 * Default implementation of the {@link IDeviceCommunication} interface.
 * 
 * @author Derek
 */
public class DefaultDeviceCommunication extends DeviceCommunication {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(DefaultDeviceCommunication.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.server.lifecycle.LifecycleComponent#getComponentName()
	 */
	@Override
	public String getComponentName() {
		return "CE Device Communication Subsystem";
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
}