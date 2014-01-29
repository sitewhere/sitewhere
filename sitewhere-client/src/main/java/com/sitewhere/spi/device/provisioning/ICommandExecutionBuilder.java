/*
 * ICommandExecutionBuilder.java 
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
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;

/**
 * Used to build an {@link IDeviceCommandExecution} from an {@link IDeviceCommand} and a
 * {@link IDeviceCommandInvocation}.
 * 
 * @author Derek
 */
public interface ICommandExecutionBuilder extends ISiteWhereLifecycle {

	/**
	 * Create an execution from a command and invocation details.
	 * 
	 * @param command
	 * @param invocation
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceCommandExecution createExecution(IDeviceCommand command, IDeviceCommandInvocation invocation)
			throws SiteWhereException;
}