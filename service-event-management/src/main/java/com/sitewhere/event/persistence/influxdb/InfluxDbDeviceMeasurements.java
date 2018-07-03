/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.persistence.influxdb;

import java.util.Map;

import org.influxdb.dto.Point;

import com.sitewhere.rest.model.device.event.DeviceMeasurement;
import com.sitewhere.spi.SiteWhereException;

/**
 * Class for saving device measurements data to InfluxDB.
 * 
 * @author Derek
 */
public class InfluxDbDeviceMeasurements {

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
	InfluxDbDeviceMeasurements.loadFromMap(mxs, values);
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
	event.setValue((Double) values.get(MX_VALUE));
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
	builder.addField(MX_VALUE, event.getValue());
	InfluxDbDeviceEvent.saveToBuilder(event, builder);
    }
}