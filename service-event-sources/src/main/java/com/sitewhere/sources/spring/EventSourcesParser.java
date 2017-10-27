/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.spring;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.device.communication.protobuf.ProtobufDeviceEventDecoder;
import com.sitewhere.microservice.spi.spring.EventSourcesBeans;
import com.sitewhere.sources.BinaryInboundEventSource;
import com.sitewhere.sources.DecodedInboundEventSource;
import com.sitewhere.sources.EventSourcesManager;
import com.sitewhere.sources.StringInboundEventSource;
import com.sitewhere.sources.activemq.ActiveMQClientEventReceiver;
import com.sitewhere.sources.activemq.ActiveMQInboundEventReceiver;
import com.sitewhere.sources.azure.EventHubInboundEventReceiver;
import com.sitewhere.sources.coap.CoapServerEventReceiver;
import com.sitewhere.sources.decoder.GroovyEventDecoder;
import com.sitewhere.sources.decoder.GroovyStringEventDecoder;
import com.sitewhere.sources.decoder.composite.BinaryCompositeDeviceEventDecoder;
import com.sitewhere.sources.decoder.composite.DeviceSpecificationDecoderChoice;
import com.sitewhere.sources.decoder.composite.GroovyMessageMetadataExtractor;
import com.sitewhere.sources.decoder.debug.EchoStringDecoder;
import com.sitewhere.sources.decoder.json.JsonBatchEventDecoder;
import com.sitewhere.sources.decoder.json.JsonDeviceRequestDecoder;
import com.sitewhere.sources.deduplicator.AlternateIdDeduplicator;
import com.sitewhere.sources.deduplicator.GroovyEventDeduplicator;
import com.sitewhere.sources.hazelcast.HazelcastQueueReceiver;
import com.sitewhere.sources.mqtt.MqttInboundEventReceiver;
import com.sitewhere.sources.rabbitmq.RabbitMqInboundEventReceiver;
import com.sitewhere.sources.rest.PollingRestInboundEventReceiver;
import com.sitewhere.sources.socket.BinarySocketInboundEventReceiver;
import com.sitewhere.sources.socket.GroovySocketInteractionHandler;
import com.sitewhere.sources.socket.HttpInteractionHandler;
import com.sitewhere.sources.socket.ReadAllInteractionHandler;
import com.sitewhere.sources.websocket.BinaryWebSocketEventReceiver;
import com.sitewhere.sources.websocket.StringWebSocketEventReceiver;
import com.sitewhere.spi.device.communication.IInboundEventReceiver;
import com.sitewhere.spi.device.communication.IInboundEventSource;
import com.sitewhere.spi.device.communication.socket.ISocketInteractionHandlerFactory;
import com.sitewhere.spring.parser.IEventSourcesParser.BinaryDecoders;
import com.sitewhere.spring.parser.IEventSourcesParser.BinarySocketInteractionHandlers;
import com.sitewhere.spring.parser.IEventSourcesParser.CompositeDecoderChoiceElements;
import com.sitewhere.spring.parser.IEventSourcesParser.CompositeDecoderMetadataExtractorElements;
import com.sitewhere.spring.parser.IEventSourcesParser.Deduplicators;
import com.sitewhere.spring.parser.IEventSourcesParser.Elements;
import com.sitewhere.spring.parser.IEventSourcesParser.StringDecoders;

/**
 * Parses the list of {@link IInboundEventSource} elements used in the
 * communication subsystem.
 * 
 * @author Derek
 */
public class EventSourcesParser extends AbstractBeanDefinitionParser {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Used to generate unique names for nested beans */
    private DefaultBeanNameGenerator nameGenerator = new DefaultBeanNameGenerator();

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#
     * parseInternal (org.w3c.dom.Element,
     * org.springframework.beans.factory.xml.ParserContext)
     */
    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
	ManagedList<Object> sources = new ManagedList<Object>();
	List<Element> children = DomUtils.getChildElements(element);
	for (Element child : children) {
	    Elements type = Elements.getByLocalName(child.getLocalName());
	    if (type == null) {
		throw new RuntimeException("Unknown event source element: " + child.getLocalName());
	    }
	    switch (type) {
	    case EventSource: {
		sources.add(parseEventSource(child, context));
		break;
	    }
	    case ActiveMQEventSource: {
		sources.add(parseActiveMQEventSource(child, context));
		break;
	    }
	    case ActiveMQClientEventSource: {
		sources.add(parseActiveMQClientEventSource(child, context));
		break;
	    }
	    case AzureEventHubEventSource: {
		sources.add(parseAzureEventHubEventSource(child, context));
		break;
	    }
	    case CoapServerEventSource: {
		sources.add(parseCoapServerEventSource(child, context));
		break;
	    }
	    case HazelcastQueueEventSource: {
		sources.add(parseHazelcastQueueEventSource(child, context));
		break;
	    }
	    case MqttEventSource: {
		sources.add(parseMqttEventSource(child, context));
		break;
	    }
	    case PollingRestEventSource: {
		sources.add(parsePollingRestEventSource(child, context));
		break;
	    }
	    case RabbitMqEventSource: {
		sources.add(parseRabbitMqEventSource(child, context));
		break;
	    }
	    case SocketEventSource: {
		sources.add(parseSocketEventSource(child, context));
		break;
	    }
	    case WebSocketEventSource: {
		sources.add(parseWebSocketEventSource(child, context));
		break;
	    }
	    }
	}

