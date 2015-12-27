/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.configuration;

import java.util.ArrayList;

import com.sitewhere.spring.handler.BatchOperationsParser;
import com.sitewhere.spring.handler.CommandDestinationsParser;
import com.sitewhere.spring.handler.CommandRoutingParser;
import com.sitewhere.spring.handler.DeviceCommunicationParser;
import com.sitewhere.spring.handler.EventSourcesParser;
import com.sitewhere.spring.handler.InboundProcessingChainParser;
import com.sitewhere.spring.handler.InboundProcessingStrategyParser;
import com.sitewhere.spring.handler.RegistrationParser;
import com.sitewhere.spring.handler.TenantConfigurationParser;
import com.sitewhere.spring.handler.TenantDatastoreParser;
import com.sitewhere.web.configuration.model.AttributeNode;
import com.sitewhere.web.configuration.model.AttributeType;
import com.sitewhere.web.configuration.model.ConfigurationModel;
import com.sitewhere.web.configuration.model.ElementNode;
import com.sitewhere.web.configuration.model.ElementRole;

/**
 * Specifies model used for tenant configuration user interface.
 * 
 * @author Derek
 */
public class TenantConfigurationModel extends ConfigurationModel {

	public TenantConfigurationModel() {
		setLocalName("tenant-configuration");
		setName("Tenant Configuration");
		setDescription("Provides a model for all aspects of tenant configuration.");
		setElements(new ArrayList<ElementNode>());
		getElements().add(createGlobals());
		getElements().add(createDataManagement());
		getElements().add(createDeviceCommunication());
		getElements().add(createInboundProcessingChain());
		getElements().add(createOutboundProcessingChain());
		getElements().add(createAssetManagement());
		setRole(ElementRole.Root.name());
	}

