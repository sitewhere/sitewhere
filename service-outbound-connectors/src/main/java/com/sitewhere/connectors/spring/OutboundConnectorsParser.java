/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.spring;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.configuration.instance.solr.SolrConfigurationChoice;
import com.sitewhere.configuration.instance.solr.SolrConfigurationParser;
import com.sitewhere.configuration.parser.IConnectorCommonParser.Solr;
import com.sitewhere.configuration.parser.IOutboundConnectorsParser.Elements;
import com.sitewhere.configuration.parser.IOutboundConnectorsParser.Filters;
import com.sitewhere.configuration.parser.IOutboundConnectorsParser.Multicasters;
import com.sitewhere.configuration.parser.IOutboundConnectorsParser.PayloadBuilders;
import com.sitewhere.configuration.parser.IOutboundConnectorsParser.RouteBuilders;
import com.sitewhere.configuration.parser.IOutboundConnectorsParser.UriBuilders;
import com.sitewhere.connectors.OutboundConnectorsManager;
import com.sitewhere.connectors.aws.sqs.SqsOutboundConnector;
import com.sitewhere.connectors.azure.EventHubOutboundConnector;
import com.sitewhere.connectors.dweetio.DweetIoOutboundConnector;
import com.sitewhere.connectors.filter.AreaFilter;
import com.sitewhere.connectors.filter.DeviceTypeFilter;
import com.sitewhere.connectors.filter.FilterOperation;
import com.sitewhere.connectors.groovy.GroovyOutboundConnector;
import com.sitewhere.connectors.groovy.common.GroovyPayloadBuilder;
import com.sitewhere.connectors.groovy.common.GroovyUriBuilder;
import com.sitewhere.connectors.groovy.filter.GroovyFilter;
import com.sitewhere.connectors.groovy.multicast.AllWithSpecificationStringMulticaster;
import com.sitewhere.connectors.groovy.routing.GroovyRouteBuilder;
import com.sitewhere.connectors.http.HttpOutboundConnector;
import com.sitewhere.connectors.initialstate.InitialStateOutboundConnector;
import com.sitewhere.connectors.mqtt.MqttOutboundConnector;
import com.sitewhere.connectors.rabbitmq.RabbitMqOutboundConnector;
import com.sitewhere.connectors.solr.SolrOutboundConnector;
import com.sitewhere.spi.microservice.spring.OutboundConnectorsBeans;

/**
 * Parses elements related to outbound connectors.
 * 
 * @author Derek
 */
