/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.logging;

/**
 * Log level information for a log message.
 * 
 * @author Derek
 */
public enum LogLevel {
    Trace(0), Debug(1), Information(2), Warning(3), Error(4);

    /** Path */
    private int level;

    private LogLevel(int level) {
	this.level = level;
    }

    public static LogLevel getByLevel(int level) {
	for (LogLevel value : LogLevel.values()) {
	    if (value.getLevel() == level) {
		return value;
	    }
	}
	return null;
    }

    public int getLevel() {
	return level;
    }
}
