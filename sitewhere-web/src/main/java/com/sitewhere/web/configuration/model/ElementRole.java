/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.configuration.model;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sitewhere.web.configuration.model.ElementRole.Serializer;

/**
 * Used to indicate role of an element.
 * 
 * @author Derek
 */
@JsonSerialize(using = Serializer.class)
public enum ElementRole {

	/** Top level element. */
	Globals_Global("Global", true, true, true),

	/** Globals element. */
	Globals(null, false, false, false, new ElementRole[] { Globals_Global }, new ElementRole[0], true),

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

	/** Data management. */
	DataManagement(null, false, false, false, new ElementRole[] {
			DataManagement_Datastore,
			DataManagement_CacheProvider,
			DataManagement_DeviceModelInitializer,
			DataManagement_AssetModelInitializer,
			DataManagement_ScheduleModelInitializer }, new ElementRole[0], true),

	/** Event source. Binary event decoder. */
	EventSource_BinaryEventDecoder("Binary Event Decoder", true, false, false),

	/** Event source. Event decoder. */
	EventSource_EventDecoder("Event Decoder", true, false, false, new ElementRole[0],
			new ElementRole[] { EventSource_BinaryEventDecoder }),

	/** Event sources container. Event source. */
	EventSources_EventSource("Event Sources", true, true, true,
			new ElementRole[] { EventSource_EventDecoder }),

	/** Device communication container. Event sources configuration. */
	DeviceCommunication_EventSources(null, false, false, false,
			new ElementRole[] { EventSources_EventSource }, new ElementRole[0], true),

	/** Inbound processing strategy container. Blocking queue strategy. */
	InboundProcessingStrategy_Strategy("Strategy", false, false, false),

	/** Device communication container. Inbound processing strategy. */
	DeviceCommunication_InboundProcessingStrategy(null, false, false, false,
			new ElementRole[] { InboundProcessingStrategy_Strategy }, new ElementRole[0], true),

	/** Registration container. Registration manager. */
	Registration_RegistrationManager("Registration Manager", false, false, false),

	/** Device communication container. Registration. */
	DeviceCommunication_Registration(null, false, false, false,
			new ElementRole[] { Registration_RegistrationManager }, new ElementRole[0], true),

	/** Batch operations container. Batch operation manager. */
	BatchOperations_BatchOperationManager("Batch Operation Manager", false, false, false),

	/** Device communication container. Batch operations. */
	DeviceCommunication_BatchOperations(null, false, false, false,
			new ElementRole[] { BatchOperations_BatchOperationManager }, new ElementRole[0], true),

	/** Specification mapping router. Mapping. */
	CommandRouting_SpecificationMappingRouter_Mapping("Mappings", true, true, true),

	/** Specification mapping router. Mapping. */
	CommandRouting_SpecificationMappingRouter("Specification Mapping Router", false, false, false,
			new ElementRole[] { CommandRouting_SpecificationMappingRouter_Mapping }),

	/** Command routing container. Command router implementation. */
	CommandRouting_CommandRouter("Command Router", false, false, false, new ElementRole[0],
			new ElementRole[] { CommandRouting_SpecificationMappingRouter }),

	/** Device communication container. Command routing configuration. */
	DeviceCommunication_CommandRouting(null, false, false, false,
			new ElementRole[] { CommandRouting_CommandRouter }, new ElementRole[0], true),

	/** Command destination. Binary command encoder. */
	CommandDestinations_BinaryCommandEncoder("Binary Command Encoder", false, false, false),

	/** Command destination. Paramter extractor. */
	CommandDestinations_ParameterExtractor("Parameter Extractor", false, false, false),

	/** Command destinations. Command destination. */
	CommandDestinations_CommandDestination("Command Destinations", true, true, true, new ElementRole[] {
			CommandDestinations_BinaryCommandEncoder,
			CommandDestinations_ParameterExtractor }),

	/** Device communication container. Command destinations configuration. */
	DeviceCommunication_CommandDestinations(null, false, false, false,
			new ElementRole[] { CommandDestinations_CommandDestination }, new ElementRole[0], true),

	/** Device communication element. */
	DeviceCommunication(null, false, false, false, new ElementRole[] {
			DeviceCommunication_EventSources,
			DeviceCommunication_InboundProcessingStrategy,
			DeviceCommunication_Registration,
			DeviceCommunication_BatchOperations,
			DeviceCommunication_CommandRouting,
			DeviceCommunication_CommandDestinations }, new ElementRole[0], true),

	/** Inbound processing chain. Event processor. */
	InboundProcessingChain_EventProcessor("Event Processors", true, true, true),

	/** Inbound processing chain element. */
	InboundProcessingChain(null, false, false, false,
			new ElementRole[] { InboundProcessingChain_EventProcessor }, new ElementRole[0], true),

	/** Outbound processing chain. Processsor filters. */
	OutboundProcessingChain_OutboundFilters("Filters", true, true, true),

	/** Outbound processing chain. Processsor filter criteria. */
	OutboundProcessingChain_Filters("Filter Criteria", true, false, false,
			new ElementRole[] { OutboundProcessingChain_OutboundFilters }),

