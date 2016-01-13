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

import com.sitewhere.rest.model.device.event.DeviceMeasurements;
import com.sitewhere.spi.SiteWhereException;

/**
 * Class for saving device measurements data to InfluxDB.
 * 
 * @author Derek
 */
public class InfluxDbDeviceMeasurements {

	/** Measurement name tag prefix */
	public static final String MEASUREMENT_PREFIX = "mx:";

	/**
	 * Parse domain object from a value map.
	 * 
	 * @param values
	 * @return
	 * @throws SiteWhereException
	 */
	public static DeviceMeasurements parse(Map<String, Object> values) throws SiteWhereException {
		DeviceMeasurements mxs = new DeviceMeasurements();
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
	public static void loadFromMap(DeviceMeasurements event, Map<String, Object> values)
			throws SiteWhereException {
		for (String key : values.keySet()) {
			if (key.startsWith(MEASUREMENT_PREFIX)) {
				String name = key.substring(MEASUREMENT_PREFIX.length());
				Double value = (Double) values.get(key);
				event.addOrReplaceMeasurement(name, value);
			}
		}
		InfluxDbDeviceEvent.loadFromMap(event, values);
	}

	/**
	 * Save ields to builder.
	 * 
	 * @param event
	 * @param builder
	 * @throws SiteWhereException
	 */
	public static void saveToBuilder(DeviceMeasurements event, Point.Builder builder)
			throws SiteWhereException {
		for (String key : event.getMeasurements().keySet()) {
			Double value = event.getMeasurement(key);
			builder.field(MEASUREMENT_PREFIX + key, value);
		}
		InfluxDbDeviceEvent.saveToBuilder(event, builder);
	}
}