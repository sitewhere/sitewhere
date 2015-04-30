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
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.azure.device.communication.EventHubOutboundEventProcessor;
import com.sitewhere.device.communication.DeviceCommandEventProcessor;
import com.sitewhere.device.event.processor.DefaultOutboundEventProcessorChain;
import com.sitewhere.geospatial.ZoneTest;
import com.sitewhere.geospatial.ZoneTestEventProcessor;
import com.sitewhere.groovy.GroovyConfiguration;
import com.sitewhere.hazelcast.HazelcastEventProcessor;
import com.sitewhere.hazelcast.SiteWhereHazelcastConfiguration;
import com.sitewhere.server.SiteWhereServerBeans;
import com.sitewhere.siddhi.GroovyStreamProcessor;
import com.sitewhere.siddhi.SiddhiEventProcessor;
import com.sitewhere.siddhi.SiddhiQuery;
import com.sitewhere.siddhi.StreamDebugger;
import com.sitewhere.solr.SiteWhereSolrConfiguration;
import com.sitewhere.solr.SolrDeviceEventProcessor;
import com.sitewhere.spi.device.event.AlertLevel;
import com.sitewhere.spi.geospatial.ZoneContainment;

/**
 * Parses configuration data from SiteWhere outbound processing chain section.
 * 
 * @author Derek
 */
public class OutboundProcessingChainParser extends AbstractBeanDefinitionParser {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#parseInternal
	 * (org.w3c.dom.Element, org.springframework.beans.factory.xml.ParserContext)
	 */
	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
		BeanDefinitionBuilder chain =
				BeanDefinitionBuilder.rootBeanDefinition(DefaultOutboundEventProcessorChain.class);
		List<Element> dsChildren = DomUtils.getChildElements(element);
		List<Object> processors = new ManagedList<Object>();
		for (Element child : dsChildren) {
			Elements type = Elements.getByLocalName(child.getLocalName());
			if (type == null) {
				throw new RuntimeException("Unknown inbound processing chain element: "
						+ child.getLocalName());
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
			}
		}
		chain.addPropertyValue("processors", processors);
		context.getRegistry().registerBeanDefinition(SiteWhereServerBeans.BEAN_OUTBOUND_PROCESSOR_CHAIN,
				chain.getBeanDefinition());
		return null;
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
		return processor.getBeanDefinition();
	}

	/**
	 * Parses configuration for Azure EventHub event processor.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected AbstractBeanDefinition parseAzureEventHubEventProcessor(Element element, ParserContext context) {
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
	protected AbstractBeanDefinition parseCommandDeliveryEventProcessor(Element element, ParserContext context) {
		BeanDefinitionBuilder processor =
				BeanDefinitionBuilder.rootBeanDefinition(DeviceCommandEventProcessor.class);

		Attr numThreads = element.getAttributeNode("numThreads");
		if (numThreads != null) {
			processor.addPropertyValue("numThreads", Integer.parseInt(numThreads.getValue()));
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
	protected AbstractBeanDefinition parseSiddhiGroovyStreamProcessor(Element element, ParserContext context) {
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
	 * Expected child elements.
	 * 
	 * @author Derek
	 */
	public static enum Elements {

		/** Reference to custom inbound event processor */
		OutboundEventProcessor("outbound-event-processor"),

		/** Tests location values against zones */
		ZoneTestEventProcessor("zone-test-event-processor"),

		/** Sends outbound events over Hazelcast topics */
		HazelcastEventProcessor("hazelcast-event-processor"),

		/** Indexes outbound events in Apache Solr */
		SolrEventProcessor("solr-event-processor"),

		/** Sends outbound events to an Azure EventHub */
		AzureEventHubEventProcessor("azure-eventhub-event-processor"),

		/** DEPRECATED: Use 'command-delivery-event-processor' */
		ProvisioningEventProcessor("provisioning-event-processor"),

		/** Outbound event processor that delivers commands via communication subsystem */
		CommandDeliveryEventProcessor("command-delivery-event-processor"),

		/** Outbound event processor that uses Siddhi for complex event processing */
		SiddhiEventProcessor("siddhi-event-processor");

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
}
