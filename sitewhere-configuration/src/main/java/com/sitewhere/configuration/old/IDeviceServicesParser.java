/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration.old;

/**
 * Enumerates elements used by device services parser.
 * 
 * @author Derek
 */
public interface IDeviceServicesParser {

    /**
     * Expected child elements.
     * 
     * @author Derek
     */
    public static enum Elements {

	/** Default registration manager */
	DefaultRegistrationManager("default-registration-manager"),

	/** Registration manager reference */
	RegistrationManager("registration-manager"),

	/** Symbol generator manager reference */
	SymbolGeneratorManager("symbol-generator-manager"),

	/** Default presence manager */
	DefaultPresenceManager("default-presence-manager");

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
    public static enum SymbolGenerators {

	/** QR-Code symbol generator */
	QRCodeSymbolGenerator("qr-code-symbol-generator");

	/** Event code */
	private String localName;

	private SymbolGenerators(String localName) {
	    this.localName = localName;
	}

	public static SymbolGenerators getByLocalName(String localName) {
	    for (SymbolGenerators value : SymbolGenerators.values()) {
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