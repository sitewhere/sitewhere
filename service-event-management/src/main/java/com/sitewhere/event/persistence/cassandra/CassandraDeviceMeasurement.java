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

import java.math.BigDecimal;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.UDTValue;
import com.sitewhere.rest.model.device.event.DeviceMeasurement;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceMeasurement;

/**
 * Handles mapping of device measurements event fields to Cassandra records.
 */
public class CassandraDeviceMeasurement implements ICassandraEventBinder<IDeviceMeasurement> {

    /** Static instance */
    public static final ICassandraEventBinder<IDeviceMeasurement> INSTANCE = new CassandraDeviceMeasurement();

    // Measurement field.
    public static final String FIELD_MEASUREMENT = "measurement";

    // Measurement name field.
    public static final String FIELD_MXNAME = "mxname";

    // Measurement value field.
    public static final String FIELD_MXVALUE = "mxvalue";

    /*
     * @see
     * com.sitewhere.event.persistence.cassandra.ICassandraEventBinder#bind(com.
     * sitewhere.event.persistence.cassandra.CassandraEventManagementClient,
     * com.datastax.driver.core.BoundStatement,
     * com.sitewhere.spi.device.event.IDeviceEvent)
     */
    @Override
    public void bind(CassandraEventManagementClient client, BoundStatement bound, IDeviceMeasurement event)
	    throws SiteWhereException {
	CassandraDeviceMeasurement.bindFields(client, bound, event);
    }

    /*
     * @see
     * com.sitewhere.event.persistence.cassandra.ICassandraEventBinder#load(com.
     * sitewhere.event.persistence.cassandra.CassandraEventManagementClient,
     * com.datastax.driver.core.Row)
     */
    @Override
    public IDeviceMeasurement load(CassandraEventManagementClient client, Row row) throws SiteWhereException {
	DeviceMeasurement event = new DeviceMeasurement();
	CassandraDeviceMeasurement.loadFields(client, event, row);
	return event;
    }

    /*
     * @see
     * com.sitewhere.event.persistence.cassandra.ICassandraEventBinder#getEventType(
     * )
     */
    @Override
    public DeviceEventType getEventType() {
	return DeviceEventType.Measurement;
    }

    /**
     * Bind fields from a device measurements event to an existing
     * {@link BoundStatement}.
     * 
     * @param client
     * @param bound
     * @param mx
     * @throws SiteWhereException
     */
    public static void bindFields(CassandraEventManagementClient client, BoundStatement bound, IDeviceMeasurement mx)
	    throws SiteWhereException {
	CassandraDeviceEvent.bindEventFields(bound, mx);

	UDTValue udt = client.getMeasurementType().newValue();
	udt.setString(FIELD_MXNAME, mx.getName());
	udt.setDouble(FIELD_MXVALUE, mx.getValue().doubleValue());
	bound.setUDTValue(FIELD_MEASUREMENT, udt);
    }

    /**
     * Load fields from a row into a device alert.
     * 
     * @param client
     * @param mx
     * @param row
     * @throws SiteWhereException
     */
    public static void loadFields(CassandraEventManagementClient client, DeviceMeasurement mx, Row row)
	    throws SiteWhereException {
	CassandraDeviceEvent.loadEventFields(mx, row);

	UDTValue udt = row.getUDTValue(FIELD_MEASUREMENT);
	mx.setName(udt.getString(FIELD_MXNAME));
	mx.setValue(new BigDecimal(udt.getDouble(FIELD_MXVALUE)));
    }
}
