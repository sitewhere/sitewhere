/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.spi.server.ISiteWhereServer;
import com.sitewhere.spi.system.IVersion;
import com.sitewhere.spi.user.IUser;

/**
 * Main class for accessing core SiteWhere functionality.
 * 
 * @author Derek
 */
public class SiteWhere {

    /** Private logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LogManager.getLogger();

    /** SiteWhere version information */
    private static IVersion VERSION = new Version();

    public static ISiteWhereServer getServer() {
	return null;
    }

    public static IUser getCurrentlyLoggedInUser() {
	return null;
    }

    /**
     * Get singleton version information instance.
     * 
     * @return
     */
    public static IVersion getVersion() {
	return VERSION;
    }
}