	/**
	 * Create the container for global overrides information.
	 * 
	 * @return
	 */
	protected ElementNode createGlobals() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Global Overrides",
						TenantConfigurationParser.Elements.Globals.getLocalName(), "cogs",
						ElementRole.Globals);
		builder.setDescription("Allow tenant-specific changes to global configuration elements.");
		return builder.build();
	}

	/**
	 * Create the container for datastore information.
	 * 
	 * @return
	 */
	protected ElementNode createDataManagement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Data Management",
						TenantConfigurationParser.Elements.TenantDatastore.getLocalName(), "database",
						ElementRole.DataManagement);
		builder.setDescription("Configure the datastore and related aspects such as caching and "
				+ "data model initialization.");
		builder.addElement(createMongoTenantDatastoreElement());
		builder.addElement(createHBaseTenantDatastoreElement());
		builder.addElement(createEHCacheElement());
		builder.addElement(createHazelcastCacheElement());
		builder.addElement(createDefaultDeviceModelInitializerElement());
		builder.addElement(createDefaultAssetModelInitializerElement());
		builder.addElement(createDefaultScheduleModelInitializerElement());
		return builder.build();
	}

	/**
	 * Create element configuration for MonogoDB tenant datastore.
	 * 
	 * @return
	 */
	protected ElementNode createMongoTenantDatastoreElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("MongoDB Tenant Datastore",
						TenantDatastoreParser.Elements.MongoTenantDatastore.getLocalName(), "database",
						ElementRole.DataManagement_Datastore);

		builder.setDescription("Store tenant data using a MongoDB database.");
		builder.addAttribute((new AttributeNode.Builder("Use bulk inserts", "useBulkEventInserts",
				AttributeType.Boolean).setDescription("Use the MongoDB bulk insert API to add "
				+ "events in groups and improve performance.").build()));
		builder.addAttribute((new AttributeNode.Builder("Bulk insert max chunk size",
				"bulkInsertMaxChunkSize", AttributeType.Integer).setDescription("Maximum number of records to send "
				+ "in a single bulk insert (if bulk inserts are enabled).").build()));
		return builder.build();
	}

	/**
	 * Create element configuration for HBase tenant datastore.
	 * 
	 * @return
	 */
	protected ElementNode createHBaseTenantDatastoreElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("HBase Tenant Datastore",
						TenantDatastoreParser.Elements.HBaseTenantDatastore.getLocalName(), "database",
						ElementRole.DataManagement_Datastore);
		builder.setDescription("Store tenant data using tables in an HBase instance.");
		return builder.build();
	}

	/**
	 * Create element configuration for EHCache cache.
	 * 
	 * @return
	 */
	protected ElementNode createEHCacheElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("EHCache Cache Provider",
						TenantDatastoreParser.Elements.EHCacheDeviceManagementCache.getLocalName(),
						"folder-open-o", ElementRole.DataManagement_CacheProvider);
		builder.setDescription("Cache device management data using EHCache. Note that this "
				+ "cache is not intended for use on clustered installations.");
		return builder.build();
	}

	/**
	 * Create element configuration for Hazelcast cache.
	 * 
	 * @return
	 */
	protected ElementNode createHazelcastCacheElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Hazelcast Distributed Cache Provider",
						TenantDatastoreParser.Elements.HazelcastCache.getLocalName(), "folder-open-o",
						ElementRole.DataManagement_CacheProvider);
		builder.setDescription("Cache device management data using Hazelcast distributed maps. "
				+ "This cache allows data to be shared between clustered SiteWhere instances.");
		return builder.build();
	}

	/**
	 * Create element configuration for device model initializer.
	 * 
	 * @return
	 */
	protected ElementNode createDefaultDeviceModelInitializerElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Default Device Model Initializer",
						TenantDatastoreParser.Elements.DefaultDeviceModelInitializer.getLocalName(), "flash",
						ElementRole.DataManagement_DeviceModelInitializer);
		builder.setDescription("This component creates sample data when no existing device data "
				+ "is detected in the datastore. A site with device specifications, devices, "
				+ "assignments, events and other example data is created on instance startup.");
		return builder.build();
	}

	/**
	 * Create element configuration for device model initializer.
	 * 
	 * @return
	 */
	protected ElementNode createDefaultAssetModelInitializerElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Default Asset Model Initializer",
						TenantDatastoreParser.Elements.DefaultAssetModelInitializer.getLocalName(), "flash",
						ElementRole.DataManagement_AssetModelInitializer);
		builder.setDescription("This component creates sample data when no existing asset data "
				+ "is detected in the datastore. If using the <strong>device model initializer</strong> "
				+ "this component should be used as well so that assets in the sample data can be resolved.");
		return builder.build();
	}

	/**
	 * Create element configuration for device model initializer.
	 * 
	 * @return
	 */
	protected ElementNode createDefaultScheduleModelInitializerElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Default Schedule Model Initializer",
						TenantDatastoreParser.Elements.DefaultScheduleModelInitializer.getLocalName(),
						"flash", ElementRole.DataManagement_ScheduleModelInitializer);
		builder.setDescription("This component creates sample data when no existing schedule data "
				+ "is detected in the datastore. It provides examples of both simple and cron-based "
				+ "schedules that are commonly used.");
		return builder.build();
	}

	/**
	 * Create the container for device communication information.
	 * 
	 * @return
	 */
	protected ElementNode createDeviceCommunication() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Device Communication",
						TenantConfigurationParser.Elements.DeviceCommunication.getLocalName(), "exchange",
						ElementRole.DeviceCommunication);
		builder.setDescription("Configure how information is received from devices, how data is queued "
				+ "for processing, and how commands are sent to devices.");
		builder.addElement(createEventSourcesElement());
		builder.addElement(createInboundProcessingStrategyElement());
		builder.addElement(createRegistrationElement());
		builder.addElement(createBatchOperationsElement());
		builder.addElement(createCommandRoutingElement());
		builder.addElement(createCommandDestinationsElement());
		return builder.build();
	}

	/**
	 * Create element configuration for event sources.
	 * 
	 * @return
	 */
	protected ElementNode createEventSourcesElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Event Sources",
						DeviceCommunicationParser.Elements.EventSources.getLocalName(), "sign-in",
						ElementRole.DeviceCommunication_EventSources);

		builder.setDescription("Event sources are responsible for bringing data into SiteWhere. They "
				+ "listen for incoming messages, convert them to a unified format, then forward them "
				+ "to the inbound processing strategy implementation to be processed.");
		builder.addElement(createMqttEventSourceElement());
		return builder.build();
	}

	/**
	 * Create element configuration for MQTT event source.
	 * 
	 * @return
	 */
	protected ElementNode createMqttEventSourceElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("MQTT Event Source",
						EventSourcesParser.Elements.MqttEventSource.getLocalName(), "sign-in",
						ElementRole.EventSources_EventSource);

		builder.setDescription("Listen for events on an MQTT topic.");
		builder.addAttribute((new AttributeNode.Builder("Source id", "sourceId", AttributeType.String).setDescription(
				"Unique id used for referencing this event source.").makeIndex().build()));

		// Add common MQTT connectivity attributes.
		addMqttConnectivityAttributes(builder);
		builder.addAttribute((new AttributeNode.Builder("MQTT topic", "topic", AttributeType.String).setDescription("MQTT topic event source uses for inbound messages.").build()));

		builder.addElement(createProtobufEventDecoderElement());
		builder.addElement(createJsonEventDecoderElement());
		return builder.build();
	}

	/**
	 * Adds common MQTT connectivity attributes.
	 * 
	 * @param builder
	 */
	protected void addMqttConnectivityAttributes(ElementNode.Builder builder) {
		builder.addAttribute((new AttributeNode.Builder("Transport protocol", "protocol",
				AttributeType.String).setDescription("Protocol used for establishing MQTT connection").setDefaultValue(
				"tcp").addChoice("tcp").addChoice("tls").build()));
		builder.addAttribute((new AttributeNode.Builder("MQTT broker hostname", "hostname",
				AttributeType.String).setDescription("Hostname used for creating the MQTT broker connection.").build()));
		builder.addAttribute((new AttributeNode.Builder("MQTT broker port", "port", AttributeType.Integer).setDescription("Port number used for creating the MQTT broker connection.").build()));
		builder.addAttribute((new AttributeNode.Builder("Trust store path", "trustStorePath",
				AttributeType.String).setDescription("Fully-qualified path to trust store for secured connections.").build()));
		builder.addAttribute((new AttributeNode.Builder("Trust store password", "trustStorePassword",
				AttributeType.String).setDescription("Password used to authenticate with trust store.").build()));
	}

	/**
	 * Create element configuration for protobuf event decoder.
	 * 
	 * @return
	 */
	protected ElementNode createProtobufEventDecoderElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Google Protocol Buffers Event Decoder",
						EventSourcesParser.BinaryDecoders.ProtobufDecoder.getLocalName(), "cogs",
						ElementRole.EventSource_BinaryEventDecoder);

		builder.setDescription("Event decoder that takes binary messages from an underlying transport "
				+ "and decodes them using the standard SiteWhere Google Protocol Buffers format. This is "
				+ "the default binary format used by the various SDKs.");
		return builder.build();
	}

	/**
	 * Create element configuration for JSON event decoder.
	 * 
	 * @return
	 */
	protected ElementNode createJsonEventDecoderElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("JSON Event Decoder",
						EventSourcesParser.BinaryDecoders.JsonDecoder.getLocalName(), "cogs",
						ElementRole.EventSource_BinaryEventDecoder);

		builder.setDescription("Event decoder that takes binary messages from an underlying transport "
				+ "and parses them as the JSON representation of a SiteWhere device event batch.");
		return builder.build();
	}

	/**
	 * Create element configuration for event sources.
	 * 
	 * @return
	 */
	protected ElementNode createInboundProcessingStrategyElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Inbound Processing Strategy",
						DeviceCommunicationParser.Elements.InboundProcessingStrategy.getLocalName(), "cogs",
						ElementRole.DeviceCommunication_InboundProcessingStrategy);

		builder.setDescription("The inbound processing strategy is responsible for moving events from event "
				+ "sources into the inbound processing chain. It is responsible for handling threading and "
				+ "reliably delivering events for processing.");
		builder.addElement(createBlockingQueueInboundStrategyElement());
		return builder.build();
	}

	/**
	 * Create element configuration for MQTT event source.
	 * 
	 * @return
	 */
	protected ElementNode createBlockingQueueInboundStrategyElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder(
						"Blocking Queue Strategy",
						InboundProcessingStrategyParser.Elements.DefaultInboundProcessingStrategy.getLocalName(),
						"cogs", ElementRole.InboundProcessingStrategy_Strategy);

		builder.setDescription("Send decoded messages into the processing pipeline by first adding them "
				+ "to a fixed-length queue, then using multiple threads to move events from the queue into "
				+ "the pipeline. The number of threads used very directly affects system performance since "
				+ "it determines how many events can be processed in parallel.");
		builder.addAttribute((new AttributeNode.Builder("Number of processing threads",
				"numEventProcessorThreads", AttributeType.Integer).setDescription(
				"Number of threads used to process incoming events in parallel").setDefaultValue("100").build()));
		builder.addAttribute((new AttributeNode.Builder("Enable monitoring", "enableMonitoring",
				AttributeType.Boolean).setDescription("Enable logging of monitoring statistics at an interval").build()));
		builder.addAttribute((new AttributeNode.Builder("Monitoring interval in seconds",
				"monitoringIntervalSec", AttributeType.Integer).setDescription("Number of seconds to wait between logging monitoring statistics.").build()));
		return builder.build();
	}

	/**
	 * Create element configuration for device registration.
	 * 
	 * @return
	 */
	protected ElementNode createRegistrationElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Device Registration Management",
						DeviceCommunicationParser.Elements.Registration.getLocalName(), "key",
						ElementRole.DeviceCommunication_Registration);

		builder.setDescription("Manages how new devices are registered with the system.");
		builder.addElement(createDefaultRegistrationManagerElement());
		return builder.build();
	}

	/**
	 * Create element configuration for default registration manager.
	 * 
	 * @return
	 */
	protected ElementNode createDefaultRegistrationManagerElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Registration Manager",
						RegistrationParser.Elements.DefaultRegistrationManager.getLocalName(), "key",
						ElementRole.Registration_RegistrationManager);

		builder.setDescription("Provides device registration management functionality.");
		builder.addAttribute((new AttributeNode.Builder("Allow registration of new devices",
				"allowNewDevices", AttributeType.Boolean).setDescription(
				"Indicates whether new devices should be allowed to register with the system").setDefaultValue(
				"true").build()));
		builder.addAttribute((new AttributeNode.Builder("Automatically assign site", "autoAssignSite",
				AttributeType.Boolean).setDescription("Indicates if a site should automatically be assigned if no site token is "
				+ "passed in registration request.").build()));
		builder.addAttribute((new AttributeNode.Builder("Site token", "autoAssignSiteToken",
				AttributeType.String).setDescription("Site token used for registering new devices if auto-assign is enabled "
				+ "and no site token is passed.").build()));
		return builder.build();
	}

	/**
	 * Create element configuration for batch operations.
	 * 
	 * @return
	 */
	protected ElementNode createBatchOperationsElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Batch Operation Management",
						DeviceCommunicationParser.Elements.BatchOperations.getLocalName(), "server",
						ElementRole.DeviceCommunication_BatchOperations);

		builder.setDescription("Manages how batch operations are processed. Batch operations are "
				+ "actions that are executed asynchronously for many devices with the ability to monitor "
				+ "progress at both the batch and element level.");
		builder.addElement(createBatchOperationManagerElement());
		return builder.build();
	}

	/**
	 * Create element configuration for batch operation manager.
	 * 
	 * @return
	 */
	protected ElementNode createBatchOperationManagerElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Batch Operation Manager",
						BatchOperationsParser.Elements.DefaultBatchOperationManager.getLocalName(), "server",
						ElementRole.BatchOperations_BatchOperationManager);

		builder.setDescription("Manages how batch operations are processed.");
		builder.addAttribute((new AttributeNode.Builder("Throttle delay (ms)", "throttleDelayMs",
				AttributeType.Integer).setDescription(
				"Number of milliseconds to wait between processing elements in a "
						+ "batch operation. This throttles the output to prevent overloading the system.").setDefaultValue(
				"0").build()));
		return builder.build();
	}

	/**
	 * Create element configuration for command routing.
	 * 
	 * @return
	 */
	protected ElementNode createCommandRoutingElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Device Command Routing",
						DeviceCommunicationParser.Elements.CommandRouting.getLocalName(),
						"sitemap fa-rotate-270", ElementRole.DeviceCommunication_CommandRouting);

		builder.setDescription("Determines how commands are routed to command destinations.");
		builder.addElement(createSpecificationMappingRouterElement());
		return builder.build();
	}

	/**
	 * Create element configuration for specification mapping command router.
	 * 
	 * @return
	 */
	protected ElementNode createSpecificationMappingRouterElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Specification Mapping Router",
						CommandRoutingParser.Elements.SpecificationMappingRouter.getLocalName(),
						"sitemap fa-rotate-270", ElementRole.CommandRouting_CommandRouter);

		builder.setDescription("Routes commands based on a direct mapping from device specification token "
				+ "to a command desitination. Commands for specifications not in the mapping list are routed to "
				+ "the default destination.");
		builder.addAttribute((new AttributeNode.Builder("Default destination", "defaultDestination",
				AttributeType.String).setDescription("Identifier for default destination commands should be routed to if no mapping is found.").build()));
		builder.addElement(createSpecificationMappingRouterMappingElement());
		return builder.build();
	}

	/**
	 * Create element configuration for specification mapping command router.
	 * 
	 * @return
	 */
	protected ElementNode createSpecificationMappingRouterMappingElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Specification Mapping", "mapping", "arrows-h",
						ElementRole.CommandRouting_SpecificationMappingRouter_Mapping);

		builder.setDescription("Maps a specification token to a command destination that should process it.");
		builder.addAttribute((new AttributeNode.Builder("Specification token", "specification",
				AttributeType.String).setDescription(
				"Unique token that identifies specification for the mapping.").makeIndex().build()));
		builder.addAttribute((new AttributeNode.Builder("Destination id", "destination", AttributeType.String).setDescription("Unique id of command destination for the mapping.").build()));
		return builder.build();
	}

	/**
	 * Create element configuration for command routing.
	 * 
	 * @return
	 */
	protected ElementNode createCommandDestinationsElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Device Command Destinations",
						DeviceCommunicationParser.Elements.CommandDestinations.getLocalName(), "sign-out",
						ElementRole.DeviceCommunication_CommandDestinations);

		builder.setDescription("Command destinations provide the information SiteWhere needs "
				+ "to route commands to devices. This includes information about how to encode the "
				+ "command and how to deliver the command via the underlying transport.");
		builder.addElement(createMqttCommandDestinationElement());
		return builder.build();
	}

	/**
	 * Add attributes common to all command destinations.
	 * 
	 * @param builder
	 */
	protected void addCommandDestinationAttributes(ElementNode.Builder builder) {
		builder.addAttribute((new AttributeNode.Builder("Destination id", "destinationId",
				AttributeType.String).setDescription("Unique identifier for command destination.").makeIndex().build()));
	}

	/**
	 * Create element configuration for MQTT command destination.
	 * 
	 * @return
	 */
	protected ElementNode createMqttCommandDestinationElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("MQTT Command Destination",
						CommandDestinationsParser.Elements.MqttCommandDestination.getLocalName(), "sign-out",
						ElementRole.CommandDestinations_CommandDestination);

		builder.setDescription("Sends commands to remote devices using the MQTT protocol. Commands are first encoded "
				+ "using a binary encoder, then a parameter extractor is used to determine the topic used "
				+ "to deliver the payload to the subscriber.");

		// Add common command destination attributes.
		addCommandDestinationAttributes(builder);

		// Add common MQTT connectivity attributes.
		addMqttConnectivityAttributes(builder);

		builder.addElement(createProtobufCommandEncoderElement());
		builder.addElement(createHardwareIdParameterExtractorElement());

		return builder.build();
	}

	/**
	 * Create element configuration for GPB command encoder.
	 * 
	 * @return
	 */
	protected ElementNode createProtobufCommandEncoderElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Google Protocol Buffers Command Encoder",
						CommandDestinationsParser.BinaryCommandEncoders.ProtobufEncoder.getLocalName(),
						"cogs", ElementRole.CommandDestinations_BinaryCommandEncoder);

		builder.setDescription("Encodes a command using the default Google Protocol Buffers representation. "
				+ "The proto file for the representation can be found in the <strong>code generation</strong> "
				+ "page for the device specification.");

		return builder.build();
	}

	/**
	 * Create element configuration for hardware id parameter extractor.
	 * 
	 * @return
	 */
	protected ElementNode createHardwareIdParameterExtractorElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Hardware Id Topic Extractor", "hardware-id-topic-extractor", "cogs",
						ElementRole.CommandDestinations_ParameterExtractor);

		builder.setDescription("Calculates MQTT topic for publishing commands by substituting the device "
				+ "hardware id into parameterized strings. The resulting values are used by the command "
				+ "destination to send the encoded command payload to the device.");
		builder.addAttribute((new AttributeNode.Builder("Command topic expression", "commandTopicExpr",
				AttributeType.String).setDescription("Expression for building topic name to which custom commands are sent. "
				+ "Add a '%s' where the hardware id should be inserted.").build()));
		builder.addAttribute((new AttributeNode.Builder("System topic expression", "systemTopicExpr",
				AttributeType.String).setDescription("Expression for building topic name to which system commands are sent. "
				+ "Add a '%s' where the hardware id should be inserted.").build()));

		return builder.build();
	}

	/**
	 * Create the container for inbound processing chain configuration.
	 * 
	 * @return
	 */
	protected ElementNode createInboundProcessingChain() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Inbound Processors",
						TenantConfigurationParser.Elements.InboundProcessingChain.getLocalName(), "sign-in",
						ElementRole.InboundProcessingChain);
		builder.setDescription("Configure a chain of processing steps that are applied to inbound data.");

		builder.addElement(createEventStorageProcessorElement());
		builder.addElement(createRegistrationProcessorElement());
		builder.addElement(createDeviceStreamProcessorElement());

		return builder.build();
	}

	/**
	 * Create element configuration event storage processor.
	 * 
	 * @return
	 */
	protected ElementNode createEventStorageProcessorElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Event Storage Processor",
						InboundProcessingChainParser.Elements.EventStorageProcessor.getLocalName(),
						"database", ElementRole.InboundProcessingChain_EventProcessor);

		builder.setDescription("Persists incoming events into the datastore. If this processor is removed, "
				+ "events will not be stored and outbound processing will not be triggered for the events.");
		builder.warnOnDelete("Deleting this component will prevent events from being persisted!");

		return builder.build();
	}

	/**
	 * Create element configuration for registration processor.
	 * 
	 * @return
	 */
	protected ElementNode createRegistrationProcessorElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Registration Processor",
						InboundProcessingChainParser.Elements.RegistrationProcessor.getLocalName(), "key",
						ElementRole.InboundProcessingChain_EventProcessor);

		builder.setDescription("Passes registration events to the registration manager. "
				+ "If this processor is removed, device registration events will be ignored.");
		builder.warnOnDelete("Deleting this component will cause registration events to be ignored!");

		return builder.build();
	}

	/**
	 * Create element configuration for device stream.
	 * 
	 * @return
	 */
	protected ElementNode createDeviceStreamProcessorElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Device Stream Processor",
						InboundProcessingChainParser.Elements.DeviceStreamProcessor.getLocalName(),
						"exchange", ElementRole.InboundProcessingChain_EventProcessor);

		builder.setDescription("Passes device stream events to the device stream manager. "
				+ "If this processor is removed, device streaming events will be ignored.");
		builder.warnOnDelete("Deleting this component will cause device stream events to be ignored!");

		return builder.build();
	}

	/**
	 * Create the container for outbound processing chain configuration.
	 * 
	 * @return
	 */
	protected ElementNode createOutboundProcessingChain() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Outbound Processors",
						TenantConfigurationParser.Elements.OutboundProcessingChain.getLocalName(),
						"sign-out", ElementRole.OutboundProcessingChain);
		builder.setDescription("Configure a chain of processing steps that are applied to outbound data.");
		return builder.build();
	}

	/**
	 * Create the container for asset management configuration.
	 * 
	 * @return
	 */
	protected ElementNode createAssetManagement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Asset Management",
						TenantConfigurationParser.Elements.AssetManagement.getLocalName(), "tag",
						ElementRole.AssetManagment);
		builder.setDescription("Configure asset management features.");
		return builder.build();
	}
}