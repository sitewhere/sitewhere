/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spring.handler;

import java.util.List;

import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.aws.SqsOutboundEventProcessor;
import com.sitewhere.azure.device.communication.EventHubOutboundEventProcessor;
import com.sitewhere.cloud.providers.dweetio.DweetIoEventProcessor;
import com.sitewhere.cloud.providers.initialstate.InitialStateEventProcessor;
import com.sitewhere.device.communication.DeviceCommandEventProcessor;
import com.sitewhere.device.communication.mqtt.MqttOutboundEventProcessor;
import com.sitewhere.device.event.processor.DefaultOutboundEventProcessorChain;
import com.sitewhere.device.event.processor.filter.FilterOperation;
import com.sitewhere.device.event.processor.filter.SiteFilter;
import com.sitewhere.device.event.processor.filter.SpecificationFilter;
import com.sitewhere.geospatial.ZoneTest;
import com.sitewhere.geospatial.ZoneTestEventProcessor;
import com.sitewhere.groovy.GroovyConfiguration;
import com.sitewhere.groovy.device.event.processor.GroovyEventProcessor;
import com.sitewhere.groovy.device.event.processor.filter.GroovyFilter;
import com.sitewhere.groovy.device.event.processor.multicast.AllWithSpecificationStringMulticaster;
import com.sitewhere.groovy.device.event.processor.routing.GroovyRouteBuilder;
import com.sitewhere.hazelcast.HazelcastEventProcessor;
import com.sitewhere.hazelcast.SiteWhereHazelcastConfiguration;
import com.sitewhere.rabbitmq.RabbitMqOutboundEventProcessor;
import com.sitewhere.siddhi.GroovyStreamProcessor;
import com.sitewhere.siddhi.SiddhiEventProcessor;
import com.sitewhere.siddhi.SiddhiQuery;
import com.sitewhere.siddhi.StreamDebugger;
import com.sitewhere.siddhi.Wso2CepEventProcessor;
import com.sitewhere.solr.SiteWhereSolrConfiguration;
import com.sitewhere.solr.SolrDeviceEventProcessor;
import com.sitewhere.spi.device.event.AlertLevel;
import com.sitewhere.spi.geospatial.ZoneContainment;

/**
 * Parses configuration data from SiteWhere outbound processing chain section.
 * 
 * @author Derek
 */
public class OutboundProcessingChainParser {

