/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.configuration;

import com.sitewhere.spring.handler.OutboundProcessingChainParser;
import com.sitewhere.spring.handler.TenantConfigurationParser;
import com.sitewhere.web.configuration.model.AttributeNode;
import com.sitewhere.web.configuration.model.AttributeType;
import com.sitewhere.web.configuration.model.ConfigurationModel;
import com.sitewhere.web.configuration.model.ElementNode;
import com.sitewhere.web.configuration.model.ElementRole;

/**
 * Configuration model for outbound processing chain elements.
 * 
 * @author Derek
 */
public class OutboundProcessingChainModel extends ConfigurationModel {

	public OutboundProcessingChainModel() {
		addElement(createOutboundProcessingChain());
		addElement(createCommandDeliveryEventProcessorElement());
		addElement(createHazelcastEventProcessorElement());
		addElement(createSolrEventProcessorElement());
		addElement(createAzureEventHubEventProcessorElement());
		addElement(createInitialStateEventProcessorElement());
		addElement(createDweetEventProcessorElement());

		addElement(createZoneTestElement());
		addElement(createZoneTestEventProcessorElement());

		addElement(createGroovyRouteBuilderElement());
		addElement(createMqttEventProcessorElement());

		// Outbound processor filters.
		addElement(createFilterCriteriaElement());
		addElement(createSiteFilterElement());
		addElement(createSpecificationFilterElement());
		addElement(createGroovyFilterElement());
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
		builder.description("Configure a chain of processing steps that are applied to outbound data.");
		return builder.build();
	}

