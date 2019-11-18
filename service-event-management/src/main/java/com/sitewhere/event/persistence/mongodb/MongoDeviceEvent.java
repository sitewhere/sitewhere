/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.persistence.mongodb;

import java.util.Date;
import java.util.UUID;

import org.bson.Document;

import com.sitewhere.mongodb.common.MongoMetadataProvider;
import com.sitewhere.rest.model.device.event.DeviceEvent;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceEvent;

/**
 * Used to load or save device event data to MongoDB.
 */
public class MongoDeviceEvent {

    /** Property for id */
    public static final String PROP_ID = "_id";

    /** Alternate (external) id */
    public static final String PROP_ALTERNATE_ID = "alid";

    /** Event type indicator */
    public static final String PROP_EVENT_TYPE = "evty";

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

    /** Property for time measurements were taken */
    public static final String PROP_EVENT_DATE = "evdt";

    /** Property for time measurements were received */
    public static final String PROP_RECEIVED_DATE = "rcdt";

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     * @param isNested
     */
    public static void toDocument(IDeviceEvent source, Document target, boolean isNested) {
	target.append(PROP_ID, source.getId());

	// Only set if there is a value (sparse index).
	if (source.getAlternateId() != null) {
	    target.append(PROP_ALTERNATE_ID, source.getAlternateId());
	}

	target.append(PROP_EVENT_TYPE, source.getEventType().name());
	target.append(PROP_DEVICE_ID, source.getDeviceId());
	target.append(PROP_DEVICE_ASSIGNMENT_ID, source.getDeviceAssignmentId());
	target.append(PROP_CUSTOMER_ID, source.getCustomerId());
	target.append(PROP_AREA_ID, source.getAreaId());
	target.append(PROP_ASSET_ID, source.getAssetId());
	target.append(PROP_EVENT_DATE, source.getEventDate());
	target.append(PROP_RECEIVED_DATE, source.getReceivedDate());

	MongoMetadataProvider.toDocument(source, target);
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     * @param isNested
     */
    public static void fromDocument(Document source, DeviceEvent target, boolean isNested) {
	UUID id = (UUID) source.get(PROP_ID);
	String alternateId = (String) source.get(PROP_ALTERNATE_ID);
	String eventType = (String) source.get(PROP_EVENT_TYPE);
	UUID deviceId = (UUID) source.get(PROP_DEVICE_ID);
	UUID assignmentId = (UUID) source.get(PROP_DEVICE_ASSIGNMENT_ID);
	UUID customerId = (UUID) source.get(PROP_CUSTOMER_ID);
	UUID areaId = (UUID) source.get(PROP_AREA_ID);
	UUID assetId = (UUID) source.get(PROP_ASSET_ID);
	Date eventDate = (Date) source.get(PROP_EVENT_DATE);
	Date receivedDate = (Date) source.get(PROP_RECEIVED_DATE);

	target.setId(id);
	if (eventType != null) {
	    target.setEventType(DeviceEventType.valueOf(eventType));
	}
	target.setAlternateId(alternateId);
	target.setDeviceId(deviceId);
	target.setDeviceAssignmentId(assignmentId);
	target.setCustomerId(customerId);
	target.setAreaId(areaId);
	target.setAssetId(assetId);
	target.setEventDate(eventDate);
	target.setReceivedDate(receivedDate);

	MongoMetadataProvider.fromDocument(source, target);
    }
}