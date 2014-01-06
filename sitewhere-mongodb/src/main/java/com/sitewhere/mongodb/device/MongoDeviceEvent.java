/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package com.sitewhere.mongodb.device;

import java.util.Date;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sitewhere.mongodb.common.MongoMetadataProvider;
import com.sitewhere.rest.model.device.DeviceEvent;
import com.sitewhere.spi.device.DeviceAssignmentType;
import com.sitewhere.spi.device.IDeviceEvent;

/**
 * Used to load or save device event data to MongoDB.
 * 
 * @author dadams
 */
public class MongoDeviceEvent {

	/** Property for site token */
	public static final String PROP_SITE_TOKEN = "siteToken";

	/** Property for device assignment token */
	public static final String PROP_DEVICE_ASSIGNMENT_TOKEN = "deviceAssignmentToken";

	/** Property for device assignment type */
	public static final String PROP_DEVICE_ASSIGNMENT_TYPE = "deviceAssignmentType";

	/** Property for asset id */
	public static final String PROP_ASSET_ID = "assetId";

	/** Property for time measurements were taken */
	public static final String PROP_EVENT_DATE = "eventDate";

	/** Property for time measurements were received */
	public static final String PROP_RECEIVED_DATE = "receivedDate";

	/**
	 * Copy information from SPI into Mongo DBObject.
	 * 
	 * @param source
	 * @param target
	 */
	public static void toDBObject(IDeviceEvent source, BasicDBObject target) {
		target.append(PROP_SITE_TOKEN, source.getSiteToken());
		target.append(PROP_DEVICE_ASSIGNMENT_TOKEN, source.getDeviceAssignmentToken());
		target.append(PROP_DEVICE_ASSIGNMENT_TYPE, source.getAssignmentType().name());
		target.append(PROP_ASSET_ID, source.getAssetId());
		target.append(PROP_EVENT_DATE, source.getEventDate());
		target.append(PROP_RECEIVED_DATE, source.getReceivedDate());

		MongoMetadataProvider.toDBObject(source, target);
	}

	/**
	 * Copy information from Mongo DBObject to model object.
	 * 
	 * @param source
	 * @param target
	 */
	public static void fromDBObject(DBObject source, DeviceEvent target) {
		ObjectId id = (ObjectId) source.get("_id");
		String siteToken = (String) source.get(PROP_SITE_TOKEN);
		String assignmentToken = (String) source.get(PROP_DEVICE_ASSIGNMENT_TOKEN);
		String assignmentType = (String) source.get(PROP_DEVICE_ASSIGNMENT_TYPE);
		String assetId = (String) source.get(PROP_ASSET_ID);
		Date eventDate = (Date) source.get(PROP_EVENT_DATE);
		Date receivedDate = (Date) source.get(PROP_RECEIVED_DATE);

		if (id != null) {
			target.setId(id.toString());
		}
		target.setSiteToken(siteToken);
		target.setDeviceAssignmentToken(assignmentToken);
		target.setAssetId(assetId);
		target.setEventDate(eventDate);
		target.setReceivedDate(receivedDate);

		if (assignmentType != null) {
			target.setAssignmentType(DeviceAssignmentType.valueOf(assignmentType));
		}

		MongoMetadataProvider.fromDBObject(source, target);
	}
}