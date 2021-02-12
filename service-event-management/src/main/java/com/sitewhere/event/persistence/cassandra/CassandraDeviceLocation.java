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
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceLocation;

/**
 * Handles mapping of device location event fields to Cassandra records.
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

    /*
     * @see
     * com.sitewhere.event.persistence.cassandra.ICassandraEventBinder#load(com.
     * sitewhere.event.persistence.cassandra.CassandraEventManagementClient,
     * com.datastax.driver.core.Row)
     */
    @Override
    public IDeviceLocation load(CassandraEventManagementClient client, Row row) throws SiteWhereException {
	DeviceLocation event = new DeviceLocation();
	CassandraDeviceLocation.loadFields(client, event, row);
	return event;
    }

    /*
     * @see
     * com.sitewhere.event.persistence.cassandra.ICassandraEventBinder#getEventType(
     * )
     */
    @Override
    public DeviceEventType getEventType() {
	return DeviceEventType.Location;
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
	udt.setDouble(FIELD_LATITUDE, location.getLatitude().doubleValue());
	udt.setDouble(FIELD_LONGITUDE, location.getLongitude().doubleValue());
	udt.setDouble(FIELD_ELEVATION, location.getElevation().doubleValue());
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
	location.setLatitude(new BigDecimal(udt.getDouble(FIELD_LATITUDE)));
	location.setLongitude(new BigDecimal(udt.getDouble(FIELD_LONGITUDE)));
	location.setElevation(new BigDecimal(udt.getDouble(FIELD_ELEVATION)));
    }
}