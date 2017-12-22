/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.persistence.mongodb;

import java.util.UUID;

import org.bson.Document;

import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.mongodb.common.MongoMetadataProvider;
import com.sitewhere.rest.model.device.DeviceStatus;
import com.sitewhere.spi.device.IDeviceStatus;

/**
 * Used to load or save device status data to MongoDB.
 * 
 * @author dadams
 */
public class MongoDeviceStatus implements MongoConverter<IDeviceStatus> {

    /** Property for id */
    public static final String PROP_ID = "id";

    /** Property for status code */
    public static final String PROP_CODE = "cd";

    /** Property for specification id */
    public static final String PROP_SPEC_ID = "si";

    /** Property for command name */
    public static final String PROP_NAME = "nm";

    /** Property for background color */
    public static final String PROP_BACKGROUND_COLOR = "bg";

    /** Property for foreground color */
    public static final String PROP_FOREGROUND_COLOR = "fg";

    /** Property for border color */
    public static final String PROP_BORDER_COLOR = "bd";

    /** Property for status icon */
    public static final String PROP_ICON = "ic";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
     */
    @Override
    public Document convert(IDeviceStatus source) {
	return MongoDeviceStatus.toDocument(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(org.bson.Document)
     */
    @Override
    public IDeviceStatus convert(Document source) {
	return MongoDeviceStatus.fromDocument(source);
    }

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     */
    public static void toDocument(IDeviceStatus source, Document target) {
	target.append(PROP_ID, source.getId());
	target.append(PROP_CODE, source.getCode());
	target.append(PROP_SPEC_ID, source.getDeviceSpecificationId());
	target.append(PROP_NAME, source.getName());
	target.append(PROP_BACKGROUND_COLOR, source.getBackgroundColor());
	target.append(PROP_FOREGROUND_COLOR, source.getForegroundColor());
	target.append(PROP_BORDER_COLOR, source.getBorderColor());
	target.append(PROP_ICON, source.getIcon());

	MongoMetadataProvider.toDocument(source, target);
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     */
    public static void fromDocument(Document source, DeviceStatus target) {
	UUID id = (UUID) source.get(PROP_ID);
	String code = (String) source.get(PROP_CODE);
	UUID specId = (UUID) source.get(PROP_SPEC_ID);
	String name = (String) source.get(PROP_NAME);
	String bgcolor = (String) source.get(PROP_BACKGROUND_COLOR);
	String fgcolor = (String) source.get(PROP_FOREGROUND_COLOR);
	String bdColor = (String) source.get(PROP_BORDER_COLOR);
	String icon = (String) source.get(PROP_ICON);

	target.setId(id);
	target.setCode(code);
	target.setDeviceSpecificationId(specId);
	target.setName(name);
	target.setBackgroundColor(bgcolor);
	target.setForegroundColor(fgcolor);
	target.setBorderColor(bdColor);
	target.setIcon(icon);

	MongoMetadataProvider.fromDocument(source, target);
    }

    /**
     * Convert SPI object to Mongo DBObject.
     * 
     * @param source
     * @return
     */
    public static Document toDocument(IDeviceStatus source) {
	Document result = new Document();
	MongoDeviceStatus.toDocument(source, result);
	return result;
    }

    /**
     * Convert a DBObject into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static DeviceStatus fromDocument(Document source) {
	DeviceStatus result = new DeviceStatus();
	MongoDeviceStatus.fromDocument(source, result);
	return result;
    }
}