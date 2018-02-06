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
import org.bson.types.ObjectId;

import com.sitewhere.mongodb.common.MongoMetadataProvider;
import com.sitewhere.rest.model.asset.DefaultAssetReferenceEncoder;
import com.sitewhere.rest.model.device.event.DeviceEvent;
import com.sitewhere.spi.device.DeviceAssignmentType;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceEvent;

/**
 * Used to load or save device event data to MongoDB.
 * 
 * @author dadams
 */
public class MongoDeviceEvent {

    /** Alternate (external) id */
    public static final String PROP_ALTERNATE_ID = "al";

    /** Event type indicator */
    public static final String PROP_EVENT_TYPE = "et";

    /** Property for site id */
    public static final String PROP_SITE_ID = "si";

    /** Property for device assignment id */
    public static final String PROP_DEVICE_ASSIGNMENT_ID = "ai";

    /** Property for device assignment type */
    public static final String PROP_DEVICE_ASSIGNMENT_TYPE = "at";

    /** Property for asset reference */
    public static final String PROP_ASSET_REFERENCE = "ar";

    /** Property for time measurements were taken */
    public static final String PROP_EVENT_DATE = "ed";

    /** Property for time measurements were received */
    public static final String PROP_RECEIVED_DATE = "rd";

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     * @param isNested
     */
    public static void toDocument(IDeviceEvent source, Document target, boolean isNested) {
	target.append("_id", (source.getId() != null) ? new ObjectId(source.getId()) : new ObjectId());

	// Only set if there is a value (sparse index).
	if (source.getAlternateId() != null) {
	    target.append(PROP_ALTERNATE_ID, source.getAlternateId());
	}

	target.append(PROP_EVENT_TYPE, source.getEventType().name());
	target.append(PROP_SITE_ID, source.getSiteId());
	target.append(PROP_DEVICE_ASSIGNMENT_ID, source.getDeviceAssignmentId());
	target.append(PROP_DEVICE_ASSIGNMENT_TYPE, source.getAssignmentType().name());
	target.append(PROP_ASSET_REFERENCE, new DefaultAssetReferenceEncoder().encode(source.getAssetReference()));
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
	ObjectId id = (ObjectId) source.get("_id");
	String alternateId = (String) source.get(PROP_ALTERNATE_ID);
	String eventType = (String) source.get(PROP_EVENT_TYPE);
	UUID siteId = (UUID) source.get(PROP_SITE_ID);
	UUID assignmentId = (UUID) source.get(PROP_DEVICE_ASSIGNMENT_ID);
	String assignmentType = (String) source.get(PROP_DEVICE_ASSIGNMENT_TYPE);
	String assetReference = (String) source.get(PROP_ASSET_REFERENCE);
	Date eventDate = (Date) source.get(PROP_EVENT_DATE);
	Date receivedDate = (Date) source.get(PROP_RECEIVED_DATE);

	if (id != null) {
	    target.setId(id.toString());
	}
	if (eventType != null) {
	    target.setEventType(DeviceEventType.valueOf(eventType));
	}
	target.setAlternateId(alternateId);
	target.setSiteId(siteId);
	target.setDeviceAssignmentId(assignmentId);
	target.setAssetReference(new DefaultAssetReferenceEncoder().decode(assetReference));
	target.setEventDate(eventDate);
	target.setReceivedDate(receivedDate);

	if (assignmentType != null) {
	    target.setAssignmentType(DeviceAssignmentType.valueOf(assignmentType));
	}

	MongoMetadataProvider.fromDocument(source, target);
    }
}