/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.routing;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sitewhere.commands.spi.ICommandDestination;
import com.sitewhere.commands.spi.ICommandDestinationsManager;
import com.sitewhere.commands.spi.IOutboundCommandRouter;
import com.sitewhere.commands.spi.microservice.ICommandDeliveryTenantEngine;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.command.ISystemCommand;

/**
 * Implementation of {@link IOutboundCommandRouter} that maps device type ids to
 * {@link ICommandDestination} ids and routes accordingly.
 */
public class DeviceTypeMappingCommandRouter extends OutboundCommandRouter {

    /** Map of specification tokens to command destination ids */
    private Map<UUID, String> mappings = new HashMap<UUID, String>();

    /** Default destination for unmapped specifications */
    private String defaultDestination = null;

    /*
     * @see
     * com.sitewhere.commands.spi.IOutboundCommandRouter#getDestinationsFor(com.
     * sitewhere.spi.device.command.IDeviceCommandExecution,
     * com.sitewhere.spi.device.IDeviceNestingContext, java.util.List)
     */
    @Override
    public List<ICommandDestination<?, ?>> getDestinationsFor(IDeviceCommandExecution execution,
	    IDeviceNestingContext nesting, List<IDeviceAssignment> assignments) throws SiteWhereException {
	return Collections.singletonList(getDestinationForDevice(nesting));
    }

    /*
     * @see
     * com.sitewhere.commands.spi.IOutboundCommandRouter#getDestinationsFor(com.
     * sitewhere.spi.device.command.ISystemCommand,
     * com.sitewhere.spi.device.IDeviceNestingContext, java.util.List)
     */
    @Override
    public List<ICommandDestination<?, ?>> getDestinationsFor(ISystemCommand command, IDeviceNestingContext nesting,
	    List<IDeviceAssignment> assignments) throws SiteWhereException {
	return Collections.singletonList(getDestinationForDevice(nesting));
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
	ICommandDestination<?, ?> destination = getCommandDestinationsManager().getCommandDestinations()
		.get(destinationId);
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

    protected ICommandDestinationsManager getCommandDestinationsManager() {
	return ((ICommandDeliveryTenantEngine) getTenantEngine()).getCommandDestinationsManager();
    }
}