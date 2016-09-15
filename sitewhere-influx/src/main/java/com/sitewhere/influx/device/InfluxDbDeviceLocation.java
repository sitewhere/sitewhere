/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.influx.device;

import java.util.Map;

import org.influxdb.dto.Point;

import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.DeviceEventType;

/**
 * Class for saving device location data to InfluxDB.
 * 
 * @author Derek
 */
public class InfluxDbDeviceLocation {

    /** Location latitude field */
    public static final String LOCATION_LATITUDE = "latitude";

    /** Location longitude field */
    public static final String LOCATION_LONGITUDE = "longitude";

    /** Location elevation field */
    public static final String LOCATION_ELEVATION = "elevation";

    /**
     * Parse domain object from a value map.
     * 
     * @param values
     * @return
     * @throws SiteWhereException
     */
    public static DeviceLocation parse(Map<String, Object> values) throws SiteWhereException {
	DeviceLocation location = new DeviceLocation();
	InfluxDbDeviceLocation.loadFromMap(location, values);
	return location;
    }

    /**
     * Load fields from value map.
     * 
     * @param event
     * @param values
     * @throws SiteWhereException
     */
    public static void loadFromMap(DeviceLocation event, Map<String, Object> values) throws SiteWhereException {
	event.setEventType(DeviceEventType.Location);
	event.setLatitude((Double) values.get(LOCATION_LATITUDE));
	event.setLongitude((Double) values.get(LOCATION_LONGITUDE));
	event.setElevation((Double) values.get(LOCATION_ELEVATION));
	InfluxDbDeviceEvent.loadFromMap(event, values);
    }

    /**
     * Save ields to builder.
     * 
     * @param event
     * @param builder
     * @throws SiteWhereException
     */
    public static void saveToBuilder(DeviceLocation event, Point.Builder builder) throws SiteWhereException {
	builder.field(LOCATION_LATITUDE, event.getLatitude());
	builder.field(LOCATION_LONGITUDE, event.getLongitude());
	builder.field(LOCATION_ELEVATION, event.getElevation());
	InfluxDbDeviceEvent.saveToBuilder(event, builder);
    }
}