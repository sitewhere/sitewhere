/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.error;

/**
 * Indicates severity of an error.
 * 
 * @author Derek
 */
public enum ErrorLevel {

    INFO('I'), WARNING('W'), ERROR('E'), CRITICAL('C');

    /** Type indicator */
    private char type;

    private ErrorLevel(char type) {
	this.type = type;
    }

    /**
     * Get the area type.
     * 
     * @return
     */
    public char getType() {
	return type;
    }

    /**
     * Get ErrorLevel from type indicator.
     * 
     * @param type
     * @return
     */
    public static ErrorLevel fromType(char type) {
	for (ErrorLevel level : ErrorLevel.values()) {
	    if (level.getType() == type) {
		return level;
	    }
	}
	throw new RuntimeException("Unknown ErrorLevel type: " + type);
    }
}