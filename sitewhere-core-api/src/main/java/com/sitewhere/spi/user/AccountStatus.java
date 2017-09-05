/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.user;

/**
 * Enumeration of possible account status values.
 * 
 * @author Derek
 */
public enum AccountStatus {

    /** Account is active */
    Active('A'),

    /** Account is disabled due to inactivity */
    Expired('E'),

    /** Account is locked due to admin action */
    Locked('L');

    /** Status code */
    private char statusCode;

    private AccountStatus(char statusCode) {
	this.statusCode = statusCode;
    }

    /**
     * Get the status code.
     * 
     * @return
     */
    public char getStatusCode() {
	return statusCode;
    }

    /**
     * Get AccountStatus from status code.
     * 
     * @param code
     * @return
     */
    public static AccountStatus fromStatusCode(char code) {
	for (AccountStatus status : AccountStatus.values()) {
	    if (status.getStatusCode() == code) {
		return status;
	    }
	}
	throw new RuntimeException("Unknown AccountStatus code: " + code);
    }
}