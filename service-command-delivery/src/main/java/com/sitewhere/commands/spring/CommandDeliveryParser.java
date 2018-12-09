/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.spring;

import java.util.List;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.commands.destination.CommandDestinationsManager;
import com.sitewhere.commands.destination.coap.CoapCommandDeliveryProvider;
import com.sitewhere.commands.destination.coap.CoapCommandDestination;
import com.sitewhere.commands.destination.coap.CoapParameters;
import com.sitewhere.commands.destination.coap.MetadataCoapParameterExtractor;
import com.sitewhere.commands.destination.mqtt.DefaultMqttParameterExtractor;
import com.sitewhere.commands.destination.mqtt.MqttCommandDeliveryProvider;
import com.sitewhere.commands.destination.mqtt.MqttCommandDestination;
import com.sitewhere.commands.destination.mqtt.MqttParameters;
import com.sitewhere.commands.destination.sms.SmsCommandDestination;
import com.sitewhere.commands.destination.sms.SmsParameters;
import com.sitewhere.commands.encoding.json.JsonCommandExecutionEncoder;
import com.sitewhere.commands.encoding.protobuf.JavaHybridProtobufExecutionEncoder;
import com.sitewhere.commands.encoding.protobuf.ProtobufExecutionEncoder;
import com.sitewhere.commands.groovy.GroovyCommandExecutionEncoder;
import com.sitewhere.commands.groovy.GroovyCommandRouter;
import com.sitewhere.commands.groovy.GroovySmsParameterExtractor;
import com.sitewhere.commands.groovy.GroovyStringCommandExecutionEncoder;
import com.sitewhere.commands.routing.DeviceTypeMappingCommandRouter;
import com.sitewhere.commands.spi.ICommandDeliveryParameterExtractor;
import com.sitewhere.commands.twilio.TwilioCommandDeliveryProvider;
import com.sitewhere.configuration.parser.ICommandDeliveryParser.BinaryCommandEncoders;
import com.sitewhere.configuration.parser.ICommandDeliveryParser.Elements;
import com.sitewhere.configuration.parser.ICommandDeliveryParser.StringCommandEncoders;
import com.sitewhere.spi.microservice.spring.CommandDestinationsBeans;

/**
 * Parses elements related to command delivery.
 * 
 * @author Derek
 */
public class CommandDeliveryParser extends AbstractBeanDefinitionParser {

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
	ManagedList<Object> destinations = new ManagedList<Object>();
	List<Element> children = DomUtils.getChildElements(element);
	for (Element child : children) {
	    Elements type = Elements.getByLocalName(child.getLocalName());
	    if (type == null) {
		throw new RuntimeException("Unknown command delivery element: " + child.getLocalName());
	    }
	    switch (type) {
	    case DeviceTypeMappingRouter: {
		parseDeviceTypeMappingRouter(child, context);
		break;
	    }
	    case GroovyCommandRouter: {
		parseGroovyCommandRouter(child, context);
		break;
	    }
	    case MqttCommandDestination: {
		destinations.add(parseMqttCommandDestination(child, context));
		break;
	    }
	    case CoapCommandDestination: {
		destinations.add(parseCoapCommandDestination(child, context));
		break;
	    }
	    case TwilioCommandDestination: {
		destinations.add(parseTwilioCommandDestination(child, context));
		break;
	    }
	    }
	}

	// Build outbound event processors manager and inject the list of beans.
	BeanDefinitionBuilder manager = BeanDefinitionBuilder.rootBeanDefinition(CommandDestinationsManager.class);
	manager.addPropertyValue("commandDestinations", destinations);
	context.getRegistry().registerBeanDefinition(CommandDestinationsBeans.BEAN_COMMAND_DESTINATIONS_MANAGER,
		manager.getBeanDefinition());

