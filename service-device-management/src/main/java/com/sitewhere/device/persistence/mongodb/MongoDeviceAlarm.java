/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.persistence.mongodb;

import java.util.Date;
import java.util.UUID;

import org.bson.Document;

import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.mongodb.common.MongoMetadataProvider;
import com.sitewhere.rest.model.device.DeviceAlarm;
import com.sitewhere.spi.device.DeviceAlarmState;
import com.sitewhere.spi.device.IDeviceAlarm;

/**
 * Used to load or save device alarm data to MongoDB.
 * 
 * @author dadams
 */
public class MongoDeviceAlarm implements MongoConverter<IDeviceAlarm> {

    /** Property for id */
    public static final String PROP_ID = "_id";

    /** Property for device id */
    public static final String PROP_DEVICE_ID = "dvid";

    /** Property for device assignment id */
    public static final String PROP_DEVICE_ASSIGNMENT_ID = "asid";

    /** Property for customer id */
    public static final String PROP_CUSTOMER_ID = "csid";

    /** Property for area id */
    public static final String PROP_AREA_ID = "arid";

    /** Property for asset id */
    public static final String PROP_ASSET_ID = "assd";

    /** Property for alarm message */
    public static final String PROP_ALARM_MESSAGE = "almg";

    /** Property for triggering event id */
    public static final String PROP_TRIGGERING_EVENT_ID = "tgid";

    /** Property for alarm state */
    public static final String PROP_ALARM_STATE = "alst";

    /** Property for triggered date */
    public static final String PROP_TRIGGERED_DATE = "tgdt";

    /** Property for acknowledged date */
    public static final String PROP_ACKNOWLEDGED_DATE = "akdt";

    /** Property for resolved date */
    public static final String PROP_RESOLVED_DATE = "rsdt";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.dao.mongodb.MongoConverter#convert(java.lang.Object)
     */
    public Document convert(IDeviceAlarm source) {
	return MongoDeviceAlarm.toDocument(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(org.bson.Document)
     */
    public DeviceAlarm convert(Document source) {
	return MongoDeviceAlarm.fromDocument(source);
    }

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     */
    public static void toDocument(IDeviceAlarm source, Document target) {
	target.append(PROP_ID, source.getId());
	target.append(PROP_DEVICE_ID, source.getDeviceId());
	target.append(PROP_DEVICE_ASSIGNMENT_ID, source.getDeviceAssignmentId());
	target.append(PROP_CUSTOMER_ID, source.getCustomerId());
	target.append(PROP_AREA_ID, source.getAreaId());
	target.append(PROP_ASSET_ID, source.getAssetId());
	target.append(PROP_ALARM_MESSAGE, source.getAlarmMessage());
	target.append(PROP_TRIGGERING_EVENT_ID, source.getTriggeringEventId());
	target.append(PROP_ALARM_STATE, source.getState().name());
	target.append(PROP_TRIGGERED_DATE, source.getTriggeredDate());
	target.append(PROP_ACKNOWLEDGED_DATE, source.getAcknowledgedDate());
	target.append(PROP_RESOLVED_DATE, source.getResolvedDate());

	MongoMetadataProvider.toDocument(source, target);
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     */
    public static void fromDocument(Document source, DeviceAlarm target) {
	UUID id = (UUID) source.get(PROP_ID);
	UUID deviceId = (UUID) source.get(PROP_DEVICE_ID);
	UUID assignmentId = (UUID) source.get(PROP_DEVICE_ASSIGNMENT_ID);
	UUID customerId = (UUID) source.get(PROP_CUSTOMER_ID);
	UUID areaId = (UUID) source.get(PROP_AREA_ID);
	UUID assetId = (UUID) source.get(PROP_ASSET_ID);
	String alarmMessage = (String) source.get(PROP_ALARM_MESSAGE);
	UUID triggerEventId = (UUID) source.get(PROP_TRIGGERING_EVENT_ID);
	String alarmStateStr = (String) source.get(PROP_ALARM_STATE);
	Date triggeredDate = (Date) source.get(PROP_TRIGGERED_DATE);
	Date acknowledgedDate = (Date) source.get(PROP_ACKNOWLEDGED_DATE);
	Date resolvedDate = (Date) source.get(PROP_RESOLVED_DATE);

	target.setId(id);
	target.setDeviceId(deviceId);
	target.setDeviceAssignmentId(assignmentId);
	target.setCustomerId(customerId);
	target.setAreaId(areaId);
	target.setAssetId(assetId);
	target.setAlarmMessage(alarmMessage);
	target.setTriggeringEventId(triggerEventId);
	target.setState(DeviceAlarmState.valueOf(alarmStateStr));
	target.setTriggeredDate(triggeredDate);
	target.setAcknowledgedDate(acknowledgedDate);
	target.setResolvedDate(resolvedDate);

	MongoMetadataProvider.fromDocument(source, target);
    }

    /**
     * Convert SPI object to Mongo DBObject.
     * 
     * @param source
     * @return
     */
    public static Document toDocument(IDeviceAlarm source) {
	Document result = new Document();
	MongoDeviceAlarm.toDocument(source, result);
	return result;
    }

    /**
     * Convert a DBObject into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static DeviceAlarm fromDocument(Document source) {
	DeviceAlarm result = new DeviceAlarm();
	MongoDeviceAlarm.fromDocument(source, result);
	return result;
    }
}
