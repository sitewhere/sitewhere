/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicestate.persistence.mongodb;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bson.Document;

import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.rest.model.device.state.DeviceState;
import com.sitewhere.spi.device.state.IDeviceState;

/**
 * Used to load or save assignment state to MongoDB.
 * 
 * @author dadams
 */
public class MongoDeviceState implements MongoConverter<IDeviceState> {

    /** Property for id */
    public static final String PROP_ID = "_id";

    /** Property for device id */
    public static final String PROP_DEVICE_ID = "dvid";

    /** Property for device type id */
    public static final String PROP_DEVICE_TYPE_ID = "dtid";

    /** Property for device assignment id */
    public static final String PROP_DEVICE_ASSIGNMENT_ID = "asid";

    /** Property for customer id */
    public static final String PROP_CUSTOMER_ID = "csid";

    /** Property for area id */
    public static final String PROP_AREA_ID = "arid";

    /** Property for asset id */
    public static final String PROP_ASSET_ID = "assd";

    /** Property for last interaction date */
    public static final String PROP_LAST_INTERACTION_DATE = "last";

    /** Property for presence missing date */
    public static final String PROP_PRESENCE_MISSING_DATE = "miss";

    /** Property for last location */
    public static final String PROP_LAST_LOCATION_ID = "lloc";

    /** Property for latest measurements */
    public static final String PROP_LAST_MEASUREMENT_IDS = "lmxs";

    /** Property for latest measurements */
    public static final String PROP_LAST_ALERT_IDS = "lalt";

    /** Attribute name for map element name */
    public static final String PROP_NAME = "name";

    /** Attribute name for map element value */
    public static final String PROP_VALUE = "valu";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
     */
    @Override
    public Document convert(IDeviceState source) {
	return MongoDeviceState.toDocument(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(org.bson.Document)
     */
    @Override
    public IDeviceState convert(Document source) {
	return MongoDeviceState.fromDocument(source);
    }

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     */
    public static void toDocument(IDeviceState source, Document target) {
	target.append(PROP_ID, source.getId());
	target.append(PROP_DEVICE_ID, source.getDeviceId());
	target.append(PROP_DEVICE_TYPE_ID, source.getDeviceTypeId());
	target.append(PROP_DEVICE_ASSIGNMENT_ID, source.getDeviceAssignmentId());
	target.append(PROP_CUSTOMER_ID, source.getCustomerId());
	target.append(PROP_AREA_ID, source.getAreaId());
	target.append(PROP_ASSET_ID, source.getAssetId());
	target.append(PROP_LAST_INTERACTION_DATE, source.getLastInteractionDate());
	target.append(PROP_PRESENCE_MISSING_DATE, source.getPresenceMissingDate());
	target.append(PROP_LAST_LOCATION_ID, source.getLastLocationEventId());

	List<Document> mxs = new ArrayList<Document>();
	for (String key : source.getLastMeasurementEventIds().keySet()) {
	    Document prop = new Document();
	    prop.put(PROP_NAME, key);
	    prop.put(PROP_VALUE, source.getLastMeasurementEventIds().get(key));
	    mxs.add(prop);
	}
	target.append(PROP_LAST_MEASUREMENT_IDS, mxs);

	List<Document> alerts = new ArrayList<Document>();
	for (String key : source.getLastAlertEventIds().keySet()) {
	    Document prop = new Document();
	    prop.put(PROP_NAME, key);
	    prop.put(PROP_VALUE, source.getLastAlertEventIds().get(key));
	    alerts.add(prop);
	}
	target.append(PROP_LAST_ALERT_IDS, alerts);
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     */
    @SuppressWarnings("unchecked")
    public static void fromDocument(Document source, DeviceState target) {
	UUID id = (UUID) source.get(PROP_ID);
	UUID deviceId = (UUID) source.get(PROP_DEVICE_ID);
	UUID deviceTypeId = (UUID) source.get(PROP_DEVICE_TYPE_ID);
	UUID deviceAssignmentId = (UUID) source.get(PROP_DEVICE_ASSIGNMENT_ID);
	UUID customerId = (UUID) source.get(PROP_CUSTOMER_ID);
	UUID areaId = (UUID) source.get(PROP_AREA_ID);
	UUID assetId = (UUID) source.get(PROP_ASSET_ID);
	Date lastInteractionDate = (Date) source.get(PROP_LAST_INTERACTION_DATE);
	Date presenceMissingDate = (Date) source.get(PROP_PRESENCE_MISSING_DATE);

	target.setId(id);
	target.setDeviceId(deviceId);
	target.setDeviceTypeId(deviceTypeId);
	target.setDeviceAssignmentId(deviceAssignmentId);
	target.setCustomerId(customerId);
	target.setAreaId(areaId);
	target.setAssetId(assetId);
	target.setLastInteractionDate(lastInteractionDate);
	target.setPresenceMissingDate(presenceMissingDate);

	List<Document> mxs = (List<Document>) source.get(PROP_LAST_MEASUREMENT_IDS);
	Map<String, UUID> lastMeasurementIds = new HashMap<>();
	if (mxs != null) {
	    for (Document entry : mxs) {
		String name = (String) entry.get(PROP_NAME);
		UUID value = (UUID) entry.get(PROP_VALUE);
		lastMeasurementIds.put(name, value);
	    }
	}
	target.setLastMeasurementEventIds(lastMeasurementIds);

	List<Document> alerts = (List<Document>) source.get(PROP_LAST_ALERT_IDS);
	Map<String, UUID> lastAlertIds = new HashMap<>();
	if (alerts != null) {
	    for (Document entry : alerts) {
		String name = (String) entry.get(PROP_NAME);
		UUID value = (UUID) entry.get(PROP_VALUE);
		lastAlertIds.put(name, value);
	    }
	}
	target.setLastAlertEventIds(lastAlertIds);
    }

    /**
     * Convert SPI object to Mongo {@link Document}.
     * 
     * @param source
     * @return
     */
    public static Document toDocument(IDeviceState source) {
	Document result = new Document();
	MongoDeviceState.toDocument(source, result);
	return result;
    }

    /**
     * Convert a {@link Document} into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static DeviceState fromDocument(Document source) {
	DeviceState result = new DeviceState();
	MongoDeviceState.fromDocument(source, result);
	return result;
    }
}