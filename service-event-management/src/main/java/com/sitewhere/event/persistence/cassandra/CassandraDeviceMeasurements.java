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
import com.sitewhere.cassandra.CassandraClient;
import com.sitewhere.rest.model.device.event.DeviceMeasurements;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceMeasurements;

/**
 * Handles mapping of device measurements event fields to Cassandra records.
 * 
 * @author Derek
 */
public class CassandraDeviceMeasurements {

    // Measurements field.
    public static final String FIELD_MEASUREMENTS = "measurements";

    // Measurement values field.
    public static final String FIELD_MXVALUES = "mx_values";

    /**
     * Bind fields from a device measurements event to an existing
     * {@link BoundStatement}.
     * 
     * @param client
     * @param bound
     * @param mxs
     * @throws SiteWhereException
     */
    public static void bindFields(CassandraClient client, BoundStatement bound, IDeviceMeasurements mxs)
	    throws SiteWhereException {
	CassandraDeviceEvent.bindEventFields(bound, mxs);

	UDTValue udt = client.getMeasurementsType().newValue();
	udt.setMap(FIELD_MXVALUES, mxs.getMeasurements());
	bound.setUDTValue(FIELD_MEASUREMENTS, udt);
    }

    /**
     * Load fields from a row into a device alert.
     * 
     * @param client
     * @param mxs
     * @param row
     * @throws SiteWhereException
     */
    public static void loadFields(CassandraClient client, DeviceMeasurements mxs, Row row) throws SiteWhereException {
	CassandraDeviceEvent.loadEventFields(mxs, row);

	UDTValue udt = row.getUDTValue(FIELD_MEASUREMENTS);
	mxs.setMeasurements(udt.getMap(FIELD_MXVALUES, String.class, Double.class));
    }
}
