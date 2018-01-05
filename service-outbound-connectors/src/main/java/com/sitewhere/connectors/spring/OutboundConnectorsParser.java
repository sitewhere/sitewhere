/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.spring;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.configuration.parser.IOutboundConnectorsParser.Elements;
import com.sitewhere.configuration.parser.IOutboundConnectorsParser.Filters;
import com.sitewhere.configuration.parser.IOutboundConnectorsParser.Multicasters;
import com.sitewhere.configuration.parser.IOutboundConnectorsParser.RouteBuilders;
import com.sitewhere.connectors.OutboundConnectorsManager;
import com.sitewhere.connectors.aws.sqs.SqsOutboundEventProcessor;
import com.sitewhere.connectors.azure.EventHubOutboundEventProcessor;
import com.sitewhere.connectors.dweetio.DweetIoEventProcessor;
import com.sitewhere.connectors.filter.FilterOperation;
import com.sitewhere.connectors.filter.SiteFilter;
import com.sitewhere.connectors.filter.SpecificationFilter;
import com.sitewhere.connectors.groovy.GroovyEventProcessor;
import com.sitewhere.connectors.groovy.filter.GroovyFilter;
import com.sitewhere.connectors.groovy.multicast.AllWithSpecificationStringMulticaster;
import com.sitewhere.connectors.groovy.routing.GroovyRouteBuilder;
import com.sitewhere.connectors.hazelcast.HazelcastEventProcessor;
import com.sitewhere.connectors.initialstate.InitialStateEventProcessor;
import com.sitewhere.connectors.mqtt.MqttOutboundConnector;
import com.sitewhere.connectors.rabbitmq.RabbitMqOutboundEventProcessor;
import com.sitewhere.connectors.solr.SolrDeviceEventProcessor;
import com.sitewhere.spi.microservice.spring.OutboundConnectorsBeans;

/**
 * Parses elements related to outbound connectors.
 * 
 * @author Derek
 */
public class OutboundConnectorsParser extends AbstractBeanDefinitionParser {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LogManager.getLogger();

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#
     * parseInternal (org.w3c.dom.Element,
     * org.springframework.beans.factory.xml.ParserContext)
     */
    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
	ManagedList<Object> connectors = new ManagedList<Object>();
	List<Element> children = DomUtils.getChildElements(element);
	for (Element child : children) {
	    Elements type = Elements.getByLocalName(child.getLocalName());
	    if (type == null) {
		throw new RuntimeException("Unknown outbound connectors element: " + child.getLocalName());
	    }
	    switch (type) {
	    case OutboundConnector: {
		connectors.add(parseOutboundConnector(child, context));
		break;
	    }
	    case MqttConnector: {
		connectors.add(parseMqttConnector(child, context));
		break;
	    }
	    case RabbitMqConnector: {
		connectors.add(parseRabbitMqConnector(child, context));
		break;
	    }
	    case HazelcastConnector: {
		connectors.add(parseHazelcastConnector(child, context));
		break;
	    }
	    case SolrConnector: {
		connectors.add(parseSolrConnector(child, context));
		break;
	    }
	    case AzureEventHubConnector: {
		connectors.add(parseAzureEventHubConnector(child, context));
		break;
	    }
	    case AmazonSqsConnector: {
		connectors.add(parseAmazonSqsConnector(child, context));
		break;
	    }
	    case InitialStateConnector: {
		connectors.add(parseInitialStateConnector(child, context));
		break;
	    }
	    case DweetIoConnector: {
		connectors.add(parseDweetIoConnector(child, context));
		break;
	    }
	    case GroovyConnector: {
		connectors.add(parseGroovyConnector(child, context));
		break;
	    }
	    }
	}

