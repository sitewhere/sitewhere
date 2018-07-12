/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.persistence.cassandra;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.UDTValue;
import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.CommandInitiator;
import com.sitewhere.spi.device.event.CommandTarget;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;

/**
 * Handles mapping of device command invocation event fields to Cassandra
 * records.
 * 
 * @author Derek
 */
public class CassandraDeviceCommandInvocation implements ICassandraEventBinder<IDeviceCommandInvocation> {

    /** Static instance */
    public static final ICassandraEventBinder<IDeviceCommandInvocation> INSTANCE = new CassandraDeviceCommandInvocation();

    // Invocation field.
    public static final String FIELD_INVOCATION = "invocation";

    // Initiator field.
    public static final String FIELD_INITIATOR = "initiator";

    // Initiator id field.
    public static final String FIELD_INITIATOR_ID = "initiator_id";

    // Target field.
    public static final String FIELD_TARGET = "target";

    // Target id field.
    public static final String FIELD_TARGET_ID = "target_id";

    // Command token field.
    public static final String FIELD_COMMAND_TOKEN = "command_token";

    // Command parameters field.
    public static final String FIELD_COMMAND_PARAMS = "command_params";

    /*
     * @see
     * com.sitewhere.event.persistence.cassandra.ICassandraEventBinder#bind(com.
     * sitewhere.event.persistence.cassandra.CassandraEventManagementClient,
     * com.datastax.driver.core.BoundStatement,
     * com.sitewhere.spi.device.event.IDeviceEvent)
     */
    @Override
    public void bind(CassandraEventManagementClient client, BoundStatement bound, IDeviceCommandInvocation event)
	    throws SiteWhereException {
	CassandraDeviceCommandInvocation.bindFields(client, bound, event);
    }

    /**
     * Bind fields from a device command invocation to an existing
     * {@link BoundStatement}.
     * 
     * @param client
     * @param bound
     * @param invocation
     * @throws SiteWhereException
     */
    public static void bindFields(CassandraEventManagementClient client, BoundStatement bound,
	    IDeviceCommandInvocation invocation) throws SiteWhereException {
	CassandraDeviceEvent.bindEventFields(bound, invocation);

	UDTValue udt = client.getInvocationType().newValue();
	udt.setByte(FIELD_INITIATOR, getIndicatorForCommandInitiator(invocation.getInitiator()));
	udt.setString(FIELD_INITIATOR_ID, invocation.getInitiatorId());
	udt.setByte(FIELD_TARGET, getIndicatorForCommandTarget(invocation.getTarget()));
	udt.setString(FIELD_TARGET_ID, invocation.getTargetId());
	udt.setString(FIELD_COMMAND_TOKEN, invocation.getCommandToken());
	udt.setMap(FIELD_COMMAND_PARAMS, invocation.getParameterValues());
	bound.setUDTValue(FIELD_INVOCATION, udt);
    }

    /**
     * Load fields from a row into a device command invocation.
     * 
     * @param client
     * @param invocation
     * @param row
     * @throws SiteWhereException
     */
    public static void loadFields(CassandraEventManagementClient client, DeviceCommandInvocation invocation, Row row)
	    throws SiteWhereException {
	CassandraDeviceEvent.loadEventFields(invocation, row);

	UDTValue udt = row.getUDTValue(FIELD_INVOCATION);
	invocation.setInitiator(getCommandInitiatorForIndicator(udt.getByte(FIELD_INITIATOR)));
	invocation.setInitiatorId(udt.getString(FIELD_INITIATOR_ID));
	invocation.setTarget(getCommandTargetForIndicator(udt.getByte(FIELD_TARGET)));
	invocation.setTargetId(udt.getString(FIELD_TARGET_ID));
	invocation.setCommandToken(udt.getString(FIELD_COMMAND_TOKEN));
	invocation.setParameterValues(udt.getMap(FIELD_COMMAND_PARAMS, String.class, String.class));
    }

    /**
     * Get indicator value for command initiator.
     * 
     * @param initiator
     * @return
     * @throws SiteWhereException
     */
    public static Byte getIndicatorForCommandInitiator(CommandInitiator initiator) throws SiteWhereException {
	switch (initiator) {
	case REST: {
	    return 0;
	}
	case Script: {
	    return 1;
	}
	case BatchOperation: {
	    return 2;
	}
	case Scheduler: {
	    return 3;
	}
	default: {
	    throw new SiteWhereException("Unsupported command initiator: " + initiator.name());
	}
	}
    }

    /**
     * Get command initiator for indicator value.
     * 
     * @param value
     * @return
     * @throws SiteWhereException
     */
    public static CommandInitiator getCommandInitiatorForIndicator(Byte value) throws SiteWhereException {
	if (value == 0) {
	    return CommandInitiator.REST;
	} else if (value == 1) {
	    return CommandInitiator.Script;
	} else if (value == 2) {
	    return CommandInitiator.BatchOperation;
	} else if (value == 3) {
	    return CommandInitiator.Scheduler;
	}
	throw new SiteWhereException("Unsupported command initiator: " + value);
    }

    /**
     * Get indicator value for command target.
     * 
     * @param target
     * @return
     * @throws SiteWhereException
     */
    public static Byte getIndicatorForCommandTarget(CommandTarget target) throws SiteWhereException {
	switch (target) {
	case Assignment: {
	    return 0;
	}
	default: {
	    throw new SiteWhereException("Unsupported command target: " + target.name());
	}
	}
    }

    /**
     * Get command target for indicator value.
     * 
     * @param value
     * @return
     * @throws SiteWhereException
     */
    public static CommandTarget getCommandTargetForIndicator(Byte value) throws SiteWhereException {
	if (value == 0) {
	    return CommandTarget.Assignment;
	}
	throw new SiteWhereException("Unsupported command target: " + value);
    }
}