	/**
	 * Create a command delivery event processor.
	 * 
	 * @return
	 */
	protected ElementNode createCommandDeliveryEventProcessorElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Command Delivery Processor",
						OutboundProcessingChainParser.Elements.CommandDeliveryEventProcessor.getLocalName(),
						"sign-out", ElementRole.OutboundProcessingChain_FilteredEventProcessor);
		builder.description("Hands off outbound device command events to the device communication subsystem. "
				+ "If this event processor is not configured, no commands will be sent to devices.");
		builder.warnOnDelete("Deleting this component will prevent commands from being sent!");
		builder.attribute((new AttributeNode.Builder("Number of processing threads", "numThreads",
				AttributeType.Integer).description(
				"Sets the number of threads used to process provisioning commands. Increase for situations "
						+ "where the load of device commands is high.").defaultValue("5").build()));
		return builder.build();
	}

	/**
	 * Create a zone test event processor.
	 * 
	 * @return
	 */
	protected ElementNode createZoneTestElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Zone Test", "zone-test", "map-pin",
						ElementRole.OutboundProcessingChain_ZoneTest);
		builder.description("Describes zone test criteria and alert to be generated in case of a match.");
		builder.attribute((new AttributeNode.Builder("Zone token", "zoneToken", AttributeType.String).description("Unique token for zone locations are to be tested against.").build()));
		builder.attribute((new AttributeNode.Builder("Condition", "condition", AttributeType.String).description(
				"Condition under which alert should be generated.").choice("inside").choice("outside").build()));
		builder.attribute((new AttributeNode.Builder("Alert type", "alertType", AttributeType.String).description("Identifier that indicates alert type.").build()));
		builder.attribute((new AttributeNode.Builder("Alert level", "alertLevel", AttributeType.String).description(
				"Level value of alert.").choice("info").choice("warning").choice("error").choice("critical").build()));
		builder.attribute((new AttributeNode.Builder("Alert message", "alertMessage", AttributeType.String).description("Message shown for alert.").build()));
		return builder.build();
	}

	/**
	 * Create a zone test event processor.
	 * 
	 * @return
	 */
	protected ElementNode createZoneTestEventProcessorElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Zone Test Processor",
						OutboundProcessingChainParser.Elements.ZoneTestEventProcessor.getLocalName(),
						"map-pin", ElementRole.OutboundProcessingChain_ZoneTestEventProcessor);
		builder.description("Allows alerts to be generated if location events are inside "
				+ "or outside of a zone based on criteria.");
		return builder.build();
	}

	/**
	 * Create a Groovy route builder.
	 * 
	 * @return
	 */
	protected ElementNode createGroovyRouteBuilderElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Groovy Route Builder",
						OutboundProcessingChainParser.RouteBuilders.GroovyRouteBuilder.getLocalName(),
						"sign-out", ElementRole.OutboundProcessingChain_RouteBuilder);
		builder.description("Route builder which executes a Groovy script to choose routes where events will be delivered.");
		builder.attribute((new AttributeNode.Builder("Script path", "scriptPath", AttributeType.String).description("Relative path to Groovy script.").build()));
		return builder.build();
	}

	/**
	 * Create an MQTT event processor.
	 * 
	 * @return
	 */
	protected ElementNode createMqttEventProcessorElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("MQTT Event Processor",
						OutboundProcessingChainParser.Elements.MqttEventProcessor.getLocalName(), "sign-out",
						ElementRole.OutboundProcessingChain_MqttEventProcessor);
		builder.description("Allows events to be forwarded to any number of MQTT topics based on configuration "
				+ "of filters and (optionally) a route builder. If no route builder is specified, the MQTT topic "
				+ "field determines where events are delivered.");
		DeviceCommunicationModel.addMqttConnectivityAttributes(builder);
		builder.attribute((new AttributeNode.Builder("MQTT topic", "topic", AttributeType.String).description("MQTT topic used if no route builder is specified.").build()));
		return builder.build();
	}

	/**
	 * Create a Hazelcast event processor.
	 * 
	 * @return
	 */
	protected ElementNode createHazelcastEventProcessorElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Hazelcast Processor",
						OutboundProcessingChainParser.Elements.HazelcastEventProcessor.getLocalName(),
						"sign-out", ElementRole.OutboundProcessingChain_FilteredEventProcessor);
		builder.description("Forwards outbound events to Hazelcast topics for processing by external consumers.");
		return builder.build();
	}

	/**
	 * Create a Solr event processor.
	 * 
	 * @return
	 */
	protected ElementNode createSolrEventProcessorElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Apache Solr Processor",
						OutboundProcessingChainParser.Elements.SolrEventProcessor.getLocalName(), "sign-out",
						ElementRole.OutboundProcessingChain_FilteredEventProcessor);
		builder.description("Forwards outbound events to Apache Solr for indexing in the search engine. This "
				+ "event processor relies on the global Solr properties to determine the Solr instance the "
				+ "client will connect with.");
		return builder.build();
	}

	/**
	 * Create a Azure event hub event processor.
	 * 
	 * @return
	 */
	protected ElementNode createAzureEventHubEventProcessorElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Azure EventHub Processor",
						OutboundProcessingChainParser.Elements.AzureEventHubEventProcessor.getLocalName(),
						"cloud", ElementRole.OutboundProcessingChain_FilteredEventProcessor);
		builder.description("Forwards outbound events to a Microsoft Azure EventHub for further processing.");
		builder.attribute((new AttributeNode.Builder("SAS Name", "sasName", AttributeType.String).description(
				"Sets the identity used for SAS authentication.").makeRequired().build()));
		builder.attribute((new AttributeNode.Builder("SAS Key", "sasKey", AttributeType.String).description(
				"Sets the key used for SAS authentication.").makeRequired().build()));
		builder.attribute((new AttributeNode.Builder("Service bus name", "serviceBusName",
				AttributeType.String).description(
				"Set the service bus to connect to (e.g. xxx.servicebus.windows.net).").makeRequired().build()));
		builder.attribute((new AttributeNode.Builder("Event hub name", "eventHubName", AttributeType.String).description(
				"Name of EventHub to connect to.").makeRequired().build()));
		return builder.build();
	}

	/**
	 * Create a InitialState event processor.
	 * 
	 * @return
	 */
	protected ElementNode createInitialStateEventProcessorElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("InitialState Processor",
						OutboundProcessingChainParser.Elements.InitialStateEventProcessor.getLocalName(),
						"cloud", ElementRole.OutboundProcessingChain_FilteredEventProcessor);
		builder.description("Forwards outbound events to InitialState.com for advanced visualization.");
		builder.attribute((new AttributeNode.Builder("Streaming access key", "streamingAccessKey",
				AttributeType.String).description(
				"Access key obtained from the InitialState.com website that specifies the account that the events will be associated with.").makeRequired().build()));
		return builder.build();
	}

	/**
	 * Create a Dweet.io event processor.
	 * 
	 * @return
	 */
	protected ElementNode createDweetEventProcessorElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Dweet.io Processor",
						OutboundProcessingChainParser.Elements.DweetIoEventProcessor.getLocalName(), "cloud",
						ElementRole.OutboundProcessingChain_FilteredEventProcessor);
		builder.description("Sends events to the Dweet.io cloud service where they can be viewed and integrated with other services. "
				+ "The unique 'thing' name will be the unique token for the device assignment the event is associated with.");
		return builder.build();
	}

	/**
	 * Create filter criteria element.
	 * 
	 * @return
	 */
	protected ElementNode createFilterCriteriaElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Filter Criteria", "filters", "filter",
						ElementRole.OutboundProcessingChain_Filters);
		builder.description("Adds filter criteria to control which events are sent to processor. "
				+ "Each filter is applied in the order below. Any events that have not been filtered "
				+ "will be passed to the outbound processor implementation.");
		return builder.build();
	}

	/**
	 * Create outbound processor site filter.
	 * 
	 * @return
	 */
	protected ElementNode createSiteFilterElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Site Filter",
						OutboundProcessingChainParser.Filters.SiteFilter.getLocalName(), "filter",
						ElementRole.OutboundProcessingChain_OutboundFilters);
		builder.description("Allows events from a given site to be included or excluded for an outbound processor.");
		builder.attribute((new AttributeNode.Builder("Site", "site", AttributeType.SiteReference).description(
				"Site filter applies to.").makeIndex().build()));
		builder.attribute((new AttributeNode.Builder("Include/Exclude", "operation", AttributeType.String).description(
				"Indicates whether events from the site should be included or excluded from processing.").choice(
				"include").choice("exclude").defaultValue("include").build()));
		return builder.build();
	}

	/**
	 * Create outbound processor specification filter.
	 * 
	 * @return
	 */
	protected ElementNode createSpecificationFilterElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Specification Filter",
						OutboundProcessingChainParser.Filters.SpecificationFilter.getLocalName(), "filter",
						ElementRole.OutboundProcessingChain_OutboundFilters);
		builder.description("Allows events for devices using a given specification to be included or "
				+ "excluded for an outbound processor.");
		builder.attribute((new AttributeNode.Builder("Specification", "specification",
				AttributeType.SpecificationReference).description("Specification filter applies to.").makeIndex().build()));
		builder.attribute((new AttributeNode.Builder("Include/Exclude", "operation", AttributeType.String).description(
				"Indicates whether events from the specification should be included or excluded from processing.").choice(
				"include").choice("exclude").defaultValue("include").build()));
		return builder.build();
	}

	/**
	 * Create outbound processor Groovy filter.
	 * 
	 * @return
	 */
	protected ElementNode createGroovyFilterElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Groovy Filter",
						OutboundProcessingChainParser.Filters.GroovyFilter.getLocalName(), "filter",
						ElementRole.OutboundProcessingChain_OutboundFilters);
		builder.description("Allows events to be filtered based on the return value of a Groovy script. "
				+ "If the script returns false, the event is filtered. See the SiteWhere documentation for "
				+ "a description of the variable bindings provided by the system.");
		builder.attribute((new AttributeNode.Builder("Script path", "scriptPath", AttributeType.String).description(
				"Script path relative to Groovy script root.").makeRequired().build()));
		return builder.build();
	}
}