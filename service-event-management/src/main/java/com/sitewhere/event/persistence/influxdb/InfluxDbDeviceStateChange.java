/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.persistence.influxdb;

import java.util.Map;

import org.influxdb.dto.Point;

import com.sitewhere.rest.model.device.event.DeviceStateChange;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.DeviceEventType;

/**
 * Class for saving device state change data to InfluxDB.
 */
public class InfluxDbDeviceStateChange {

    /** State change attribute field */
    public static final String STATE_CHANGE_ATTRIBUTE = "sc_attribute";

    /** State change type field */
    public static final String STATE_CHANGE_TYPE = "sc_type";

    /** Previous value field */
    public static final String PREVIOUS_VALUE = "previous";

    /** Updated value field */
    public static final String UPDATED_VALUE = "updated";

    /**
     * Parse domain object from a value map.
     * 
     * @param values
     * @return
     * @throws SiteWhereException
     */
    public static DeviceStateChange parse(Map<String, Object> values) throws SiteWhereException {
	DeviceStateChange location = new DeviceStateChange();
	InfluxDbDeviceStateChange.loadFromMap(location, values);
	return location;
    }

    /**
     * Load fields from value map.
     * 
     * @param event
     * @param values
     * @throws SiteWhereException
     */
    public static void loadFromMap(DeviceStateChange event, Map<String, Object> values) throws SiteWhereException {
	event.setEventType(DeviceEventType.StateChange);
	event.setAttribute((String) values.get(STATE_CHANGE_ATTRIBUTE));
	event.setType((String) values.get(STATE_CHANGE_TYPE));
	event.setPreviousState((String) values.get(PREVIOUS_VALUE));
	event.setNewState((String) values.get(UPDATED_VALUE));
	InfluxDbDeviceEvent.loadFromMap(event, values);
    }

    /**
     * Save fields to builder.
     * 
     * @param event
     * @param builder
     * @throws SiteWhereException
     */
    public static void saveToBuilder(DeviceStateChange event, Point.Builder builder) throws SiteWhereException {
	builder.tag(STATE_CHANGE_ATTRIBUTE, event.getAttribute());
	builder.tag(STATE_CHANGE_TYPE, event.getType());
	if (event.getPreviousState() != null) {
	    builder.addField(PREVIOUS_VALUE, event.getPreviousState());
	}
	if (event.getNewState() != null) {
	    builder.addField(UPDATED_VALUE, event.getNewState());
	}
	InfluxDbDeviceEvent.saveToBuilder(event, builder);
    }
}