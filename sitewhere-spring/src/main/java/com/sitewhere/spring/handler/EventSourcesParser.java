/*
 * EventSourcesParser.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
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
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.device.provisioning.BinaryInboundEventSource;
import com.sitewhere.device.provisioning.mqtt.MqttInboundEventReceiver;
import com.sitewhere.spi.device.provisioning.IInboundEventSource;

/**
 * Parses the list of {@link IInboundEventSource} elements used in provisioning.
 * 
 * @author Derek
 */
public class EventSourcesParser {

	/** Used to generate unique names for nested beans */
	private DefaultBeanNameGenerator nameGenerator = new DefaultBeanNameGenerator();

	/**
	 * Parse the list of event sources.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected ManagedList<?> parse(Element element, ParserContext context) {
		ManagedList<Object> result = new ManagedList<Object>();
		List<Element> children = DomUtils.getChildElements(element);
		for (Element child : children) {
			Elements type = Elements.getByLocalName(child.getLocalName());
			if (type == null) {
				throw new RuntimeException("Unknown event source element: " + child.getLocalName());
			}
			switch (type) {
			case EventSource: {
				result.add(parseEventSource(child, context));
				break;
			}
			case MqttEventSource: {
				result.add(parseMqttEventSource(child, context));
				break;
			}
			}
		}
		return result;
	}

	/**
	 * Parse an event source reference.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected RuntimeBeanReference parseEventSource(Element element, ParserContext context) {
		Attr ref = element.getAttributeNode("ref");
		if (ref != null) {
			return new RuntimeBeanReference(ref.getValue());
		}
		throw new RuntimeException("Event source reference does not have ref defined.");
	}

	/**
	 * Parse an MQTT event source.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected AbstractBeanDefinition parseMqttEventSource(Element element, ParserContext context) {
		BeanDefinitionBuilder source =
				BeanDefinitionBuilder.rootBeanDefinition(BinaryInboundEventSource.class);

		// Create MQTT event receiver bean and register it.
		AbstractBeanDefinition receiver = createMqttEventReceiver(element);
		String receiverName = nameGenerator.generateBeanName(receiver, context.getRegistry());
		context.getRegistry().registerBeanDefinition(receiverName, receiver);

		// Create list with bean reference and add it as property.
		ManagedList<Object> list = new ManagedList<Object>();
		RuntimeBeanReference ref = new RuntimeBeanReference(receiverName);
		list.add(ref);
		source.addPropertyValue("inboundEventReceivers", list);

		// Add decoder reference.
		Element decoder = DomUtils.getChildElementByTagName(element, "decoder");
		if (decoder == null) {
			throw new RuntimeException("Command decoder required but not specified.");
		}
		Attr decoderRef = decoder.getAttributeNode("ref");
		if (decoderRef == null) {
			throw new RuntimeException("Command decoder ref required but not specified.");
		}
		source.addPropertyReference("deviceEventDecoder", decoderRef.getValue());

		return source.getBeanDefinition();
	}

	/**
	 * Create MQTT event receiver from XML element.
	 * 
	 * @param element
	 * @return
	 */
	protected AbstractBeanDefinition createMqttEventReceiver(Element element) {
		BeanDefinitionBuilder mqtt = BeanDefinitionBuilder.rootBeanDefinition(MqttInboundEventReceiver.class);

		Attr hostname = element.getAttributeNode("hostname");
		if (hostname == null) {
			throw new RuntimeException("MQTT hostname attribute not provided.");
		}
		mqtt.addPropertyValue("hostname", hostname.getValue());

		Attr port = element.getAttributeNode("port");
		if (port == null) {
			throw new RuntimeException("MQTT port attribute not provided.");
		}
		mqtt.addPropertyValue("port", port.getValue());

		Attr topic = element.getAttributeNode("topic");
		if (topic == null) {
			throw new RuntimeException("MQTT topic attribute not provided.");
		}
		mqtt.addPropertyValue("topic", topic.getValue());

		return mqtt.getBeanDefinition();
	}

	/**
	 * Expected child elements.
	 * 
	 * @author Derek
	 */
	public static enum Elements {

		/** Event source */
		EventSource("event-source"),

		/** Event source */
		MqttEventSource("mqtt-event-source");

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
