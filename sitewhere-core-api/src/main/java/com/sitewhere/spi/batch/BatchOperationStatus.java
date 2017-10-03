/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.batch;

/**
 * Status indicators for batch operation processing.
 * 
 * @author Derek
 */
public enum BatchOperationStatus {

    /** Indicates a batch operation has not been processed */
    Unprocessed('U'),

    /** Indicates a batch operation is currently being processed */
    Processing('P'),

    /** Indicates processing succeeded for the batch operation */
    FinishedSuccessfully('F'),

    /** Indicates processing finished with errors for the batch operation */
    FinishedWithErrors('E');

    /** Event code */
    private char code;

    private BatchOperationStatus(char code) {
	this.code = code;
    }

    public static BatchOperationStatus getByCode(char code) {
	for (BatchOperationStatus value : BatchOperationStatus.values()) {
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