	/**
	 * Parse elements for the outbound processing chain.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected Object parse(Element element, ParserContext context) {
		BeanDefinitionBuilder chain =
				BeanDefinitionBuilder.rootBeanDefinition(DefaultOutboundEventProcessorChain.class);
		List<Element> dsChildren = DomUtils.getChildElements(element);
		List<Object> processors = new ManagedList<Object>();
		for (Element child : dsChildren) {
			Elements type = Elements.getByLocalName(child.getLocalName());
			if (type == null) {
				throw new RuntimeException(
						"Unknown inbound processing chain element: " + child.getLocalName());
			}
			switch (type) {
			case OutboundEventProcessor: {
				processors.add(parseOutboundEventProcessor(child, context));
				break;
			}
			case ZoneTestEventProcessor: {
				processors.add(parseZoneTestEventProcessor(child, context));
				break;
			}
			case MqttEventProcessor: {
				processors.add(parseMqttEventProcessor(child, context));
				break;
			}
			case RabbitMqEventProcessor: {
				processors.add(parseRabbitMqEventProcessor(child, context));
				break;
			}
			case HazelcastEventProcessor: {
				processors.add(parseHazelcastEventProcessor(child, context));
				break;
			}
			case SolrEventProcessor: {
				processors.add(parseSolrEventProcessor(child, context));
				break;
			}
			case AzureEventHubEventProcessor: {
				processors.add(parseAzureEventHubEventProcessor(child, context));
				break;
			}
			case AmazonSqsEventProcessor: {
				processors.add(parseAmazonSqsEventProcessor(child, context));
				break;
			}
			case InitialStateEventProcessor: {
				processors.add(parseInitialStateEventProcessor(child, context));
				break;
			}
			case DweetIoEventProcessor: {
				processors.add(parseDweetIoEventProcessor(child, context));
				break;
			}
			case ProvisioningEventProcessor: {
				processors.add(parseCommandDeliveryEventProcessor(child, context));
				break;
			}
			case CommandDeliveryEventProcessor: {
				processors.add(parseCommandDeliveryEventProcessor(child, context));
				break;
			}
			case SiddhiEventProcessor: {
				processors.add(parseSiddhiEventProcessor(child, context));
				break;
			}
			case Wso2CepEventProcessor: {
				processors.add(parseWso2CepEventProcessor(child, context));
				break;
			}
			case GroovyEventProcessor: {
				processors.add(parseGroovyEventProcessor(child, context));
				break;
			}
			}
		}
		chain.addPropertyValue("processors", processors);
		return chain.getBeanDefinition();
	}

	/**
	 * Parse configuration for custom outbound event processor.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected RuntimeBeanReference parseOutboundEventProcessor(Element element, ParserContext context) {
		Attr ref = element.getAttributeNode("ref");
		if (ref != null) {
			return new RuntimeBeanReference(ref.getValue());
		}
		throw new RuntimeException("Outbound event processor does not have ref defined.");
	}

	/**
	 * Parse configuration for event processor that tests location events against zone
	 * boundaries for firing alert conditions.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected AbstractBeanDefinition parseZoneTestEventProcessor(Element element, ParserContext context) {
		BeanDefinitionBuilder processor =
				BeanDefinitionBuilder.rootBeanDefinition(ZoneTestEventProcessor.class);
		List<Element> children = DomUtils.getChildElementsByTagName(element, "zone-test");
		List<Object> tests = new ManagedList<Object>();
		for (Element testElm : children) {
			ZoneTest test = new ZoneTest();

			Attr zoneToken = testElm.getAttributeNode("zoneToken");
			if (zoneToken == null) {
				throw new RuntimeException("Zone test missing 'zoneToken' attribute.");
			}
			test.setZoneToken(zoneToken.getValue());

			Attr condition = testElm.getAttributeNode("condition");
			if (condition == null) {
				throw new RuntimeException("Zone test missing 'condition' attribute.");
			}
			ZoneContainment containment =
					(condition.getValue().equalsIgnoreCase("inside") ? ZoneContainment.Inside
							: ZoneContainment.Outside);
			test.setCondition(containment);

			Attr alertType = testElm.getAttributeNode("alertType");
			if (alertType == null) {
				throw new RuntimeException("Zone test missing 'alertType' attribute.");
			}
			test.setAlertType(alertType.getValue());

			Attr alertMessage = testElm.getAttributeNode("alertMessage");
			if (alertMessage == null) {
				throw new RuntimeException("Zone test missing 'alertMessage' attribute.");
			}
			test.setAlertMessage(alertMessage.getValue());

			Attr alertLevel = testElm.getAttributeNode("alertLevel");
			AlertLevel level = AlertLevel.Error;
			if (alertLevel != null) {
				level = convertAlertLevel(alertLevel.getValue());
			}
			test.setAlertLevel(level);

			tests.add(test);
		}
		processor.addPropertyValue("zoneTests", tests);

		// Parse nested filters.
		processor.addPropertyValue("filters", parseFilters(element, context));

		return processor.getBeanDefinition();
	}

	protected AlertLevel convertAlertLevel(String input) {
		if (input.equalsIgnoreCase("info")) {
			return AlertLevel.Info;
		}
		if (input.equalsIgnoreCase("warning")) {
			return AlertLevel.Warning;
		}
		if (input.equalsIgnoreCase("error")) {
			return AlertLevel.Error;
		}
		if (input.equalsIgnoreCase("critical")) {
			return AlertLevel.Critical;
		}
		throw new RuntimeException("Invalid alert level value: " + input);
	}

	/**
	 * Parse configuration for event processor that delivers events to an MQTT topic.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected AbstractBeanDefinition parseMqttEventProcessor(Element element, ParserContext context) {
		BeanDefinitionBuilder processor =
				BeanDefinitionBuilder.rootBeanDefinition(MqttOutboundEventProcessor.class);

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
	 * Parse configuration for event processor that delivers events to a RabbitMQ
	 * exchange.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected AbstractBeanDefinition parseRabbitMqEventProcessor(Element element, ParserContext context) {
		BeanDefinitionBuilder processor =
				BeanDefinitionBuilder.rootBeanDefinition(RabbitMqOutboundEventProcessor.class);

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
	 * Parse configuration for Hazelcast outbound event processor.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected AbstractBeanDefinition parseHazelcastEventProcessor(Element element, ParserContext context) {
		BeanDefinitionBuilder processor =
				BeanDefinitionBuilder.rootBeanDefinition(HazelcastEventProcessor.class);
		processor.addPropertyReference("configuration",
				SiteWhereHazelcastConfiguration.HAZELCAST_CONFIGURATION_BEAN);

		// Parse nested filters.
		processor.addPropertyValue("filters", parseFilters(element, context));

		return processor.getBeanDefinition();
	}

	/**
	 * Parse configuration for Solr outbound event processor.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected AbstractBeanDefinition parseSolrEventProcessor(Element element, ParserContext context) {
		BeanDefinitionBuilder processor =
				BeanDefinitionBuilder.rootBeanDefinition(SolrDeviceEventProcessor.class);
		processor.addPropertyReference("solr", SiteWhereSolrConfiguration.SOLR_CONFIGURATION_BEAN);

		// Parse nested filters.
		processor.addPropertyValue("filters", parseFilters(element, context));

		return processor.getBeanDefinition();
	}

	/**
	 * Parses configuration for Azure EventHub event processor.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected AbstractBeanDefinition parseAzureEventHubEventProcessor(Element element,
			ParserContext context) {
		BeanDefinitionBuilder processor =
				BeanDefinitionBuilder.rootBeanDefinition(EventHubOutboundEventProcessor.class);

		Attr sasKey = element.getAttributeNode("sasKey");
		if (sasKey == null) {
			throw new RuntimeException("SAS key required for Azure EventHub event processor.");
		}
		processor.addPropertyValue("sasKey", sasKey.getValue());

		Attr sasName = element.getAttributeNode("sasName");
		if (sasName == null) {
			throw new RuntimeException("SAS name required for Azure EventHub event processor.");
		}
		processor.addPropertyValue("sasName", sasName.getValue());

		Attr serviceBusName = element.getAttributeNode("serviceBusName");
		if (serviceBusName == null) {
			throw new RuntimeException("Service bus name required for Azure EventHub event processor.");
		}
		processor.addPropertyValue("serviceBusName", serviceBusName.getValue());

		Attr eventHubName = element.getAttributeNode("eventHubName");
		if (eventHubName == null) {
			throw new RuntimeException("EventHub name required for Azure EventHub event processor.");
		}
		processor.addPropertyValue("eventHubName", eventHubName.getValue());

		// Parse nested filters.
		processor.addPropertyValue("filters", parseFilters(element, context));

		return processor.getBeanDefinition();
	}

	/**
	 * Parses configuration for Amazon SQS event processor.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected AbstractBeanDefinition parseAmazonSqsEventProcessor(Element element, ParserContext context) {
		BeanDefinitionBuilder processor =
				BeanDefinitionBuilder.rootBeanDefinition(SqsOutboundEventProcessor.class);

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
	 * Parse configuration for event processor that delivers events to InitialState.com.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected AbstractBeanDefinition parseInitialStateEventProcessor(Element element, ParserContext context) {
		BeanDefinitionBuilder processor =
				BeanDefinitionBuilder.rootBeanDefinition(InitialStateEventProcessor.class);

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
	 * Parse configuration for event processor that delivers events to dweet.io.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected AbstractBeanDefinition parseDweetIoEventProcessor(Element element, ParserContext context) {
		BeanDefinitionBuilder processor =
				BeanDefinitionBuilder.rootBeanDefinition(DweetIoEventProcessor.class);

		// Parse nested filters.
		processor.addPropertyValue("filters", parseFilters(element, context));

		return processor.getBeanDefinition();
	}

	/**
	 * Parse configuration for event processor that routes commands to communication
	 * subsystem.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected AbstractBeanDefinition parseCommandDeliveryEventProcessor(Element element,
			ParserContext context) {
		BeanDefinitionBuilder processor =
				BeanDefinitionBuilder.rootBeanDefinition(DeviceCommandEventProcessor.class);

		Attr numThreads = element.getAttributeNode("numThreads");
		if (numThreads != null) {
			processor.addPropertyValue("numThreads", Integer.parseInt(numThreads.getValue()));
		}

		// Parse nested filters.
		processor.addPropertyValue("filters", parseFilters(element, context));

		return processor.getBeanDefinition();
	}

	/**
	 * Parse configuration for event processor that publishes SiteWhere events to an
	 * external WSO2 CEP engine.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected AbstractBeanDefinition parseWso2CepEventProcessor(Element element, ParserContext context) {
		BeanDefinitionBuilder processor =
				BeanDefinitionBuilder.rootBeanDefinition(Wso2CepEventProcessor.class);

		Attr hostname = element.getAttributeNode("hostname");
		if (hostname != null) {
			processor.addPropertyValue("siddhiHost", hostname.getValue());
		}

		Attr port = element.getAttributeNode("port");
		if (port != null) {
			processor.addPropertyValue("siddhiPort", port.getValue());
		}

		Attr username = element.getAttributeNode("username");
		if (username != null) {
			processor.addPropertyValue("siddhiUsername", username.getValue());
		}

		Attr password = element.getAttributeNode("password");
		if (password != null) {
			processor.addPropertyValue("siddhiPassword", password.getValue());
		}

		return processor.getBeanDefinition();
	}

	/**
	 * Parse configuration for event processor delegates processing to a Groovy script.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected AbstractBeanDefinition parseGroovyEventProcessor(Element element, ParserContext context) {
		BeanDefinitionBuilder processor =
				BeanDefinitionBuilder.rootBeanDefinition(GroovyEventProcessor.class);
		processor.addPropertyReference("configuration", GroovyConfiguration.GROOVY_CONFIGURATION_BEAN);
		processor.addPropertyReference("hazelcast",
				SiteWhereHazelcastConfiguration.HAZELCAST_CONFIGURATION_BEAN);

		Attr scriptPath = element.getAttributeNode("scriptPath");
		if (scriptPath != null) {
			processor.addPropertyValue("scriptPath", scriptPath.getValue());
		}

		return processor.getBeanDefinition();
	}

	/**
	 * Parse configuration for event processor that uses Siddhi to perform complex event
	 * processing.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected AbstractBeanDefinition parseSiddhiEventProcessor(Element element, ParserContext context) {
		BeanDefinitionBuilder processor =
				BeanDefinitionBuilder.rootBeanDefinition(SiddhiEventProcessor.class);

		List<Object> queries = new ManagedList<Object>();
		List<Element> queryElements = DomUtils.getChildElementsByTagName(element, "siddhi-query");
		for (Element queryElement : queryElements) {
			queries.add(parseSiddhiQuery(queryElement, context));
		}
		processor.addPropertyValue("queries", queries);

		// Parse nested filters.
		processor.addPropertyValue("filters", parseFilters(element, context));

		return processor.getBeanDefinition();
	}

	/**
	 * Parse a single {@link SiddhiQuery}.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected AbstractBeanDefinition parseSiddhiQuery(Element element, ParserContext context) {
		BeanDefinitionBuilder query = BeanDefinitionBuilder.rootBeanDefinition(SiddhiQuery.class);

		Attr selector = element.getAttributeNode("selector");
		if (selector == null) {
			throw new RuntimeException("Selector attribute is required for siddhi-query.");
		}
		query.addPropertyValue("selector", selector.getValue());

		List<Object> callbacks = new ManagedList<Object>();

		// Process stream debugger callbacks.
		List<Element> debuggerElements = DomUtils.getChildElementsByTagName(element, "stream-debugger");
		for (Element debuggerElement : debuggerElements) {
			callbacks.add(parseSiddhiStreamDebugger(debuggerElement, context));
		}

		// Process Groovy stream processor callbacks.
		List<Element> groovyElements = DomUtils.getChildElementsByTagName(element, "groovy-stream-processor");
		for (Element groovyElement : groovyElements) {
			callbacks.add(parseSiddhiGroovyStreamProcessor(groovyElement, context));
		}

		query.addPropertyValue("callbacks", callbacks);

		return query.getBeanDefinition();
	}

	/**
	 * Parse a Siddhi {@link StreamDebugger} element.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected AbstractBeanDefinition parseSiddhiStreamDebugger(Element element, ParserContext context) {
		BeanDefinitionBuilder debugger = BeanDefinitionBuilder.rootBeanDefinition(StreamDebugger.class);

		Attr stream = element.getAttributeNode("stream");
		if (stream == null) {
			throw new RuntimeException("Stream attribute is required for debug-callback.");
		}
		debugger.addPropertyValue("streamId", stream.getValue());

		return debugger.getBeanDefinition();
	}

	/**
	 * Parse a Siddhi {@link GroovyStreamProcessor} element.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected AbstractBeanDefinition parseSiddhiGroovyStreamProcessor(Element element,
			ParserContext context) {
		BeanDefinitionBuilder groovy = BeanDefinitionBuilder.rootBeanDefinition(GroovyStreamProcessor.class);
		groovy.addPropertyReference("configuration", GroovyConfiguration.GROOVY_CONFIGURATION_BEAN);

		Attr stream = element.getAttributeNode("stream");
		if (stream == null) {
			throw new RuntimeException("Stream attribute is required for groovy-stream-processor.");
		}
		groovy.addPropertyValue("streamId", stream.getValue());

		Attr scriptPath = element.getAttributeNode("scriptPath");
		if (scriptPath == null) {
			throw new RuntimeException("The scriptPath attribute is required for groovy-stream-processor.");
		}
		groovy.addPropertyValue("scriptPath", scriptPath.getValue());

		return groovy.getBeanDefinition();
	}

	/**
	 * Parse filters associated with an outbound processor.
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
				if (!IConfigurationElements.SITEWHERE_CE_TENANT_NS.equals(child.getNamespaceURI())) {
					NamespaceHandler nested =
							context.getReaderContext().getNamespaceHandlerResolver().resolve(
									child.getNamespaceURI());
					if (nested != null) {
						nested.parse(child, context);
						continue;
					} else {
						throw new RuntimeException(
								"Invalid nested element found in 'filters' section: " + child.toString());
					}
				}
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
			FilterOperation op =
					"include".equals(operation.getValue()) ? FilterOperation.Include
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
			FilterOperation op =
					"include".equals(operation.getValue()) ? FilterOperation.Include
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
		filter.addPropertyReference("configuration", GroovyConfiguration.GROOVY_CONFIGURATION_BEAN);

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
	protected AbstractBeanDefinition parseAllWithSpecificationMulticaster(Element element,
			ParserContext context) {
		BeanDefinitionBuilder multicaster =
				BeanDefinitionBuilder.rootBeanDefinition(AllWithSpecificationStringMulticaster.class);
		multicaster.addPropertyReference("configuration", GroovyConfiguration.GROOVY_CONFIGURATION_BEAN);

		Attr specification = element.getAttributeNode("specification");
		if (specification == null) {
			throw new RuntimeException(
					"Attribute 'specification' is required for all-with-specification-multicaster.");
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
		builder.addPropertyReference("configuration", GroovyConfiguration.GROOVY_CONFIGURATION_BEAN);

		Attr scriptPath = element.getAttributeNode("scriptPath");
		if (scriptPath != null) {
			builder.addPropertyValue("scriptPath", scriptPath.getValue());
		}

		return builder.getBeanDefinition();
	}

	/**
	 * Expected child elements.
	 * 
	 * @author Derek
	 */
	public static enum Elements {

