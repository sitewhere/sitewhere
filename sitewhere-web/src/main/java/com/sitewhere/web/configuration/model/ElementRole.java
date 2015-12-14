/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.configuration.model;

/**
 * Used to indicate role of an element.
 * 
 * @author Derek
 */
public enum ElementRole {

	/** Top level. Globals configuration. */
	Top_Globals,

	/** Top level. Data management configuration. */
	Top_DataManagement,

	/** Data management container. Datastore configuration. */
	DataManagement_Datastore,

	/** Data management container. Cache provider configuration. */
	DataManagement_CacheProvider,

	/** Data management container. Device model initializer configuration. */
	DataManagement_DeviceModelInitializer,

	/** Data management container. Asset model initializer configuration. */
	DataManagement_AssetModelInitializer,

	/** Data management container. Schedule model initializer configuration. */
	DataManagement_ScheduleModelInitializer,

	/** Top level. Device communication configuration. */
	Top_DeviceCommunication,

	/** Top level. Inbound processing chain configuration. */
	Top_InboundProcessingChain,

	/** Top level. Outbound processing chain configuration. */
	Top_OutboundProcessingChain,

	/** Top level. Asset management configuration. */
	Top_AssetManagement,
}