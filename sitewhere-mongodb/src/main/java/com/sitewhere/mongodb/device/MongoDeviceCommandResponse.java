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
import com.sitewhere.rest.model.device.event.DeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;

/**
 * Used to load or save device command response data to MongoDB.
 * 
 * @author dadams
 */
public class MongoDeviceCommandResponse implements MongoConverter<IDeviceCommandResponse> {

	/** Property for originating event id */
	public static final String PROP_ORIGINATING_EVENT_ID = "originatingEventId";

	/** Property for response event id */
	public static final String PROP_RESPONSE_EVENT_ID = "responseEventId";

	/** Property for response */
	public static final String PROP_RESPONSE = "response";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
	 */
	@Override
	public BasicDBObject convert(IDeviceCommandResponse source) {
		return MongoDeviceCommandResponse.toDBObject(source);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.MongoConverter#convert(com.mongodb.DBObject)
	 */
	@Override
	public IDeviceCommandResponse convert(DBObject source) {
		return MongoDeviceCommandResponse.fromDBObject(source);
	}

	/**
	 * Copy information from SPI into Mongo DBObject.
	 * 
	 * @param source
	 * @param target
	 */
	public static void toDBObject(IDeviceCommandResponse source, BasicDBObject target) {
		MongoDeviceEvent.toDBObject(source, target, false);

		target.append(PROP_ORIGINATING_EVENT_ID, source.getOriginatingEventId());
		target.append(PROP_RESPONSE_EVENT_ID, source.getResponseEventId());
		target.append(PROP_RESPONSE, source.getResponse());
	}

	/**
	 * Copy information from Mongo DBObject to model object.
	 * 
	 * @param source
	 * @param target
	 */
	public static void fromDBObject(DBObject source, DeviceCommandResponse target) {
		MongoDeviceEvent.fromDBObject(source, target, false);

		String originator = (String) source.get(PROP_ORIGINATING_EVENT_ID);
		String responder = (String) source.get(PROP_RESPONSE_EVENT_ID);
		String response = (String) source.get(PROP_RESPONSE);

		target.setOriginatingEventId(originator);
		target.setResponseEventId(responder);
		target.setResponse(response);
	}

	/**
	 * Convert SPI object to Mongo DBObject.
	 * 
	 * @param source
	 * @return
	 */
	public static BasicDBObject toDBObject(IDeviceCommandResponse source) {
		BasicDBObject result = new BasicDBObject();
		MongoDeviceCommandResponse.toDBObject(source, result);
		return result;
	}

	/**
	 * Convert a DBObject into the SPI equivalent.
	 * 
	 * @param source
	 * @return
	 */
	public static DeviceCommandResponse fromDBObject(DBObject source) {
		DeviceCommandResponse result = new DeviceCommandResponse();
		MongoDeviceCommandResponse.fromDBObject(source, result);
		return result;
	}
}