		/** Reference to custom inbound event processor */
		OutboundEventProcessor("outbound-event-processor"),

		/** Tests location values against zones */
		ZoneTestEventProcessor("zone-test-event-processor"),

		/** Sends outbound events to an MQTT topic */
		MqttEventProcessor("mqtt-event-processor"),

		/** Sends outbound events to a RabbitMQ exchange */
		RabbitMqEventProcessor("rabbit-mq-event-processor"),

		/** Sends outbound events over Hazelcast topics */
		HazelcastEventProcessor("hazelcast-event-processor"),

		/** Indexes outbound events in Apache Solr */
		SolrEventProcessor("solr-event-processor"),

		/** Sends outbound events to an Azure EventHub */
		AzureEventHubEventProcessor("azure-eventhub-event-processor"),

		/** Sends outbound events to an Amazon SQS queue */
		AmazonSqsEventProcessor("amazon-sqs-event-processor"),

		/** Sends outbound events to InitialState.com */
		InitialStateEventProcessor("initial-state-event-processor"),

		/** Sends outbound events to dweet.io */
		DweetIoEventProcessor("dweet-io-event-processor"),

		/** DEPRECATED: Use 'command-delivery-event-processor' */
		ProvisioningEventProcessor("provisioning-event-processor"),

		/** Outbound event processor that delivers commands via communication subsystem */
		CommandDeliveryEventProcessor("command-delivery-event-processor"),

