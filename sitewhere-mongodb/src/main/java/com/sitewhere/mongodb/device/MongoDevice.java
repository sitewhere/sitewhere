/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb.device;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.mongodb.common.MongoMetadataProvider;
import com.sitewhere.mongodb.common.MongoSiteWhereEntity;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceElementMapping;

/**
 * Used to load or save device data to MongoDB.
 * 
 * @author dadams
 */
public class MongoDevice implements MongoConverter<IDevice> {

    /** Property for hardware id */
    public static final String PROP_HARDWARE_ID = "hardwareId";

    /** Property for specification token */
    public static final String PROP_SITE_TOKEN = "siteToken";

    /** Property for specification token */
    public static final String PROP_SPECIFICATION_TOKEN = "specificationToken";

    /** Property for parent hardware id (if nested) */
    public static final String PROP_PARENT_HARDWARE_ID = "parentHardwareId";

    /** Property for device element mappings */
    public static final String PROP_DEVICE_ELEMENT_MAPPINGS = "deviceElementMappings";

    /** Property for comments */
    public static final String PROP_COMMENTS = "comments";

    /** Property for current assignment */
    public static final String PROP_ASSIGNMENT_TOKEN = "assignmentToken";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.dao.mongodb.MongoConverter#convert(java.lang.Object)
     */
    public BasicDBObject convert(IDevice source) {
	return MongoDevice.toDBObject(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.dao.mongodb.MongoConverter#convert(com.mongodb.DBObject)
     */
    public Device convert(DBObject source) {
	return MongoDevice.fromDBObject(source);
    }

    /**
     * Copy information from SPI into Mongo DBObject.
     * 
     * @param source
     * @param target
     */
    public static void toDBObject(IDevice source, BasicDBObject target) {
	target.append(PROP_HARDWARE_ID, source.getHardwareId());
	target.append(PROP_SITE_TOKEN, source.getSiteToken());
	target.append(PROP_SPECIFICATION_TOKEN, source.getSpecificationToken());
	target.append(PROP_PARENT_HARDWARE_ID, source.getParentHardwareId());
	target.append(PROP_COMMENTS, source.getComments());
	target.append(PROP_ASSIGNMENT_TOKEN, source.getAssignmentToken());

	// Save nested list of mappings.
	List<BasicDBObject> mappings = new ArrayList<BasicDBObject>();
	for (IDeviceElementMapping mapping : source.getDeviceElementMappings()) {
	    mappings.add(MongoDeviceElementMapping.toDBObject(mapping));
	}
	target.append(PROP_DEVICE_ELEMENT_MAPPINGS, mappings);

	MongoSiteWhereEntity.toDBObject(source, target);
	MongoMetadataProvider.toDBObject(source, target);
    }

    /**
     * Copy information from Mongo DBObject to model object.
     * 
     * @param source
     * @param target
     */
    @SuppressWarnings("unchecked")
    public static void fromDBObject(DBObject source, Device target) {
	String hardwareId = (String) source.get(PROP_HARDWARE_ID);
	String siteToken = (String) source.get(PROP_SITE_TOKEN);
	String specificationToken = (String) source.get(PROP_SPECIFICATION_TOKEN);
	String parentHardwareId = (String) source.get(PROP_PARENT_HARDWARE_ID);
	String comments = (String) source.get(PROP_COMMENTS);
	String assignmentToken = (String) source.get(PROP_ASSIGNMENT_TOKEN);

	target.setHardwareId(hardwareId);
	target.setSiteToken(siteToken);
	target.setSpecificationToken(specificationToken);
	target.setParentHardwareId(parentHardwareId);
	target.setComments(comments);
	target.setAssignmentToken(assignmentToken);

	List<DBObject> mappings = (List<DBObject>) source.get(PROP_DEVICE_ELEMENT_MAPPINGS);
	if (mappings != null) {
	    for (DBObject mapping : mappings) {
		target.getDeviceElementMappings().add(MongoDeviceElementMapping.fromDBObject(mapping));
	    }
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
    public static BasicDBObject toDBObject(IDevice source) {
	BasicDBObject result = new BasicDBObject();
	MongoDevice.toDBObject(source, result);
	return result;
    }

    /**
     * Convert a DBObject into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static Device fromDBObject(DBObject source) {
	Device result = new Device();
	MongoDevice.fromDBObject(source, result);
	return result;
    }
}