package com.sitewhere.event.persistence.influxdb;

import java.util.Map;

import org.influxdb.dto.Point;

import com.sitewhere.rest.model.device.event.DeviceStateChange;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.state.StateChangeCategory;
import com.sitewhere.spi.device.event.state.StateChangeType;

/**
 * Class for saving device state change data to InfluxDB.
 * 
 * @author Derek
 */
public class InfluxDbDeviceStateChange {

    /** State change category field */
    public static final String STATE_CHANGE_CATEGORY = "sc_category";

    /** State change type field */
    public static final String STATE_CHANGE_TYPE = "sc_type";

    /** Previous value field */
    public static final String PREVIOUS_VALUE = "previous";

    /** Updated value field */
    public static final String UPDATED_VALUE = "updated";

    /**
     * Parse domain object from a value map.
     * 
     * @param values
     * @return
     * @throws SiteWhereException
     */
    public static DeviceStateChange parse(Map<String, Object> values) throws SiteWhereException {
	DeviceStateChange location = new DeviceStateChange();
	InfluxDbDeviceStateChange.loadFromMap(location, values);
	return location;
    }

    /**
     * Load fields from value map.
     * 
     * @param event
     * @param values
     * @throws SiteWhereException
     */
    public static void loadFromMap(DeviceStateChange event, Map<String, Object> values) throws SiteWhereException {
	event.setEventType(DeviceEventType.StateChange);
	event.setCategory(StateChangeCategory.valueOf((String) values.get(STATE_CHANGE_CATEGORY)));
	event.setType(StateChangeType.valueOf((String) values.get(STATE_CHANGE_TYPE)));
	event.setPreviousState((String) values.get(PREVIOUS_VALUE));
	event.setNewState((String) values.get(UPDATED_VALUE));
	InfluxDbDeviceEvent.loadFromMap(event, values);
    }

    /**
     * Save fields to builder.
     * 
     * @param event
     * @param builder
     * @throws SiteWhereException
     */
    public static void saveToBuilder(DeviceStateChange event, Point.Builder builder) throws SiteWhereException {
	builder.tag(STATE_CHANGE_CATEGORY, event.getCategory().name());
	builder.tag(STATE_CHANGE_TYPE, event.getType().name());
	if (event.getPreviousState() != null) {
	    builder.addField(PREVIOUS_VALUE, event.getPreviousState());
	}
	if (event.getNewState() != null) {
	    builder.addField(UPDATED_VALUE, event.getNewState());
	}
	InfluxDbDeviceEvent.saveToBuilder(event, builder);
    }
}