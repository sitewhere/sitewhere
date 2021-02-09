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
import com.datastax.driver.core.UDTValue;
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.AlertLevel;
import com.sitewhere.spi.device.event.AlertSource;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceAlert;

/**
 * Handles mapping of device alert event fields to Cassandra records.
 */
public class CassandraDeviceAlert implements ICassandraEventBinder<IDeviceAlert> {

    /** Static instance */
    public static final ICassandraEventBinder<IDeviceAlert> INSTANCE = new CassandraDeviceAlert();

    // Alert field.
    public static final String FIELD_ALERT = "alert";

    // Source field.
    public static final String FIELD_SOURCE = "source";

    // Level field.
    public static final String FIELD_LEVEL = "level";

    // Type field.
    public static final String FIELD_TYPE = "type";

    // Message field.
    public static final String FIELD_MESSAGE = "message";

    /*
     * @see
     * com.sitewhere.event.persistence.cassandra.ICassandraEventBinder#bind(com.
     * sitewhere.event.persistence.cassandra.CassandraEventManagementClient,
     * com.datastax.driver.core.BoundStatement,
     * com.sitewhere.spi.device.event.IDeviceEvent)
     */
    @Override
    public void bind(CassandraEventManagementClient client, BoundStatement bound, IDeviceAlert event)
	    throws SiteWhereException {
	CassandraDeviceAlert.bindFields(client, bound, event);
    }

    /*
     * @see
     * com.sitewhere.event.persistence.cassandra.ICassandraEventBinder#load(com.
     * sitewhere.event.persistence.cassandra.CassandraEventManagementClient,
     * com.datastax.driver.core.Row)
     */
    @Override
    public IDeviceAlert load(CassandraEventManagementClient client, Row row) throws SiteWhereException {
	DeviceAlert event = new DeviceAlert();
	CassandraDeviceAlert.loadFields(client, event, row);
	return event;
    }

    /*
     * @see
     * com.sitewhere.event.persistence.cassandra.ICassandraEventBinder#getEventType(
     * )
     */
    @Override
    public DeviceEventType getEventType() {
	return DeviceEventType.Alert;
    }

    /**
     * Bind fields from a device alert to an existing {@link BoundStatement}.
     * 
     * @param client
     * @param bound
     * @param alert
     * @throws SiteWhereException
     */
    public static void bindFields(CassandraEventManagementClient client, BoundStatement bound, IDeviceAlert alert)
	    throws SiteWhereException {
	CassandraDeviceEvent.bindEventFields(bound, alert);

	UDTValue udt = client.getAlertType().newValue();
	udt.setByte(FIELD_SOURCE, getIndicatorForAlertSource(alert.getSource()));
	udt.setByte(FIELD_LEVEL, getIndicatorForAlertLevel(alert.getLevel()));
	udt.setString(FIELD_TYPE, alert.getType());
	udt.setString(FIELD_MESSAGE, alert.getMessage());
	bound.setUDTValue(FIELD_ALERT, udt);
    }

    /**
     * Load fields from a row into a device alert.
     * 
     * @param client
     * @param alert
     * @param row
     * @throws SiteWhereException
     */
    public static void loadFields(CassandraEventManagementClient client, DeviceAlert alert, Row row)
	    throws SiteWhereException {
	CassandraDeviceEvent.loadEventFields(alert, row);

	UDTValue udt = row.getUDTValue(FIELD_ALERT);
	alert.setSource(getAlertSourceForIndicator(udt.getByte(FIELD_SOURCE)));
	alert.setLevel(getAlertLevelForIndicator(udt.getByte(FIELD_LEVEL)));
	alert.setType(udt.getString(FIELD_TYPE));
	alert.setMessage(udt.getString(FIELD_MESSAGE));
    }

    /**
     * Get indicator value for alert source.
     * 
     * @param source
     * @return
     * @throws SiteWhereException
     */
    public static Byte getIndicatorForAlertSource(AlertSource source) throws SiteWhereException {
	switch (source) {
	case Device: {
	    return 0;
	}
	case System: {
	    return 1;
	}
	default: {
	    throw new SiteWhereException("Unsupported alert source: " + source.name());
	}
	}
    }

    /**
     * Get alert source for indicator value.
     * 
     * @param value
     * @return
     * @throws SiteWhereException
     */
    public static AlertSource getAlertSourceForIndicator(Byte value) throws SiteWhereException {
	if (value == 0) {
	    return AlertSource.Device;
	} else if (value == 1) {
	    return AlertSource.System;
	}
	throw new SiteWhereException("Unsupported alert source: " + value);
    }

    /**
     * Get indicator value for alert level.
     * 
     * @param level
     * @return
     * @throws SiteWhereException
     */
    public static Byte getIndicatorForAlertLevel(AlertLevel level) throws SiteWhereException {
	switch (level) {
	case Info: {
	    return 0;
	}
	case Warning: {
	    return 1;
	}
	case Error: {
	    return 2;
	}
	case Critical: {
	    return 3;
	}
	default: {
	    throw new SiteWhereException("Unsupported alert level: " + level.name());
	}
	}
    }

    /**
     * Get alert level for indicator value.
     * 
     * @param value
     * @return
     * @throws SiteWhereException
     */
    public static AlertLevel getAlertLevelForIndicator(Byte value) throws SiteWhereException {
	if (value == 0) {
	    return AlertLevel.Info;
	} else if (value == 1) {
	    return AlertLevel.Warning;
	} else if (value == 2) {
	    return AlertLevel.Error;
	} else if (value == 3) {
	    return AlertLevel.Critical;
	}
	throw new SiteWhereException("Unsupported alert level: " + value);
    }
}
