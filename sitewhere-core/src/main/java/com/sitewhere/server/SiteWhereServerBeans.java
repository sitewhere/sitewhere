/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package com.sitewhere.server;

/**
 * Constants for Spring beans needed by the SiteWhere server.
 * 
 * @author Derek
 */
public interface SiteWhereServerBeans {

	/*************************
	 * MANAGEMENT INTERFACES *
	 *************************/

	/** Bean id for user management in server configuration */
	public static final String BEAN_USER_MANAGEMENT = "userManagement";

	/** Bean id for device management in server configuration */
	public static final String BEAN_DEVICE_MANAGEMENT = "deviceManagement";

	/** Bean id for asset module manager in server configuration */
	public static final String BEAN_ASSET_MODULE_MANAGER = "assetModuleManager";

	/*********************
	 * DATA INITIALIZERS *
	 *********************/

	/** Bean id for user management data initializer in server configuration */
	public static final String BEAN_USER_MODEL_INITIALIZER = "userModelInitializer";

	/** Bean id for device management data initializer in server configuration */
	public static final String BEAN_DEVICE_MODEL_INITIALIZER = "deviceModelInitializer";
}