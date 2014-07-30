/*
 * TracerCategory.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.server.debug;

/**
 * Allows hierarchy levels in {@link ITracer} stacks to be categorized.
 * 
 * @author Derek
 */
public enum TracerCategory {

	/** Denotes a web service entry point */
	WebService,

	/** Denotes a call to the core device management API */
	DeviceManagementApi,

	/** Denotes a data store entry point */
	DataStore,

	/** Indicates no category specified */
	Unspecified
}