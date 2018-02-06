/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration.parser;

/**
 * Enumerates elements used by search providers parser.
 * 
 * @author Derek
 */
public interface IEventSearchParser {

    // Root element name.
    public static final String ROOT = "event-search";

    /**
     * Expected child elements.
     * 
     * @author Derek
     */
    public static enum Elements {

	/** Solr search provider */
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

    /**
     * Expected child elements.
     * 
     * @author Derek
     */
    public static enum SearchProvidersElements {

	/** Solr search provider */
	SolrSearchProvider("solr-search-provider");

	/** Event code */
	private String localName;

	private SearchProvidersElements(String localName) {
	    this.localName = localName;
	}

	public static SearchProvidersElements getByLocalName(String localName) {
	    for (SearchProvidersElements value : SearchProvidersElements.values()) {
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
