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

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.mongodb.common.MongoMetadataProvider;
import com.sitewhere.mongodb.common.MongoSiteWhereEntity;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.DeviceAssignmentState;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.DeviceAssignmentType;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceAssignmentState;

/**
 * Used to load or save device assignment data to MongoDB.
 * 
 * @author dadams
 */
public class MongoDeviceAssignment implements MongoConverter<IDeviceAssignment> {

	/** Property for active date */
	public static final String PROP_ACTIVE_DATE = "activeDate";

	/** Property for asset module id */
	public static final String PROP_ASSET_MODULE_ID = "assetModuleId";

	/** Property for asset id */
	public static final String PROP_ASSET_ID = "assetId";

	/** Property for assignment type */
	public static final String PROP_ASSIGNMENT_TYPE = "assignmentType";

	/** Property for released date */
	public static final String PROP_RELEASED_DATE = "releasedDate";

	/** Property for status */
	public static final String PROP_STATUS = "status";

	/** Property for token */
	public static final String PROP_TOKEN = "token";

	/** Property for device hardware id */
	public static final String PROP_DEVICE_HARDWARE_ID = "deviceHardwareId";

	/** Property for site token */
	public static final String PROP_SITE_TOKEN = "siteToken";

	/** Property for assignment state */
	public static final String PROP_STATE = "state";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.dao.mongodb.MongoConverter#convert(java.lang.Object)
	 */
	public BasicDBObject convert(IDeviceAssignment source) {
		return MongoDeviceAssignment.toDBObject(source);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.dao.mongodb.MongoConverter#convert(com.mongodb.DBObject)
	 */
	public IDeviceAssignment convert(DBObject source) {
		return MongoDeviceAssignment.fromDBObject(source);
	}

	/**
	 * Copy information from SPI into Mongo DBObject.
	 * 
	 * @param source
	 * @param target
	 */
	public static void toDBObject(IDeviceAssignment source, BasicDBObject target) {
		if (source.getActiveDate() != null) {
			target.append(PROP_ACTIVE_DATE, source.getActiveDate());
		}
		target.append(PROP_ASSET_MODULE_ID, source.getAssetModuleId());
		target.append(PROP_ASSET_ID, source.getAssetId());
		if (source.getAssignmentType() != null) {
			target.append(PROP_ASSIGNMENT_TYPE, source.getAssignmentType().name());
		}
		if (source.getReleasedDate() != null) {
			target.append(PROP_RELEASED_DATE, source.getReleasedDate());
		}
		if (source.getStatus() != null) {
			target.append(PROP_STATUS, source.getStatus().name());
		}
		target.append(PROP_TOKEN, source.getToken());
		target.append(PROP_DEVICE_HARDWARE_ID, source.getDeviceHardwareId());
		target.append(PROP_SITE_TOKEN, source.getSiteToken());

		if (source.getState() != null) {
			setState(source.getState(), target);
		}

		MongoSiteWhereEntity.toDBObject(source, target);
		MongoMetadataProvider.toDBObject(source, target);
	}

	/**
	 * Set state information for the assignment.
	 * 
	 * @param source
	 * @param target
	 */
	public static void setState(IDeviceAssignmentState source, DBObject target) {
		BasicDBObject state = new BasicDBObject();
		MongoDeviceAssignmentState.toDBObject(source, state);
		target.put(PROP_STATE, state);
	}

	/**
	 * Copy information from Mongo DBObject to model object.
	 * 
	 * @param source
	 * @param target
	 */
	public static void fromDBObject(DBObject source, DeviceAssignment target) {
		Date activeDate = (Date) source.get(PROP_ACTIVE_DATE);
		String assetModuleId = (String) source.get(PROP_ASSET_MODULE_ID);
		String assetId = (String) source.get(PROP_ASSET_ID);
		String assignmentType = (String) source.get(PROP_ASSIGNMENT_TYPE);
		Date releasedDate = (Date) source.get(PROP_RELEASED_DATE);
		String status = (String) source.get(PROP_STATUS);
		String token = (String) source.get(PROP_TOKEN);
		String deviceHardwareId = (String) source.get(PROP_DEVICE_HARDWARE_ID);
		String siteToken = (String) source.get(PROP_SITE_TOKEN);

		if (activeDate != null) {
			target.setActiveDate(activeDate);
		}
		target.setAssetModuleId(assetModuleId);
		target.setAssetId(assetId);
		if (assignmentType != null) {
			target.setAssignmentType(DeviceAssignmentType.valueOf(assignmentType));
		}
		if (releasedDate != null) {
			target.setReleasedDate(releasedDate);
		}
		if (status != null) {
			target.setStatus(DeviceAssignmentStatus.valueOf(status));
		}
		target.setToken(token);
		target.setDeviceHardwareId(deviceHardwareId);
		target.setSiteToken(siteToken);

		DBObject sstate = (DBObject) source.get(PROP_STATE);
		if (sstate != null) {
			DeviceAssignmentState state = MongoDeviceAssignmentState.fromDBObject(sstate);
			target.setState(state);
		}

		MongoSiteWhereEntity.fromDBObject(source, target);
		MongoMetadataProvider.fromDBObject(source, target);
	}

	/**
	 * Convert SPI object to Mongo DBObject.
	 * 
	 * @param source
	 * @return
	 */
	public static BasicDBObject toDBObject(IDeviceAssignment source) {
		BasicDBObject result = new BasicDBObject();
		MongoDeviceAssignment.toDBObject(source, result);
		return result;
	}

	/**
	 * Convert a DBObject into the SPI equivalent.
	 * 
	 * @param source
	 * @return
	 */
	public static DeviceAssignment fromDBObject(DBObject source) {
		DeviceAssignment result = new DeviceAssignment();
		MongoDeviceAssignment.fromDBObject(source, result);
		return result;
	}
}