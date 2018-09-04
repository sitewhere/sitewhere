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
import com.sitewhere.rest.model.customer.CustomerType;
import com.sitewhere.spi.customer.ICustomerType;

/**
 * Used to load or save customer type data to MongoDB.
 * 
 * @author dadams
 */
public class MongoCustomerType implements MongoConverter<ICustomerType> {

    /** Property for name */
    public static final String PROP_NAME = "name";

    /** Property for description */
    public static final String PROP_DESCRIPTION = "desc";

    /** Property for contained customer type ids */
    public static final String PROP_CONTAINED_CUSTOMER_TYPE_IDS = "ccty";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.dao.mongodb.MongoConverter#convert(java.lang.Object)
     */
    @Override
    public Document convert(ICustomerType source) {
	return MongoCustomerType.toDocument(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(org.bson.Document)
     */
    @Override
    public ICustomerType convert(Document source) {
	return MongoCustomerType.fromDocument(source);
    }

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     */
    public static void toDocument(ICustomerType source, Document target) {
	target.append(PROP_NAME, source.getName());
	target.append(PROP_DESCRIPTION, source.getDescription());
	target.append(PROP_CONTAINED_CUSTOMER_TYPE_IDS, source.getContainedCustomerTypeIds());

	MongoBrandedEntity.toDocument(source, target);
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     */
    @SuppressWarnings("unchecked")
    public static void fromDocument(Document source, CustomerType target) {
	String name = (String) source.get(PROP_NAME);
	String description = (String) source.get(PROP_DESCRIPTION);
	List<UUID> containedCustomerTypeIds = (List<UUID>) source.get(PROP_CONTAINED_CUSTOMER_TYPE_IDS);

	target.setName(name);
	target.setDescription(description);
	target.setContainedCustomerTypeIds(containedCustomerTypeIds);

	MongoBrandedEntity.fromDocument(source, target);
    }

    /**
     * Convert SPI object to Mongo {@link Document}.
     * 
     * @param source
     * @return
     */
    public static Document toDocument(ICustomerType source) {
	Document result = new Document();
	MongoCustomerType.toDocument(source, result);
	return result;
    }

    /**
     * Convert a {@link Document} into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static CustomerType fromDocument(Document source) {
	CustomerType result = new CustomerType();
	MongoCustomerType.fromDocument(source, result);
	return result;
    }
}
