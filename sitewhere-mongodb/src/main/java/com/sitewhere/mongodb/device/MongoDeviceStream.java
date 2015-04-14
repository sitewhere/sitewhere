/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
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
import com.sitewhere.rest.model.device.streaming.DeviceStream;
import com.sitewhere.spi.device.streaming.IDeviceStream;

/**
 * Handles loading/saving {@link DeviceStream} objects to a MongoDB datastore.
 * 
 * @author Derek
 */
public class MongoDeviceStream implements MongoConverter<IDeviceStream> {

	/** Property for parent assignment token */
	public static final String PROP_ASSIGNMENT_TOKEN = "assignmentToken";

	/** Property for stream id */
	public static final String PROP_STREAM_ID = "streamId";

	/** Property for content type */
	public static final String PROP_CONTENT_TYPE = "contentType";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
	 */
	@Override
	public BasicDBObject convert(IDeviceStream source) {
		return MongoDeviceStream.toDBObject(source);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.MongoConverter#convert(com.mongodb.DBObject)
	 */
	@Override
	public IDeviceStream convert(DBObject source) {
		return MongoDeviceStream.fromDBObject(source);
	}

	/**
	 * Copy information from SPI into Mongo DBObject.
	 * 
	 * @param source
	 * @param target
	 */
	public static void toDBObject(IDeviceStream source, BasicDBObject target) {
		target.append(PROP_ASSIGNMENT_TOKEN, source.getAssignmentToken());
		target.append(PROP_STREAM_ID, source.getStreamId());
		target.append(PROP_CONTENT_TYPE, source.getContentType());

		MongoSiteWhereEntity.toDBObject(source, target);
		MongoMetadataProvider.toDBObject(source, target);
	}

	/**
	 * Copy information from Mongo DBObject to model object.
	 * 
	 * @param source
	 * @param target
	 */
	public static void fromDBObject(DBObject source, DeviceStream target) {
		String assignmentToken = (String) source.get(PROP_ASSIGNMENT_TOKEN);
		String streamId = (String) source.get(PROP_STREAM_ID);
		String contentType = (String) source.get(PROP_CONTENT_TYPE);

		target.setAssignmentToken(assignmentToken);
		target.setStreamId(streamId);
		target.setContentType(contentType);

		MongoSiteWhereEntity.fromDBObject(source, target);
		MongoMetadataProvider.fromDBObject(source, target);
	}

	/**
	 * Convert SPI object to Mongo DBObject.
	 * 
	 * @param source
	 * @return
	 */
	public static BasicDBObject toDBObject(IDeviceStream source) {
		BasicDBObject result = new BasicDBObject();
		MongoDeviceStream.toDBObject(source, result);
		return result;
	}

	/**
	 * Convert a DBObject into the SPI equivalent.
	 * 
	 * @param source
	 * @return
	 */
	public static DeviceStream fromDBObject(DBObject source) {
		DeviceStream result = new DeviceStream();
		MongoDeviceStream.fromDBObject(source, result);
		return result;
	}
}