public class OutboundConnectorsParser extends AbstractBeanDefinitionParser {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(OutboundConnectorsParser.class);

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
	    case HttpConnector: {
		connectors.add(parseHttpConnector(child, context));
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

	Attr clientId = element.getAttributeNode("clientId");
	if (clientId != null) {
	    processor.addPropertyValue("clientId", clientId.getValue());
	}

	Attr cleanSession = element.getAttributeNode("cleanSession");
	if (cleanSession != null) {
	    processor.addPropertyValue("cleanSession", cleanSession.getValue());
	}

	Attr qos = element.getAttributeNode("qos");
	if (qos != null) {
	    processor.addPropertyValue("qos", qos.getValue());
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
	BeanDefinitionBuilder processor = BeanDefinitionBuilder.rootBeanDefinition(RabbitMqOutboundConnector.class);

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
     * Parse configuration for Solr outbound connector.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseSolrConnector(Element element, ParserContext context) {
	List<Element> children = DomUtils.getChildElements(element);
	BeanDefinitionBuilder connector = BeanDefinitionBuilder.rootBeanDefinition(SolrOutboundConnector.class);

	for (Element child : children) {
	    Solr type = Solr.getByLocalName(child.getLocalName());
	    if (type == null) {
		throw new RuntimeException("Unknown outbound connectors element: " + child.getLocalName());
	    }
	    switch (type) {
	    case SolrConfigurationChoice: {
		SolrConfigurationChoice config = SolrConfigurationParser.parseSolrConfigurationChoice(child, context);
		switch (config.getType()) {
		case SolrConfiguration: {
		    connector.addPropertyValue("solrConfiguration", config.getConfiguration());
		    break;
		}
		case SolrConfigurationReference: {
		    connector.addPropertyReference("solrConfiguration", (String) config.getConfiguration());
		    break;
		}
		default: {
		    throw new RuntimeException("Invalid Solr configuration specified: " + config.getType());
		}
		}
		break;
	    }
	    }
	}

	// Parse common outbound connector attributes.
	parseCommonOutboundConnectorAttributes(element, connector);

	// Parse nested filters.
	connector.addPropertyValue("filters", parseFilters(element, context));

	return connector.getBeanDefinition();
    }

    /**
     * Parses configuration for Azure EventHub connector.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseAzureEventHubConnector(Element element, ParserContext context) {
	BeanDefinitionBuilder connector = BeanDefinitionBuilder.rootBeanDefinition(EventHubOutboundConnector.class);

	// Parse common outbound connector attributes.
	parseCommonOutboundConnectorAttributes(element, connector);

	Attr sasKey = element.getAttributeNode("sasKey");
	if (sasKey == null) {
	    throw new RuntimeException("SAS key required for Azure EventHub connector.");
	}
	connector.addPropertyValue("sasKey", sasKey.getValue());

	Attr sasName = element.getAttributeNode("sasName");
	if (sasName == null) {
	    throw new RuntimeException("SAS name required for Azure EventHub connector.");
	}
	connector.addPropertyValue("sasName", sasName.getValue());

	Attr serviceBusName = element.getAttributeNode("serviceBusName");
	if (serviceBusName == null) {
	    throw new RuntimeException("Service bus name required for Azure EventHub connector.");
	}
	connector.addPropertyValue("serviceBusName", serviceBusName.getValue());

	Attr eventHubName = element.getAttributeNode("eventHubName");
	if (eventHubName == null) {
	    throw new RuntimeException("EventHub name required for Azure EventHub connector.");
	}
	connector.addPropertyValue("eventHubName", eventHubName.getValue());

	// Parse nested filters.
	connector.addPropertyValue("filters", parseFilters(element, context));

	return connector.getBeanDefinition();
    }

    /**
     * Parses configuration for Amazon SQS connector.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseAmazonSqsConnector(Element element, ParserContext context) {
	BeanDefinitionBuilder connector = BeanDefinitionBuilder.rootBeanDefinition(SqsOutboundConnector.class);

	// Parse common outbound connector attributes.
	parseCommonOutboundConnectorAttributes(element, connector);

	Attr accessKey = element.getAttributeNode("accessKey");
	if (accessKey == null) {
	    throw new RuntimeException("Amazon access key required for SQS event processor.");
	}
	connector.addPropertyValue("accessKey", accessKey.getValue());

	Attr secretKey = element.getAttributeNode("secretKey");
	if (secretKey == null) {
	    throw new RuntimeException("Amazon secret key required for SQS event processor.");
	}
	connector.addPropertyValue("secretKey", secretKey.getValue());

	Attr queueUrl = element.getAttributeNode("queueUrl");
	if (queueUrl == null) {
	    throw new RuntimeException("Queue URL required for Amazon SQS event processor.");
	}
	connector.addPropertyValue("queueUrl", queueUrl.getValue());

	// Parse nested filters.
	connector.addPropertyValue("filters", parseFilters(element, context));

	return connector.getBeanDefinition();
    }

    /**
     * Parse configuration for connector that delivers HTTP payloads for events.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseHttpConnector(Element element, ParserContext context) {
	BeanDefinitionBuilder connector = BeanDefinitionBuilder.rootBeanDefinition(HttpOutboundConnector.class);

	// Parse common outbound connector attributes.
	parseCommonOutboundConnectorAttributes(element, connector);

	Attr method = element.getAttributeNode("method");
	if (method != null) {
	    connector.addPropertyValue("method", method.getValue());
	}

	// Parse URI builder.
	connector.addPropertyValue("uriBuilder", parseUriBuilder(element, context));

	// Parse payload builder.
	connector.addPropertyValue("payloadBuilder", parsePayloadBuilder(element, context));

	// Parse nested filters.
	connector.addPropertyValue("filters", parseFilters(element, context));

	return connector.getBeanDefinition();
    }

    /**
     * Parse configuration for connector that delivers events to InitialState.com.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseInitialStateConnector(Element element, ParserContext context) {
	BeanDefinitionBuilder connector = BeanDefinitionBuilder.rootBeanDefinition(InitialStateOutboundConnector.class);

	// Parse common outbound connector attributes.
	parseCommonOutboundConnectorAttributes(element, connector);

	Attr streamingAccessKey = element.getAttributeNode("streamingAccessKey");
	if (streamingAccessKey == null) {
	    throw new RuntimeException("Streaming access key is required for InitialState.com connectivity.");
	}
	connector.addPropertyValue("streamingAccessKey", streamingAccessKey.getValue());

	// Parse nested filters.
	connector.addPropertyValue("filters", parseFilters(element, context));

	return connector.getBeanDefinition();
    }

    /**
     * Parse configuration for connector that delivers events to dweet.io.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseDweetIoConnector(Element element, ParserContext context) {
	BeanDefinitionBuilder connector = BeanDefinitionBuilder.rootBeanDefinition(DweetIoOutboundConnector.class);

	// Parse common outbound connector attributes.
	parseCommonOutboundConnectorAttributes(element, connector);

	// Parse nested filters.
	connector.addPropertyValue("filters", parseFilters(element, context));

	return connector.getBeanDefinition();
    }

    /**
     * Parse configuration for connector delegates processing to a Groovy script.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseGroovyConnector(Element element, ParserContext context) {
	BeanDefinitionBuilder connector = BeanDefinitionBuilder.rootBeanDefinition(GroovyOutboundConnector.class);

	// Parse common outbound connector attributes.
	parseCommonOutboundConnectorAttributes(element, connector);

	Attr scriptId = element.getAttributeNode("scriptId");
	if (scriptId != null) {
	    connector.addPropertyValue("scriptId", scriptId.getValue());
	}

	return connector.getBeanDefinition();
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
		case AreaFilter: {
		    result.add(parseAreaFilter(child, context));
		    break;
		}
		case DeviceTypeFilter: {
		    result.add(parseDeviceTypeFilter(child, context));
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
     * Parse a area filter element.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseAreaFilter(Element element, ParserContext context) {
	BeanDefinitionBuilder filter = BeanDefinitionBuilder.rootBeanDefinition(AreaFilter.class);

	Attr areaToken = element.getAttributeNode("areaToken");
	if (areaToken == null) {
	    throw new RuntimeException("Area token is required.");
	}
	filter.addPropertyValue("areaToken", areaToken.getValue());

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
    protected AbstractBeanDefinition parseDeviceTypeFilter(Element element, ParserContext context) {
	BeanDefinitionBuilder filter = BeanDefinitionBuilder.rootBeanDefinition(DeviceTypeFilter.class);

	Attr deviceTypeToken = element.getAttributeNode("deviceTypeToken");
	if (deviceTypeToken == null) {
	    throw new RuntimeException("Device type token is required.");
	}
	filter.addPropertyValue("deviceTypeToken", deviceTypeToken.getValue());

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
     * Parse a URI builder.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseUriBuilder(Element element, ParserContext context) {
	List<Element> children = DomUtils.getChildElements(element);
	for (Element child : children) {
	    UriBuilders type = UriBuilders.getByLocalName(child.getLocalName());
	    if (type != null) {
		switch (type) {
		case GroovyUriBuilder: {
		    return parseGroovyUriBuilder(child, context);
		}
		}
	    }
	}

	return null;
    }

    /**
     * Parse the Groovy URI builder.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseGroovyUriBuilder(Element element, ParserContext context) {
	BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(GroovyUriBuilder.class);

	Attr scriptId = element.getAttributeNode("scriptId");
	if (scriptId != null) {
	    builder.addPropertyValue("scriptId", scriptId.getValue());
	}

	return builder.getBeanDefinition();
    }

    /**
     * Parse a payload builder.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parsePayloadBuilder(Element element, ParserContext context) {
	List<Element> children = DomUtils.getChildElements(element);
	for (Element child : children) {
	    PayloadBuilders type = PayloadBuilders.getByLocalName(child.getLocalName());
	    if (type != null) {
		switch (type) {
		case GroovyPayloadBuilder: {
		    return parseGroovyPayloadBuilder(child, context);
		}
		}
	    }
	}

	return null;
    }

    /**
     * Parse the Groovy payload builder.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseGroovyPayloadBuilder(Element element, ParserContext context) {
	BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(GroovyPayloadBuilder.class);

	Attr scriptId = element.getAttributeNode("scriptId");
	if (scriptId != null) {
	    builder.addPropertyValue("scriptId", scriptId.getValue());
	}

	return builder.getBeanDefinition();
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