/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spring.handler;

/**
 * Enumerates elements used by datastore parser.
 * 
 * @author Derek
 */
public interface IDatastoreParser {

    /**
     * Expected child elements.
     * 
     * @author Derek
     */
    public static enum Elements {

	/** Mongo datastore and service providers */
	Mongo("mongo-datastore"),

	/** HBase datastore and service providers */
	HBase("hbase-datastore"),

	/** Creates sample data if no user data is present */
	DefaultUserModelInitializer("default-user-model-initializer"),

	/** Uses Groovy script to create user data */
	GroovyUserModelInitializer("groovy-user-model-initializer"),

	/** Creates sample data if no tenant data is present */
	DefaultTenantModelInitializer("default-tenant-model-initializer"),

	/** Uses Groovy script to create tenant data */
	GroovyTenantModelInitializer("groovy-tenant-model-initializer");

	/** Event code */
	private String localName;

	private Elements(String localName) {
	    this.localName = localName;
	}

	public static Elements getByLocalName(String localName) {
	    for (Elements value : Elements.values()) {
		if (value.getLocalName().equals(localName)) {
		    return value;
		}
	    }
	    return null;
	}

	public String getLocalName() {
	    return localName;
	}

	public void setLocalName(String localName) {
	    this.localName = localName;
	}
    }
}