/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.persistence.mongodb;

import java.util.List;
import java.util.UUID;

import org.bson.Document;

import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.mongodb.common.MongoBrandedEntity;
import com.sitewhere.mongodb.common.MongoPersistentEntity;
import com.sitewhere.rest.model.device.group.DeviceGroup;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Used to load or save device group data to MongoDB.
 * 
 * @author dadams
 */
public class MongoDeviceGroup implements MongoConverter<IDeviceGroup> {

    /** Property for name */
    public static final String PROP_NAME = "name";

    /** Property for description */
    public static final String PROP_DESCRIPTION = "desc";

    /** Property for list of roles */
    public static final String PROP_ROLES = "role";

    /** Property for element list */
    public static final String PROP_LAST_INDEX = "lidx";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
     */
    @Override
    public Document convert(IDeviceGroup source) {
	return MongoDeviceGroup.toDocument(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(org.bson.Document)
     */
    @Override
    public IDeviceGroup convert(Document source) {
	return MongoDeviceGroup.fromDocument(source);
    }

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     */
    public static void toDocument(IDeviceGroup source, Document target) {
	target.append(PROP_NAME, source.getName());
	target.append(PROP_DESCRIPTION, source.getDescription());
	target.append(PROP_ROLES, source.getRoles());

	MongoBrandedEntity.toDocument(source, target);
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     */
    @SuppressWarnings("unchecked")
    public static void fromDocument(Document source, DeviceGroup target) {
	String name = (String) source.get(PROP_NAME);
	String desc = (String) source.get(PROP_DESCRIPTION);
	List<String> roles = (List<String>) source.get(PROP_ROLES);

	target.setName(name);
	target.setDescription(desc);
	target.setRoles(roles);

	MongoBrandedEntity.fromDocument(source, target);
    }

    /**
     * Convert SPI object to Mongo {@link Document}.
     * 
     * @param source
     * @return
     */
    public static Document toDocument(IDeviceGroup source) {
	Document result = new Document();
	MongoDeviceGroup.toDocument(source, result);
	return result;
    }

    /**
     * Convert a {@link Document} into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static DeviceGroup fromDocument(Document source) {
	DeviceGroup result = new DeviceGroup();
	MongoDeviceGroup.fromDocument(source, result);
	return result;
    }

    /**
     * Get the next available index for the group.
     * 
     * @param mongo
     * @param tenant
     * @param groupId
     * @return
     * @throws SiteWhereException
     */
    public static long getNextGroupIndex(IDeviceManagementMongoClient mongo, ITenant tenant, UUID groupId)
	    throws SiteWhereException {
	Document query = new Document(MongoPersistentEntity.PROP_ID, groupId);
	Document update = new Document(MongoDeviceGroup.PROP_LAST_INDEX, (long) 1);
	Document increment = new Document("$inc", update);
	Document updated = mongo.getDeviceGroupsCollection().findOneAndUpdate(query, increment);
	return (Long) updated.get(PROP_LAST_INDEX);
    }
}