	// Build outbound event processors manager and inject the list of beans.
	BeanDefinitionBuilder manager = BeanDefinitionBuilder.rootBeanDefinition(OutboundConnectorsManager.class);
	manager.addPropertyValue("outboundConnectors", connectors);
	context.getRegistry().registerBeanDefinition(OutboundConnectorsBeans.BEAN_OUTBOUND_CONNECTORS_MANAGER,
		manager.getBeanDefinition());

	return null;
    }

    /**
     * Parse attributes that are common to all outbound connectors.
     * 
     * @param element
     * @param builder
     */
    protected void parseCommonOutboundConnectorAttributes(Element element, BeanDefinitionBuilder builder) {
	// Handle connector id.
	Attr connectorId = element.getAttributeNode("connectorId");
	if (connectorId != null) {
	    builder.addPropertyValue("connectorId", connectorId.getValue());
	}

	// Handle number of processing threads.
	Attr numProcessingThreads = element.getAttributeNode("numProcessingThreads");
	if (numProcessingThreads != null) {
	    builder.addPropertyValue("numProcessingThreads", numProcessingThreads.getValue());
	}
    }

    /**
     * Parse configuration for custom outbound connector.
     * 
     * @param element
     * @param context
     * @return
     */
    protected RuntimeBeanReference parseOutboundConnector(Element element, ParserContext context) {
	Attr ref = element.getAttributeNode("ref");
	if (ref != null) {
	    return new RuntimeBeanReference(ref.getValue());
	}
	throw new RuntimeException("Outbound connector does not have ref defined.");
    }

    /**
     * Parse configuration for connector that delivers events to an MQTT topic.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseMqttConnector(Element element, ParserContext context) {
	BeanDefinitionBuilder processor = BeanDefinitionBuilder.rootBeanDefinition(MqttOutboundConnector.class);

	// Parse common outbound connector attributes.
	parseCommonOutboundConnectorAttributes(element, processor);

	Attr protocol = element.getAttributeNode("protocol");
	if (protocol != null) {
	    processor.addPropertyValue("protocol", protocol.getValue());
	}

	Attr hostname = element.getAttributeNode("hostname");
	if (hostname == null) {
	    throw new RuntimeException("MQTT hostname attribute not provided.");
	}
	processor.addPropertyValue("hostname", hostname.getValue());

	Attr port = element.getAttributeNode("port");
	if (port == null) {
	    throw new RuntimeException("MQTT port attribute not provided.");
	}
	processor.addPropertyValue("port", port.getValue());

	Attr username = element.getAttributeNode("username");
	if (username != null) {
	    processor.addPropertyValue("username", username.getValue());
	}

	Attr password = element.getAttributeNode("password");
	if (password != null) {
	    processor.addPropertyValue("password", password.getValue());
	}

	Attr trustStorePath = element.getAttributeNode("trustStorePath");
	if (trustStorePath != null) {
	    processor.addPropertyValue("trustStorePath", trustStorePath.getValue());
	}

	Attr trustStorePassword = element.getAttributeNode("trustStorePassword");
	if (trustStorePassword != null) {
	    processor.addPropertyValue("trustStorePassword", trustStorePassword.getValue());
	}

	Attr topic = element.getAttributeNode("topic");
	if (topic != null) {
	    processor.addPropertyValue("topic", topic.getValue());
	}

	// Parse nested filters.
	processor.addPropertyValue("filters", parseFilters(element, context));

	// Parse multicaster.
	processor.addPropertyValue("multicaster", parseMulticaster(element, context));

	// Parse route builder.
	processor.addPropertyValue("routeBuilder", parseRouteBuilder(element, context));

	return processor.getBeanDefinition();
    }

    /**
     * Parse configuration for connector that delivers events to a RabbitMQ
     * exchange.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseRabbitMqConnector(Element element, ParserContext context) {
	BeanDefinitionBuilder processor = BeanDefinitionBuilder
		.rootBeanDefinition(RabbitMqOutboundEventProcessor.class);

	// Parse common outbound connector attributes.
	parseCommonOutboundConnectorAttributes(element, processor);

	Attr connectionUri = element.getAttributeNode("connectionUri");
	if (connectionUri != null) {
	    processor.addPropertyValue("connectionUri", connectionUri.getValue());
	}

	Attr topic = element.getAttributeNode("topic");
	if (topic != null) {
	    processor.addPropertyValue("topic", topic.getValue());
	}

	// Parse nested filters.
	processor.addPropertyValue("filters", parseFilters(element, context));

	// Parse multicaster.
	processor.addPropertyValue("multicaster", parseMulticaster(element, context));

	// Parse route builder.
	processor.addPropertyValue("routeBuilder", parseRouteBuilder(element, context));

	return processor.getBeanDefinition();
    }

    /**
     * Parse configuration for Hazelcast outbound connector.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseHazelcastConnector(Element element, ParserContext context) {
	BeanDefinitionBuilder processor = BeanDefinitionBuilder.rootBeanDefinition(HazelcastEventProcessor.class);

	// Parse common outbound connector attributes.
	parseCommonOutboundConnectorAttributes(element, processor);

	// Parse nested filters.
	processor.addPropertyValue("filters", parseFilters(element, context));

	return processor.getBeanDefinition();
    }

    /**
     * Parse configuration for Solr outbound connector.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseSolrConnector(Element element, ParserContext context) {
	BeanDefinitionBuilder processor = BeanDefinitionBuilder.rootBeanDefinition(SolrDeviceEventProcessor.class);
	// processor.addPropertyReference("solr",
	// SolrConnection.SOLR_CONFIGURATION_BEAN);

	// Parse common outbound connector attributes.
	parseCommonOutboundConnectorAttributes(element, processor);

	// Parse nested filters.
	processor.addPropertyValue("filters", parseFilters(element, context));

	return processor.getBeanDefinition();
    }

    /**
     * Parses configuration for Azure EventHub connector.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseAzureEventHubConnector(Element element, ParserContext context) {
	BeanDefinitionBuilder processor = BeanDefinitionBuilder
		.rootBeanDefinition(EventHubOutboundEventProcessor.class);

	// Parse common outbound connector attributes.
	parseCommonOutboundConnectorAttributes(element, processor);

	Attr sasKey = element.getAttributeNode("sasKey");
	if (sasKey == null) {
	    throw new RuntimeException("SAS key required for Azure EventHub connector.");
	}
	processor.addPropertyValue("sasKey", sasKey.getValue());

	Attr sasName = element.getAttributeNode("sasName");
	if (sasName == null) {
	    throw new RuntimeException("SAS name required for Azure EventHub connector.");
	}
	processor.addPropertyValue("sasName", sasName.getValue());

	Attr serviceBusName = element.getAttributeNode("serviceBusName");
	if (serviceBusName == null) {
	    throw new RuntimeException("Service bus name required for Azure EventHub connector.");
	}
	processor.addPropertyValue("serviceBusName", serviceBusName.getValue());

	Attr eventHubName = element.getAttributeNode("eventHubName");
	if (eventHubName == null) {
	    throw new RuntimeException("EventHub name required for Azure EventHub connector.");
	}
	processor.addPropertyValue("eventHubName", eventHubName.getValue());

	// Parse nested filters.
	processor.addPropertyValue("filters", parseFilters(element, context));

	return processor.getBeanDefinition();
    }

    /**
     * Parses configuration for Amazon SQS connector.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseAmazonSqsConnector(Element element, ParserContext context) {
	BeanDefinitionBuilder processor = BeanDefinitionBuilder.rootBeanDefinition(SqsOutboundEventProcessor.class);

	// Parse common outbound connector attributes.
	parseCommonOutboundConnectorAttributes(element, processor);

	Attr accessKey = element.getAttributeNode("accessKey");
	if (accessKey == null) {
	    throw new RuntimeException("Amazon access key required for SQS event processor.");
	}
	processor.addPropertyValue("accessKey", accessKey.getValue());

	Attr secretKey = element.getAttributeNode("secretKey");
	if (secretKey == null) {
	    throw new RuntimeException("Amazon secret key required for SQS event processor.");
	}
	processor.addPropertyValue("secretKey", secretKey.getValue());

	Attr queueUrl = element.getAttributeNode("queueUrl");
	if (queueUrl == null) {
	    throw new RuntimeException("Queue URL required for Amazon SQS event processor.");
	}
	processor.addPropertyValue("queueUrl", queueUrl.getValue());

	// Parse nested filters.
	processor.addPropertyValue("filters", parseFilters(element, context));

	return processor.getBeanDefinition();
    }

    /**
     * Parse configuration for connector that delivers events to InitialState.com.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseInitialStateConnector(Element element, ParserContext context) {
	BeanDefinitionBuilder processor = BeanDefinitionBuilder.rootBeanDefinition(InitialStateEventProcessor.class);

	// Parse common outbound connector attributes.
	parseCommonOutboundConnectorAttributes(element, processor);

	Attr streamingAccessKey = element.getAttributeNode("streamingAccessKey");
	if (streamingAccessKey == null) {
	    throw new RuntimeException("Streaming access key is required for InitialState.com connectivity.");
	}
	processor.addPropertyValue("streamingAccessKey", streamingAccessKey.getValue());

	// Parse nested filters.
	processor.addPropertyValue("filters", parseFilters(element, context));

	return processor.getBeanDefinition();
    }

    /**
     * Parse configuration for connector that delivers events to dweet.io.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseDweetIoConnector(Element element, ParserContext context) {
	BeanDefinitionBuilder processor = BeanDefinitionBuilder.rootBeanDefinition(DweetIoEventProcessor.class);

	// Parse common outbound connector attributes.
	parseCommonOutboundConnectorAttributes(element, processor);

	// Parse nested filters.
	processor.addPropertyValue("filters", parseFilters(element, context));

	return processor.getBeanDefinition();
    }

    /**
     * Parse configuration for connector delegates processing to a Groovy script.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseGroovyConnector(Element element, ParserContext context) {
	BeanDefinitionBuilder processor = BeanDefinitionBuilder.rootBeanDefinition(GroovyEventProcessor.class);

	// Parse common outbound connector attributes.
	parseCommonOutboundConnectorAttributes(element, processor);

	Attr scriptPath = element.getAttributeNode("scriptPath");
	if (scriptPath != null) {
	    processor.addPropertyValue("scriptPath", scriptPath.getValue());
	}

	return processor.getBeanDefinition();
    }

    /**
     * Parse filters associated with an outbound connector.
     * 
     * @param element
     * @param context
     * @return
     */
    protected ManagedList<?> parseFilters(Element element, ParserContext context) {
	ManagedList<Object> result = new ManagedList<Object>();

	// Look for a 'filters' element.
	Element filters = DomUtils.getChildElementByTagName(element, "filters");
	if (filters != null) {
	    // Process the list of filters.
	    List<Element> children = DomUtils.getChildElements(filters);
	    for (Element child : children) {
		Filters type = Filters.getByLocalName(child.getLocalName());
		if (type == null) {
		    throw new RuntimeException("Unknown filter element: " + child.getLocalName());
		}
		switch (type) {
		case SiteFilter: {
		    result.add(parseSiteFilter(child, context));
		    break;
		}
		case SpecificationFilter: {
		    result.add(parseSpecificationFilter(child, context));
		    break;
		}
		case GroovyFilter: {
		    result.add(parseGroovyFilter(child, context));
		    break;
		}
		}
	    }
	}

	return result;
    }

    /**
     * Parse a site filter element.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseSiteFilter(Element element, ParserContext context) {
	BeanDefinitionBuilder filter = BeanDefinitionBuilder.rootBeanDefinition(SiteFilter.class);

	Attr site = element.getAttributeNode("site");
	if (site == null) {
	    throw new RuntimeException("Attribute 'site' is required for site-filter.");
	}
	filter.addPropertyValue("siteToken", site.getValue());

	Attr operation = element.getAttributeNode("operation");
	if (operation != null) {
	    FilterOperation op = "include".equals(operation.getValue()) ? FilterOperation.Include
		    : FilterOperation.Exclude;
	    filter.addPropertyValue("operation", op);
	}

	return filter.getBeanDefinition();
    }

    /**
     * Parse a specification filter element.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseSpecificationFilter(Element element, ParserContext context) {
	BeanDefinitionBuilder filter = BeanDefinitionBuilder.rootBeanDefinition(SpecificationFilter.class);

	Attr specification = element.getAttributeNode("specification");
	if (specification == null) {
	    throw new RuntimeException("Attribute 'specification' is required for specification-filter.");
	}
	filter.addPropertyValue("specificationToken", specification.getValue());

	Attr operation = element.getAttributeNode("operation");
	if (operation != null) {
	    FilterOperation op = "include".equals(operation.getValue()) ? FilterOperation.Include
		    : FilterOperation.Exclude;
	    filter.addPropertyValue("operation", op);
	}

	return filter.getBeanDefinition();
    }

    /**
     * Parse configuration for Groovy filter.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseGroovyFilter(Element element, ParserContext context) {
	BeanDefinitionBuilder filter = BeanDefinitionBuilder.rootBeanDefinition(GroovyFilter.class);

	Attr scriptPath = element.getAttributeNode("scriptPath");
	if (scriptPath == null) {
	    throw new RuntimeException("Attribute 'scriptPath' is required for groovy-filter.");
	}
	filter.addPropertyValue("scriptPath", scriptPath.getValue());

	return filter.getBeanDefinition();
    }

    /**
     * Parse a multicaster if defined.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseMulticaster(Element element, ParserContext context) {
	List<Element> children = DomUtils.getChildElements(element);
	for (Element child : children) {
	    Multicasters type = Multicasters.getByLocalName(child.getLocalName());
	    if (type != null) {
		switch (type) {
		case AllWithSpecificationMulticaster: {
		    return parseAllWithSpecificationMulticaster(child, context);
		}
		}
	    }
	}

	return null;
    }

    /**
     * Parse the "all with specification" multicaster.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseAllWithSpecificationMulticaster(Element element, ParserContext context) {
	BeanDefinitionBuilder multicaster = BeanDefinitionBuilder
		.rootBeanDefinition(AllWithSpecificationStringMulticaster.class);

	Attr specification = element.getAttributeNode("specification");
	if (specification == null) {
	    throw new RuntimeException("Attribute 'specification' is required for all-with-specification-multicaster.");
	}
	multicaster.addPropertyValue("specificationToken", specification.getValue());

	Attr scriptPath = element.getAttributeNode("scriptPath");
	if (scriptPath != null) {
	    multicaster.addPropertyValue("scriptPath", scriptPath.getValue());
	}

	return multicaster.getBeanDefinition();
    }

    /**
     * Parse a route builder if defined.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseRouteBuilder(Element element, ParserContext context) {
	List<Element> children = DomUtils.getChildElements(element);
	for (Element child : children) {
	    RouteBuilders type = RouteBuilders.getByLocalName(child.getLocalName());
	    if (type != null) {
		switch (type) {
		case GroovyRouteBuilder: {
		    return parseGroovyRouteBuilder(child, context);
		}
		}
	    }
	}

	return null;
    }

    /**
     * Parse the Groovy route builder.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseGroovyRouteBuilder(Element element, ParserContext context) {
	BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(GroovyRouteBuilder.class);

	Attr scriptPath = element.getAttributeNode("scriptPath");
	if (scriptPath != null) {
	    builder.addPropertyValue("scriptPath", scriptPath.getValue());
	}

	return builder.getBeanDefinition();
    }
}