	/** Outbound processing chain. Filtered event processor. */
	OutboundProcessingChain_FilteredEventProcessor("Filtered Event Processors", true, true, true,
			new ElementRole[] { OutboundProcessingChain_Filters }),

	/** Zone test event processsor. Zone test. */
	OutboundProcessingChain_ZoneTest("Zone Tests", true, true, true),

	/** Outbound processing chain. Zone test event processor. */
	OutboundProcessingChain_ZoneTestEventProcessor("Zone Test Event Processor", true, true, true,
			new ElementRole[] { OutboundProcessingChain_ZoneTest, OutboundProcessingChain_Filters }),

	/** MQTT event processsor. Route builder. */
	OutboundProcessingChain_RouteBuilder("Route Builder", true, false, false),

	/** Outbound processing chain. MQTT event processor. */
	OutboundProcessingChain_MqttEventProcessor("MQTT Event Processor", true, true, true, new ElementRole[] {
			OutboundProcessingChain_Filters,
			OutboundProcessingChain_RouteBuilder }),

	/** Outbound processing chain. Event processor. */
	OutboundProcessingChain_EventProcessor("Event Processors", true, true, true, new ElementRole[0],
			new ElementRole[] {
					OutboundProcessingChain_FilteredEventProcessor,
					OutboundProcessingChain_ZoneTestEventProcessor,
					OutboundProcessingChain_MqttEventProcessor }),

	/** Outbound processing chain element. */
	OutboundProcessingChain(null, false, false, false,
			new ElementRole[] { OutboundProcessingChain_EventProcessor }, new ElementRole[0], true),

	/** Asset Management element. */
	AssetManagment(null, false, false, false, new ElementRole[0], new ElementRole[0], true),

	/** Top level element. */
	Root(null, false, false, false, new ElementRole[] {
			Globals,
			DataManagement,
			DeviceCommunication,
			InboundProcessingChain,
			OutboundProcessingChain,
			AssetManagment });

	/** Role name */
	private String name;

	/** Indicates if role is optional */
	private boolean optional;

	/** Indicates if multiple elements in role are allowed */
	private boolean multiple;

	/** Indicates if elements in role can be reordered */
	private boolean reorderable;

	/** Indicates if element is permanent */
	private boolean permanent;

	/** Child roles in the order they should appear */
	private ElementRole[] children;

	/** Subtypes that specialize the given role */
	private ElementRole[] subtypes;

	private ElementRole(String name, boolean optional, boolean multiple, boolean reorderable) {
		this(name, optional, multiple, reorderable, new ElementRole[0]);
	}

	private ElementRole(String name, boolean optional, boolean multiple, boolean reorderable,
			ElementRole[] children) {
		this(name, optional, multiple, reorderable, children, new ElementRole[0]);
	}

	private ElementRole(String name, boolean optional, boolean multiple, boolean reorderable,
			ElementRole[] children, ElementRole[] subtypes) {
		this(name, optional, multiple, reorderable, children, subtypes, false);
	}

	private ElementRole(String name, boolean optional, boolean multiple, boolean reorderable,
			ElementRole[] children, ElementRole[] subtypes, boolean permanent) {
		this.name = name;
		this.optional = optional;
		this.multiple = multiple;
		this.reorderable = reorderable;
		this.children = children;
		this.subtypes = subtypes;
		this.permanent = permanent;
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

	public boolean isPermanent() {
		return permanent;
	}

	public void setPermanent(boolean permanent) {
		this.permanent = permanent;
	}

	public ElementRole[] getChildren() {
		return children;
	}

	public void setChildren(ElementRole[] children) {
		this.children = children;
	}

	public ElementRole[] getSubtypes() {
		return subtypes;
	}

	public void setSubtypes(ElementRole[] subtypes) {
		this.subtypes = subtypes;
	}

	public static class Serializer extends JsonSerializer<ElementRole> {

		public void serialize(ElementRole value, JsonGenerator generator, SerializerProvider provider)
				throws IOException, JsonProcessingException {
			generator.writeStartObject();
			generator.writeFieldName("name");
			generator.writeString(value.getName());
			generator.writeFieldName("optional");
			generator.writeBoolean(value.isOptional());
			generator.writeFieldName("multiple");
			generator.writeBoolean(value.isMultiple());
			generator.writeFieldName("reorderable");
			generator.writeBoolean(value.isReorderable());
			generator.writeFieldName("permanent");
			generator.writeBoolean(value.isPermanent());

			if (value.getChildren() != null) {
				generator.writeArrayFieldStart("children");
				for (ElementRole child : value.getChildren()) {
					generator.writeString(child.name());
				}
				generator.writeEndArray();
			}

			if (value.getSubtypes() != null) {
				generator.writeArrayFieldStart("subtypes");
				for (ElementRole child : value.getSubtypes()) {
					generator.writeString(child.name());
				}
				generator.writeEndArray();
			}

			generator.writeEndObject();
		}
	}
}