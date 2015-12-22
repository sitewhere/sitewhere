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

	/** Device communication container. Event sources configuration. */
	DeviceCommunication_EventSources,

	/** Event sources container. MQTT event source. */
	EventSources_MqttEventSource,

	/** Event sources container. Event decoder. */
	EventSources_EventDecoder,

	/** Device communication container. Inbound processing strategy. */
	DeviceCommunication_InboundProcessingStrategy,

	/** Inbound processing strategy container. Blocking queue strategy. */
	InboundProcessingStrategy_BlockingQueue,

	/** Device communication container. Registration. */
	DeviceCommunication_Registration,

	/** Registration container. Registration manager. */
	Registration_RegistrationManager,

	/** Device communication container. Batch operations. */
	DeviceCommunication_BatchOperations,

	/** Batch operations container. Batch operation manager. */
	BatchOperations_BatchOperationManager,

	/** Device communication container. Command routing configuration. */
	DeviceCommunication_CommandRouting,

	/** Command routing container. Command router implementation. */
	CommandRouting_CommandRouter,

	/** Specification mapping router. Mapping. */
	CommandRouting_SpecificationMappingRouter_Mapping,

	/** Device communication container. Command destinations configuration. */
	DeviceCommunication_CommandDestinations,

	/** Command destinations. Command destination. */
	CommandDestinations_CommandDestination,

	/** Command destination. Binary command encoder. */
	CommandDestinations_BinaryCommandEncoder,

	/** Command destination. Paramter extractor. */
	CommandDestinations_ParameterExtractor,

	/** Top level. Inbound processing chain configuration. */
	Top_InboundProcessingChain,

	/** Top level. Outbound processing chain configuration. */
	Top_OutboundProcessingChain,

	/** Top level. Asset management configuration. */
	Top_AssetManagement,
}