	// Build event sources manager and inject the list of beans.
	BeanDefinitionBuilder manager = BeanDefinitionBuilder.rootBeanDefinition(EventSourcesManager.class);
	manager.addPropertyValue("eventSources", sources);
	context.getRegistry().registerBeanDefinition(EventSourcesBeans.BEAN_EVENT_SOURCES_MANAGER,
		manager.getBeanDefinition());

	return null;
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
	BeanDefinitionBuilder source = BeanDefinitionBuilder.rootBeanDefinition(BinaryInboundEventSource.class);

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
	    throw new RuntimeException("No event decoder specified for MQTT event source: " + element.toString());
	}

	// Parse deduplicator if configured.
	parseDeduplicator(element, context, source);

	return source.getBeanDefinition();
    }

    /**
     * Get implementation class for MQTT event receiver.
     * 
     * @return
     */
    protected Class<? extends IInboundEventReceiver<byte[]>> getMqttEventReceiverImplementation() {
	return MqttInboundEventReceiver.class;
    }

    /**
     * Create MQTT event receiver from XML element.
     * 
     * @param element
     * @return
     */
    protected AbstractBeanDefinition createMqttEventReceiver(Element element) {
	BeanDefinitionBuilder mqtt = BeanDefinitionBuilder.rootBeanDefinition(getMqttEventReceiverImplementation());

	Attr protocol = element.getAttributeNode("protocol");
	if (protocol != null) {
	    mqtt.addPropertyValue("protocol", protocol.getValue());
	}

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

	Attr username = element.getAttributeNode("username");
	if (username != null) {
	    mqtt.addPropertyValue("username", username.getValue());
	}

	Attr password = element.getAttributeNode("password");
	if (password != null) {
	    mqtt.addPropertyValue("password", password.getValue());
	}

	Attr topic = element.getAttributeNode("topic");
	if (topic == null) {
	    throw new RuntimeException("MQTT topic attribute not provided.");
	}
	mqtt.addPropertyValue("topic", topic.getValue());

	Attr trustStorePath = element.getAttributeNode("trustStorePath");
	if (trustStorePath != null) {
	    mqtt.addPropertyValue("trustStorePath", trustStorePath.getValue());
	}

	Attr trustStorePassword = element.getAttributeNode("trustStorePassword");
	if (trustStorePassword != null) {
	    mqtt.addPropertyValue("trustStorePassword", trustStorePassword.getValue());
	}

	Attr keyStorePath = element.getAttributeNode("keyStorePath");
	if (keyStorePath != null) {
	    mqtt.addPropertyValue("keyStorePath", keyStorePath.getValue());
	}

	Attr keyStorePassword = element.getAttributeNode("keyStorePassword");
	if (keyStorePassword != null) {
	    mqtt.addPropertyValue("keyStorePassword", keyStorePassword.getValue());
	}

	return mqtt.getBeanDefinition();
    }

    /**
     * Parse an RabbitMQ event source.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseRabbitMqEventSource(Element element, ParserContext context) {
	BeanDefinitionBuilder source = BeanDefinitionBuilder.rootBeanDefinition(BinaryInboundEventSource.class);

	// Verify that a sourceId was provided and set it on the bean.
	parseEventSourceId(element, source);

	// Create event receiver bean and register it.
	AbstractBeanDefinition receiver = createRabbitMqEventReceiver(element);
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
	    throw new RuntimeException("No event decoder specified for RabbitMQ event source: " + element.toString());
	}

	// Parse deduplicator if configured.
	parseDeduplicator(element, context, source);

	return source.getBeanDefinition();
    }

    /**
     * Get implementation class for MQTT event receiver.
     * 
     * @return
     */
    protected Class<? extends IInboundEventReceiver<byte[]>> getRabbitMqEventReceiverImplementation() {
	return RabbitMqInboundEventReceiver.class;
    }

    /**
     * Create RabbitMQ event receiver from XML element.
     * 
     * @param element
     * @return
     */
    protected AbstractBeanDefinition createRabbitMqEventReceiver(Element element) {
	BeanDefinitionBuilder mqtt = BeanDefinitionBuilder.rootBeanDefinition(getRabbitMqEventReceiverImplementation());

	Attr connectionUri = element.getAttributeNode("connectionUri");
	if (connectionUri != null) {
	    mqtt.addPropertyValue("connectionUri", connectionUri.getValue());
	}

	Attr queueName = element.getAttributeNode("queueName");
	if (connectionUri != null) {
	    mqtt.addPropertyValue("queueName", queueName.getValue());
	}

	Attr durable = element.getAttributeNode("durable");
	if (durable != null) {
	    mqtt.addPropertyValue("durable", durable.getValue());
	}

	Attr numConsumers = element.getAttributeNode("numConsumers");
	if (connectionUri != null) {
	    mqtt.addPropertyValue("numConsumers", numConsumers.getValue());
	}

	return mqtt.getBeanDefinition();
    }

    /**
     * Parse an EventHub event source.
     *
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseAzureEventHubEventSource(Element element, ParserContext context) {
	BeanDefinitionBuilder source = BeanDefinitionBuilder.rootBeanDefinition(BinaryInboundEventSource.class);

	// Verify that a sourceId was provided and set it on the bean.
	parseEventSourceId(element, source);

	// Create EventHub event receiver bean and register it.
	AbstractBeanDefinition receiver = createEventHubEventReceiver(element);
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
	    throw new RuntimeException("No event decoder specified for EvenHub event source: " + element.toString());
	}

	// Parse deduplicator if configured.
	parseDeduplicator(element, context, source);

	return source.getBeanDefinition();
    }

    /**
     * Create EventHub event receiver from XML element.
     *
     * @param element
     * @return
     */
    protected AbstractBeanDefinition createEventHubEventReceiver(Element element) {
	BeanDefinitionBuilder eh = BeanDefinitionBuilder.rootBeanDefinition(EventHubInboundEventReceiver.class);

	Attr targetFqn = element.getAttributeNode("targetFqn");
	if (targetFqn == null) {
	    throw new RuntimeException("targetFqn attribute not provided.");
	}
	eh.addPropertyValue("targetFqn", targetFqn.getValue());

	Attr namespace = element.getAttributeNode("namespace");
	if (namespace == null) {
	    throw new RuntimeException("namespace attribute not provided.");
	}
	eh.addPropertyValue("namespace", namespace.getValue());

	Attr entityPath = element.getAttributeNode("entityPath");
	if (entityPath == null) {
	    throw new RuntimeException("entityPath attribute not provided.");
	}
	eh.addPropertyValue("entityPath", entityPath.getValue());

	Attr partitionCount = element.getAttributeNode("partitionCount");
	if (partitionCount == null) {
	    throw new RuntimeException("partitionCount attribute not provided.");
	}
	eh.addPropertyValue("partitionCount", partitionCount.getValue());

	Attr zkStateStore = element.getAttributeNode("zkStateStore");
	if (zkStateStore == null) {
	    throw new RuntimeException("zkStateStore attribute not provided.");
	}
	eh.addPropertyValue("zkStateStore", zkStateStore.getValue());

	Attr username = element.getAttributeNode("username");
	if (username == null) {
	    throw new RuntimeException("username attribute not provided.");
	}
	eh.addPropertyValue("username", username.getValue());

	Attr password = element.getAttributeNode("password");
	if (password == null) {
	    throw new RuntimeException("password attribute not provided.");
	}
	eh.addPropertyValue("password", password.getValue());

	return eh.getBeanDefinition();
    }

    /**
     * Parse an ActiveMQ event source.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseActiveMQEventSource(Element element, ParserContext context) {
	BeanDefinitionBuilder source = BeanDefinitionBuilder.rootBeanDefinition(BinaryInboundEventSource.class);

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
	    throw new RuntimeException("No event decoder specified for ActiveMQ event source: " + element.toString());
	}

	// Parse deduplicator if configured.
	parseDeduplicator(element, context, source);

	return source.getBeanDefinition();
    }

    /**
     * Get implementation class for ActiveMQ event receiver.
     * 
     * @return
     */
    protected Class<? extends IInboundEventReceiver<byte[]>> getActiveMQEventReceiverImplementation() {
	return ActiveMQInboundEventReceiver.class;
    }

    /**
     * Create ActiveMQ event receiver from XML element.
     * 
     * @param element
     * @return
     */
    protected AbstractBeanDefinition createActiveMQEventReceiver(Element element) {
	BeanDefinitionBuilder mq = BeanDefinitionBuilder.rootBeanDefinition(getActiveMQEventReceiverImplementation());

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
     * Parse an event source that uses ActiveMQ to connect to a remote broker
     * and ingest messages.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseActiveMQClientEventSource(Element element, ParserContext context) {
	BeanDefinitionBuilder source = BeanDefinitionBuilder.rootBeanDefinition(BinaryInboundEventSource.class);

	// Verify that a sourceId was provided and set it on the bean.
	parseEventSourceId(element, source);

	// Create ActiveMQ event receiver bean and register it.
	AbstractBeanDefinition receiver = createActiveMQClientEventReceiver(element);
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
	    throw new RuntimeException(
		    "No event decoder specified for ActiveMQ client event source: " + element.toString());
	}

	// Parse deduplicator if configured.
	parseDeduplicator(element, context, source);

	return source.getBeanDefinition();
    }

    /**
     * Get implementation class for ActiveMQ event receiver.
     * 
     * @return
     */
    protected Class<? extends IInboundEventReceiver<byte[]>> getActiveMQClientEventReceiverImplementation() {
	return ActiveMQClientEventReceiver.class;
    }

    /**
     * Create ActiveMQ event receiver from XML element.
     * 
     * @param element
     * @return
     */
    protected AbstractBeanDefinition createActiveMQClientEventReceiver(Element element) {
	BeanDefinitionBuilder mq = BeanDefinitionBuilder
		.rootBeanDefinition(getActiveMQClientEventReceiverImplementation());

	Attr remoteUri = element.getAttributeNode("remoteUri");
	if (remoteUri == null) {
	    throw new RuntimeException("ActiveMQ 'remoteUri' attribute not provided.");
	}
	mq.addPropertyValue("remoteUri", remoteUri.getValue());

	Attr queueName = element.getAttributeNode("queueName");
	if (queueName == null) {
	    throw new RuntimeException("ActiveMQ 'queueName' attribute not provided.");
	}
	mq.addPropertyValue("queueName", queueName.getValue());

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
	BeanDefinitionBuilder source = BeanDefinitionBuilder.rootBeanDefinition(BinaryInboundEventSource.class);

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
	    throw new RuntimeException("No event decoder specified for socket event source: " + element.toString());
	}

	// Parse deduplicator if configured.
	parseDeduplicator(element, context, source);

	return source.getBeanDefinition();
    }

    /**
     * Get implementation class for socket event receiver.
     * 
     * @return
     */
    protected Class<? extends IInboundEventReceiver<byte[]>> getSocketEventReceiverImplementation() {
	return BinarySocketInboundEventReceiver.class;
    }

    /**
     * Create socket event receiver from XML element.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition createSocketEventReceiver(Element element, ParserContext context) {
	BeanDefinitionBuilder socket = BeanDefinitionBuilder.rootBeanDefinition(getSocketEventReceiverImplementation());

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
     * Parse a socket interaction handler factory from the list of
     * possibilities.
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
	    BinarySocketInteractionHandlers type = BinarySocketInteractionHandlers.getByLocalName(child.getLocalName());
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
	    case HttpInteractionHandlerFactory: {
		parseHttpFactory(parent, child, context, source);
		return true;
	    }
	    case GroovySocketInteractionHandlerFactory: {
		parseGroovyFactory(parent, child, context, source);
		return true;
	    }
	    }
	}
	return false;
    }

    /**
     * Parse reference to an external {@link ISocketInteractionHandlerFactory}
     * bean.
     * 
     * @param parent
     * @param decoder
     * @param context
     * @param source
     */
    protected void parseInteractionHandlerFactoryReference(Element parent, Element decoder, ParserContext context,
	    BeanDefinitionBuilder source) {
	Attr factoryRef = decoder.getAttributeNode("ref");
	if (factoryRef == null) {
	    throw new RuntimeException("Socket interaction handler factory 'ref' attribute is required.");
	}
	LOGGER.debug("Configuring reference to " + factoryRef.getValue() + " for " + parent.getLocalName());
	source.addPropertyReference("handlerFactory", factoryRef.getValue());
    }

    /**
     * Parse configuration for {@link ReadAllInteractionHandler} factory
     * implementation.
     * 
     * @param parent
     * @param decoder
     * @param context
     * @param source
     */
    protected void parseReadAllFactory(Element parent, Element decoder, ParserContext context,
	    BeanDefinitionBuilder source) {
	LOGGER.debug("Configuring 'read all' socket interaction handler factory for " + parent.getLocalName());
	BeanDefinitionBuilder builder = BeanDefinitionBuilder
		.rootBeanDefinition(ReadAllInteractionHandler.Factory.class);
	AbstractBeanDefinition bean = builder.getBeanDefinition();
	String name = nameGenerator.generateBeanName(bean, context.getRegistry());
	context.getRegistry().registerBeanDefinition(name, bean);
	source.addPropertyReference("handlerFactory", name);
    }

    /**
     * Parse configuration for {@link HttpInteractionHandler} factory
     * implementation.
     * 
     * @param parent
     * @param decoder
     * @param context
     * @param source
     */
    protected void parseHttpFactory(Element parent, Element decoder, ParserContext context,
	    BeanDefinitionBuilder source) {
	LOGGER.debug("Configuring HTTP socket interaction handler factory for " + parent.getLocalName());
	BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(HttpInteractionHandler.Factory.class);
	AbstractBeanDefinition bean = builder.getBeanDefinition();
	String name = nameGenerator.generateBeanName(bean, context.getRegistry());
	context.getRegistry().registerBeanDefinition(name, bean);
	source.addPropertyReference("handlerFactory", name);
    }

    /**
     * Parse configuration for {@link GroovySocketInteractionHandler} factory
     * implementation.
     * 
     * @param parent
     * @param decoder
     * @param context
     * @param source
     */
    protected void parseGroovyFactory(Element parent, Element element, ParserContext context,
	    BeanDefinitionBuilder source) {
	LOGGER.debug("Configuring Groovy socket interaction handler factory for " + parent.getLocalName());
	BeanDefinitionBuilder builder = BeanDefinitionBuilder
		.rootBeanDefinition(GroovySocketInteractionHandler.Factory.class);

	Attr scriptPath = element.getAttributeNode("scriptPath");
	if (scriptPath != null) {
	    builder.addPropertyValue("scriptPath", scriptPath.getValue());
	}

	AbstractBeanDefinition bean = builder.getBeanDefinition();
	String name = nameGenerator.generateBeanName(bean, context.getRegistry());
	context.getRegistry().registerBeanDefinition(name, bean);
	source.addPropertyReference("handlerFactory", name);
    }

    /**
     * Parse a polling REST event source.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parsePollingRestEventSource(Element element, ParserContext context) {
	BeanDefinitionBuilder source = BeanDefinitionBuilder.rootBeanDefinition(BinaryInboundEventSource.class);

	// Verify that a sourceId was provided and set it on the bean.
	parseEventSourceId(element, source);

	// Create socket event receiver bean and register it.
	AbstractBeanDefinition receiver = createPollingRestEventReceiver(element, context);
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
	    throw new RuntimeException("No event decoder specified for socket event source: " + element.toString());
	}

	// Parse deduplicator if configured.
	parseDeduplicator(element, context, source);

	return source.getBeanDefinition();
    }

    /**
     * Create polling REST event receiver from XML element.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition createPollingRestEventReceiver(Element element, ParserContext context) {
	BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(PollingRestInboundEventReceiver.class);

	Attr pollIntervalMs = element.getAttributeNode("pollIntervalMs");
	if (pollIntervalMs != null) {
	    builder.addPropertyValue("pollIntervalMs", pollIntervalMs.getValue());
	}

	Attr scriptPath = element.getAttributeNode("scriptPath");
	if (scriptPath != null) {
	    builder.addPropertyValue("scriptPath", scriptPath.getValue());
	}

	Attr baseUrl = element.getAttributeNode("baseUrl");
	if (baseUrl != null) {
	    builder.addPropertyValue("baseUrl", baseUrl.getValue());
	}

	Attr username = element.getAttributeNode("username");
	if (username != null) {
	    builder.addPropertyValue("username", username.getValue());
	}

	Attr password = element.getAttributeNode("password");
	if (password != null) {
	    builder.addPropertyValue("password", password.getValue());
	}

	return builder.getBeanDefinition();
    }

    /**
     * Configure components needed to realize a web socket event source.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseWebSocketEventSource(Element element, ParserContext context) {
	Attr payloadType = element.getAttributeNode("payloadType");
	if (payloadType == null) {
	    throw new RuntimeException("Web socket event source expects 'payloadType' attribute.");
	}
	String type = payloadType.getValue();

	BeanDefinitionBuilder source;
	if ("string".equals(type)) {
	    source = BeanDefinitionBuilder.rootBeanDefinition(StringInboundEventSource.class);
	} else if ("binary".equals(type)) {
	    source = BeanDefinitionBuilder.rootBeanDefinition(BinaryInboundEventSource.class);
	} else {
	    throw new RuntimeException("Invalid 'payloadType' attribute specified for web socket event source.");
	}

	// Verify that a sourceId was provided and set it on the bean.
	parseEventSourceId(element, source);

	// Create event receiver bean and register it.
	AbstractBeanDefinition receiver = createWebSocketEventReceiver(type, element, context);
	String receiverName = nameGenerator.generateBeanName(receiver, context.getRegistry());
	context.getRegistry().registerBeanDefinition(receiverName, receiver);

	// Create list with bean reference and add it as property.
	ManagedList<Object> list = new ManagedList<Object>();
	RuntimeBeanReference ref = new RuntimeBeanReference(receiverName);
	list.add(ref);
	source.addPropertyValue("inboundEventReceivers", list);

	// Add decoder reference.
	boolean hadDecoder = false;
	if ("string".equals(type)) {
	    hadDecoder = parseStringDecoder(element, context, source);
	} else if ("binary".equals(type)) {
	    hadDecoder = parseBinaryDecoder(element, context, source);
	}

	if (!hadDecoder) {
	    throw new RuntimeException(
		    "No valid event decoder specified for web socket event source: " + element.toString());
	}

	// Parse deduplicator if configured.
	parseDeduplicator(element, context, source);

	return source.getBeanDefinition();
    }

    /**
     * Create web socket event receiver for String payloads.
     * 
     * @param type
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition createWebSocketEventReceiver(String type, Element element, ParserContext context) {
	BeanDefinitionBuilder receiver;
	if ("string".equals(type)) {
	    receiver = BeanDefinitionBuilder.rootBeanDefinition(StringWebSocketEventReceiver.class);
	} else if ("binary".equals(type)) {
	    receiver = BeanDefinitionBuilder.rootBeanDefinition(BinaryWebSocketEventReceiver.class);
	} else {
	    throw new RuntimeException("Invalid 'payloadType' attribute specified for web socket event source.");
	}

	Attr webSocketUrl = element.getAttributeNode("webSocketUrl");
	if (webSocketUrl != null) {
	    receiver.addPropertyValue("webSocketUrl", webSocketUrl.getValue());
	}

	List<Element> children = DomUtils.getChildElements(element);
	Map<String, String> headers = new HashMap<String, String>();
	for (Element child : children) {
	    if (child.getLocalName().equals("header")) {
		Attr name = child.getAttributeNode("name");
		if (name == null) {
		    throw new RuntimeException("Header value does not contain 'name' attribute.");
		}
		Attr value = child.getAttributeNode("value");
		if (value == null) {
		    throw new RuntimeException("Header value does not contain 'value' attribute.");
		}
		headers.put(name.getValue(), value.getValue());
	    }
	}
	receiver.addPropertyValue("headers", headers);

	return receiver.getBeanDefinition();
    }

    /**
     * Configure components needed to realize a CoAP server event source.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseCoapServerEventSource(Element element, ParserContext context) {
	BeanDefinitionBuilder source = BeanDefinitionBuilder.rootBeanDefinition(BinaryInboundEventSource.class);

	// Verify that a sourceId was provided and set it on the bean.
	parseEventSourceId(element, source);

	// Create event receiver bean and register it.
	AbstractBeanDefinition receiver = createCoapServerEventReceiver(element, context);
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
	    throw new RuntimeException(
		    "No event decoder specified for CoAP server event source: " + element.toString());
	}

	// Parse deduplicator if configured.
	parseDeduplicator(element, context, source);

	return source.getBeanDefinition();
    }

    /**
     * Create CoAP server event receiver.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition createCoapServerEventReceiver(Element element, ParserContext context) {
	BeanDefinitionBuilder receiver = BeanDefinitionBuilder.rootBeanDefinition(CoapServerEventReceiver.class);

	Attr hostname = element.getAttributeNode("hostname");
	if (hostname != null) {
	    receiver.addPropertyValue("hostname", hostname.getValue());
	}

	return receiver.getBeanDefinition();
    }

    /**
     * Configure components needed to realize a Hazelcast queue event source.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseHazelcastQueueEventSource(Element element, ParserContext context) {
	BeanDefinitionBuilder source = BeanDefinitionBuilder.rootBeanDefinition(DecodedInboundEventSource.class);

	// Verify that a sourceId was provided and set it on the bean.
	parseEventSourceId(element, source);

	// Create event receiver bean and register it.
	AbstractBeanDefinition receiver = createHazelcastQueueEventReceiver(element, context);
	String receiverName = nameGenerator.generateBeanName(receiver, context.getRegistry());
	context.getRegistry().registerBeanDefinition(receiverName, receiver);

	// Create list with bean reference and add it as property.
	ManagedList<Object> list = new ManagedList<Object>();
	RuntimeBeanReference ref = new RuntimeBeanReference(receiverName);
	list.add(ref);
	source.addPropertyValue("inboundEventReceivers", list);

	return source.getBeanDefinition();
    }

    /**
     * Create Hazelcast queue event receiver.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition createHazelcastQueueEventReceiver(Element element, ParserContext context) {
	BeanDefinitionBuilder receiver = BeanDefinitionBuilder.rootBeanDefinition(HazelcastQueueReceiver.class);
	return receiver.getBeanDefinition();
    }

    /**
     * Parse a binary decoder from the list of possibilities.
     * 
     * @param decoder
     * @param context
     * @param source
     * @return
     */
    @SuppressWarnings("deprecation")
    protected boolean parseBinaryDecoder(Element parent, ParserContext context, BeanDefinitionBuilder source) {
	List<Element> children = DomUtils.getChildElements(parent);
	AbstractBeanDefinition decoder = null;
	for (Element child : children) {
	    BinaryDecoders type = BinaryDecoders.getByLocalName(child.getLocalName());
	    if (type == null) {
		continue;
	    }
	    switch (type) {
	    case ProtobufDecoder: {
		decoder = parseProtobufDecoder(parent, child, context);
		break;
	    }
	    case JsonDeviceRequestDecoder: {
		decoder = parseJsonDeviceRequestDecoder(parent, child, context);
		break;
	    }
	    case JsonEventDecoder:
	    case JsonBatchEventDecoder: {
		decoder = parseJsonBatchDecoder(parent, child, context);
		break;
	    }
	    case GroovyEventDecoder: {
		decoder = parseGroovyDecoder(parent, child, context);
		break;
	    }
	    case CompositeDecoder: {
		decoder = parseCompositeDecoder(parent, child, context);
		break;
	    }
	    case EventDecoder: {
		parseDecoderRef(parent, child, context, source);
		return true;
	    }
	    }
	}

	if (decoder != null) {
	    String name = nameGenerator.generateBeanName(decoder, context.getRegistry());
	    context.getRegistry().registerBeanDefinition(name, decoder);
	    source.addPropertyReference("deviceEventDecoder", name);
	    return true;
	}
	return false;
    }

    /**
     * Parse a binary decoder from the list of possibilities.
     * 
     * @param decoder
     * @param context
     * @param source
     * @return
     */
    protected boolean parseStringDecoder(Element parent, ParserContext context, BeanDefinitionBuilder source) {
	List<Element> children = DomUtils.getChildElements(parent);
	AbstractBeanDefinition decoder = null;
	for (Element child : children) {
	    StringDecoders type = StringDecoders.getByLocalName(child.getLocalName());
	    if (type == null) {
		continue;
	    }
	    switch (type) {
	    case EchoStringDecoder: {
		decoder = parseEchoStringDecoder(parent, child, context);
		break;
	    }
	    case GroovyStringDecoder: {
		decoder = parseGroovyStringDecoder(parent, child, context);
		break;
	    }
	    case EventDecoder: {
		parseDecoderRef(parent, child, context, source);
		return true;
	    }
	    }
	}
	if (decoder != null) {
	    String name = nameGenerator.generateBeanName(decoder, context.getRegistry());
	    context.getRegistry().registerBeanDefinition(name, decoder);
	    source.addPropertyReference("deviceEventDecoder", name);
	    return true;
	}
	return false;
    }

    /**
     * Create parser for SiteWhere Google Protocol Buffer format.
     * 
     * @param parent
     * @param decoder
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseProtobufDecoder(Element parent, Element decoder, ParserContext context) {
	BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(ProtobufDeviceEventDecoder.class);
	return builder.getBeanDefinition();
    }

    /**
     * Parse configuration for JSON device request decoder.
     * 
     * @param parent
     * @param decoder
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseJsonDeviceRequestDecoder(Element parent, Element decoder,
	    ParserContext contex) {
	BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(JsonDeviceRequestDecoder.class);
	return builder.getBeanDefinition();
    }

    /**
     * Create parser for JSON batch event format.
     * 
     * @param parent
     * @param decoder
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseJsonBatchDecoder(Element parent, Element decoder, ParserContext context) {
	BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(JsonBatchEventDecoder.class);
	return builder.getBeanDefinition();
    }

    /**
     * Parse decoder that uses a Groovy script to decode events.
     * 
     * @param parent
     * @param decoder
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseGroovyDecoder(Element parent, Element decoder, ParserContext context) {
	BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(GroovyEventDecoder.class);

	Attr scriptPath = decoder.getAttributeNode("scriptPath");
	if (scriptPath == null) {
	    throw new RuntimeException("Script path not set for Groovy event decoder.");
	}
	builder.addPropertyValue("scriptPath", scriptPath.getValue());

	return builder.getBeanDefinition();
    }

    /**
     * Parse decoder that echoes String values to the logger.
     * 
     * @param parent
     * @param decoder
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseEchoStringDecoder(Element parent, Element decoder, ParserContext context) {
	LOGGER.debug("Configuring echo String decoder for " + parent.getLocalName());
	BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(EchoStringDecoder.class);
	return builder.getBeanDefinition();
    }

    /**
     * Parse decoder that uses a Groovy script to decode events.
     * 
     * @param parent
     * @param decoder
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseGroovyStringDecoder(Element parent, Element decoder, ParserContext context) {
	LOGGER.debug("Configuring Groovy String decoder for " + parent.getLocalName());
	BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(GroovyStringEventDecoder.class);

	Attr scriptPath = decoder.getAttributeNode("scriptPath");
	if (scriptPath == null) {
	    throw new RuntimeException("Script path not set for Groovy event decoder.");
	}
	builder.addPropertyValue("scriptPath", scriptPath.getValue());

	return builder.getBeanDefinition();
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
     * Parse information for a composite device event decoder.
     * 
     * @param parent
     * @param decoder
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseCompositeDecoder(Element parent, Element decoder, ParserContext context) {
	BeanDefinitionBuilder builder = BeanDefinitionBuilder
		.rootBeanDefinition(BinaryCompositeDeviceEventDecoder.class);

	List<Element> children = DomUtils.getChildElements(decoder);
	for (Element child : children) {
	    if (child.getLocalName().equals("choices")) {
		ManagedList<Object> choices = parseChoicesList(decoder, child, context);
		builder.addPropertyValue("decoderChoices", choices);
	    } else {
		AbstractBeanDefinition extractor = parseCompositeDecoderMetadataExtractor(child, context);
		builder.addPropertyValue("metadataExtractor", extractor);
	    }
	}

	return builder.getBeanDefinition();
    }

    /**
     * Parse list of choices specified for a composite device decoder.
     * 
     * @param decoder
     * @param choices
     * @param context
     * @param source
     * @return
     */
    protected ManagedList<Object> parseChoicesList(Element decoder, Element choices, ParserContext context) {
	ManagedList<Object> result = new ManagedList<Object>();
	List<Element> children = DomUtils.getChildElements(choices);
	for (Element child : children) {
	    CompositeDecoderChoiceElements type = CompositeDecoderChoiceElements.getByLocalName(child.getLocalName());
	    if (type == null) {
		throw new RuntimeException("Unknown choice for composite decoder element: " + child.getLocalName());
	    }
	    switch (type) {
	    case DeviceSpecificationDecoderChoice: {
		result.add(parseDeviceSpecificationDecoderChoice(child, context));
		break;
	    }
	    }
	}
	return result;
    }

    /**
     * Parse a device specification decoder choice.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseDeviceSpecificationDecoderChoice(Element element, ParserContext context) {
	BeanDefinitionBuilder builder = BeanDefinitionBuilder
		.rootBeanDefinition(DeviceSpecificationDecoderChoice.class);

	// Device specification token is required.
	Attr token = element.getAttributeNode("token");
	if (token == null) {
	    throw new RuntimeException("Token not set for device specification decoder choice.");
	}
	builder.addPropertyValue("deviceSpecificationToken", token.getValue());

	// Parse decoder associated with choice.
	parseBinaryDecoder(element, context, builder);

	return builder.getBeanDefinition();
    }

    /**
     * Parse the message metadata extractor for a composite device event
     * decoder.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseCompositeDecoderMetadataExtractor(Element element, ParserContext context) {
	CompositeDecoderMetadataExtractorElements type = CompositeDecoderMetadataExtractorElements
		.getByLocalName(element.getLocalName());
	if (type == null) {
	    throw new RuntimeException(
		    "Unknown metadata extractor for composite decoder element: " + element.getLocalName());
	}
	switch (type) {
	case GroovyDeviceMetadataExtractor: {
	    return parseGroovyMessageMetatataExtractor(element, context);
	}
	}
	return null;
    }

    /**
     * Parse the Groovy message metadata extractor.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseGroovyMessageMetatataExtractor(Element element, ParserContext context) {
	BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(GroovyMessageMetadataExtractor.class);

	// Script path is required.
	Attr scriptPath = element.getAttributeNode("scriptPath");
	if (scriptPath == null) {
	    throw new RuntimeException("Groovy script path is required for message metadata extractor.");
	}
	builder.addPropertyValue("scriptPath", scriptPath.getValue());

	return builder.getBeanDefinition();
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
	    throw new RuntimeException("No 'sourceId' attribute specified for event source: " + element.toString());
	}
	builder.addPropertyValue("sourceId", sourceId.getValue());
    }

    protected boolean parseDeduplicator(Element parent, ParserContext context, BeanDefinitionBuilder source) {
	List<Element> children = DomUtils.getChildElements(parent);
	AbstractBeanDefinition deduplicator = null;
	for (Element child : children) {
	    Deduplicators type = Deduplicators.getByLocalName(child.getLocalName());
	    if (type == null) {
		continue;
	    }
	    switch (type) {
	    case AlternateIdDeduplicator: {
		deduplicator = parseAlternateIdDeduplicator(parent, child, context);
		break;
	    }
	    case GroovyEventDeduplicator: {
		deduplicator = parseGroovyEventDeduplicator(parent, child, context);
		break;
	    }
	    }
	}
	if (deduplicator != null) {
	    String name = nameGenerator.generateBeanName(deduplicator, context.getRegistry());
	    context.getRegistry().registerBeanDefinition(name, deduplicator);
	    source.addPropertyReference("deviceEventDeduplicator", name);
	    return true;
	}
	return false;
    }

    /**
     * Parse bean definition for alternate id deduplicator.
     * 
     * @param parent
     * @param decoder
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseAlternateIdDeduplicator(Element parent, Element decoder,
	    ParserContext context) {
	BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(AlternateIdDeduplicator.class);
	return builder.getBeanDefinition();
    }

    /**
     * Parse bean definition for Groovy event deduplicator.
     * 
     * @param parent
     * @param decoder
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseGroovyEventDeduplicator(Element parent, Element decoder,
	    ParserContext context) {
	BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(GroovyEventDeduplicator.class);

	Attr scriptPath = decoder.getAttributeNode("scriptPath");
	if (scriptPath == null) {
	    throw new RuntimeException("Script path not set for Groovy event deduplicator.");
	}
	builder.addPropertyValue("scriptPath", scriptPath.getValue());

	return builder.getBeanDefinition();
    }
}