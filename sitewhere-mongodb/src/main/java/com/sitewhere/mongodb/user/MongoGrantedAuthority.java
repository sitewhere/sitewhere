/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb.user;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sitewhere.rest.model.user.GrantedAuthority;
import com.sitewhere.spi.user.IGrantedAuthority;

/**
 * Used to load or save authority data to MongoDB.
 * 
 * @author dadams
 */
public class MongoGrantedAuthority {

    /** Property for authority name */
    public static final String PROP_AUTHORITY = "authority";

    /** Property for authority description */
    public static final String PROP_DESCRIPTION = "description";

    /** Property for parent */
    public static final String PROP_PARENT = "parent";

    /** Property for group indicator */
    public static final String PROP_GROUP = "group";

    /**
     * Copy information from SPI into Mongo DBObject.
     * 
     * @param source
     * @param target
     */
    public static void toDBObject(IGrantedAuthority source, BasicDBObject target) {
	target.append(PROP_AUTHORITY, source.getAuthority());
	target.append(PROP_DESCRIPTION, source.getDescription());
	target.append(PROP_PARENT, source.getParent());
	target.append(PROP_GROUP, source.isGroup());
    }

    /**
     * Copy information from Mongo DBObject to model object.
     * 
     * @param source
     * @param target
     */
    public static void fromDBObject(DBObject source, GrantedAuthority target) {
	String authority = (String) source.get(PROP_AUTHORITY);
	String description = (String) source.get(PROP_DESCRIPTION);
	String parent = (String) source.get(PROP_PARENT);
	Boolean group = (Boolean) source.get(PROP_GROUP);

	target.setAuthority(authority);
	target.setDescription(description);
	target.setParent(parent);
	if (group != null) {
	    target.setGroup(group.booleanValue());
	}
    }

    /**
     * Convert SPI object to Mongo DBObject.
     * 
     * @param source
     * @return
     */
    public static BasicDBObject toDBObject(IGrantedAuthority source) {
	BasicDBObject result = new BasicDBObject();
	MongoGrantedAuthority.toDBObject(source, result);
	return result;
    }

    /**
     * Convert a DBObject into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static GrantedAuthority fromDBObject(DBObject source) {
	GrantedAuthority result = new GrantedAuthority();
	MongoGrantedAuthority.fromDBObject(source, result);
	return result;
    }
}