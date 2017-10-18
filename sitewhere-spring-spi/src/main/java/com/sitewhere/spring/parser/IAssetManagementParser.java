/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spring.parser;

/**
 * Enumerates elements used by asset management parser.
 * 
 * @author Derek
 */
public interface IAssetManagementParser {

    public static enum Elements {

	/** Default MongoDB datastore */
	DefaultMongoDatastore("default-mongodb-datastore"),

	/** Asset modules section */
	AssetModules("asset-modules");

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

    /**
     * Enumerates asset module elements.
     * 
     * @author Derek
     */
    public static interface IAssetModulesParser {

	public static enum Elements {

	    /** References an asset module defined as a Spring bean */
	    AssetModuleReference("asset-module"),

	    /** Asset module that pulls data from WSO2 Identity Server */
	    Wso2IdentityAssetModule("wso2-identity-asset-module");

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
}