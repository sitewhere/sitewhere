/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.event.persistence.influxdb;

import java.util.Map;

import org.influxdb.dto.Point;

import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.CommandInitiator;
import com.sitewhere.spi.device.event.CommandTarget;
import com.sitewhere.spi.device.event.DeviceEventType;

/**
 * Class for saving device command invocation data to InfluxDB.
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

    /** Tag for command id */
    public static final String CMD_COMMAND_ID = "command";

    /** Field prefix for parameter values */
    public static final String CMD_PARAMETER_VALUE_PREFIX = "param:";

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
	event.setDeviceCommandId(
		InfluxDbDeviceEvent.convertUUID((String) InfluxDbDeviceEvent.find(values, CMD_COMMAND_ID)));

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
	builder.tag(CMD_COMMAND_ID, event.getDeviceCommandId().toString());

	for (String key : event.getParameterValues().keySet()) {
	    String value = event.getParameterValues().get(key);
	    builder.addField(CMD_PARAMETER_VALUE_PREFIX + key, value);
	}

	InfluxDbDeviceEvent.saveToBuilder(event, builder);
    }
}