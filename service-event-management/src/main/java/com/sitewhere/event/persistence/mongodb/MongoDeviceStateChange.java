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
import com.sitewhere.rest.model.device.event.DeviceStateChange;
import com.sitewhere.spi.device.event.IDeviceStateChange;

/**
 * Used to load or save device state change data to MongoDB.
 * 
 * @author dadams
 */
public class MongoDeviceStateChange implements MongoConverter<IDeviceStateChange> {

    /** Property for state category */
    public static final String PROP_CATEGORY = "catg";

    /** Property for state type */
    public static final String PROP_TYPE = "type";

    /** Property for previous state value */
    public static final String PROP_PREVIOUS_STATE = "prev";

    /** Property for new state value */
    public static final String PROP_NEW_STATE = "news";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
     */
    @Override
    public Document convert(IDeviceStateChange source) {
	return MongoDeviceStateChange.toDocument(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(org.bson.Document)
     */
    @Override
    public IDeviceStateChange convert(Document source) {
	return MongoDeviceStateChange.fromDocument(source);
    }

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     */
    public static void toDocument(IDeviceStateChange source, Document target) {
	MongoDeviceEvent.toDocument(source, target, false);

	target.append(PROP_CATEGORY, source.getCategory());
	target.append(PROP_TYPE, source.getType());
	target.append(PROP_PREVIOUS_STATE, source.getPreviousState());
	target.append(PROP_NEW_STATE, source.getNewState());
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     */
    public static void fromDocument(Document source, DeviceStateChange target) {
	MongoDeviceEvent.fromDocument(source, target, false);

	String category = (String) source.get(PROP_CATEGORY);
	String type = (String) source.get(PROP_TYPE);
	String previousState = (String) source.get(PROP_PREVIOUS_STATE);
	String newState = (String) source.get(PROP_NEW_STATE);

	target.setCategory(category);
	target.setType(type);
	target.setPreviousState(previousState);
	target.setNewState(newState);
    }

    /**
     * Convert SPI object to Mongo {@link Document}.
     * 
     * @param source
     * @return
     */
    public static Document toDocument(IDeviceStateChange source) {
	Document result = new Document();
	MongoDeviceStateChange.toDocument(source, result);
	return result;
    }

    /**
     * Convert a {@link Document} into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static DeviceStateChange fromDocument(Document source) {
	DeviceStateChange result = new DeviceStateChange();
	MongoDeviceStateChange.fromDocument(source, result);
	return result;
    }
}