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
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.spi.device.event.AlertLevel;
import com.sitewhere.spi.device.event.AlertSource;
import com.sitewhere.spi.device.event.IDeviceAlert;

/**
 * Used to load or save device alert data to MongoDB.
 * 
 * @author dadams
 */
public class MongoDeviceAlert implements MongoConverter<IDeviceAlert> {

    /** Property for source */
    public static final String PROP_SOURCE = "source";

    /** Property for alert level */
    public static final String PROP_LEVEL = "level";

    /** Property for type */
    public static final String PROP_TYPE = "type";

    /** Property for message */
    public static final String PROP_MESSAGE = "message";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.dao.mongodb.MongoConverter#convert(java.lang.Object)
     */
    public Document convert(IDeviceAlert source) {
	return MongoDeviceAlert.toDocument(source, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(org.bson.Document)
     */
    public IDeviceAlert convert(Document source) {
	return MongoDeviceAlert.fromDocument(source, false);
    }

    /**
     * Copy information from SPI into Mongo DBObject.
     * 
     * @param source
     * @param target
     * @param isNested
     */
    public static void toDocument(IDeviceAlert source, Document target, boolean isNested) {
	MongoDeviceEvent.toDocument(source, target, isNested);

	target.append(PROP_SOURCE, source.getSource().name());
	target.append(PROP_LEVEL, source.getLevel().name());
	target.append(PROP_TYPE, source.getType());
	target.append(PROP_MESSAGE, source.getMessage());
    }

    /**
     * Copy information from Mongo DBObject to model object.
     * 
     * @param source
     * @param target
     * @param isNested
     */
    public static void fromDocument(Document source, DeviceAlert target, boolean isNested) {
	MongoDeviceEvent.fromDocument(source, target, isNested);

	String sourceName = (String) source.get(PROP_SOURCE);
	String levelName = (String) source.get(PROP_LEVEL);
	String type = (String) source.get(PROP_TYPE);
	String message = (String) source.get(PROP_MESSAGE);

	if (sourceName != null) {
	    target.setSource(AlertSource.valueOf(sourceName));
	}
	if (levelName != null) {
	    target.setLevel(AlertLevel.valueOf(levelName));
	}
	target.setType(type);
	target.setMessage(message);
    }

    /**
     * Convert SPI object to Mongo {@link Document}.
     * 
     * @param source
     * @param isNested
     * @return
     */
    public static Document toDocument(IDeviceAlert source, boolean isNested) {
	Document result = new Document();
	MongoDeviceAlert.toDocument(source, result, isNested);
	return result;
    }

    /**
     * Convert a {@link Document} into the SPI equivalent.
     * 
     * @param source
     * @param isNested
     * @return
     */
    public static DeviceAlert fromDocument(Document source, boolean isNested) {
	DeviceAlert result = new DeviceAlert();
	MongoDeviceAlert.fromDocument(source, result, isNested);
	return result;
    }
}