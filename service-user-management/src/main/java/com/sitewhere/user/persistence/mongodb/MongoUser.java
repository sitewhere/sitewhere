/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.user.persistence.mongodb;

import java.util.Date;
import java.util.List;

import org.bson.Document;

import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.mongodb.common.MongoPersistentEntity;
import com.sitewhere.rest.model.user.User;
import com.sitewhere.spi.user.AccountStatus;
import com.sitewhere.spi.user.IUser;

/**
 * Used to load or save user data to MongoDB.
 * 
 * @author dadams
 */
public class MongoUser implements MongoConverter<IUser> {

    /** Property for username */
    public static final String PROP_USERNAME = "usnm";

    /** Property for hashed password */
    public static final String PROP_HASHED_PASSWORD = "hpwd";

    /** Property for first name */
    public static final String PROP_FIRST_NAME = "fnam";

    /** Property for last name */
    public static final String PROP_LAST_NAME = "lnam";

    /** Property for last login date */
    public static final String PROP_LAST_LOGIN = "llog";

    /** Property for account status */
    public static final String PROP_STATUS = "stat";

    /** List of granted authority names */
    public static final String PROP_AUTHORITIES = "auth";

    /*
     * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
     */
    public Document convert(IUser source) {
	return MongoUser.toDocument(source);
    }

    /*
     * @see com.sitewhere.mongodb.MongoConverter#convert(org.bson.Document)
     */
    public User convert(Document source) {
	return MongoUser.fromDocument(source);
    }

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     */
    public static void toDocument(IUser source, Document target) {
	target.append(PROP_USERNAME, source.getUsername());
	target.append(PROP_HASHED_PASSWORD, source.getHashedPassword());
	target.append(PROP_FIRST_NAME, source.getFirstName());
	target.append(PROP_LAST_NAME, source.getLastName());
	target.append(PROP_LAST_LOGIN, source.getLastLogin());
	target.append(PROP_AUTHORITIES, source.getAuthorities());
	if (source.getStatus() != null) {
	    target.append(PROP_STATUS, source.getStatus().name());
	}

	MongoPersistentEntity.toDocument(source, target);
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     */
    @SuppressWarnings("unchecked")
    public static void fromDocument(Document source, User target) {
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

	MongoPersistentEntity.fromDocument(source, target);
    }

    /**
     * Convert SPI object to Mongo {@link Document}.
     * 
     * @param source
     * @return
     */
    public static Document toDocument(IUser source) {
	Document result = new Document();
	MongoUser.toDocument(source, result);
	return result;
    }

    /**
     * Convert a {@link Document} into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static User fromDocument(Document source) {
	User result = new User();
	MongoUser.fromDocument(source, result);
	return result;
    }
}