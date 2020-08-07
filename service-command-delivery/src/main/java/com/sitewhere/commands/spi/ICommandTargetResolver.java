/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.spi;

import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Allows an {@link IDeviceCommandInvocation} to be resolved to one or more
 * {@link IDeviceAssignment} records that should receive the command.
 */
public interface ICommandTargetResolver extends ITenantEngineLifecycleComponent {

    /**
     * Resolves a command invocation to a list of assignments that should receive
     * the command.
     * 
     * @param invocation
     * @return
     * @throws SiteWhereException
     */
    public List<IDeviceAssignment> resolveTargets(IDeviceCommandInvocation invocation) throws SiteWhereException;
}