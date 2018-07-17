/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.user.persistence.mongodb;

import org.bson.Document;

import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.rest.model.user.GrantedAuthority;
import com.sitewhere.spi.user.IGrantedAuthority;

/**
 * Used to load or save authority data to MongoDB.
 * 
 * @author dadams
 */
public class MongoGrantedAuthority implements MongoConverter<IGrantedAuthority> {

    /** Property for authority name */
    public static final String PROP_AUTHORITY = "authority";

    /** Property for authority description */
    public static final String PROP_DESCRIPTION = "description";

    /** Property for parent */
    public static final String PROP_PARENT = "parent";

    /** Property for group indicator */
    public static final String PROP_GROUP = "group";

    /*
     * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
     */
    public Document convert(IGrantedAuthority source) {
	return MongoGrantedAuthority.toDocument(source);
    }

    /*
     * @see com.sitewhere.mongodb.MongoConverter#convert(org.bson.Document)
     */
    public GrantedAuthority convert(Document source) {
	return MongoGrantedAuthority.fromDocument(source);
    }

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     */
    public static void toDocument(IGrantedAuthority source, Document target) {
	target.append(PROP_AUTHORITY, source.getAuthority());
	target.append(PROP_DESCRIPTION, source.getDescription());
	target.append(PROP_PARENT, source.getParent());
	target.append(PROP_GROUP, source.isGroup());
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     */
    public static void fromDocument(Document source, GrantedAuthority target) {
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
     * Convert SPI object to Mongo {@link Document}.
     * 
     * @param source
     * @return
     */
    public static Document toDocument(IGrantedAuthority source) {
	Document result = new Document();
	MongoGrantedAuthority.toDocument(source, result);
	return result;
    }

    /**
     * Convert a {@link Document} into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static GrantedAuthority fromDocument(Document source) {
	GrantedAuthority result = new GrantedAuthority();
	MongoGrantedAuthority.fromDocument(source, result);
	return result;
    }
}