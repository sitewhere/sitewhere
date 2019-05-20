/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.commands.spi.ICommandTargetResolver;
import com.sitewhere.commands.spi.microservice.ICommandDeliveryMicroservice;
import com.sitewhere.grpc.client.spi.client.IDeviceManagementApiChannel;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Uses information in an {@link IDeviceCommandInvocation} to determine a list
 * of target {@link IDeviceAssignment} objects. This implementation returns the
 * {@link IDeviceAssignment} associated with the invocation.
 * 
 * @author Derek
 */
public class DefaultCommandTargetResolver extends TenantEngineLifecycleComponent implements ICommandTargetResolver {

    public DefaultCommandTargetResolver() {
	super(LifecycleComponentType.CommandTargetResolver);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.ICommandTargetResolver#
     * resolveTargets(com .sitewhere.spi.device.event.IDeviceCommandInvocation)
     */
    @Override
    public List<IDeviceAssignment> resolveTargets(IDeviceCommandInvocation invocation) throws SiteWhereException {
	IDeviceAssignment assignment = getDeviceManagementApiChannel()
		.getDeviceAssignment(invocation.getDeviceAssignmentId());
	List<IDeviceAssignment> results = new ArrayList<IDeviceAssignment>();
	results.add(assignment);
	return results;
    }

    private IDeviceManagementApiChannel<?> getDeviceManagementApiChannel() {
	return ((ICommandDeliveryMicroservice) getMicroservice()).getDeviceManagementApiChannel();
    }
}