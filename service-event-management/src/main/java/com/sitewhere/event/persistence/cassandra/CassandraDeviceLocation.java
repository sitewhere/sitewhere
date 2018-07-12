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
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceLocation;

/**
 * Handles mapping of device location event fields to Cassandra records.
 * 
 * @author Derek
 */
public class CassandraDeviceLocation implements ICassandraEventBinder<IDeviceLocation> {

    /** Static instance */
    public static final ICassandraEventBinder<IDeviceLocation> INSTANCE = new CassandraDeviceLocation();

    // Location field.
    public static final String FIELD_LOCATION = "location";

    // Latitude field.
    public static final String FIELD_LATITUDE = "latitude";

    // Longitude field.
    public static final String FIELD_LONGITUDE = "longitude";

    // Elevation field.
    public static final String FIELD_ELEVATION = "elevation";

    /*
     * @see
     * com.sitewhere.event.persistence.cassandra.ICassandraEventBinder#bind(com.
     * sitewhere.event.persistence.cassandra.CassandraEventManagementClient,
     * com.datastax.driver.core.BoundStatement,
     * com.sitewhere.spi.device.event.IDeviceEvent)
     */
    @Override
    public void bind(CassandraEventManagementClient client, BoundStatement bound, IDeviceLocation event)
	    throws SiteWhereException {
	CassandraDeviceLocation.bindFields(client, bound, event);
    }

    /**
     * Bind fields from a device location to an existing {@link BoundStatement}.
     * 
     * @param client
     * @param bound
     * @param location
     * @throws SiteWhereException
     */
    public static void bindFields(CassandraEventManagementClient client, BoundStatement bound, IDeviceLocation location)
	    throws SiteWhereException {
	CassandraDeviceEvent.bindEventFields(bound, location);

	UDTValue udt = client.getLocationType().newValue();
	udt.setDouble(FIELD_LATITUDE, location.getLatitude());
	udt.setDouble(FIELD_LONGITUDE, location.getLongitude());
	udt.setDouble(FIELD_ELEVATION, location.getElevation());
	bound.setUDTValue(FIELD_LOCATION, udt);
    }

    /**
     * Load fields from a row into a device location.
     * 
     * @param client
     * @param location
     * @param row
     * @throws SiteWhereException
     */
    public static void loadFields(CassandraEventManagementClient client, DeviceLocation location, Row row)
	    throws SiteWhereException {
	CassandraDeviceEvent.loadEventFields(location, row);

	UDTValue udt = row.getUDTValue(FIELD_LOCATION);
	location.setLatitude(udt.getDouble(FIELD_LATITUDE));
	location.setLongitude(udt.getDouble(FIELD_LONGITUDE));
	location.setElevation(udt.getDouble(FIELD_ELEVATION));
    }
}