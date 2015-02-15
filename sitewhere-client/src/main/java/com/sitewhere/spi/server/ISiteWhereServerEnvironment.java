/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.server;

/**
 * Environment variables that affect SiteWhere server behavior.
 * 
 * @author Derek
 */
public interface ISiteWhereServerEnvironment {

	/**
	 * If specified, overrides the default instance id for the server with another value.
	 * Useful for when a container (such as Docker) needs to assign the instance id.
	 */
	public static final String ENV_INSTANCE_ID = "SW_INSTANCE_ID";

	/**
	 * If specified, overrides the default configuration file with a URL from which the
	 * configuration will be loaded.
	 */
	public static final String ENV_EXTERNAL_CONFIGURATION_URL = "SW_CONFIG_URL";
}