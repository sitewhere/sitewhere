/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb.user;

import java.util.Date;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sitewhere.mongodb.common.MongoMetadataProvider;
import com.sitewhere.mongodb.common.MongoSiteWhereEntity;
import com.sitewhere.rest.model.user.User;
import com.sitewhere.spi.user.AccountStatus;
import com.sitewhere.spi.user.IUser;

/**
 * Used to load or save user data to MongoDB.
 * 
 * @author dadams
 */
public class MongoUser {

    /** Property for username */
    public static final String PROP_USERNAME = "username";

    /** Property for hashed password */
    public static final String PROP_HASHED_PASSWORD = "hashedPassword";

    /** Property for first name */
    public static final String PROP_FIRST_NAME = "firstName";

    /** Property for last name */
    public static final String PROP_LAST_NAME = "lastName";

    /** Property for last login date */
    public static final String PROP_LAST_LOGIN = "lastLogin";

    /** Property for account status */
    public static final String PROP_STATUS = "status";

    /** List of granted authority names */
    public static final String PROP_AUTHORITIES = "authorities";

    /**
     * Copy information from SPI into Mongo DBObject.
     * 
     * @param source
     * @param target
     */
    public static void toDBObject(IUser source, BasicDBObject target) {
	target.append(PROP_USERNAME, source.getUsername());
	target.append(PROP_HASHED_PASSWORD, source.getHashedPassword());
	target.append(PROP_FIRST_NAME, source.getFirstName());
	target.append(PROP_LAST_NAME, source.getLastName());
	target.append(PROP_LAST_LOGIN, source.getLastLogin());
	target.append(PROP_AUTHORITIES, source.getAuthorities());
	if (source.getStatus() != null) {
	    target.append(PROP_STATUS, source.getStatus().name());
	}
	MongoSiteWhereEntity.toDBObject(source, target);
	MongoMetadataProvider.toDBObject(source, target);
    }

    /**
     * Copy information from Mongo DBObject to model object.
     * 
     * @param source
     * @param target
     */
    @SuppressWarnings("unchecked")
    public static void fromDBObject(DBObject source, User target) {
	String username = (String) source.get(PROP_USERNAME);
	String hashedPassword = (String) source.get(PROP_HASHED_PASSWORD);
	String firstName = (String) source.get(PROP_FIRST_NAME);
	String lastName = (String) source.get(PROP_LAST_NAME);
	Date lastLogin = (Date) source.get(PROP_LAST_LOGIN);
	String status = (String) source.get(PROP_STATUS);
	List<String> authorities = (List<String>) source.get(PROP_AUTHORITIES);

	target.setUsername(username);
	target.setHashedPassword(hashedPassword);
	target.setFirstName(firstName);
	target.setLastName(lastName);
	target.setLastLogin(lastLogin);
	target.setAuthorities(authorities);
	if (status != null) {
	    target.setStatus(AccountStatus.valueOf(status));
	}
	MongoSiteWhereEntity.fromDBObject(source, target);
	MongoMetadataProvider.fromDBObject(source, target);
    }

    /**
     * Convert SPI object to Mongo DBObject.
     * 
     * @param source
     * @return
     */
    public static BasicDBObject toDBObject(IUser source) {
	BasicDBObject result = new BasicDBObject();
	MongoUser.toDBObject(source, result);
	return result;
    }

    /**
     * Convert a DBObject into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static User fromDBObject(DBObject source) {
	User result = new User();
	MongoUser.fromDBObject(source, result);
	return result;
    }
}