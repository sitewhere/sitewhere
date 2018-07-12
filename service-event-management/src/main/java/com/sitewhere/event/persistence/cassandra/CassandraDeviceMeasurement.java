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
import com.sitewhere.rest.model.device.event.DeviceMeasurement;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceMeasurement;

/**
 * Handles mapping of device measurements event fields to Cassandra records.
 * 
 * @author Derek
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
	udt.setDouble(FIELD_MXVALUE, mx.getValue());
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
	mx.setValue(udt.getDouble(FIELD_MXVALUE));
    }
}
