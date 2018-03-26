/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.persistence.cassandra;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.UDTValue;
import com.sitewhere.cassandra.CassandraClient;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceLocation;

public class CassandraDeviceLocation {

    /**
     * Bind fields from a device location to an existing {@link BoundStatement}.
     * 
     * @param client
     * @param bound
     * @param location
     * @throws SiteWhereException
     */
    public static void bindFields(CassandraClient client, BoundStatement bound, IDeviceLocation location)
	    throws SiteWhereException {
	CassandraDeviceEvent.bindEventFields(bound, location);

	UDTValue udt = client.getLocationType().newValue();
	udt.setDouble("latitude", location.getLatitude());
	udt.setDouble("longitude", location.getLongitude());
	udt.setDouble("elevation", location.getElevation());
	bound.setUDTValue("location", udt);
    }
}