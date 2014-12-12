/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.server;

/**
 * Status indicator for SiteWhere server.
 * 
 * @author Derek
 */
public enum ServerStatus {

	/** Server is stopped */
	Stopped,

	/** Server is starting */
	Starting,

	/** Server is started */
	Started,

	/** Server is stopping */
	Stopping,

	/** Server startup failed with an error */
	Error;
}