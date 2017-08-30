/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicemanagement.mongodb;

import java.util.Date;

import org.bson.Document;

import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.mongodb.common.MongoMetadataProvider;
import com.sitewhere.mongodb.common.MongoSiteWhereEntity;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.DeviceAssignmentType;
import com.sitewhere.spi.device.IDeviceAssignment;

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

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.dao.mongodb.MongoConverter#convert(java.lang.Object)
     */
    public Document convert(IDeviceAssignment source) {
	return MongoDeviceAssignment.toDocument(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(org.bson.Document)
     */
    public IDeviceAssignment convert(Document source) {
	return MongoDeviceAssignment.fromDocument(source);
    }

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     */
    public static void toDocument(IDeviceAssignment source, Document target) {
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

	MongoSiteWhereEntity.toDocument(source, target);
	MongoMetadataProvider.toDocument(source, target);
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     */
    public static void fromDocument(Document source, DeviceAssignment target) {
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

	MongoSiteWhereEntity.fromDocument(source, target);
	MongoMetadataProvider.fromDocument(source, target);
    }

    /**
     * Convert SPI object to Mongo {@link Document}.
     * 
     * @param source
     * @return
     */
    public static Document toDocument(IDeviceAssignment source) {
	Document result = new Document();
	MongoDeviceAssignment.toDocument(source, result);
	return result;
    }

    /**
     * Convert a {@link Document} into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static DeviceAssignment fromDocument(Document source) {
	DeviceAssignment result = new DeviceAssignment();
	MongoDeviceAssignment.fromDocument(source, result);
	return result;
    }
}