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

	/** Top level element. */
	Top_Level(null, false, true, false),

	/** Top level element. */
	Globals_Global("Global", true, true, true),

	/** Data management container. Datastore configuration. */
	DataManagement_Datastore("Datastore", false, false, false),

	/** Data management container. Cache provider configuration. */
	DataManagement_CacheProvider("Cache Provider", true, false, false),

	/** Data management container. Device model initializer configuration. */
	DataManagement_DeviceModelInitializer("Device Model Initializer", true, false, false),

	/** Data management container. Asset model initializer configuration. */
	DataManagement_AssetModelInitializer("Asset Model Initializer", true, false, false),

	/** Data management container. Schedule model initializer configuration. */
	DataManagement_ScheduleModelInitializer("Schedule Model Initializer", true, false, false),

	/** Device communication container. Event sources configuration. */
	DeviceCommunication_EventSources(null, false, false, false),

	/** Event sources container. Event source. */
	EventSources_EventSource("Event Source", true, true, true),

	/** Event source. Binary event decoder. */
	EventSources_BinaryEventDecoder("Binary Event Decoder", true, false, false),

	/** Device communication container. Inbound processing strategy. */
	DeviceCommunication_InboundProcessingStrategy(null, false, false, false),

	/** Inbound processing strategy container. Blocking queue strategy. */
	InboundProcessingStrategy_Strategy("Strategy", false, false, false),

	/** Device communication container. Registration. */
	DeviceCommunication_Registration(null, false, false, false),

	/** Registration container. Registration manager. */
	Registration_RegistrationManager("Registration Manager", false, false, false),

	/** Device communication container. Batch operations. */
	DeviceCommunication_BatchOperations(null, false, false, false),

	/** Batch operations container. Batch operation manager. */
	BatchOperations_BatchOperationManager("Batch Operation Manager", false, false, false),

	/** Device communication container. Command routing configuration. */
	DeviceCommunication_CommandRouting(null, false, false, false),

	/** Command routing container. Command router implementation. */
	CommandRouting_CommandRouter("Command Router", false, false, false),

	/** Specification mapping router. Mapping. */
	CommandRouting_SpecificationMappingRouter_Mapping("Mapping", true, true, true),

	/** Device communication container. Command destinations configuration. */
	DeviceCommunication_CommandDestinations(null, false, false, false),

	/** Command destinations. Command destination. */
	CommandDestinations_CommandDestination("Command Destination", true, true, true),

	/** Command destination. Binary command encoder. */
	CommandDestinations_BinaryCommandEncoder("Binary Command Encoder", false, false, false),

	/** Command destination. Paramter extractor. */
	CommandDestinations_ParameterExtractor("Parameter Extractor", false, false, false),

	/** Inbound processing chain. Event processor. */
	InboundProcessingChain_EventProcessor("Event Processor", true, true, true);

	/** Role name */
	private String name;

	/** Indicates if role is optional */
	private boolean optional;

	/** Indicates if multiple elements in role are allowed */
	private boolean multiple;

	/** Indicates if elements in role can be reordered */
	private boolean reorderable;

	private ElementRole(String name, boolean optional, boolean multiple, boolean reorderable) {
		this.name = name;
		this.optional = optional;
		this.multiple = multiple;
		this.reorderable = reorderable;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	public boolean isMultiple() {
		return multiple;
	}

	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

	public boolean isReorderable() {
		return reorderable;
	}

	public void setReorderable(boolean reorderable) {
		this.reorderable = reorderable;
	}
}