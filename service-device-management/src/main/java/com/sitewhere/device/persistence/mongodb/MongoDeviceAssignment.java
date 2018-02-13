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
import com.sitewhere.mongodb.common.MongoSiteWhereEntity;
import com.sitewhere.rest.model.asset.DefaultAssetReferenceEncoder;
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

    /** Property for id */
    public static final String PROP_ID = "id";

    /** Property for active date */
    public static final String PROP_ACTIVE_DATE = "ad";

    /** Property for asset reference */
    public static final String PROP_ASSET_REFERENCE = "ar";

    /** Property for assignment type */
    public static final String PROP_ASSIGNMENT_TYPE = "at";

    /** Property for released date */
    public static final String PROP_RELEASED_DATE = "rd";

    /** Property for status */
    public static final String PROP_STATUS = "st";

    /** Property for token */
    public static final String PROP_TOKEN = "tk";

    /** Property for device hardware id */
    public static final String PROP_DEVICE_ID = "di";

    /** Property for area id */
    public static final String PROP_AREA_ID = "ai";

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
	target.append(PROP_ID, source.getId());
	target.append(PROP_TOKEN, source.getToken());
	target.append(PROP_DEVICE_ID, source.getDeviceId());
	target.append(PROP_AREA_ID, source.getAreaId());

	if (source.getActiveDate() != null) {
	    target.append(PROP_ACTIVE_DATE, source.getActiveDate());
	}
	target.append(PROP_ASSET_REFERENCE, new DefaultAssetReferenceEncoder().encode(source.getAssetReference()));
	if (source.getAssignmentType() != null) {
	    target.append(PROP_ASSIGNMENT_TYPE, source.getAssignmentType().name());
	}
	if (source.getReleasedDate() != null) {
	    target.append(PROP_RELEASED_DATE, source.getReleasedDate());
	}
	if (source.getStatus() != null) {
	    target.append(PROP_STATUS, source.getStatus().name());
	}

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
	UUID id = (UUID) source.get(PROP_ID);
	String token = (String) source.get(PROP_TOKEN);
	UUID deviceId = (UUID) source.get(PROP_DEVICE_ID);
	UUID areaId = (UUID) source.get(PROP_AREA_ID);
	String status = (String) source.get(PROP_STATUS);
	Date activeDate = (Date) source.get(PROP_ACTIVE_DATE);
	String assetReference = (String) source.get(PROP_ASSET_REFERENCE);
	String assignmentType = (String) source.get(PROP_ASSIGNMENT_TYPE);
	Date releasedDate = (Date) source.get(PROP_RELEASED_DATE);

	target.setId(id);
	target.setToken(token);
	target.setDeviceId(deviceId);
	target.setAreaId(areaId);
	if (activeDate != null) {
	    target.setActiveDate(activeDate);
	}
	target.setAssetReference(new DefaultAssetReferenceEncoder().decode(assetReference));
	if (assignmentType != null) {
	    target.setAssignmentType(DeviceAssignmentType.valueOf(assignmentType));
	}
	if (releasedDate != null) {
	    target.setReleasedDate(releasedDate);
	}
	if (status != null) {
	    target.setStatus(DeviceAssignmentStatus.valueOf(status));
	}

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