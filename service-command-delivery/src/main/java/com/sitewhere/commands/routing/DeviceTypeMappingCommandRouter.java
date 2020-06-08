/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.routing;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.sitewhere.commands.configuration.router.devicetypemapping.DeviceTypeMapping;
import com.sitewhere.commands.configuration.router.devicetypemapping.DeviceTypeMappingConfiguration;
import com.sitewhere.commands.spi.ICommandDestination;
import com.sitewhere.commands.spi.ICommandDestinationsManager;
import com.sitewhere.commands.spi.IOutboundCommandRouter;
import com.sitewhere.commands.spi.microservice.ICommandDeliveryMicroservice;
import com.sitewhere.commands.spi.microservice.ICommandDeliveryTenantEngine;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.command.ISystemCommand;

/**
 * Implementation of {@link IOutboundCommandRouter} that maps device type ids to
 * {@link ICommandDestination} ids and routes accordingly.
 */
public class DeviceTypeMappingCommandRouter extends OutboundCommandRouter {

    /** Configuration */
    private DeviceTypeMappingConfiguration configuration;

    public DeviceTypeMappingCommandRouter(DeviceTypeMappingConfiguration configuration) {
	this.configuration = configuration;
    }

    /*
     * @see
     * com.sitewhere.commands.spi.IOutboundCommandRouter#getDestinationsFor(com.
     * sitewhere.spi.device.command.IDeviceCommandExecution,
     * com.sitewhere.spi.device.IDeviceNestingContext, java.util.List)
     */
    @Override
    public List<ICommandDestination<?, ?>> getDestinationsFor(IDeviceCommandExecution execution,
	    IDeviceNestingContext nesting, List<? extends IDeviceAssignment> assignments) throws SiteWhereException {
	return getDestinationsForDevice(nesting);
    }

    /*
     * @see
     * com.sitewhere.commands.spi.IOutboundCommandRouter#getDestinationsFor(com.
     * sitewhere.spi.device.command.ISystemCommand,
     * com.sitewhere.spi.device.IDeviceNestingContext, java.util.List)
     */
    @Override
    public List<ICommandDestination<?, ?>> getDestinationsFor(ISystemCommand command, IDeviceNestingContext nesting,
	    List<? extends IDeviceAssignment> assignments) throws SiteWhereException {
	return getDestinationsForDevice(nesting);
    }

    /**
     * Get {@link ICommandDestination} entries for device based on specification
     * token associated with the device.
     * 
     * @param nesting
     * @return
     * @throws SiteWhereException
     */
    protected List<ICommandDestination<?, ?>> getDestinationsForDevice(IDeviceNestingContext nesting)
	    throws SiteWhereException {
	UUID deviceTypeId = nesting.getGateway().getDeviceTypeId();
	IDeviceType deviceType = getDeviceManagement().getDeviceType(deviceTypeId);
	List<ICommandDestination<?, ?>> matches = new ArrayList<>();
	for (DeviceTypeMapping mapping : getConfiguration().getMappings()) {
	    if (mapping.getDeviceTypeToken() == deviceType.getToken()) {
		ICommandDestination<?, ?> destination = getCommandDestinationsManager().getCommandDestinationsMap()
			.get(mapping.getDestinationId());
		if (destination == null) {
		    getLogger().error(String.format("Destination not found: %s", mapping.getDestinationId()));
		} else {
		    matches.add(destination);
		}
	    }
	}
	if (matches.isEmpty()) {
	    String destinationId = getConfiguration().getDefaultDestination();
	    ICommandDestination<?, ?> destination = getCommandDestinationsManager().getCommandDestinationsMap()
		    .get(destinationId);
	    if (destination == null) {
		getLogger().error(String.format("Default destination not found: %s", destinationId));
	    } else {
		matches.add(destination);
	    }
	}
	return matches;
    }

    protected IDeviceManagement getDeviceManagement() {
	return ((ICommandDeliveryMicroservice) getMicroservice()).getDeviceManagement();
    }

    protected ICommandDestinationsManager getCommandDestinationsManager() {
	return ((ICommandDeliveryTenantEngine) getTenantEngine()).getCommandDestinationsManager();
    }

    protected DeviceTypeMappingConfiguration getConfiguration() {
	return configuration;
    }
}