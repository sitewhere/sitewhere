/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.routing;

import java.util.List;

import com.sitewhere.commands.spi.ICommandDestination;
import com.sitewhere.commands.spi.IOutboundCommandRouter;
import com.sitewhere.commands.spi.kafka.IUndeliveredCommandInvocationsProducer;
import com.sitewhere.grpc.client.event.EventModelMarshaler;
import com.sitewhere.rest.model.device.event.kafka.EnrichedEventPayload;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.command.ISystemCommand;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

public class CommandRoutingLogic {

    /**
     * Route a command using the given router.
     * 
     * @param router
     * @param undelivered
     * @param eventContext
     * @param execution
     * @param nesting
     * @param assignment
     * @throws SiteWhereException
     */
    public static void routeCommand(IOutboundCommandRouter router, IUndeliveredCommandInvocationsProducer undelivered,
	    IDeviceEventContext eventContext, IDeviceCommandExecution execution, IDeviceNestingContext nesting,
	    IDeviceAssignment assignment) throws SiteWhereException {
	List<ICommandDestination<?, ?>> destinations = router.getDestinationsFor(execution, nesting, assignment);
	boolean deliveredToAll = true;
	for (ICommandDestination<?, ?> destination : destinations) {
	    if (destination.getLifecycleStatus() == LifecycleStatus.Started) {
		try {
		    deliverCommand(destination, execution, nesting, assignment);
		} catch (SiteWhereException e) {
		    router.getLogger().error("Unable to deliver command to destination.", e);
		    deliveredToAll = false;
		}
	    } else {
		deliveredToAll = false;
	    }
	}
	// If any command destination was not available, add to undelivered topic.
	if (!deliveredToAll) {
	    EnrichedEventPayload payload = new EnrichedEventPayload();
	    payload.setEventContext(eventContext);
	    payload.setEvent(execution.getInvocation());
	    byte[] message = EventModelMarshaler.buildEnrichedEventPayloadMessage(payload);
	    undelivered.send(eventContext.getDeviceId().toString(), message);
	    router.getLogger().warn("Due to delivery failure, pushed command to undeliverable topic.");
	}
    }

    /**
     * Route a system command using the given router.
     * 
     * @param router
     * @param command
     * @param nesting
     * @param assignment
     * @throws SiteWhereException
     */
    public static void routeSystemCommand(IOutboundCommandRouter router, ISystemCommand command,
	    IDeviceNestingContext nesting, IDeviceAssignment assignment) throws SiteWhereException {
	List<ICommandDestination<?, ?>> destinations = router.getDestinationsFor(command, nesting, assignment);
	for (ICommandDestination<?, ?> destination : destinations) {
	    deliverSystemCommand(destination, command, nesting, assignment);
	    // TODO: How are failures handled?
	}
    }

    /**
     * Deliver a command to the given destination.
     * 
     * @param destination
     * @param execution
     * @param nesting
     * @param assignment
     * @throws SiteWhereException
     */
    public static void deliverCommand(ICommandDestination<?, ?> destination, IDeviceCommandExecution execution,
	    IDeviceNestingContext nesting, IDeviceAssignment assignment) throws SiteWhereException {
	destination.deliverCommand(execution, nesting, assignment);
    }

    /**
     * Deliver a system command to the given destination.
     * 
     * @param destination
     * @param command
     * @param nesting
     * @param assignment
     * @throws SiteWhereException
     */
    public static void deliverSystemCommand(ICommandDestination<?, ?> destination, ISystemCommand command,
	    IDeviceNestingContext nesting, IDeviceAssignment assignment) throws SiteWhereException {
	destination.deliverSystemCommand(command, nesting, assignment);
    }
}