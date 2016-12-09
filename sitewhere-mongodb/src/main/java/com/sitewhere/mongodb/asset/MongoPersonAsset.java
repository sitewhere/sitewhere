/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb.asset;

import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.rest.model.asset.PersonAsset;
import com.sitewhere.spi.asset.IPersonAsset;

/**
 * Used to load or save person asset data to MongoDB.
 * 
 * @author dadams
 */
public class MongoPersonAsset implements MongoConverter<IPersonAsset> {

    /** Property for username */
    public static final String PROP_USER_NAME = "user";

    /** Property for email address */
    public static final String PROP_EMAIL = "email";

    /** Property for roles */
    public static final String PROP_ROLES = "roles";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
     */
    @Override
    public BasicDBObject convert(IPersonAsset source) {
	return MongoPersonAsset.toDBObject(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(com.mongodb.DBObject)
     */
    @Override
    public IPersonAsset convert(DBObject source) {
	return MongoPersonAsset.fromDBObject(source);
    }

    /**
     * Copy information from SPI into Mongo DBObject.
     * 
     * @param source
     * @param target
     */
    public static void toDBObject(IPersonAsset source, BasicDBObject target) {
	MongoAsset.toDBObject(source, target);

	target.append(PROP_USER_NAME, source.getUserName());
	target.append(PROP_EMAIL, source.getEmailAddress());
	target.append(PROP_ROLES, source.getRoles());
    }

    /**
     * Copy information from Mongo DBObject to model object.
     * 
     * @param source
     * @param target
     */
    @SuppressWarnings("unchecked")
    public static void fromDBObject(DBObject source, PersonAsset target) {
	MongoAsset.fromDBObject(source, target);

	String username = (String) source.get(PROP_USER_NAME);
	String email = (String) source.get(PROP_EMAIL);
	List<String> roles = (List<String>) source.get(PROP_ROLES);

	target.setUserName(username);
	target.setEmailAddress(email);
	target.setRoles(roles);
    }

    /**
     * Convert SPI object to Mongo DBObject.
     * 
     * @param source
     * @return
     */
    public static BasicDBObject toDBObject(IPersonAsset source) {
	BasicDBObject result = new BasicDBObject();
	MongoPersonAsset.toDBObject(source, result);
	return result;
    }

    /**
     * Convert a DBObject into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static PersonAsset fromDBObject(DBObject source) {
	PersonAsset result = new PersonAsset();
	MongoPersonAsset.fromDBObject(source, result);
	return result;
    }
}