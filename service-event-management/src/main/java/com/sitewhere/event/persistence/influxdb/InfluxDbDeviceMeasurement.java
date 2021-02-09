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

import com.sitewhere.rest.model.device.event.DeviceMeasurement;
import com.sitewhere.spi.SiteWhereException;

/**
 * Class for saving device measurement data to InfluxDB.
 */
public class InfluxDbDeviceMeasurement {

    /** Measurement name field */
    public static final String MX_NAME = "mxname";

    /** Measurement value field */
    public static final String MX_VALUE = "mxvalue";

    /**
     * Parse domain object from a value map.
     * 
     * @param values
     * @return
     * @throws SiteWhereException
     */
    public static DeviceMeasurement parse(Map<String, Object> values) throws SiteWhereException {
	DeviceMeasurement mxs = new DeviceMeasurement();
	InfluxDbDeviceMeasurement.loadFromMap(mxs, values);
	return mxs;
    }

    /**
     * Load fields from value map.
     * 
     * @param event
     * @param values
     * @throws SiteWhereException
     */
    public static void loadFromMap(DeviceMeasurement event, Map<String, Object> values) throws SiteWhereException {
	event.setName((String) values.get(MX_NAME));
	event.setValue(new BigDecimal((Double) values.get(MX_VALUE)));
	InfluxDbDeviceEvent.loadFromMap(event, values);
    }

    /**
     * Save ields to builder.
     * 
     * @param event
     * @param builder
     * @throws SiteWhereException
     */
    public static void saveToBuilder(DeviceMeasurement event, Point.Builder builder) throws SiteWhereException {
	builder.addField(MX_NAME, event.getName());
	builder.addField(MX_VALUE, event.getValue().doubleValue());
	InfluxDbDeviceEvent.saveToBuilder(event, builder);
    }
}