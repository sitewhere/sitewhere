/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.spi;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.command.ISystemCommand;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Defines the flow executed for processing a command for delivery.
 */
public interface ICommandProcessingStrategy extends ITenantEngineLifecycleComponent {

    /**
     * Get the {@link ICommandTargetResolver} implementation.
     * 
     * @return
     */
    public ICommandTargetResolver getCommandTargetResolver();

    /**
     * Deliver a command invocation.
     * 
     * @param eventContext
     * @param invocation
     * @throws SiteWhereException
     */
    public void deliverCommand(IDeviceEventContext eventContext, IDeviceCommandInvocation invocation)
	    throws SiteWhereException;

    /**
     * Deliver a system command.
     * 
     * @param eventContext
     * @param deviceToken
     * @param command
     * @throws SiteWhereException
     */
    public void deliverSystemCommand(IDeviceEventContext eventContext, String deviceToken, ISystemCommand command)
	    throws SiteWhereException;
}