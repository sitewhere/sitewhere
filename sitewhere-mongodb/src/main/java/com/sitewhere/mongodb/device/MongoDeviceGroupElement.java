/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb.device;

import java.util.List;

import org.bson.Document;

import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.rest.model.device.group.DeviceGroupElement;
import com.sitewhere.spi.device.group.GroupElementType;
import com.sitewhere.spi.device.group.IDeviceGroupElement;

/**
 * Used to load or save device group element data to MongoDB.
 * 
 * @author Derek
 */
public class MongoDeviceGroupElement implements MongoConverter<IDeviceGroupElement> {

    /** Property for element group token */
    public static final String PROP_GROUP_TOKEN = "groupToken";

    /** Property for element type */
    public static final String PROP_TYPE = "type";

    /** Property for element id */
    public static final String PROP_ELEMENT_ID = "elementId";

    /** Property for list of roles */
    public static final String PROP_ROLES = "roles";

    /** Property for element index */
    public static final String PROP_INDEX = "index";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
     */
    @Override
    public Document convert(IDeviceGroupElement source) {
	return MongoDeviceGroupElement.toDocument(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(org.bson.Document)
     */
    @Override
    public IDeviceGroupElement convert(Document source) {
	return MongoDeviceGroupElement.fromDocument(source);
    }

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     */
    public static void toDocument(IDeviceGroupElement source, Document target) {
	target.append(PROP_GROUP_TOKEN, source.getGroupToken());
	target.append(PROP_INDEX, source.getIndex());
	target.append(PROP_TYPE, source.getType().name());
	target.append(PROP_ELEMENT_ID, source.getElementId());
	target.append(PROP_ROLES, source.getRoles());
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     */
    @SuppressWarnings("unchecked")
    public static void fromDocument(Document source, DeviceGroupElement target) {
	String group = (String) source.get(PROP_GROUP_TOKEN);
	Long index = (Long) source.get(PROP_INDEX);
	String type = (String) source.get(PROP_TYPE);
	String elementId = (String) source.get(PROP_ELEMENT_ID);
	List<String> roles = (List<String>) source.get(PROP_ROLES);

	if (type == null) {
	    throw new RuntimeException("Group element type not stored.");
	}
	target.setGroupToken(group);
	target.setType(GroupElementType.valueOf(type));
	target.setElementId(elementId);
	target.setRoles(roles);
	target.setIndex(index);
    }

    /**
     * Convert SPI object to Mongo {@link Document}.
     * 
     * @param source
     * @return
     */
    public static Document toDocument(IDeviceGroupElement source) {
	Document result = new Document();
	MongoDeviceGroupElement.toDocument(source, result);
	return result;
    }

    /**
     * Convert a {@link Document} into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static DeviceGroupElement fromDocument(Document source) {
	DeviceGroupElement result = new DeviceGroupElement();
	MongoDeviceGroupElement.fromDocument(source, result);
	return result;
    }
}