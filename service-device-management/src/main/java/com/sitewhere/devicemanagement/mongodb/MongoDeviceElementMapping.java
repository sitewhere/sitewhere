/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicemanagement.mongodb;

import org.bson.Document;

import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.rest.model.device.DeviceElementMapping;
import com.sitewhere.spi.device.IDeviceElementMapping;

/**
 * Used to load or save device element mapping data to MongoDB.
 * 
 * @author dadams
 */
public class MongoDeviceElementMapping implements MongoConverter<IDeviceElementMapping> {

    /** Property for device element schema path */
    public static final String PROP_DEVICE_ELEMENT_SCHEMA_PATH = "deviceElementSchemaPath";

    /** Property for mapped hardware id */
    public static final String PROP_HARDWARE_ID = "hardwareId";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
     */
    @Override
    public Document convert(IDeviceElementMapping source) {
	return MongoDeviceElementMapping.toDocument(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(org.bson.Document)
     */
    @Override
    public IDeviceElementMapping convert(Document source) {
	return MongoDeviceElementMapping.fromDocument(source);
    }

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     */
    public static void toDocument(IDeviceElementMapping source, Document target) {
	target.append(PROP_DEVICE_ELEMENT_SCHEMA_PATH, source.getDeviceElementSchemaPath());
	target.append(PROP_HARDWARE_ID, source.getHardwareId());
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     */
    public static void fromDocument(Document source, DeviceElementMapping target) {
	String path = (String) source.get(PROP_DEVICE_ELEMENT_SCHEMA_PATH);
	String hardwareId = (String) source.get(PROP_HARDWARE_ID);

	target.setDeviceElementSchemaPath(path);
	target.setHardwareId(hardwareId);
    }

    /**
     * Convert SPI object to Mongo {@link Document}.
     * 
     * @param source
     * @return
     */
    public static Document toDocument(IDeviceElementMapping source) {
	Document result = new Document();
	MongoDeviceElementMapping.toDocument(source, result);
	return result;
    }

    /**
     * Convert a {@link Document} into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static DeviceElementMapping fromDocument(Document source) {
	DeviceElementMapping result = new DeviceElementMapping();
	MongoDeviceElementMapping.fromDocument(source, result);
	return result;
    }
}