	return null;
    }

    /**
     * Parse the configuration for a {@link DeviceTypeMappingCommandRouter}.
     * 
     * @param element
     * @param context
     */
    protected void parseDeviceTypeMappingRouter(Element element, ParserContext context) {
	BeanDefinitionBuilder router = BeanDefinitionBuilder.rootBeanDefinition(DeviceTypeMappingCommandRouter.class);

	Attr defaultDestination = element.getAttributeNode("defaultDestination");
	if (defaultDestination != null) {
	    router.addPropertyValue("defaultDestination", defaultDestination.getValue());
	}

	ManagedMap<String, String> map = new ManagedMap<String, String>();
	List<Element> mappings = DomUtils.getChildElementsByTagName(element, "mapping");
	for (Element mapping : mappings) {
	    Attr deviceTypeId = mapping.getAttributeNode("deviceTypeId");
	    if (deviceTypeId == null) {
		throw new RuntimeException("Device type mapping missing device type id.");
	    }
	    Attr destination = mapping.getAttributeNode("destination");
	    if (destination == null) {
		throw new RuntimeException("Device type mapping missing destination id.");
	    }
	    map.put(deviceTypeId.getValue(), destination.getValue());
	}
	router.addPropertyValue("mappings", map);

	context.getRegistry().registerBeanDefinition(CommandDestinationsBeans.BEAN_COMMAND_ROUTER,
		router.getBeanDefinition());
    }

    /**
     * Parse the configuration for a {@link GroovyCommandRouter}.
     * 
     * @param element
     * @param context
     */
    protected void parseGroovyCommandRouter(Element element, ParserContext context) {
	BeanDefinitionBuilder router = BeanDefinitionBuilder.rootBeanDefinition(GroovyCommandRouter.class);

	Attr scriptId = element.getAttributeNode("scriptId");
	if (scriptId != null) {
	    router.addPropertyValue("scriptId", scriptId.getValue());
	}

	context.getRegistry().registerBeanDefinition(CommandDestinationsBeans.BEAN_COMMAND_ROUTER,
		router.getBeanDefinition());
    }

    /**
     * Parse the MQTT command destination configuration and create beans necessary
     * for implementation.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseMqttCommandDestination(Element element, ParserContext context) {
	BeanDefinitionBuilder mqtt = BeanDefinitionBuilder.rootBeanDefinition(MqttCommandDestination.class);
	addCommonAttributes(mqtt, element, context);

	// Add encoder reference.
	parseBinaryCommandEncoder(element, context, mqtt);

	// Add MQTT command delivery provider bean.
	AbstractBeanDefinition delivery = createMqttDeliveryProvider(element);
	String deliveryName = nameGenerator.generateBeanName(delivery, context.getRegistry());
	context.getRegistry().registerBeanDefinition(deliveryName, delivery);
	mqtt.addPropertyReference("commandDeliveryProvider", deliveryName);

	// Add parameter extractor reference.
	addParameterExtractor(mqtt, element, context, MqttParameters.class);

	return mqtt.getBeanDefinition();
    }

    /**
     * Create MQTT command delivery provider from XML element.
     * 
     * @param element
     * @return
     */
    protected AbstractBeanDefinition createMqttDeliveryProvider(Element element) {
	BeanDefinitionBuilder mqtt = BeanDefinitionBuilder.rootBeanDefinition(MqttCommandDeliveryProvider.class);

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
	    mqtt.addPropertyValue("keyStorePassword", trustStorePassword.getValue());
	}

	return mqtt.getBeanDefinition();
    }

    /**
     * Parse the CoAP command destination configuration and create beans necessary
     * for implementation.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseCoapCommandDestination(Element element, ParserContext context) {
	BeanDefinitionBuilder mqtt = BeanDefinitionBuilder.rootBeanDefinition(CoapCommandDestination.class);
	addCommonAttributes(mqtt, element, context);

	// Add encoder reference.
	parseBinaryCommandEncoder(element, context, mqtt);

	// Add CoAP command delivery provider bean.
	AbstractBeanDefinition delivery = createCoapDeliveryProvider(element);
	String deliveryName = nameGenerator.generateBeanName(delivery, context.getRegistry());
	context.getRegistry().registerBeanDefinition(deliveryName, delivery);
	mqtt.addPropertyReference("commandDeliveryProvider", deliveryName);

	// Add parameter extractor reference.
	addParameterExtractor(mqtt, element, context, CoapParameters.class);

	return mqtt.getBeanDefinition();
    }

    /**
     * Create MQTT command delivery provider from XML element.
     * 
     * @param element
     * @return
     */
    protected AbstractBeanDefinition createCoapDeliveryProvider(Element element) {
	BeanDefinitionBuilder mqtt = BeanDefinitionBuilder.rootBeanDefinition(CoapCommandDeliveryProvider.class);
	return mqtt.getBeanDefinition();
    }

    /**
     * Parse the Twilio command destination configuration and create beans necessary
     * for implementation.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseTwilioCommandDestination(Element element, ParserContext context) {
	BeanDefinitionBuilder sms = BeanDefinitionBuilder.rootBeanDefinition(SmsCommandDestination.class);
	addCommonAttributes(sms, element, context);

	// Add encoder reference.
	parseStringCommandEncoder(element, context, sms);

	// Add Twilio command delivery provider bean.
	AbstractBeanDefinition twilioDef = createTwilioDeliveryProvider(element);
	String twilioName = nameGenerator.generateBeanName(twilioDef, context.getRegistry());
	context.getRegistry().registerBeanDefinition(twilioName, twilioDef);
	sms.addPropertyReference("commandDeliveryProvider", twilioName);

	// Add parameter extractor reference.
	addParameterExtractor(sms, element, context, SmsParameters.class);

	return sms.getBeanDefinition();
    }

    /**
     * Create Twilio command delivery provider from XML element.
     * 
     * @param element
     * @return
     */
    protected AbstractBeanDefinition createTwilioDeliveryProvider(Element element) {
	BeanDefinitionBuilder twilio = BeanDefinitionBuilder.rootBeanDefinition(TwilioCommandDeliveryProvider.class);

	Attr accountSid = element.getAttributeNode("accountSid");
	if (accountSid == null) {
	    throw new RuntimeException("Twilio account SID attribute not provided.");
	}
	twilio.addPropertyValue("accountSid", accountSid.getValue());

	Attr authToken = element.getAttributeNode("authToken");
	if (authToken == null) {
	    throw new RuntimeException("Twilio auth token attribute not provided.");
	}
	twilio.addPropertyValue("authToken", authToken.getValue());

	Attr fromPhoneNumber = element.getAttributeNode("fromPhoneNumber");
	if (fromPhoneNumber == null) {
	    throw new RuntimeException("Twilio from phone number attribute not provided.");
	}
	twilio.addPropertyValue("fromPhoneNumber", fromPhoneNumber.getValue());

	return twilio.getBeanDefinition();
    }

    /**
     * Add attributes common to all command destinations.
     * 
     * @param builder
     * @param element
     * @param context
     */
    protected void addCommonAttributes(BeanDefinitionBuilder builder, Element element, ParserContext context) {
	Attr destinationId = element.getAttributeNode("destinationId");
	if (destinationId == null) {
	    throw new RuntimeException("Command destination does not contain destinationId attribute.");
	}
	builder.addPropertyValue("destinationId", destinationId.getValue());
    }

    /**
     * Locate a command encoder element and register it with the command
     * destination.
     * 
     * @param decoder
     * @param context
     * @param source
     * @return
     */
    protected boolean parseBinaryCommandEncoder(Element parent, ParserContext context,
	    BeanDefinitionBuilder destination) {
	List<Element> children = DomUtils.getChildElements(parent);
	for (Element child : children) {
	    BinaryCommandEncoders type = BinaryCommandEncoders.getByLocalName(child.getLocalName());
	    if (type == null) {
		return false;
	    }
	    switch (type) {
	    case ProtobufEncoder: {
		parseProtobufCommandEncoder(child, context, destination);
		return true;
	    }
	    case JavaHybridProtobufEncoder: {
		parseJavaHybridProtobufEncoder(child, context, destination);
		return true;
	    }
	    case JsonCommandEncoder: {
		parseJsonCommandEncoder(child, context, destination);
		return true;
	    }
	    case GroovyCommandEncoder: {
		parseGroovyCommandEncoder(child, context, destination);
		return true;
	    }
	    }
	}
	return false;
    }

    /**
     * Parse definition of SiteWhere GPB command encoder.
     * 
     * @param encoder
     * @param context
     * @param destination
     */
    protected void parseProtobufCommandEncoder(Element encoder, ParserContext context,
	    BeanDefinitionBuilder destination) {
	BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(ProtobufExecutionEncoder.class);
	AbstractBeanDefinition bean = builder.getBeanDefinition();
	String name = nameGenerator.generateBeanName(bean, context.getRegistry());
	context.getRegistry().registerBeanDefinition(name, bean);
	destination.addPropertyReference("commandExecutionEncoder", name);
    }

    /**
     * Parse definition of SiteWhere Java/GPB hybrid command encoder.
     * 
     * @param encoder
     * @param context
     * @param destination
     */
    protected void parseJavaHybridProtobufEncoder(Element encoder, ParserContext context,
	    BeanDefinitionBuilder destination) {
	BeanDefinitionBuilder builder = BeanDefinitionBuilder
		.rootBeanDefinition(JavaHybridProtobufExecutionEncoder.class);
	AbstractBeanDefinition bean = builder.getBeanDefinition();
	String name = nameGenerator.generateBeanName(bean, context.getRegistry());
	context.getRegistry().registerBeanDefinition(name, bean);
	destination.addPropertyReference("commandExecutionEncoder", name);
    }

    /**
     * Parse definition of JSON command encoder.
     * 
     * @param encoder
     * @param context
     * @param destination
     */
    protected void parseJsonCommandEncoder(Element encoder, ParserContext context, BeanDefinitionBuilder destination) {
	BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(JsonCommandExecutionEncoder.class);
	AbstractBeanDefinition bean = builder.getBeanDefinition();
	String name = nameGenerator.generateBeanName(bean, context.getRegistry());
	context.getRegistry().registerBeanDefinition(name, bean);
	destination.addPropertyReference("commandExecutionEncoder", name);
    }

    /**
     * Parse definition of Groovy command encoder.
     * 
     * @param encoder
     * @param context
     * @param destination
     */
    protected void parseGroovyCommandEncoder(Element encoder, ParserContext context,
	    BeanDefinitionBuilder destination) {
	BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(GroovyCommandExecutionEncoder.class);

	Attr scriptId = encoder.getAttributeNode("scriptId");
	if (scriptId != null) {
	    builder.addPropertyValue("scriptId", scriptId.getValue());
	}

	AbstractBeanDefinition bean = builder.getBeanDefinition();
	String name = nameGenerator.generateBeanName(bean, context.getRegistry());
	context.getRegistry().registerBeanDefinition(name, bean);
	destination.addPropertyReference("commandExecutionEncoder", name);
    }

    /**
     * Parse command encoder that results in a String payload.
     * 
     * @param parent
     * @param context
     * @param destination
     * @return
     */
    protected boolean parseStringCommandEncoder(Element parent, ParserContext context,
	    BeanDefinitionBuilder destination) {
	List<Element> children = DomUtils.getChildElements(parent);
	for (Element child : children) {
	    StringCommandEncoders type = StringCommandEncoders.getByLocalName(child.getLocalName());
	    if (type == null) {
		return false;
	    }
	    switch (type) {
	    case GroovyStringCommandEncoder: {
		parseGroovyStringCommandEncoder(child, context, destination);
		return true;
	    }
	    }
	}
	return false;
    }

    /**
     * Parse definition of Groovy String command encoder.
     * 
     * @param encoder
     * @param context
     * @param destination
     */
    protected void parseGroovyStringCommandEncoder(Element encoder, ParserContext context,
	    BeanDefinitionBuilder destination) {
	BeanDefinitionBuilder builder = BeanDefinitionBuilder
		.rootBeanDefinition(GroovyStringCommandExecutionEncoder.class);

	Attr scriptId = encoder.getAttributeNode("scriptId");
	if (scriptId != null) {
	    builder.addPropertyValue("scriptId", scriptId.getValue());
	}

	AbstractBeanDefinition bean = builder.getBeanDefinition();
	String name = nameGenerator.generateBeanName(bean, context.getRegistry());
	context.getRegistry().registerBeanDefinition(name, bean);
	destination.addPropertyReference("commandExecutionEncoder", name);
    }

    /**
     * Add parameter extractor beans.
     * 
     * @param builder
     * @param element
     * @param context
     * @param paramType
     * @return
     */
    protected boolean addParameterExtractor(BeanDefinitionBuilder builder, Element element, ParserContext context,
	    Class<?> paramType) {
	// Locate parameter extractor reference.
	Element extractor = DomUtils.getChildElementByTagName(element, "parameter-extractor");
	if (extractor != null) {
	    Attr pref = extractor.getAttributeNode("ref");
	    if (pref == null) {
		throw new RuntimeException("Parameter extractor ref required but not specified.");
	    }
	    builder.addPropertyReference("commandDeliveryParameterExtractor", pref.getValue());
	    return true;
	}
	if (paramType == MqttParameters.class) {
	    extractor = DomUtils.getChildElementByTagName(element, "hardware-id-topic-extractor");
	    if (extractor != null) {
		AbstractBeanDefinition xref = createHardwareIdMqttParameterExtractor(extractor);
		String xname = nameGenerator.generateBeanName(xref, context.getRegistry());
		context.getRegistry().registerBeanDefinition(xname, xref);
		builder.addPropertyReference("commandDeliveryParameterExtractor", xname);
		return true;
	    }
	} else if (paramType == CoapParameters.class) {
	    extractor = DomUtils.getChildElementByTagName(element, "metadata-coap-parameter-extractor");
	    if (extractor != null) {
		AbstractBeanDefinition xref = createCoapMetadataParameterExtractor(extractor);
		String xname = nameGenerator.generateBeanName(xref, context.getRegistry());
		context.getRegistry().registerBeanDefinition(xname, xref);
		builder.addPropertyReference("commandDeliveryParameterExtractor", xname);
		return true;
	    }
	} else if (paramType == SmsParameters.class) {
	    extractor = DomUtils.getChildElementByTagName(element, "groovy-sms-parameter-extractor");
	    if (extractor != null) {
		AbstractBeanDefinition xref = createGroovySmsParameterExtractor(extractor);
		String xname = nameGenerator.generateBeanName(xref, context.getRegistry());
		context.getRegistry().registerBeanDefinition(xname, xref);
		builder.addPropertyReference("commandDeliveryParameterExtractor", xname);
		return true;
	    }
	}
	return false;
    }

    /**
     * Create hardware id MQTT parameter extractor.
     * 
     * @param element
     * @return
     */
    protected AbstractBeanDefinition createHardwareIdMqttParameterExtractor(Element element) {
	BeanDefinitionBuilder extractor = BeanDefinitionBuilder
		.rootBeanDefinition(DefaultMqttParameterExtractor.class);

	Attr commandTopicExpr = element.getAttributeNode("commandTopicExpr");
	if (commandTopicExpr != null) {
	    extractor.addPropertyValue("commandTopicExpr", commandTopicExpr.getValue());
	}

	Attr systemTopicExpr = element.getAttributeNode("systemTopicExpr");
	if (systemTopicExpr != null) {
	    extractor.addPropertyValue("systemTopicExpr", systemTopicExpr.getValue());
	}
	return extractor.getBeanDefinition();
    }

    /**
     * Create CoAP device metadata extractor.
     * 
     * @param element
     * @return
     */
    protected AbstractBeanDefinition createCoapMetadataParameterExtractor(Element element) {
	BeanDefinitionBuilder extractor = BeanDefinitionBuilder
		.rootBeanDefinition(MetadataCoapParameterExtractor.class);

	Attr hostnameMetadataField = element.getAttributeNode("hostnameMetadataField");
	if (hostnameMetadataField != null) {
	    extractor.addPropertyValue("hostnameMetadataField", hostnameMetadataField.getValue());
	}

	Attr portMetadataField = element.getAttributeNode("portMetadataField");
	if (portMetadataField != null) {
	    extractor.addPropertyValue("portMetadataField", portMetadataField.getValue());
	}

	Attr urlMetadataField = element.getAttributeNode("urlMetadataField");
	if (urlMetadataField != null) {
	    extractor.addPropertyValue("urlMetadataField", urlMetadataField.getValue());
	}

	return extractor.getBeanDefinition();
    }

    /**
     * Create {@link ICommandDeliveryParameterExtractor} that uses a Groovy script
     * to extract an {@link SmsParameters} object for use by the command
     * destination.
     * 
     * @param element
     * @return
     */
    protected AbstractBeanDefinition createGroovySmsParameterExtractor(Element element) {
	BeanDefinitionBuilder extractor = BeanDefinitionBuilder.rootBeanDefinition(GroovySmsParameterExtractor.class);

	Attr scriptId = element.getAttributeNode("scriptId");
	if (scriptId != null) {
	    extractor.addPropertyValue("scriptId", scriptId.getValue());
	}

	return extractor.getBeanDefinition();
    }
}