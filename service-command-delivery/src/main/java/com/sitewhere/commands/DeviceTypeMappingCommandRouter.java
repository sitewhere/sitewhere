/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.commands.spi.ICommandDestination;
import com.sitewhere.commands.spi.IOutboundCommandRouter;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.command.ISystemCommand;

/**
 * Implementation of {@link IOutboundCommandRouter} that maps device type ids to
 * {@link ICommandDestination} ids and routes accordingly.
 * 
 * @author Derek
 */
public class DeviceTypeMappingCommandRouter extends OutboundCommandRouter {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(DeviceTypeMappingCommandRouter.class);

    /** Map of specification tokens to command destination ids */
    private Map<UUID, String> mappings = new HashMap<UUID, String>();

    /** Default destination for unmapped specifications */
    private String defaultDestination = null;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Log getLogger() {
	return LOGGER;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IOutboundCommandRouter#
     * routeCommand(com. sitewhere.spi.device.command.IDeviceCommandExecution,
     * com.sitewhere.spi.device.IDeviceNestingContext,
     * com.sitewhere.spi.device.IDeviceAssignment)
     */
    @Override
    public void routeCommand(IDeviceCommandExecution execution, IDeviceNestingContext nesting,
	    IDeviceAssignment assignment) throws SiteWhereException {
	ICommandDestination<?, ?> destination = getDestinationForDevice(nesting);
	destination.deliverCommand(execution, nesting, assignment);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IOutboundCommandRouter#
     * routeSystemCommand (com.sitewhere.spi.device.command.ISystemCommand,
     * com.sitewhere.spi.device.IDeviceNestingContext,
     * com.sitewhere.spi.device.IDeviceAssignment)
     */
    @Override
    public void routeSystemCommand(ISystemCommand command, IDeviceNestingContext nesting, IDeviceAssignment assignment)
	    throws SiteWhereException {
	ICommandDestination<?, ?> destination = getDestinationForDevice(nesting);
	destination.deliverSystemCommand(command, nesting, assignment);
    }

    /**
     * Get {@link ICommandDestination} for device based on specification token
     * associated with the device.
     * 
     * @param nesting
     * @return
     * @throws SiteWhereException
     */
    protected ICommandDestination<?, ?> getDestinationForDevice(IDeviceNestingContext nesting)
	    throws SiteWhereException {
	UUID deviceTypeId = nesting.getGateway().getDeviceTypeId();
	String destinationId = mappings.get(deviceTypeId);
	if (destinationId == null) {
	    if (getDefaultDestination() != null) {
		destinationId = getDefaultDestination();
	    } else {
		throw new SiteWhereException("No command destination mapping for device type: " + deviceTypeId);
	    }
	}
	ICommandDestination<?, ?> destination = getDestinations().get(destinationId);
	if (destination == null) {
	    throw new SiteWhereException("No destination found for destination id: " + destinationId);
	}
	return destination;
    }

    public Map<UUID, String> getMappings() {
	return mappings;
    }

    public void setMappings(Map<UUID, String> mappings) {
	this.mappings = mappings;
    }

    public String getDefaultDestination() {
	return defaultDestination;
    }

    public void setDefaultDestination(String defaultDestination) {
	this.defaultDestination = defaultDestination;
    }
}