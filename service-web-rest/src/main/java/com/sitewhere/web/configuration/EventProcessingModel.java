/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.configuration;

import com.sitewhere.spring.handler.IEventProcessingParser;
import com.sitewhere.spring.handler.IInboundProcessingChainParser;
import com.sitewhere.spring.handler.IInboundProcessingStrategyParser;
import com.sitewhere.spring.handler.IOutboundProcessingStrategyParser;
import com.sitewhere.spring.handler.ITenantConfigurationParser;
import com.sitewhere.spring.parser.IOutboundProcessingParser;
import com.sitewhere.web.configuration.model.AttributeNode;
import com.sitewhere.web.configuration.model.AttributeType;
import com.sitewhere.web.configuration.model.ConfigurationModel;
import com.sitewhere.web.configuration.model.ElementNode;
import com.sitewhere.web.configuration.model.ElementRole;

/**
 * Configuration model for event processing elements.
 * 
 * @author Derek
 */
public class EventProcessingModel extends ConfigurationModel {

    public EventProcessingModel() {
	addElement(createEventProcessing());

	// Inbound processing chain.
	addElement(createInboundProcessingChain());
	addElement(createInboundProcessorElement());
	addElement(createEventStorageProcessorElement());
	addElement(createRegistrationProcessorElement());
	addElement(createDeviceStreamProcessorElement());
	addElement(createHazelcastQueueElement());

	// Outbound processing chain.
	addElement(createOutboundProcessingChain());
	addElement(createOutboundProcessorElement());
	addElement(createCommandDeliveryEventProcessorElement());
	addElement(createHazelcastEventProcessorElement());
	addElement(createSolrEventProcessorElement());
	addElement(createAzureEventHubEventProcessorElement());
	addElement(createAmazonSqsEventProcessorElement());
	addElement(createInitialStateEventProcessorElement());
	addElement(createDweetEventProcessorElement());
	addElement(createGroovyEventProcessorElement());

	// Zone test elements.
	addElement(createZoneTestElement());
	addElement(createZoneTestEventProcessorElement());

	// MQTT processor elements.
	addElement(createGroovyRouteBuilderElement());
	addElement(createMqttEventProcessorElement());
	addElement(createRabbitMqEventProcessorElement());

	// Outbound processor filters.
	addElement(createFilterCriteriaElement());
	addElement(createSiteFilterElement());
	addElement(createSpecificationFilterElement());
	addElement(createGroovyFilterElement());

	// Inbound processing strategy.
	addElement(createInboundProcessingStrategyElement());
	addElement(createDefaultInboundStrategyElement());
	addElement(createBlockingQueueInboundStrategyElement());

	// Outbound processing strategy.
	addElement(createOutboundProcessingStrategyElement());
	addElement(createDefaultOutboundStrategyElement());
	addElement(createBlockingQueueOutboundStrategyElement());
    }

    /**
     * Create the container for event processing.
     * 
     * @return
     */
    protected ElementNode createEventProcessing() {
	ElementNode.Builder builder = new ElementNode.Builder("Event Processing",
		ITenantConfigurationParser.Elements.EventProcessing.getLocalName(), "cogs",
		ElementRole.EventProcessing);
	builder.description("Configure how events are processed internally in the system. This includes "
		+ "strategies for things like queueing and threading as well as pluggable chains of "
		+ "event processors for adding new behaviors to the system.");
	return builder.build();
    }

    /**
     * Create the container for inbound processing chain configuration.
     * 
     * @return
     */
    protected ElementNode createInboundProcessingChain() {
	ElementNode.Builder builder = new ElementNode.Builder("Inbound Processors",
		IEventProcessingParser.Elements.InboundProcessingChain.getLocalName(), "sign-in",
		ElementRole.InboundProcessingChain);
	builder.description("Configure a chain of processing steps that are applied to inbound data.");

	return builder.build();
    }

