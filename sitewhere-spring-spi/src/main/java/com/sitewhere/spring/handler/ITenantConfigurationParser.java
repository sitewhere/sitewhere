/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spring.handler;

/**
 * Enumerates elements used by tenant configuration parser.
 * 
 * @author Derek
 */
public interface ITenantConfigurationParser {

    /**
     * Expected child elements.
     * 
     * @author Derek
     */
    public static enum Elements {

	/** Globals */
	Globals("globals"),

	/** Tenant datastore */
	TenantDatastore("tenant-datastore"),

	/** Device Communication Subsystem */
	DeviceCommunication("device-communication"),

	/** Event processing Subsystem */
	EventProcessing("event-processing"),

	/** Asset management */
	AssetManagement("asset-management"),

	/** Search providers */
	SearchProviders("search-providers");

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