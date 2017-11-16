/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.persistence.mongodb;

import org.bson.Document;

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
    public static final String PROP_ORIGINATING_EVENT_ID = "oi";

    /** Property for response event id */
    public static final String PROP_RESPONSE_EVENT_ID = "ri";

    /** Property for response */
    public static final String PROP_RESPONSE = "rs";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
     */
    @Override
    public Document convert(IDeviceCommandResponse source) {
	return MongoDeviceCommandResponse.toDocument(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(org.bson.Document)
     */
    @Override
    public IDeviceCommandResponse convert(Document source) {
	return MongoDeviceCommandResponse.fromDocument(source);
    }

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     */
    public static void toDocument(IDeviceCommandResponse source, Document target) {
	MongoDeviceEvent.toDocument(source, target, false);

	target.append(PROP_ORIGINATING_EVENT_ID, source.getOriginatingEventId());
	target.append(PROP_RESPONSE_EVENT_ID, source.getResponseEventId());
	target.append(PROP_RESPONSE, source.getResponse());
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     */
    public static void fromDocument(Document source, DeviceCommandResponse target) {
	MongoDeviceEvent.fromDocument(source, target, false);

	String originator = (String) source.get(PROP_ORIGINATING_EVENT_ID);
	String responder = (String) source.get(PROP_RESPONSE_EVENT_ID);
	String response = (String) source.get(PROP_RESPONSE);

	target.setOriginatingEventId(originator);
	target.setResponseEventId(responder);
	target.setResponse(response);
    }

    /**
     * Convert SPI object to Mongo {@link Document}.
     * 
     * @param source
     * @return
     */
    public static Document toDocument(IDeviceCommandResponse source) {
	Document result = new Document();
	MongoDeviceCommandResponse.toDocument(source, result);
	return result;
    }

    /**
     * Convert a {@link Document} into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static DeviceCommandResponse fromDocument(Document source) {
	DeviceCommandResponse result = new DeviceCommandResponse();
	MongoDeviceCommandResponse.fromDocument(source, result);
	return result;
    }
}