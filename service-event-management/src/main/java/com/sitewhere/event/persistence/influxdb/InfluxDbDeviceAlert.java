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