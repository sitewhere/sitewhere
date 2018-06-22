/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration.parser;

/**
 * Enumerates elements used by common connector parser.
 * 
 * @author Derek
 */
public interface IConnectorCommonParser {

    public static enum Solr {

	/** Solr configuration choice */
	SolrConfigurationChoice("solr-configuration-choice");

	/** Event code */
	private String localName;

	private Solr(String localName) {
	    this.localName = localName;
	}

	public static Solr getByLocalName(String localName) {
	    for (Solr value : Solr.values()) {
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

    public static enum SolrConnectorElements {

	/** Custom Solr configuration */
	SolrConfiguration("solr-configuration"),

	/** Solr configuration reference */
	SolrConfigurationReference("solr-configuration-reference");

	/** Event code */
	private String localName;

	private SolrConnectorElements(String localName) {
	    this.localName = localName;
	}

	public static SolrConnectorElements getByLocalName(String localName) {
	    for (SolrConnectorElements value : SolrConnectorElements.values()) {
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
