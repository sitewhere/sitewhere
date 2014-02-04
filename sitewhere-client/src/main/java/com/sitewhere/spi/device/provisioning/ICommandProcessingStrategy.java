/*
 * ICommandProcessingStrategy.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.provisioning;

import com.sitewhere.spi.ISiteWhereLifecycle;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;

/**
 * Defines the flow executed for processing a command for delivery.
 * 
 * @author Derek
 */
public interface ICommandProcessingStrategy extends ISiteWhereLifecycle {

	/**
	 * Send a command using the given provisioning implementation.
	 * 
	 * @param provisioning
	 * @param invocation
	 * @throws SiteWhereException
	 */
	public void deliverCommand(IDeviceProvisioning provisioning, IDeviceCommandInvocation invocation)
			throws SiteWhereException;

	/**
	 * Delivers a system command using the given provisioning implementation.
	 * 
	 * @param provisioning
	 * @param hardwareId
	 * @param command
	 * @throws SiteWhereException
	 */
	public void deliverSystemCommand(IDeviceProvisioning provisioning, String hardwareId, Object command)
			throws SiteWhereException;
}