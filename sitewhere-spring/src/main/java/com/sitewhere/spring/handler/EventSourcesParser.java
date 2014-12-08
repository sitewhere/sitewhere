/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spring.handler;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.device.provisioning.BinaryInboundEventSource;
import com.sitewhere.device.provisioning.json.JsonBatchEventDecoder;
import com.sitewhere.device.provisioning.mqtt.MqttInboundEventReceiver;
import com.sitewhere.device.provisioning.socket.BinarySocketInboundEventReceiver;
import com.sitewhere.device.provisioning.socket.ReadAllInteractionHandler;
import com.sitewhere.spi.device.provisioning.IInboundEventSource;
import com.sitewhere.spi.device.provisioning.socket.ISocketInteractionHandlerFactory;

/**
 * Parses the list of {@link IInboundEventSource} elements used in provisioning.
 * 
 * @author Derek
 */
public class EventSourcesParser {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(EventSourcesParser.class);

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
			if (!IConfigurationElements.SITEWHERE_COMMUNITY_NS.equals(child.getNamespaceURI())) {
				NamespaceHandler nested =
						context.getReaderContext().getNamespaceHandlerResolver().resolve(
								child.getNamespaceURI());
				if (nested != null) {
					nested.parse(child, context);
					continue;
				} else {
					throw new RuntimeException("Invalid nested element found in 'event-sources' section: "
							+ child.toString());
				}
			}
			Elements type = Elements.getByLocalName(child.getLocalName());
			if (type == null) {
				throw new RuntimeException("Unknown event source element: " + child.getLocalName());
			}
			switch (type) {
			case EventSource: {
				result.add(parseEventSource(child, context));
				break;
			}
			case ActiveMQEventSource: {
				result.add(parseActiveMQEventSource(child, context));
				break;
			}
			case SocketEventSource: {
				result.add(parseSocketEventSource(child, context));
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

		// Verify that a sourceId was provided and set it on the bean.
		parseEventSourceId(element, source);

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
		boolean hadDecoder = parseBinaryDecoder(element, context, source);
		if (!hadDecoder) {
			throw new RuntimeException("No event decoder specified for MQTT event source: "
					+ element.toString());
		}

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
	 * Parse an ActiveMQ event source.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected AbstractBeanDefinition parseActiveMQEventSource(Element element, ParserContext context) {
		BeanDefinitionBuilder source =
				BeanDefinitionBuilder.rootBeanDefinition(BinaryInboundEventSource.class);

		// Verify that a sourceId was provided and set it on the bean.
		parseEventSourceId(element, source);

		// Create ActiveMQ event receiver bean and register it.
		AbstractBeanDefinition receiver = createActiveMQEventReceiver(element);
		String receiverName = nameGenerator.generateBeanName(receiver, context.getRegistry());
		context.getRegistry().registerBeanDefinition(receiverName, receiver);

		// Create list with bean reference and add it as property.
		ManagedList<Object> list = new ManagedList<Object>();
		RuntimeBeanReference ref = new RuntimeBeanReference(receiverName);
		list.add(ref);
		source.addPropertyValue("inboundEventReceivers", list);

		// Add decoder reference.
		boolean hadDecoder = parseBinaryDecoder(element, context, source);
		if (!hadDecoder) {
			throw new RuntimeException("No event decoder specified for ActiveMQ event source: "
					+ element.toString());
		}

		return source.getBeanDefinition();
	}

	/**
	 * Create ActiveMQ event receiver from XML element.
	 * 
	 * @param element
	 * @return
	 */
	protected AbstractBeanDefinition createActiveMQEventReceiver(Element element) {
		BeanDefinitionBuilder mq =
				BeanDefinitionBuilder.rootBeanDefinition("com.sitewhere.activemq.ActiveMQInboundEventReceiver");

		Attr sourceId = element.getAttributeNode("sourceId");
		if (sourceId == null) {
			throw new RuntimeException("ActiveMQ 'sourceId' attribute not provided.");
		}
		mq.addPropertyValue("brokerName", sourceId.getValue());

		Attr transportUri = element.getAttributeNode("transportUri");
		if (transportUri == null) {
			throw new RuntimeException("ActiveMQ 'transportUri' attribute not provided.");
		}
		mq.addPropertyValue("transportUri", transportUri.getValue());

		Attr queueName = element.getAttributeNode("queueName");
		if (queueName == null) {
			throw new RuntimeException("ActiveMQ 'queueName' attribute not provided.");
		}
		mq.addPropertyValue("queueName", queueName.getValue());

		Attr dataDirectory = element.getAttributeNode("dataDirectory");
		if (dataDirectory != null) {
			mq.addPropertyValue("dataDirectory", dataDirectory.getValue());
		}

		Attr numConsumers = element.getAttributeNode("numConsumers");
		if (numConsumers != null) {
			mq.addPropertyValue("numConsumers", numConsumers.getValue());
		}

		return mq.getBeanDefinition();
	}

	/**
	 * Parse a socket event source.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected AbstractBeanDefinition parseSocketEventSource(Element element, ParserContext context) {
		BeanDefinitionBuilder source =
				BeanDefinitionBuilder.rootBeanDefinition(BinaryInboundEventSource.class);

		// Verify that a sourceId was provided and set it on the bean.
		parseEventSourceId(element, source);

		// Create socket event receiver bean and register it.
		AbstractBeanDefinition receiver = createSocketEventReceiver(element, context);
		String receiverName = nameGenerator.generateBeanName(receiver, context.getRegistry());
		context.getRegistry().registerBeanDefinition(receiverName, receiver);

		// Create list with bean reference and add it as property.
		ManagedList<Object> list = new ManagedList<Object>();
		RuntimeBeanReference ref = new RuntimeBeanReference(receiverName);
		list.add(ref);
		source.addPropertyValue("inboundEventReceivers", list);

		// Add decoder reference.
		boolean hadDecoder = parseBinaryDecoder(element, context, source);
		if (!hadDecoder) {
			throw new RuntimeException("No event decoder specified for socket event source: "
					+ element.toString());
		}

		return source.getBeanDefinition();
	}

	/**
	 * Create socket event receiver from XML element.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected AbstractBeanDefinition createSocketEventReceiver(Element element, ParserContext context) {
		BeanDefinitionBuilder socket =
				BeanDefinitionBuilder.rootBeanDefinition(BinarySocketInboundEventReceiver.class);

		Attr port = element.getAttributeNode("port");
		if (port != null) {
			socket.addPropertyValue("port", port.getValue());
		}

		Attr numThreads = element.getAttributeNode("numThreads");
		if (numThreads != null) {
			socket.addPropertyValue("numThreads", numThreads.getValue());
		}

		// Parse configured socket interaction handler factory if available.
		parseSocketInteractionHandlerFactory(element, context, socket);

		return socket.getBeanDefinition();
	}

	/**
	 * Parse a socket interaction handler factory from the list of possibilities.
	 * 
	 * @param decoder
	 * @param context
	 * @param source
	 * @return
	 */
	protected boolean parseSocketInteractionHandlerFactory(Element parent, ParserContext context,
			BeanDefinitionBuilder source) {
		List<Element> children = DomUtils.getChildElements(parent);
		for (Element child : children) {
			if (!IConfigurationElements.SITEWHERE_COMMUNITY_NS.equals(child.getNamespaceURI())) {
				NamespaceHandler nested =
						context.getReaderContext().getNamespaceHandlerResolver().resolve(
								child.getNamespaceURI());
				if (nested != null) {
					BeanDefinition factoryBean = nested.parse(child, context);
					String factoryName = nameGenerator.generateBeanName(factoryBean, context.getRegistry());
					context.getRegistry().registerBeanDefinition(factoryName, factoryBean);
					source.addPropertyReference("handlerFactory", factoryName);
					return true;
				} else {
					continue;
				}
			}
			BinarySocketInteractionHandlers type =
					BinarySocketInteractionHandlers.getByLocalName(child.getLocalName());
			if (type == null) {
				continue;
			}
			switch (type) {
			case InteractionHandlerFactoryReference: {
				parseInteractionHandlerFactoryReference(parent, child, context, source);
				return true;
			}
			case ReadAllInteractionHandlerFactory: {
				parseReadAllFactory(parent, child, context, source);
				return true;
			}
			}
		}
		return false;
	}

	/**
	 * Parse reference to an external {@link ISocketInteractionHandlerFactory} bean.
	 * 
	 * @param parent
	 * @param decoder
	 * @param context
	 * @param source
	 */
	protected void parseInteractionHandlerFactoryReference(Element parent, Element decoder,
			ParserContext context, BeanDefinitionBuilder source) {
		Attr factoryRef = decoder.getAttributeNode("ref");
		if (factoryRef == null) {
			throw new RuntimeException("Socket interaction handler factory 'ref' attribute is required.");
		}
		LOGGER.debug("Configuring reference to " + factoryRef.getValue() + " for " + parent.getLocalName());
		source.addPropertyReference("handlerFactory", factoryRef.getValue());
	}

	/**
	 * Parse configuration for {@link ReadAllInteractionHandler} factory implementation.
	 * 
	 * @param parent
	 * @param decoder
	 * @param context
	 * @param source
	 */
	protected void parseReadAllFactory(Element parent, Element decoder, ParserContext context,
			BeanDefinitionBuilder source) {
		LOGGER.debug("Configuring 'read all' socket interaction handler factory for " + parent.getLocalName());
		BeanDefinitionBuilder builder =
				BeanDefinitionBuilder.rootBeanDefinition(ReadAllInteractionHandler.Factory.class);
		AbstractBeanDefinition bean = builder.getBeanDefinition();
		String name = nameGenerator.generateBeanName(bean, context.getRegistry());
		context.getRegistry().registerBeanDefinition(name, bean);
		source.addPropertyReference("handlerFactory", name);
	}

	/**
	 * Parse a binary decoder from the list of possibilities.
	 * 
	 * @param decoder
	 * @param context
	 * @param source
	 * @return
	 */
	protected boolean parseBinaryDecoder(Element parent, ParserContext context, BeanDefinitionBuilder source) {
		List<Element> children = DomUtils.getChildElements(parent);
		for (Element child : children) {
			if (!IConfigurationElements.SITEWHERE_COMMUNITY_NS.equals(child.getNamespaceURI())) {
				NamespaceHandler nested =
						context.getReaderContext().getNamespaceHandlerResolver().resolve(
								child.getNamespaceURI());
				if (nested != null) {
					BeanDefinition decoderBean = nested.parse(child, context);
					String decoderName = nameGenerator.generateBeanName(decoderBean, context.getRegistry());
					context.getRegistry().registerBeanDefinition(decoderName, decoderBean);
					source.addPropertyReference("deviceEventDecoder", decoderName);
					return true;
				} else {
					continue;
				}
			}
			BinaryDecoders type = BinaryDecoders.getByLocalName(child.getLocalName());
			if (type == null) {
				continue;
			}
			switch (type) {
			case ProtobufDecoder: {
				parseProtobufDecoder(parent, child, context, source);
				return true;
			}
			case JsonDecoder: {
				parseJsonDecoder(parent, child, context, source);
				return true;
			}
			case EventDecoder: {
				parseDecoderRef(parent, child, context, source);
				return true;
			}
			}
		}
		return false;
	}

	/**
	 * Create parser for SiteWhere Google Protocol Buffer format.
	 * 
	 * @param parent
	 * @param decoder
	 * @param context
	 * @param source
	 */
	protected void parseProtobufDecoder(Element parent, Element decoder, ParserContext context,
			BeanDefinitionBuilder source) {
		LOGGER.debug("Configuring SiteWhere Google Protocol Buffer event decoder for "
				+ parent.getLocalName());
		BeanDefinitionBuilder builder =
				BeanDefinitionBuilder.rootBeanDefinition("com.sitewhere.device.provisioning.protobuf.ProtobufDeviceEventDecoder");
		AbstractBeanDefinition bean = builder.getBeanDefinition();
		String name = nameGenerator.generateBeanName(bean, context.getRegistry());
		context.getRegistry().registerBeanDefinition(name, bean);
		source.addPropertyReference("deviceEventDecoder", name);
	}

	/**
	 * Create parser for SiteWhere Google Protocol Buffer format.
	 * 
	 * @param parent
	 * @param decoder
	 * @param context
	 * @param source
	 */
	protected void parseJsonDecoder(Element parent, Element decoder, ParserContext context,
			BeanDefinitionBuilder source) {
		LOGGER.debug("Configuring SiteWhere JSON batch event decoder for " + parent.getLocalName());
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(JsonBatchEventDecoder.class);
		AbstractBeanDefinition bean = builder.getBeanDefinition();
		String name = nameGenerator.generateBeanName(bean, context.getRegistry());
		context.getRegistry().registerBeanDefinition(name, bean);
		source.addPropertyReference("deviceEventDecoder", name);
	}

	/**
	 * Create parser for SiteWhere Google Protocol Buffer format.
	 * 
	 * @param parent
	 * @param decoder
	 * @param context
	 * @param source
	 */
	protected void parseDecoderRef(Element parent, Element decoder, ParserContext context,
			BeanDefinitionBuilder source) {
		Attr decoderRef = decoder.getAttributeNode("ref");
		if (decoderRef == null) {
			throw new RuntimeException("Event decoder 'ref' attribute is required.");
		}
		LOGGER.debug("Configuring reference to " + decoderRef.getValue() + " for " + parent.getLocalName());
		source.addPropertyReference("deviceEventDecoder", decoderRef.getValue());
	}

	/**
	 * Verify that a 'sourceId' was provided and set it on the bean.
	 * 
	 * @param element
	 * @param builder
	 */
	protected void parseEventSourceId(Element element, BeanDefinitionBuilder builder) {
		Attr sourceId = element.getAttributeNode("sourceId");
		if (sourceId == null) {
			throw new RuntimeException("No 'sourceId' attribute specified for event source: "
					+ element.toString());
		}
		builder.addPropertyValue("sourceId", sourceId.getValue());
	}

	/**
	 * Expected child elements.
	 * 
	 * @author Derek
	 */
	public static enum Elements {

		/** Event source */
		EventSource("event-source"),

		/** ActiveMQ event source */
		ActiveMQEventSource("activemq-event-source"),

		/** Socket event source */
		SocketEventSource("socket-event-source"),

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

	/**
	 * Expected binary decoder elements.
	 * 
	 * @author Derek
	 */
	public static enum BinaryDecoders {

		/** SiteWhere Google Protocol Buffer decoder */
		ProtobufDecoder("protobuf-event-decoder"),

		/** SiteWhere JSON batch decoder */
		JsonDecoder("json-event-decoder"),

		/** Reference to externally defined event decoder */
		EventDecoder("event-decoder");

		/** Event code */
		private String localName;

		private BinaryDecoders(String localName) {
			this.localName = localName;
		}

		public static BinaryDecoders getByLocalName(String localName) {
			for (BinaryDecoders value : BinaryDecoders.values()) {
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
	 * Expected binary socket interaction handler elements.
	 * 
	 * @author Derek
	 */
	public static enum BinarySocketInteractionHandlers {

		/** Reference to a socket interaction handler factory defined in a Spring bean */
		InteractionHandlerFactoryReference("interaction-handler-factory"),

		/** Produces socket interaction handlers that read all data from the client socket */
		ReadAllInteractionHandlerFactory("read-all-interaction-handler-factory");

		/** Event code */
		private String localName;

		private BinarySocketInteractionHandlers(String localName) {
			this.localName = localName;
		}

		public static BinarySocketInteractionHandlers getByLocalName(String localName) {
			for (BinarySocketInteractionHandlers value : BinarySocketInteractionHandlers.values()) {
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