		/** Outbound event processor that delivers event to external WSO2 CEP instance */
		Wso2CepEventProcessor("wso2-cep-event-processor"),

		/** Outbound event processor that uses Siddhi for complex event processing */
		SiddhiEventProcessor("siddhi-event-processor"),

		/** Outbound event processor that delegates to a Groovy script */
		GroovyEventProcessor("groovy-event-processor");

		/** Event code */
		private String localName;

		private Elements(String localName) {
			this.localName = localName;
		}

		public static Elements getByLocalName(String localName) {
			for (Elements value : Elements.values()) {
				if (value.getLocalName().equals(localName)) {
					return value;
				}
			}
			return null;
		}

		public String getLocalName() {
			return localName;
		}

		public void setLocalName(String localName) {
			this.localName = localName;
		}
	}

	/**
	 * Expected filter elements.
	 * 
	 * @author Derek
	 */
	public static enum Filters {

		/** Include or exclude events for a site */
		SiteFilter("site-filter"),

		/** Include or exclude events for a specification */
		SpecificationFilter("specification-filter"),

		/** Include or exclude events based on running a script */
		GroovyFilter("groovy-filter");

		/** Event code */
		private String localName;

		private Filters(String localName) {
			this.localName = localName;
		}

		public static Filters getByLocalName(String localName) {
			for (Filters value : Filters.values()) {
				if (value.getLocalName().equals(localName)) {
					return value;
				}
			}
			return null;
		}

