/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.batch;

/**
 * Indicates the processing status of a single {@link IBatchElement}.
 * 
 * @author Derek
 */
public enum ElementProcessingStatus {

    /** Indicates a batch element has not been processed */
    Unprocessed('U'),

    /** Indicates a batch element is currently being processed */
    Processing('P'),

    /** Indicates processing failed for the batch element */
    Failed('F'),

    /** Indicates processing succeeded for the batch element */
    Succeeded('S');

    /** Event code */
    private char code;

    private ElementProcessingStatus(char code) {
	this.code = code;
    }

    public static ElementProcessingStatus getByCode(char code) {
	for (ElementProcessingStatus value : ElementProcessingStatus.values()) {
	    if (value.getCode() == code) {
		return value;
	    }
	}
	return null;
    }

    public char getCode() {
	return code;
    }

    public void setCode(char code) {
	this.code = code;
    }
}