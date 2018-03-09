/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration.parser;

/**
 * Enumerates elements used by label generation parser.
 * 
 * @author Derek
 */
public interface ILabelGenerationParser {

    // Root element name.
    public static final String ROOT = "label-generation";

    /**
     * Expected child elements.
     * 
     * @author Derek
     */
    public static enum Elements {

	/** Label generator manager */
	LabelGeneratorManager("label-generator-manager");

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
     * Elements for label generators.
     * 
     * @author Derek
     */
    public static enum LabelGeneratorElements {

	/** QR code label generator */
	QrCodeLabelGenerator("qr-code-label-generator");

	/** Event code */
	private String localName;

	private LabelGeneratorElements(String localName) {
	    this.localName = localName;
	}

	public static LabelGeneratorElements getByLocalName(String localName) {
	    for (LabelGeneratorElements value : LabelGeneratorElements.values()) {
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