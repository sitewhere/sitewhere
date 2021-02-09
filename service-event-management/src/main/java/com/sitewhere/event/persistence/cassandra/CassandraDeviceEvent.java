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
package com.sitewhere.event.persistence.cassandra;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Row;
import com.sitewhere.rest.model.device.event.DeviceEvent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceEvent;

public class CassandraDeviceEvent {

    // Device id field.
    public static final String FIELD_DEVICE_ID = "device_id";

    // Event id field.
    public static final String FIELD_EVENT_ID = "event_id";

    // Alternate id field.
    public static final String FIELD_ALTERNATE_ID = "alt_id";

    // Event type field.
    public static final String FIELD_EVENT_TYPE = "event_type";

    // Assignment id field.
    public static final String FIELD_ASSIGNMENT_ID = "assignment_id";

    // Customer id field.
    public static final String FIELD_CUSTOMER_ID = "customer_id";

    // Area id field.
    public static final String FIELD_AREA_ID = "area_id";

    // Asset id field.
    public static final String FIELD_ASSET_ID = "asset_id";

    // Event date field.
    public static final String FIELD_EVENT_DATE = "event_date";

    // Received date field.
    public static final String FIELD_RECEIVED_DATE = "received_date";

    /**
     * Bind fields from a device event to an existing {@link BoundStatement}.
     * 
     * @param bound
     * @param event
     * @throws SiteWhereException
     */
    public static void bindEventFields(BoundStatement bound, IDeviceEvent event) throws SiteWhereException {
	bound.setUUID(FIELD_DEVICE_ID, event.getDeviceId());
	bound.setUUID(FIELD_EVENT_ID, event.getId());
	if (event.getAlternateId() != null) {
	    bound.setString(FIELD_ALTERNATE_ID, event.getAlternateId());
	}
	bound.setByte(FIELD_EVENT_TYPE, getIndicatorForEventType(event.getEventType()));
	bound.setUUID(FIELD_ASSIGNMENT_ID, event.getDeviceAssignmentId());
	if (event.getCustomerId() != null) {
	    bound.setUUID(FIELD_CUSTOMER_ID, event.getCustomerId());
	}
	if (event.getAreaId() != null) {
	    bound.setUUID(FIELD_AREA_ID, event.getAreaId());
	}
	if (event.getAssetId() != null) {
	    bound.setUUID(FIELD_ASSET_ID, event.getAssetId());
	}
	bound.setTimestamp(FIELD_EVENT_DATE, event.getEventDate());
	bound.setTimestamp(FIELD_RECEIVED_DATE, event.getReceivedDate());
    }

    /**
     * Load fields from a row into a device event.
     * 
     * @param event
     * @param row
     * @throws SiteWhereException
     */
    public static void loadEventFields(DeviceEvent event, Row row) throws SiteWhereException {
	event.setDeviceId(row.getUUID(FIELD_DEVICE_ID));
	event.setId(row.getUUID(FIELD_EVENT_ID));
	event.setAlternateId(row.getString(FIELD_ALTERNATE_ID));
	event.setEventType(getEventTypeForIndicator(row.getByte(FIELD_EVENT_TYPE)));
	event.setDeviceAssignmentId(row.getUUID(FIELD_ASSIGNMENT_ID));
	event.setCustomerId(row.getUUID(FIELD_CUSTOMER_ID));
	event.setAreaId(row.getUUID(FIELD_AREA_ID));
	event.setAssetId(row.getUUID(FIELD_ASSET_ID));
	event.setEventDate(row.getTimestamp(FIELD_EVENT_DATE));
	event.setReceivedDate(row.getTimestamp(FIELD_RECEIVED_DATE));
    }

    /**
     * Get indicator value for event type.
     * 
     * @param type
     * @return
     * @throws SiteWhereException
     */
    public static Byte getIndicatorForEventType(DeviceEventType type) throws SiteWhereException {
	switch (type) {
	case Measurement: {
	    return 0;
	}
	case Location: {
	    return 1;
	}
	case Alert: {
	    return 2;
	}
	case CommandInvocation: {
	    return 3;
	}
	case CommandResponse: {
	    return 4;
	}
	case StateChange: {
	    return 5;
	}
	default: {
	    throw new SiteWhereException("Unsupported event type: " + type.name());
	}
	}
    }

    /**
     * Get type for indicator value.
     * 
     * @param value
     * @return
     * @throws SiteWhereException
     */
    public static DeviceEventType getEventTypeForIndicator(Byte value) throws SiteWhereException {
	if (value == 0) {
	    return DeviceEventType.Measurement;
	} else if (value == 1) {
	    return DeviceEventType.Location;
	} else if (value == 2) {
	    return DeviceEventType.Alert;
	} else if (value == 3) {
	    return DeviceEventType.CommandInvocation;
	} else if (value == 4) {
	    return DeviceEventType.CommandResponse;
	} else if (value == 5) {
	    return DeviceEventType.StateChange;
	}
	throw new SiteWhereException("Unsupported event type: " + value);
    }
}
