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