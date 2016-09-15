/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.influx.device;

import java.util.Map;

import org.influxdb.dto.Point;

import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.CommandInitiator;
import com.sitewhere.spi.device.event.CommandStatus;
import com.sitewhere.spi.device.event.CommandTarget;
import com.sitewhere.spi.device.event.DeviceEventType;

/**
 * Class for saving device command invocation data to InfluxDB.
 * 
 * @author Derek
 */
public class InfluxDbDeviceCommandInvocation {

    /** Tag for initiator */
    public static final String CMD_INITIATOR = "initiator";

    /** Tag for initiator id */
    public static final String CMD_INITIATOR_ID = "initiatorId";

    /** Tag for target */
    public static final String CMD_TARGET = "target";

    /** Tag for target id */
    public static final String CMD_TARGET_ID = "targetId";

    /** Tag for command token */
    public static final String CMD_COMMAND_TOKEN = "command";

    /** Field prefix for parameter values */
    public static final String CMD_PARAMETER_VALUE_PREFIX = "param:";

    /** Tag for status */
    public static final String CMD_STATUS = "status";

    /**
     * Parse domain object from a value map.
     * 
     * @param values
     * @return
     * @throws SiteWhereException
     */
    public static DeviceCommandInvocation parse(Map<String, Object> values) throws SiteWhereException {
	DeviceCommandInvocation ci = new DeviceCommandInvocation();
	InfluxDbDeviceCommandInvocation.loadFromMap(ci, values);
	return ci;
    }

    /**
     * Load fields from value map.
     * 
     * @param event
     * @param values
     * @throws SiteWhereException
     */
    public static void loadFromMap(DeviceCommandInvocation event, Map<String, Object> values)
	    throws SiteWhereException {
	event.setEventType(DeviceEventType.CommandInvocation);
	event.setInitiator(CommandInitiator.valueOf(InfluxDbDeviceEvent.find(values, CMD_INITIATOR)));
	event.setInitiatorId(InfluxDbDeviceEvent.find(values, CMD_INITIATOR_ID, true));
	event.setTarget(CommandTarget.valueOf(InfluxDbDeviceEvent.find(values, CMD_TARGET)));
	event.setTargetId(InfluxDbDeviceEvent.find(values, CMD_TARGET_ID, true));
	event.setCommandToken(InfluxDbDeviceEvent.find(values, CMD_COMMAND_TOKEN));
	event.setStatus(CommandStatus.valueOf(InfluxDbDeviceEvent.find(values, CMD_STATUS)));

	// Copy parameter values.
	for (String key : values.keySet()) {
	    if (key.startsWith(CMD_PARAMETER_VALUE_PREFIX)) {
		String name = key.substring(CMD_PARAMETER_VALUE_PREFIX.length());
		String value = InfluxDbDeviceEvent.find(values, key);
		event.getParameterValues().put(name, value);
	    }
	}

	InfluxDbDeviceEvent.loadFromMap(event, values);
    }

    /**
     * Save fields to builder.
     * 
     * @param event
     * @param builder
     * @throws SiteWhereException
     */
    public static void saveToBuilder(DeviceCommandInvocation event, Point.Builder builder) throws SiteWhereException {
	builder.tag(CMD_INITIATOR, event.getInitiator().name());
	if (event.getInitiatorId() != null) {
	    builder.tag(CMD_INITIATOR_ID, event.getInitiatorId());
	}
	builder.tag(CMD_TARGET, event.getTarget().name());
	if (event.getTargetId() != null) {
	    builder.tag(CMD_TARGET_ID, event.getTargetId());
	}
	builder.tag(CMD_COMMAND_TOKEN, event.getCommandToken());
	builder.tag(CMD_STATUS, event.getStatus().name());

	for (String key : event.getParameterValues().keySet()) {
	    String value = event.getParameterValues().get(key);
	    builder.field(CMD_PARAMETER_VALUE_PREFIX + key, value);
	}

	InfluxDbDeviceEvent.saveToBuilder(event, builder);
    }
}