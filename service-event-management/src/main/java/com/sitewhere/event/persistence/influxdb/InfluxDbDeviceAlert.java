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

import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.AlertLevel;
import com.sitewhere.spi.device.event.AlertSource;
import com.sitewhere.spi.device.event.DeviceEventType;

/**
 * Class for saving device alert data to InfluxDB.
 */
public class InfluxDbDeviceAlert {

    /** Alert type tag */
    public static final String ALERT_TYPE = "alert";

    /** Alert source tag */
    public static final String ALERT_SOURCE = "source";

    /** Alert level tag */
    public static final String ALERT_LEVEL = "level";

    /** Alert message field */
    public static final String ALERT_MESSAGE = "message";

    /**
     * Parse domain object from a value map.
     * 
     * @param values
     * @return
     * @throws SiteWhereException
     */
    public static DeviceAlert parse(Map<String, Object> values) throws SiteWhereException {
	DeviceAlert alert = new DeviceAlert();
	InfluxDbDeviceAlert.loadFromMap(alert, values);
	return alert;
    }

    /**
     * Load fields from value map.
     * 
     * @param event
     * @param values
     * @throws SiteWhereException
     */
    public static void loadFromMap(DeviceAlert event, Map<String, Object> values) throws SiteWhereException {
	event.setEventType(DeviceEventType.Alert);
	event.setType((String) values.get(ALERT_TYPE));
	event.setSource(AlertSource.valueOf((String) values.get(ALERT_SOURCE)));
	event.setLevel(AlertLevel.valueOf((String) values.get(ALERT_LEVEL)));
	event.setMessage((String) values.get(ALERT_MESSAGE));
	InfluxDbDeviceEvent.loadFromMap(event, values);
    }

    /**
     * Save fields to builder.
     * 
     * @param event
     * @param builder
     * @throws SiteWhereException
     */
    public static void saveToBuilder(DeviceAlert event, Point.Builder builder) throws SiteWhereException {
	builder.tag(ALERT_TYPE, event.getType());
	builder.tag(ALERT_SOURCE, event.getSource().name());
	builder.tag(ALERT_LEVEL, event.getLevel().name());
	builder.addField(ALERT_MESSAGE, event.getMessage());
	InfluxDbDeviceEvent.saveToBuilder(event, builder);
    }
}