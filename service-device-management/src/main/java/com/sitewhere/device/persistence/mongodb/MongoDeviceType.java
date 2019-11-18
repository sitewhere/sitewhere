/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.persistence.mongodb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;
import org.bson.types.Binary;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.mongodb.common.MongoBrandedEntity;
import com.sitewhere.rest.model.device.DeviceType;
import com.sitewhere.rest.model.device.element.DeviceElementSchema;
import com.sitewhere.spi.device.DeviceContainerPolicy;
import com.sitewhere.spi.device.IDeviceType;

/**
 * Used to load or save device specification data to MongoDB.
 */
public class MongoDeviceType implements MongoConverter<IDeviceType> {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(MongoDeviceType.class);

    /** Property for name */
    public static final String PROP_NAME = "name";

    /** Property for description */
    public static final String PROP_DESCRIPTION = "desc";

    /** Property for container policy */
    public static final String PROP_CONTAINER_POLICY = "cpol";

    /** Property for device element schema */
    public static final String PROP_DEVICE_ELEMENT_SCHEMA = "elsc";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
     */
    @Override
    public Document convert(IDeviceType source) {
	return MongoDeviceType.toDocument(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(com.mongodb.DBObject)
     */
    @Override
    public IDeviceType convert(Document source) {
	return MongoDeviceType.fromDocument(source);
    }

    /**
     * Copy information from SPI into Mongo DBObject.
     * 
     * @param source
     * @param target
     */
    public static void toDocument(IDeviceType source, Document target) {
	target.append(PROP_NAME, source.getName());
	target.append(PROP_DESCRIPTION, source.getDescription());
	target.append(PROP_CONTAINER_POLICY, source.getContainerPolicy().name());

	MongoBrandedEntity.toDocument(source, target);

	// Marshal device element schema as JSON.
	if (source.getDeviceElementSchema() != null) {
	    ObjectMapper mapper = new ObjectMapper();
	    try {
		byte[] schemaJson = mapper.writeValueAsBytes(source.getDeviceElementSchema());
		target.append(PROP_DEVICE_ELEMENT_SCHEMA, new Binary(schemaJson));
	    } catch (JsonProcessingException e) {
		LOGGER.error("Unable to marshal device element schema for MongoDB persistence.", e);
	    }
	}
    }

    /**
     * Copy information from Mongo DBObject to model object.
     * 
     * @param source
     * @param target
     */
    public static void fromDocument(Document source, DeviceType target) {
	String name = (String) source.get(PROP_NAME);
	String description = (String) source.get(PROP_DESCRIPTION);
	String containerPolicy = (String) source.get(PROP_CONTAINER_POLICY);
	Binary schemaBytes = (Binary) source.get(PROP_DEVICE_ELEMENT_SCHEMA);

	target.setName(name);
	target.setDescription(description);

	if (containerPolicy != null) {
	    target.setContainerPolicy(DeviceContainerPolicy.valueOf(containerPolicy));
	}

	// Unmarshal device element schema.
	if (schemaBytes != null) {
	    ObjectMapper mapper = new ObjectMapper();
	    try {
		DeviceElementSchema schema = mapper.readValue(schemaBytes.getData(), DeviceElementSchema.class);
		target.setDeviceElementSchema(schema);
	    } catch (Throwable e) {
		LOGGER.error("Unable to unmarshal device element schema from MongoDB persistence.", e);
	    }
	}

	MongoBrandedEntity.fromDocument(source, target);
    }

    /**
     * Convert SPI object to Mongo {@link Document}.
     * 
     * @param source
     * @return
     */
    public static Document toDocument(IDeviceType source) {
	Document result = new Document();
	MongoDeviceType.toDocument(source, result);
	return result;
    }

    /**
     * Convert a {@link Document} into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static DeviceType fromDocument(Document source) {
	DeviceType result = new DeviceType();
	MongoDeviceType.fromDocument(source, result);
	return result;
    }
}