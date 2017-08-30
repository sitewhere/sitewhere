/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.assetmanagement.mongodb;

import java.util.List;

import org.bson.Document;

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
    public Document convert(IPersonAsset source) {
	return MongoPersonAsset.toDocument(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(org.bson.Document)
     */
    @Override
    public IPersonAsset convert(Document source) {
	return MongoPersonAsset.fromDocument(source);
    }

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     */
    public static void toDocument(IPersonAsset source, Document target) {
	MongoAsset.toDocument(source, target);

	target.append(PROP_USER_NAME, source.getUserName());
	target.append(PROP_EMAIL, source.getEmailAddress());
	target.append(PROP_ROLES, source.getRoles());
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     */
    @SuppressWarnings("unchecked")
    public static void fromDocument(Document source, PersonAsset target) {
	MongoAsset.fromDocument(source, target);

	String username = (String) source.get(PROP_USER_NAME);
	String email = (String) source.get(PROP_EMAIL);
	List<String> roles = (List<String>) source.get(PROP_ROLES);

	target.setUserName(username);
	target.setEmailAddress(email);
	target.setRoles(roles);
    }

    /**
     * Convert SPI object to Mongo {@link Document}.
     * 
     * @param source
     * @return
     */
    public static Document toDocument(IPersonAsset source) {
	Document result = new Document();
	MongoPersonAsset.toDocument(source, result);
	return result;
    }

    /**
     * Convert a {@link Document} into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static PersonAsset fromDocument(Document source) {
	PersonAsset result = new PersonAsset();
	MongoPersonAsset.fromDocument(source, result);
	return result;
    }
}