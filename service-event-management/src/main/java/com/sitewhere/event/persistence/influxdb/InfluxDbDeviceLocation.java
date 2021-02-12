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

import java.math.BigDecimal;
import java.util.Map;

import org.influxdb.dto.Point;

import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.DeviceEventType;

/**
 * Class for saving device location data to InfluxDB.
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
	event.setLatitude(new BigDecimal((Double) values.get(LOCATION_LATITUDE)));
	event.setLongitude(new BigDecimal((Double) values.get(LOCATION_LONGITUDE)));
	event.setElevation(new BigDecimal((Double) values.get(LOCATION_ELEVATION)));
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
	builder.addField(LOCATION_LATITUDE, event.getLatitude().doubleValue());
	builder.addField(LOCATION_LONGITUDE, event.getLongitude().doubleValue());
	builder.addField(LOCATION_ELEVATION, event.getElevation().doubleValue());
	InfluxDbDeviceEvent.saveToBuilder(event, builder);
    }
}