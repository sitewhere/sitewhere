/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
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

	/*******************************
	 * COMMON SYSTEM FUNCTIONALITY *
	 *******************************/

	/** Bean id for system tracer implementation */
	public static final String BEAN_TRACER = "tracer";

	/*************************
	 * MANAGEMENT INTERFACES *
	 *************************/

	/** Bean id for user management in server configuration */
	public static final String BEAN_USER_MANAGEMENT = "userManagement";

	/** Bean id for device management in server configuration */
	public static final String BEAN_DEVICE_MANAGEMENT = "deviceManagement";

	/** Bean id for device communication subsystem in server configuration */
	public static final String BEAN_DEVICE_COMMUNICATION = "deviceCommunication";

	/** Bean id for asset management in server configuration */
	public static final String BEAN_ASSET_MANAGEMENT = "assetManagement";

	/** Bean id for schedule management in server configuration */
	public static final String BEAN_SCHEDULE_MANAGEMENT = "scheduleManagement";

	/** Bean id for asset module manager in server configuration */
	public static final String BEAN_ASSET_MODULE_MANAGER = "assetModuleManager";

	/**********************************
	 * EVENT PROCESSING CONFIGURATION *
	 **********************************/

	/** Bean id for inbound event processor chain */
	public static final String BEAN_INBOUND_PROCESSOR_CHAIN = "inboundProcessorChain";

	/** Bean id for outbound event processor chain */
	public static final String BEAN_OUTBOUND_PROCESSOR_CHAIN = "outboundProcessorChain";

	/*******************
	 * CACHE PROVIDERS *
	 *******************/

	/** Bean id for device management cache provider */
	public static final String BEAN_DEVICE_MANAGEMENT_CACHE_PROVIDER = "deviceManagementCacheProvider";

	/*****************************
	 * SEARCH PROVIDER MANAGMENT *
	 *****************************/

	/** Bean id for search provider manager */
	public static final String BEAN_SEARCH_PROVIDER_MANAGER = "searchProviderManager";

	/*********************
	 * DATA INITIALIZERS *
	 *********************/

	/** Bean id for user management data initializer in server configuration */
	public static final String BEAN_USER_MODEL_INITIALIZER = "userModelInitializer";

	/** Bean id for device management data initializer in server configuration */
	public static final String BEAN_DEVICE_MODEL_INITIALIZER = "deviceModelInitializer";

	/** Bean id for asset management data initializer in server configuration */
	public static final String BEAN_ASSET_MODEL_INITIALIZER = "assetModelInitializer";
}