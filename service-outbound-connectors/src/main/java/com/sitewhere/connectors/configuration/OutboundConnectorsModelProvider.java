/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.configuration;

import com.sitewhere.configuration.CommonConnectorModel;
import com.sitewhere.configuration.model.ConfigurationModelProvider;
import com.sitewhere.configuration.parser.IOutboundConnectorsParser;
import com.sitewhere.rest.model.configuration.AttributeNode;
import com.sitewhere.rest.model.configuration.ElementNode;
import com.sitewhere.spi.microservice.configuration.model.AttributeType;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider;

/**
 * Configuration model provider for outbound connectors microservice.
 * 
 * @author Derek
 */
public class OutboundConnectorsModelProvider extends ConfigurationModelProvider {

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#getDefaultXmlNamespace()
     */
    @Override
    public String getDefaultXmlNamespace() {
	return "http://sitewhere.io/schema/sitewhere/microservice/outbound-connectors";
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#getRootRole()
     */
    @Override
    public IConfigurationRoleProvider getRootRole() {
	return OutboundConnectorsRoles.OutboundConnectors;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#initializeElements()
     */
    @Override
    public void initializeElements() {
	addElement(createOutboundConnectorsElement());

	// Outbound connectors.
	addElement(createOutboundConnectorElement());
	addElement(createHazelcastConnectorElement());
	addElement(createSolrConnectorElement());
	addElement(createAzureEventHubConnectorElement());
	addElement(createAmazonSqsConnectorElement());
	addElement(createInitialStateConnectorElement());
	addElement(createDweetConnectorElement());
	addElement(createGroovyConnectorElement());

	// MQTT connector elements.
	addElement(createGroovyRouteBuilderElement());
	addElement(createMqttConnectorElement());
	addElement(createRabbitMqConnectorElement());

	// Outbound connector filters.
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
	for (OutboundConnectorsRoles role : OutboundConnectorsRoles.values()) {
	    getRolesById().put(role.getRole().getKey().getId(), role.getRole());
	}
    }

    /**
     * Create outbound processing element.
     * 
     * @return
     */
    protected ElementNode createOutboundConnectorsElement() {
	ElementNode.Builder builder = new ElementNode.Builder(
		OutboundConnectorsRoles.OutboundConnectors.getRole().getName(), IOutboundConnectorsParser.ROOT,
		"sign-out", OutboundConnectorsRoleKeys.OutboundConnectors, this);

	builder.description("Manages a list of connectors used to forward events to external systems.");

	return builder.build();
    }

    /**
     * Create a generic outbound event connector reference.
     * 
     * @return
     */
    protected ElementNode createOutboundConnectorElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Outbound Connector Bean Reference",
		IOutboundConnectorsParser.Elements.OutboundConnector.getLocalName(), "sign-out",
		OutboundConnectorsRoleKeys.OutboundConnector, this);
	builder.description("Configures an outbound connector that is declared in an external Spring bean.");

	addCommonConnectorAttributes(builder);
	builder.attribute((new AttributeNode.Builder("Bean reference name", "ref", AttributeType.String).description(
		"Name of Spring bean that will be referenced as an outbound connector. The bean should implement the expected SiteWhere outbound connector APIs")
		.build()));
	return builder.build();
    }