    /**
     * Create a generic inbound event processor reference.
     * 
     * @return
     */
    protected ElementNode createInboundProcessorElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Inbound Processor Bean Reference",
		IInboundProcessingChainParser.Elements.InboundEventProcessor.getLocalName(), "sign-in",
		ElementRole.InboundProcessingChain_EventProcessor);
	builder.description("Configures an inbound event processor that is declared in an external Spring bean.");
	builder.attribute((new AttributeNode.Builder("Bean reference name", "ref", AttributeType.String).description(
		"Name of Spring bean that will be referenced as an inbound event processor. The bean should implement the expected SiteWhere inbound event processor APIs")
		.build()));
	return builder.build();
    }

    /**
     * Create element configuration event storage processor.
     * 
     * @return
     */
    protected ElementNode createEventStorageProcessorElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Event Storage Processor",
		IInboundProcessingChainParser.Elements.EventStorageProcessor.getLocalName(), "database",
		ElementRole.InboundProcessingChain_EventProcessor);

	builder.description("Persists incoming events into the datastore. If this processor is removed, "
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
	ElementNode.Builder builder = new ElementNode.Builder("Registration Processor",
		IInboundProcessingChainParser.Elements.RegistrationProcessor.getLocalName(), "key",
		ElementRole.InboundProcessingChain_EventProcessor);

	builder.description("Passes registration events to the registration manager. "
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
	ElementNode.Builder builder = new ElementNode.Builder("Device Stream Processor",
		IInboundProcessingChainParser.Elements.DeviceStreamProcessor.getLocalName(), "exchange",
		ElementRole.InboundProcessingChain_EventProcessor);

	builder.description("Passes device stream events to the device stream manager. "
		+ "If this processor is removed, device streaming events will be ignored.");
	builder.warnOnDelete("Deleting this component will cause device stream events to be ignored!");

	return builder.build();
    }

    /**
     * Create element configuration for Hazelcast queue processor.
     * 
     * @return
     */
    protected ElementNode createHazelcastQueueElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Hazelcast Queue Processor",
		IInboundProcessingChainParser.Elements.HazelcastQueueProcessor.getLocalName(), "long-arrow-right",
		ElementRole.InboundProcessingChain_EventProcessor);

	builder.description("Forwards device events to a Hazelcast queue. This processor is often "
		+ "configured to allow events to be processed by other SiteWhere instances in the "
		+ "same Hazelcast group. By adding this processor and removing all others, this "
		+ "instance will load-balance event processing between subordinate instances.");

	return builder.build();
    }

    /**
     * Create the container for outbound processing chain configuration.
     * 
     * @return
     */
    protected ElementNode createOutboundProcessingChain() {
	ElementNode.Builder builder = new ElementNode.Builder("Outbound Processors",
		IEventProcessingParser.Elements.OutboundProcessingChain.getLocalName(), "sign-out",
		ElementRole.OutboundProcessingChain);
	builder.description("Configure a chain of processing steps that are applied to outbound data.");
	return builder.build();
    }

    /**
     * Create a generic outbound event processor reference.
     * 
     * @return
     */
    protected ElementNode createOutboundProcessorElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Outbound Processor Bean Reference",
		IOutboundProcessingParser.Elements.OutboundEventProcessor.getLocalName(), "sign-out",
		ElementRole.OutboundProcessingChain_EventProcessor);
	builder.description("Configures an outbound event processor that is declared in an external Spring bean.");
	builder.attribute((new AttributeNode.Builder("Bean reference name", "ref", AttributeType.String).description(
		"Name of Spring bean that will be referenced as an outbound event processor. The bean should implement the expected SiteWhere outbound event processor APIs")
		.build()));
	return builder.build();
    }

    /**
     * Create a command delivery event processor.
     * 
     * @return
     */
    protected ElementNode createCommandDeliveryEventProcessorElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Command Delivery Processor",
		IOutboundProcessingParser.Elements.CommandDeliveryEventProcessor.getLocalName(), "bolt",
		ElementRole.OutboundProcessingChain_FilteredEventProcessor);
	builder.description("Hands off outbound device command events to the device communication subsystem. "
		+ "If this event processor is not configured, no commands will be sent to devices.");
	builder.warnOnDelete("Deleting this component will prevent commands from being sent!");
	builder.attribute((new AttributeNode.Builder("Number of processing threads", "numThreads",
		AttributeType.Integer).description(
			"Sets the number of threads used to process provisioning commands. Increase for situations "
				+ "where the load of device commands is high.")
			.defaultValue("5").build()));
	return builder.build();
    }

    /**
     * Create a zone test event processor.
     * 
     * @return
     */
    protected ElementNode createZoneTestElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Zone Test", "zone-test", "map-pin",
		ElementRole.OutboundProcessingChain_ZoneTest);
	builder.description("Describes zone test criteria and alert to be generated in case of a match.");
	builder.attribute((new AttributeNode.Builder("Zone token", "zoneToken", AttributeType.String)
		.description("Unique token for zone locations are to be tested against.").build()));
	builder.attribute((new AttributeNode.Builder("Condition", "condition", AttributeType.String)
		.description("Condition under which alert should be generated.").choice("inside").choice("outside")
		.build()));
	builder.attribute((new AttributeNode.Builder("Alert type", "alertType", AttributeType.String)
		.description("Identifier that indicates alert type.").build()));
	builder.attribute((new AttributeNode.Builder("Alert level", "alertLevel", AttributeType.String)
		.description("Level value of alert.").choice("info").choice("warning").choice("error")
		.choice("critical").build()));
	builder.attribute((new AttributeNode.Builder("Alert message", "alertMessage", AttributeType.String)
		.description("Message shown for alert.").build()));
	return builder.build();
    }

    /**
     * Create a zone test event processor.
     * 
     * @return
     */
    protected ElementNode createZoneTestEventProcessorElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Zone Test Processor",
		IOutboundProcessingParser.Elements.ZoneTestEventProcessor.getLocalName(), "map-pin",
		ElementRole.OutboundProcessingChain_ZoneTestEventProcessor);
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
	ElementNode.Builder builder = new ElementNode.Builder("Groovy Route Builder",
		IOutboundProcessingParser.RouteBuilders.GroovyRouteBuilder.getLocalName(), "sign-out",
		ElementRole.OutboundProcessingChain_RouteBuilder);
	builder.description(
		"Route builder which executes a Groovy script to choose routes where events will be delivered.");
	builder.attribute((new AttributeNode.Builder("Script path", "scriptPath", AttributeType.String)
		.description("Relative path to Groovy script.").build()));
	return builder.build();
    }

    /**
     * Create an MQTT event processor.
     * 
     * @return
     */
    protected ElementNode createMqttEventProcessorElement() {
	ElementNode.Builder builder = new ElementNode.Builder("MQTT Event Processor",
		IOutboundProcessingParser.Elements.MqttEventProcessor.getLocalName(), "sign-out",
		ElementRole.OutboundProcessingChain_MqttEventProcessor);
	builder.description("Allows events to be forwarded to any number of MQTT topics based on configuration "
		+ "of filters and (optionally) a route builder. If no route builder is specified, the MQTT topic "
		+ "field determines where events are delivered.");
	DeviceCommunicationModel.addMqttConnectivityAttributes(builder);
	builder.attribute((new AttributeNode.Builder("MQTT topic", "topic", AttributeType.String)
		.description("MQTT topic used if no route builder is specified.").build()));
	return builder.build();
    }

    /**
     * Create a RabbitMQ event processor.
     * 
     * @return
     */
    protected ElementNode createRabbitMqEventProcessorElement() {
	ElementNode.Builder builder = new ElementNode.Builder("RabbitMQ Event Processor",
		IOutboundProcessingParser.Elements.RabbitMqEventProcessor.getLocalName(), "sign-out",
		ElementRole.OutboundProcessingChain_RabbitMqEventProcessor);
	builder.description("Allows events to be forwarded to any number of RabbitMQ exchanges based on configuration "
		+ "of filters and (optionally) a route builder. If no route builder is specified, the exchange "
		+ "field determines where events are delivered.");
	builder.attribute((new AttributeNode.Builder("Connection URI", "connectionUri", AttributeType.String)
		.defaultValue("amqp://localhost")
		.description("URI that provides information about the RabbitMQ instance to connect to.").build()));
	builder.attribute((new AttributeNode.Builder("Topic", "topic", AttributeType.String)
		.defaultValue("sitewhere.output").description("Topic used if no route builder is specified.").build()));
	return builder.build();
    }

    /**
     * Create a Hazelcast event processor.
     * 
     * @return
     */
    protected ElementNode createHazelcastEventProcessorElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Hazelcast Processor",
		IOutboundProcessingParser.Elements.HazelcastEventProcessor.getLocalName(), "sign-out",
		ElementRole.OutboundProcessingChain_FilteredEventProcessor);
	builder.description("Forwards outbound events to Hazelcast topics for processing by external consumers.");
	return builder.build();
    }

    /**
     * Create a Solr event processor.
     * 
     * @return
     */
    protected ElementNode createSolrEventProcessorElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Apache Solr Processor",
		IOutboundProcessingParser.Elements.SolrEventProcessor.getLocalName(), "sign-out",
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
	ElementNode.Builder builder = new ElementNode.Builder("Azure EventHub Processor",
		IOutboundProcessingParser.Elements.AzureEventHubEventProcessor.getLocalName(), "cloud",
		ElementRole.OutboundProcessingChain_FilteredEventProcessor);
	builder.description("Forwards outbound events to a Microsoft Azure EventHub for further processing.");
	builder.attribute((new AttributeNode.Builder("SAS Name", "sasName", AttributeType.String)
		.description("Sets the identity used for SAS authentication.").makeRequired().build()));
	builder.attribute((new AttributeNode.Builder("SAS Key", "sasKey", AttributeType.String)
		.description("Sets the key used for SAS authentication.").makeRequired().build()));
	builder.attribute((new AttributeNode.Builder("Service bus name", "serviceBusName", AttributeType.String)
		.description("Set the service bus to connect to (e.g. xxx.servicebus.windows.net).").makeRequired()
		.build()));
	builder.attribute((new AttributeNode.Builder("Event hub name", "eventHubName", AttributeType.String)
		.description("Name of EventHub to connect to.").makeRequired().build()));
	return builder.build();
    }

    /**
     * Create an Amazon SQS queue event processor.
     * 
     * @return
     */
    protected ElementNode createAmazonSqsEventProcessorElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Amazon SQS Processor",
		IOutboundProcessingParser.Elements.AmazonSqsEventProcessor.getLocalName(), "cloud",
		ElementRole.OutboundProcessingChain_FilteredEventProcessor);
	builder.description("Forwards outbound events to an Amazon SQS queue for further processing.");
	builder.attribute((new AttributeNode.Builder("Access key", "accessKey", AttributeType.String)
		.description("Amazon AWS access key for account owning SQS queue.").makeRequired().build()));
	builder.attribute((new AttributeNode.Builder("Secret key", "secretKey", AttributeType.String)
		.description("Amazon AWS secret key for account owning SQS queue.").makeRequired().build()));
	builder.attribute((new AttributeNode.Builder("SQS queue URL", "queueUrl", AttributeType.String)
		.description("Unique URL for SQS queue.").makeRequired().build()));
	return builder.build();
    }

    /**
     * Create a InitialState event processor.
     * 
     * @return
     */
    protected ElementNode createInitialStateEventProcessorElement() {
	ElementNode.Builder builder = new ElementNode.Builder("InitialState Processor",
		IOutboundProcessingParser.Elements.InitialStateEventProcessor.getLocalName(), "cloud",
		ElementRole.OutboundProcessingChain_FilteredEventProcessor);
	builder.description("Forwards outbound events to InitialState.com for advanced visualization.");
	builder.attribute((new AttributeNode.Builder("Streaming access key", "streamingAccessKey", AttributeType.String)
		.description(
			"Access key obtained from the InitialState.com website that specifies the account that the events will be associated with.")
		.makeRequired().build()));
	return builder.build();
    }

    /**
     * Create a Dweet.io event processor.
     * 
     * @return
     */
    protected ElementNode createDweetEventProcessorElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Dweet.io Processor",
		IOutboundProcessingParser.Elements.DweetIoEventProcessor.getLocalName(), "cloud",
		ElementRole.OutboundProcessingChain_FilteredEventProcessor);
	builder.description(
		"Sends events to the Dweet.io cloud service where they can be viewed and integrated with other services. "
			+ "The unique 'thing' name will be the unique token for the device assignment the event is associated with.");
	return builder.build();
    }

    /**
     * Create a Groovy event processor.
     * 
     * @return
     */
    protected ElementNode createGroovyEventProcessorElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Groovy Processor",
		IOutboundProcessingParser.Elements.GroovyEventProcessor.getLocalName(), "cogs",
		ElementRole.OutboundProcessingChain_FilteredEventProcessor);
	builder.description("Delegates event processing to a Groovy script which can execute "
		+ "conditional logic, create new events, or carry out other tasks.");
	builder.attribute((new AttributeNode.Builder("Script path", "scriptPath", AttributeType.String)
		.description("Script path relative to Groovy script root.").makeRequired().build()));
	return builder.build();
    }

    /**
     * Create filter criteria element.
     * 
     * @return
     */
    protected ElementNode createFilterCriteriaElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Filter Criteria", "filters", "filter",
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
	ElementNode.Builder builder = new ElementNode.Builder("Site Filter",
		IOutboundProcessingParser.Filters.SiteFilter.getLocalName(), "filter",
		ElementRole.OutboundProcessingChain_OutboundFilters);
	builder.description("Allows events from a given site to be included or excluded for an outbound processor.");
	builder.attribute((new AttributeNode.Builder("Site", "site", AttributeType.SiteReference)
		.description("Site filter applies to.").makeIndex().build()));
	builder.attribute((new AttributeNode.Builder("Include/Exclude", "operation", AttributeType.String)
		.description("Indicates whether events from the site should be included or excluded from processing.")
		.choice("include").choice("exclude").defaultValue("include").build()));
	return builder.build();
    }

    /**
     * Create outbound processor specification filter.
     * 
     * @return
     */
    protected ElementNode createSpecificationFilterElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Specification Filter",
		IOutboundProcessingParser.Filters.SpecificationFilter.getLocalName(), "filter",
		ElementRole.OutboundProcessingChain_OutboundFilters);
	builder.description("Allows events for devices using a given specification to be included or "
		+ "excluded for an outbound processor.");
	builder.attribute(
		(new AttributeNode.Builder("Specification", "specification", AttributeType.SpecificationReference)
			.description("Specification filter applies to.").makeIndex().build()));
	builder.attribute((new AttributeNode.Builder("Include/Exclude", "operation", AttributeType.String).description(
		"Indicates whether events from the specification should be included or excluded from processing.")
		.choice("include").choice("exclude").defaultValue("include").build()));
	return builder.build();
    }

    /**
     * Create outbound processor Groovy filter.
     * 
     * @return
     */
    protected ElementNode createGroovyFilterElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Groovy Filter",
		IOutboundProcessingParser.Filters.GroovyFilter.getLocalName(), "filter",
		ElementRole.OutboundProcessingChain_OutboundFilters);
	builder.description("Allows events to be filtered based on the return value of a Groovy script. "
		+ "If the script returns false, the event is filtered. See the SiteWhere documentation for "
		+ "a description of the variable bindings provided by the system.");
	builder.attribute((new AttributeNode.Builder("Script path", "scriptPath", AttributeType.String)
		.description("Script path relative to Groovy script root.").makeRequired().build()));
	return builder.build();
    }

    /**
     * Create element configuration for event sources.
     * 
     * @return
     */
    protected ElementNode createInboundProcessingStrategyElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Inbound Processing Strategy",
		IEventProcessingParser.Elements.InboundProcessingStrategy.getLocalName(), "cogs",
		ElementRole.EventProcessing_InboundProcessingStrategy);

	builder.description("The inbound processing strategy is responsible for moving events from event "
		+ "sources into the inbound processing chain. It is responsible for handling threading and "
		+ "reliably delivering events for processing.");
	return builder.build();
    }

    /**
     * Create element configuration for default inbound processing strategy.
     * 
     * @return
     */
    protected ElementNode createDefaultInboundStrategyElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Blocking Queue Strategy",
		IInboundProcessingStrategyParser.Elements.DefaultInboundProcessingStrategy.getLocalName(), "cogs",
		ElementRole.InboundProcessingStrategy_Strategy);

	addBlockingQueueInboundStrategyFields(builder);
	return builder.build();
    }

    /**
     * Create element configuration for blocking queue inbound processing strategy.
     * 
     * @return
     */
    protected ElementNode createBlockingQueueInboundStrategyElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Blocking Queue Strategy",
		IInboundProcessingStrategyParser.Elements.BlockingQueueInboundProcessingStrategy.getLocalName(), "cogs",
		ElementRole.InboundProcessingStrategy_Strategy);

	addBlockingQueueInboundStrategyFields(builder);
	return builder.build();
    }

    /**
     * Add fields for blocking queue inbound processing strategy.
     * 
     * @param builder
     */
    protected void addBlockingQueueInboundStrategyFields(ElementNode.Builder builder) {
	builder.description("Send decoded messages into the processing pipeline by first adding them "
		+ "to a fixed-length queue, then using multiple threads to move events from the queue into "
		+ "the pipeline. The number of threads used very directly affects system performance since "
		+ "it determines how many events can be processed in parallel.");
	builder.attribute((new AttributeNode.Builder("Max queue size", "maxQueueSize", AttributeType.Integer)
		.description("Maximum number of events in queue before blocking occurs.").defaultValue("10000")
		.build()));
	builder.attribute((new AttributeNode.Builder("Number of processing threads", "numEventProcessorThreads",
		AttributeType.Integer).description("Number of threads used to process incoming events in parallel")
			.defaultValue("100").build()));
	builder.attribute((new AttributeNode.Builder("Enable monitoring", "enableMonitoring", AttributeType.Boolean)
		.description("Enable logging of monitoring statistics at an interval").build()));
	builder.attribute((new AttributeNode.Builder("Monitoring interval in seconds", "monitoringIntervalSec",
		AttributeType.Integer).description("Number of seconds to wait between logging monitoring statistics.")
			.build()));
    }

    /**
     * Create element configuration for outbound processing strategy.
     * 
     * @return
     */
    protected ElementNode createOutboundProcessingStrategyElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Outbound Processing Strategy",
		IEventProcessingParser.Elements.OutboundProcessingStrategy.getLocalName(), "cogs",
		ElementRole.EventProcessing_OutboundProcessingStrategy);

	builder.description("The outbound processing strategy is responsible for taking stored events and passing "
		+ "them into the outbound processing chain. It is responsible for handling threading and "
		+ "reliably delivering events for outbound processing.");
	return builder.build();
    }

    /**
     * Create element configuration for default outbound processing strategy.
     * 
     * @return
     */
    protected ElementNode createDefaultOutboundStrategyElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Blocking Queue Strategy",
		IOutboundProcessingStrategyParser.Elements.DefaultOutboundProcessingStrategy.getLocalName(), "cogs",
		ElementRole.OutboundProcessingStrategy_Strategy);

	addBlockingQueueOutboundStrategyFields(builder);
	return builder.build();
    }

    /**
     * Create element configuration for blocking queue outbound processing strategy.
     * 
     * @return
     */
    protected ElementNode createBlockingQueueOutboundStrategyElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Blocking Queue Strategy",
		IOutboundProcessingStrategyParser.Elements.BlockingQueueOutboundProcessingStrategy.getLocalName(),
		"cogs", ElementRole.OutboundProcessingStrategy_Strategy);

	addBlockingQueueOutboundStrategyFields(builder);
	return builder.build();
    }

    /**
     * Add fields for blocking queue outbound processing strategy.
     * 
     * @param builder
     */
    protected void addBlockingQueueOutboundStrategyFields(ElementNode.Builder builder) {
	builder.description("Sends stored messages into the outbound processing pipeline by first adding them "
		+ "to a fixed-length queue, then using multiple threads to move events from the queue into "
		+ "the outbound pipeline. The number of threads used very directly affects system performance since "
		+ "it determines how many events can be processed in parallel.");
	builder.attribute((new AttributeNode.Builder("Max queue size", "maxQueueSize", AttributeType.Integer)
		.description("Maximum number of events in queue before blocking occurs.").defaultValue("10000")
		.build()));
	builder.attribute((new AttributeNode.Builder("Number of processing threads", "numEventProcessorThreads",
		AttributeType.Integer).description("Number of threads used to process incoming events in parallel")
			.defaultValue("100").build()));
    }
}