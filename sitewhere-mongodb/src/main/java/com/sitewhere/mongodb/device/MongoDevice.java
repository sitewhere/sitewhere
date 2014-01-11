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

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.mongodb.common.MongoMetadataProvider;
import com.sitewhere.mongodb.common.MongoSiteWhereEntity;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.spi.device.IDevice;

/**
 * Used to load or save device data to MongoDB.
 * 
 * @author dadams
 */
public class MongoDevice implements MongoConverter<IDevice> {

	/** Property for hardware id */
	public static final String PROP_HARDWARE_ID = "hardwareId";

	/** Property for specification token */
	public static final String PROP_SPECIFICATION_TOKEN = "specificationToken";

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
	 * @see com.sitewhere.dao.mongodb.MongoConverter#convert(com.mongodb.DBObject)
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
		target.append(PROP_SPECIFICATION_TOKEN, source.getSpecificationToken());
		target.append(PROP_COMMENTS, source.getComments());
		target.append(PROP_ASSIGNMENT_TOKEN, source.getAssignmentToken());
		MongoSiteWhereEntity.toDBObject(source, target);
		MongoMetadataProvider.toDBObject(source, target);
	}

	/**
	 * Copy information from Mongo DBObject to model object.
	 * 
	 * @param source
	 * @param target
	 */
	public static void fromDBObject(DBObject source, Device target) {
		String hardwareId = (String) source.get(PROP_HARDWARE_ID);
		String specificationToken = (String) source.get(PROP_SPECIFICATION_TOKEN);
		String comments = (String) source.get(PROP_COMMENTS);
		String assignmentToken = (String) source.get(PROP_ASSIGNMENT_TOKEN);

		target.setHardwareId(hardwareId);
		target.setSpecificationToken(specificationToken);
		target.setComments(comments);
		target.setAssignmentToken(assignmentToken);
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