    /**
     * Create a Groovy route builder.
     * 
     * @return
     */
    protected ElementNode createGroovyRouteBuilderElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Groovy Route Builder",
		IOutboundConnectorsParser.RouteBuilders.GroovyRouteBuilder.getLocalName(), "sign-out",
		OutboundConnectorsRoleKeys.GroovyRouteBuilder, this);
	builder.description(
		"Route builder which executes a Groovy script to choose routes where events will be delivered.");

	addCommonConnectorAttributes(builder);
	builder.attribute((new AttributeNode.Builder("Script path", "scriptPath", AttributeType.String)
		.description("Relative path to Groovy script.").build()));
	return builder.build();
    }

    /**
     * Create an MQTT connector.
     * 
     * @return
     */
    protected ElementNode createMqttConnectorElement() {
	ElementNode.Builder builder = new ElementNode.Builder("MQTT Connector",
		IOutboundConnectorsParser.Elements.MqttConnector.getLocalName(), "sign-out",
		OutboundConnectorsRoleKeys.MqttConnector, this);
	builder.description("Allows events to be forwarded to any number of MQTT topics based on configuration "
		+ "of filters and (optionally) a route builder. If no route builder is specified, the MQTT topic "
		+ "field determines where events are delivered.");

	addCommonConnectorAttributes(builder);
	CommonConnectorModel.addMqttConnectivityAttributes(builder);
	builder.attribute((new AttributeNode.Builder("MQTT topic", "topic", AttributeType.String)
		.description("MQTT topic used if no route builder is specified.").build()));
	return builder.build();
    }

    /**
     * Create a RabbitMQ connector.
     * 
     * @return
     */
    protected ElementNode createRabbitMqConnectorElement() {
	ElementNode.Builder builder = new ElementNode.Builder("RabbitMQ Connector",
		IOutboundConnectorsParser.Elements.RabbitMqConnector.getLocalName(), "sign-out",
		OutboundConnectorsRoleKeys.RabbitMqConnector, this);
	builder.description("Allows events to be forwarded to any number of RabbitMQ exchanges based on configuration "
		+ "of filters and (optionally) a route builder. If no route builder is specified, the exchange "
		+ "field determines where events are delivered.");

	addCommonConnectorAttributes(builder);
	builder.attribute((new AttributeNode.Builder("Connection URI", "connectionUri", AttributeType.String)
		.defaultValue("amqp://localhost")
		.description("URI that provides information about the RabbitMQ instance to connect to.").build()));
	builder.attribute((new AttributeNode.Builder("Topic", "topic", AttributeType.String)
		.defaultValue("sitewhere.output").description("Topic used if no route builder is specified.").build()));
	return builder.build();
    }

    /**
     * Create a Hazelcast connector.
     * 
     * @return
     */
    protected ElementNode createHazelcastConnectorElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Hazelcast Connector",
		IOutboundConnectorsParser.Elements.HazelcastConnector.getLocalName(), "sign-out",
		OutboundConnectorsRoleKeys.FilteredConnector, this);
	builder.description("Forwards outbound events to Hazelcast topics for processing by external consumers.");

	addCommonConnectorAttributes(builder);
	return builder.build();
    }

    /**
     * Create a Solr connector.
     * 
     * @return
     */
    protected ElementNode createSolrConnectorElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Apache Solr Connector",
		IOutboundConnectorsParser.Elements.SolrConnector.getLocalName(), "sign-out",
		OutboundConnectorsRoleKeys.FilteredConnector, this);
	builder.description("Forwards outbound events to Apache Solr for indexing in the search engine. This "
		+ "event processor relies on the global Solr properties to determine the Solr instance the "
		+ "client will connect with.");

	addCommonConnectorAttributes(builder);
	return builder.build();
    }

    /**
     * Create a Azure event hub connector.
     * 
     * @return
     */
    protected ElementNode createAzureEventHubConnectorElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Azure EventHub Connector",
		IOutboundConnectorsParser.Elements.AzureEventHubConnector.getLocalName(), "cloud",
		OutboundConnectorsRoleKeys.FilteredConnector, this);
	builder.description("Forwards outbound events to a Microsoft Azure EventHub for further processing.");

	addCommonConnectorAttributes(builder);
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
     * Create an Amazon SQS queue connector.
     * 
     * @return
     */
    protected ElementNode createAmazonSqsConnectorElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Amazon SQS Connector",
		IOutboundConnectorsParser.Elements.AmazonSqsConnector.getLocalName(), "cloud",
		OutboundConnectorsRoleKeys.FilteredConnector, this);
	builder.description("Forwards outbound events to an Amazon SQS queue for further processing.");

	addCommonConnectorAttributes(builder);
	builder.attribute((new AttributeNode.Builder("Access key", "accessKey", AttributeType.String)
		.description("Amazon AWS access key for account owning SQS queue.").makeRequired().build()));
	builder.attribute((new AttributeNode.Builder("Secret key", "secretKey", AttributeType.String)
		.description("Amazon AWS secret key for account owning SQS queue.").makeRequired().build()));
	builder.attribute((new AttributeNode.Builder("SQS queue URL", "queueUrl", AttributeType.String)
		.description("Unique URL for SQS queue.").makeRequired().build()));
	return builder.build();
    }

    /**
     * Create a InitialState connector.
     * 
     * @return
     */
    protected ElementNode createInitialStateConnectorElement() {
	ElementNode.Builder builder = new ElementNode.Builder("InitialState Connector",
		IOutboundConnectorsParser.Elements.InitialStateConnector.getLocalName(), "cloud",
		OutboundConnectorsRoleKeys.FilteredConnector, this);
	builder.description("Forwards outbound events to InitialState.com for advanced visualization.");

	addCommonConnectorAttributes(builder);
	builder.attribute((new AttributeNode.Builder("Streaming access key", "streamingAccessKey", AttributeType.String)
		.description(
			"Access key obtained from the InitialState.com website that specifies the account that the events will be associated with.")
		.makeRequired().build()));
	return builder.build();
    }

    /**
     * Create a Dweet.io connector.
     * 
     * @return
     */
    protected ElementNode createDweetConnectorElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Dweet.io Connector",
		IOutboundConnectorsParser.Elements.DweetIoConnector.getLocalName(), "cloud",
		OutboundConnectorsRoleKeys.FilteredConnector, this);
	builder.description(
		"Sends events to the Dweet.io cloud service where they can be viewed and integrated with other services. "
			+ "The unique 'thing' name will be the unique token for the device assignment the event is associated with.");

	addCommonConnectorAttributes(builder);
	return builder.build();
    }

    /**
     * Create a Groovy connector.
     * 
     * @return
     */
    protected ElementNode createGroovyConnectorElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Groovy Connector",
		IOutboundConnectorsParser.Elements.GroovyConnector.getLocalName(), "cogs",
		OutboundConnectorsRoleKeys.FilteredConnector, this);
	builder.description("Delegates event processing to a Groovy script which can execute "
		+ "conditional logic, create new events, or carry out other tasks.");

	addCommonConnectorAttributes(builder);
	builder.attribute((new AttributeNode.Builder("Script path", "scriptPath", AttributeType.String)
		.description("Script path relative to Groovy script root.").makeRequired().build()));
	return builder.build();
    }

    /**
     * Add common connector attributes.
     * 
     * @param builder
     */
    public static void addCommonConnectorAttributes(ElementNode.Builder builder) {
	builder.attributeGroup("common", "Outbound Connector Attributes");
	builder.attribute((new AttributeNode.Builder("Connector id", "connectorId", AttributeType.String)
		.description("Unique id used for referencing this connector.").group("common").makeIndex()
		.makeRequired().build()));
	builder.attribute((new AttributeNode.Builder("Number of processing threads", "numProcessingThreads",
		AttributeType.Integer).description("Number of threads used to load inbound events into connector.")
			.group("common").makeRequired().build()));
    }

    /**
     * Create filter criteria element.
     * 
     * @return
     */
    protected ElementNode createFilterCriteriaElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Filter Criteria", "filters", "filter",
		OutboundConnectorsRoleKeys.Filters, this);
	builder.description("Adds filter criteria to control which events are sent to processor. "
		+ "Each filter is applied in the order below. Any events that have not been filtered "
		+ "will be passed to the outbound processor implementation.");
	return builder.build();
    }

    /**
     * Create connector site filter.
     * 
     * @return
     */
    protected ElementNode createSiteFilterElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Site Filter",
		IOutboundConnectorsParser.Filters.SiteFilter.getLocalName(), "filter",
		OutboundConnectorsRoleKeys.OutboundFilters, this);
	builder.description("Allows events from a given site to be included or excluded for an outbound processor.");
	builder.attribute((new AttributeNode.Builder("Site", "site", AttributeType.SiteReference)
		.description("Site filter applies to.").makeIndex().build()));
	builder.attribute((new AttributeNode.Builder("Include/Exclude", "operation", AttributeType.String)
		.description("Indicates whether events from the site should be included or excluded from processing.")
		.choice("include").choice("exclude").defaultValue("include").build()));
	return builder.build();
    }

    /**
     * Create connector specification filter.
     * 
     * @return
     */
    protected ElementNode createSpecificationFilterElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Specification Filter",
		IOutboundConnectorsParser.Filters.SpecificationFilter.getLocalName(), "filter",
		OutboundConnectorsRoleKeys.OutboundFilters, this);
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
     * Create connector Groovy filter.
     * 
     * @return
     */
    protected ElementNode createGroovyFilterElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Groovy Filter",
		IOutboundConnectorsParser.Filters.GroovyFilter.getLocalName(), "filter",
		OutboundConnectorsRoleKeys.OutboundFilters, this);
	builder.description("Allows events to be filtered based on the return value of a Groovy script. "
		+ "If the script returns false, the event is filtered. See the SiteWhere documentation for "
		+ "a description of the variable bindings provided by the system.");
	builder.attribute((new AttributeNode.Builder("Script path", "scriptPath", AttributeType.String)
		.description("Script path relative to Groovy script root.").makeRequired().build()));
	return builder.build();
    }
}