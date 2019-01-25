/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.persistence.mongodb;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bson.Document;

import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.mongodb.common.MongoPersistentEntity;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceElementMapping;

/**
 * Used to load or save device data to MongoDB.
 * 
 * @author dadams
 */
public class MongoDevice implements MongoConverter<IDevice> {

    /** Property for device type id */
    public static final String PROP_DEVICE_TYPE_ID = "dtid";

    /** Property for parent device id (if nested) */
    public static final String PROP_PARENT_DEVICE_ID = "pdid";

    /** Property for device element mappings */
    public static final String PROP_DEVICE_ELEMENT_MAPPINGS = "elmp";

    /** Property for status */
    public static final String PROP_STATUS = "stat";

    /** Property for comments */
    public static final String PROP_COMMENTS = "comm";

    /** Property for current assignment */
    public static final String PROP_ASSIGNMENT_ID = "asid";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.dao.mongodb.MongoConverter#convert(java.lang.Object)
     */
    public Document convert(IDevice source) {
	return MongoDevice.toDocument(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(org.bson.Document)
     */
    public Device convert(Document source) {
	return MongoDevice.fromDocument(source);
    }

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     */
    public static void toDocument(IDevice source, Document target) {
	target.append(PROP_DEVICE_TYPE_ID, source.getDeviceTypeId());
	target.append(PROP_PARENT_DEVICE_ID, source.getParentDeviceId());
	target.append(PROP_STATUS, source.getStatus());
	target.append(PROP_COMMENTS, source.getComments());
	target.append(PROP_ASSIGNMENT_ID, source.getDeviceAssignmentId());

	// Save nested list of mappings.
	List<Document> mappings = new ArrayList<Document>();
	for (IDeviceElementMapping mapping : source.getDeviceElementMappings()) {
	    mappings.add(MongoDeviceElementMapping.toDocument(mapping));
	}
	target.append(PROP_DEVICE_ELEMENT_MAPPINGS, mappings);

	MongoPersistentEntity.toDocument(source, target);
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     */
    @SuppressWarnings("unchecked")
    public static void fromDocument(Document source, Device target) {
	UUID typeId = (UUID) source.get(PROP_DEVICE_TYPE_ID);
	UUID parentDeviceId = (UUID) source.get(PROP_PARENT_DEVICE_ID);
	String status = (String) source.get(PROP_STATUS);
	String comments = (String) source.get(PROP_COMMENTS);
	UUID assignmentId = (UUID) source.get(PROP_ASSIGNMENT_ID);

	target.setDeviceTypeId(typeId);
	target.setParentDeviceId(parentDeviceId);
	target.setStatus(status);
	target.setComments(comments);
	target.setDeviceAssignmentId(assignmentId);

	List<Document> mappings = (List<Document>) source.get(PROP_DEVICE_ELEMENT_MAPPINGS);
	if (mappings != null) {
	    for (Document mapping : mappings) {
		target.getDeviceElementMappings().add(MongoDeviceElementMapping.fromDocument(mapping));
	    }
	}

	MongoPersistentEntity.fromDocument(source, target);
    }

    /**
     * Convert SPI object to Mongo DBObject.
     * 
     * @param source
     * @return
     */
    public static Document toDocument(IDevice source) {
	Document result = new Document();
	MongoDevice.toDocument(source, result);
	return result;
    }

    /**
     * Convert a DBObject into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static Device fromDocument(Document source) {
	Device result = new Device();
	MongoDevice.fromDocument(source, result);
	return result;
    }
}