		public String getLocalName() {
			return localName;
		}

		public void setLocalName(String localName) {
			this.localName = localName;
		}
	}

	/**
	 * Expected multicaster elements.
	 * 
	 * @author Derek
	 */
	public static enum Multicasters {

		/** Multicasts to all devices with a given specification */
		AllWithSpecificationMulticaster("all-with-specification-multicaster");

		/** Event code */
		private String localName;

		private Multicasters(String localName) {
			this.localName = localName;
		}

		public static Multicasters getByLocalName(String localName) {
			for (Multicasters value : Multicasters.values()) {
				if (value.getLocalName().equals(localName)) {
					return value;
				}
			}
			return null;
		}

		public String getLocalName() {
			return localName;
		}

		public void setLocalName(String localName) {
			this.localName = localName;
		}
	}

	/**
	 * Expected multicaster elements.
	 * 
	 * @author Derek
	 */
	public static enum RouteBuilders {

		/** Uses Groovy script to build routes */
		GroovyRouteBuilder("groovy-route-builder");

		/** Event code */
		private String localName;

		private RouteBuilders(String localName) {
			this.localName = localName;
		}

		public static RouteBuilders getByLocalName(String localName) {
			for (RouteBuilders value : RouteBuilders.values()) {
				if (value.getLocalName().equals(localName)) {
					return value;
				}
			}
			return null;
		}

		public String getLocalName() {
			return localName;
		}

		public void setLocalName(String localName) {
			this.localName = localName;
		}
	}
}
