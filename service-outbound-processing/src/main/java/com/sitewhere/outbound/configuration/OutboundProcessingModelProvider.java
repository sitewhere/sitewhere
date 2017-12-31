/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.outbound.configuration;

import com.sitewhere.configuration.CommonCommunicationModel;
import com.sitewhere.configuration.model.ConfigurationModelProvider;
import com.sitewhere.configuration.parser.IOutboundProcessingParser;
import com.sitewhere.rest.model.configuration.AttributeNode;
import com.sitewhere.rest.model.configuration.ElementNode;
import com.sitewhere.spi.microservice.configuration.model.AttributeType;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider;

/**
 * Configuration model provider for outbound processing microservice.
 * 
 * @author Derek
 */
public class OutboundProcessingModelProvider extends ConfigurationModelProvider {

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#getDefaultXmlNamespace()
     */
    @Override
    public String getDefaultXmlNamespace() {
	return "http://sitewhere.io/schema/sitewhere/microservice/outbound-processing";
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#getRootRole()
     */
    @Override
    public IConfigurationRoleProvider getRootRole() {
	return OutboundProcessingRoles.OutboundProcessing;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#initializeElements()
     */
    @Override
    public void initializeElements() {
	addElement(createOutboundProcessingElement());

	// Outbound processing chain.
	addElement(createOutboundProcessorElement());
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
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#initializeRoles()
     */
    @Override
    public void initializeRoles() {
	for (OutboundProcessingRoles role : OutboundProcessingRoles.values()) {
	    getRolesById().put(role.getRole().getKey().getId(), role.getRole());
	}
    }

    /**
     * Create outbound processing element.
     * 
     * @return
     */
    protected ElementNode createOutboundProcessingElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Outbound Processing", IOutboundProcessingParser.ROOT,
		"sign-out", OutboundProcessingRoleKeys.OutboundProcessing, this);

	builder.description("Handles outbound processing tasks such as forwarding events for external processing.");

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
		OutboundProcessingRoleKeys.OutboundEventProcessor, this);
	builder.description("Configures an outbound event processor that is declared in an external Spring bean.");
	builder.attribute((new AttributeNode.Builder("Bean reference name", "ref", AttributeType.String).description(
		"Name of Spring bean that will be referenced as an outbound event processor. The bean should implement the expected SiteWhere outbound event processor APIs")
		.build()));
	return builder.build();
    }

    /**
     * Create a zone test event processor.
     * 
     * @return
     */
    protected ElementNode createZoneTestElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Zone Test", "zone-test", "map-pin",
		OutboundProcessingRoleKeys.ZoneTestElement, this);
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
		OutboundProcessingRoleKeys.ZoneTestEventProcessor, this);
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
		OutboundProcessingRoleKeys.GroovyRouteBuilder, this);
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
		OutboundProcessingRoleKeys.MqttEventProcessor, this);
	builder.description("Allows events to be forwarded to any number of MQTT topics based on configuration "
		+ "of filters and (optionally) a route builder. If no route builder is specified, the MQTT topic "
		+ "field determines where events are delivered.");
	CommonCommunicationModel.addMqttConnectivityAttributes(builder);
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
		OutboundProcessingRoleKeys.RabbitMqEventProcessor, this);
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
		OutboundProcessingRoleKeys.FilteredEventProcessor, this);
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
		OutboundProcessingRoleKeys.FilteredEventProcessor, this);
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
		OutboundProcessingRoleKeys.FilteredEventProcessor, this);
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
		OutboundProcessingRoleKeys.FilteredEventProcessor, this);
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
		OutboundProcessingRoleKeys.FilteredEventProcessor, this);
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
		OutboundProcessingRoleKeys.FilteredEventProcessor, this);
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
		OutboundProcessingRoleKeys.FilteredEventProcessor, this);
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
		OutboundProcessingRoleKeys.Filters, this);
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
		OutboundProcessingRoleKeys.OutboundFilters, this);
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
		OutboundProcessingRoleKeys.OutboundFilters, this);
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
		OutboundProcessingRoleKeys.OutboundFilters, this);
	builder.description("Allows events to be filtered based on the return value of a Groovy script. "
		+ "If the script returns false, the event is filtered. See the SiteWhere documentation for "
		+ "a description of the variable bindings provided by the system.");
	builder.attribute((new AttributeNode.Builder("Script path", "scriptPath", AttributeType.String)
		.description("Script path relative to Groovy script root.").makeRequired().build()));
	return builder.build();
    }
}