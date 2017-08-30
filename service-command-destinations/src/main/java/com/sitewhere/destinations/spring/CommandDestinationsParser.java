/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.destinations.spring;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import com.sitewhere.destinations.twilio.TwilioCommandDeliveryProvider;
import com.sitewhere.device.communication.coap.CoapCommandDeliveryProvider;
import com.sitewhere.device.communication.coap.CoapCommandDestination;
import com.sitewhere.device.communication.coap.CoapParameters;
import com.sitewhere.device.communication.coap.MetadataCoapParameterExtractor;
import com.sitewhere.device.communication.json.JsonCommandExecutionEncoder;
import com.sitewhere.device.communication.mqtt.HardwareIdMqttParameterExtractor;
import com.sitewhere.device.communication.mqtt.MqttCommandDeliveryProvider;
import com.sitewhere.device.communication.mqtt.MqttCommandDestination;
import com.sitewhere.device.communication.mqtt.MqttParameters;
import com.sitewhere.device.communication.protobuf.JavaHybridProtobufExecutionEncoder;
import com.sitewhere.device.communication.protobuf.ProtobufExecutionEncoder;
import com.sitewhere.device.communication.sms.SmsCommandDestination;
import com.sitewhere.device.communication.sms.SmsParameters;
import com.sitewhere.groovy.device.communication.GroovyCommandExecutionEncoder;
import com.sitewhere.groovy.device.communication.GroovySmsParameterExtractor;
import com.sitewhere.groovy.device.communication.GroovyStringCommandExecutionEncoder;
import com.sitewhere.spi.device.communication.ICommandDeliveryParameterExtractor;
import com.sitewhere.spi.device.communication.ICommandDestination;
import com.sitewhere.spring.handler.ICommandDestinationsParser.BinaryCommandEncoders;
import com.sitewhere.spring.handler.ICommandDestinationsParser.Elements;
import com.sitewhere.spring.handler.ICommandDestinationsParser.StringCommandEncoders;
import com.sitewhere.spring.handler.IConfigurationElements;
import com.sitewhere.spring.handler.SiteWhereBeanListParser;

/**
 * Parses the list of {@link ICommandDestination} elements used in the
 * communication subsystem.
 * 
 * @author Derek
 */
public class CommandDestinationsParser extends SiteWhereBeanListParser {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LogManager.getLogger();

    /** Used to generate unique names for nested beans */
    private DefaultBeanNameGenerator nameGenerator = new DefaultBeanNameGenerator();

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spring.handler.SiteWhereBeanListParser#parse(org.w3c.dom.
     * Element, org.springframework.beans.factory.xml.ParserContext)
     */
    public ManagedList<?> parse(Element element, ParserContext context) {
	ManagedList<Object> result = new ManagedList<Object>();
	List<Element> children = DomUtils.getChildElements(element);
	for (Element child : children) {
	    Elements type = Elements.getByLocalName(child.getLocalName());
	    if (type == null) {
		throw new RuntimeException("Unknown command destination element: " + child.getLocalName());
	    }
	    switch (type) {
	    case CommandDestination: {
		result.add(parseCommandDestinationReference(child, context));
		break;
	    }
	    case MqttCommandDestination: {
		result.add(parseMqttCommandDestination(child, context));
		break;
	    }
	    case CoapCommandDestination: {
		result.add(parseCoapCommandDestination(child, context));
		break;
	    }
	    case TwilioCommandDestination: {
		result.add(parseTwilioCommandDestination(child, context));
		break;
	    }
	    }
	}
	return result;
    }

    /**
     * Parse a command destination reference.
     * 
     * @param element
     * @param context
     * @return
     */
    protected RuntimeBeanReference parseCommandDestinationReference(Element element, ParserContext context) {
	Attr ref = element.getAttributeNode("ref");
	if (ref != null) {
	    return new RuntimeBeanReference(ref.getValue());
	}
	throw new RuntimeException("Command destination reference not contain ref attribute.");
    }

    /**
     * Parse the MQTT command destination configuration and create beans
     * necessary for implementation.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseMqttCommandDestination(Element element, ParserContext context) {
	BeanDefinitionBuilder mqtt = getBuilderFor(MqttCommandDestination.class);
	addCommonAttributes(mqtt, element, context);

	// Add encoder reference.
	if (!parseBinaryCommandEncoder(element, context, mqtt)) {
	    throw new RuntimeException("Command encoder required for MQTT command destination but was not specified.");
	}

	// Add MQTT command delivery provider bean.
	AbstractBeanDefinition delivery = createMqttDeliveryProvider(element);
	String deliveryName = nameGenerator.generateBeanName(delivery, context.getRegistry());
	context.getRegistry().registerBeanDefinition(deliveryName, delivery);
	mqtt.addPropertyReference("commandDeliveryProvider", deliveryName);

	// Locate parameter extractor reference.
	if (!addParameterExtractor(mqtt, element, context, MqttParameters.class)) {
	    throw new RuntimeException("Parameter extractor required but not specified.");
	}

	return mqtt.getBeanDefinition();
    }

    /**
     * Create MQTT command delivery provider from XML element.
     * 
     * @param element
     * @return
     */
    protected AbstractBeanDefinition createMqttDeliveryProvider(Element element) {
	BeanDefinitionBuilder mqtt = getBuilderFor(MqttCommandDeliveryProvider.class);

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
     * Parse the CoAP command destination configuration and create beans
     * necessary for implementation.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseCoapCommandDestination(Element element, ParserContext context) {
	BeanDefinitionBuilder mqtt = getBuilderFor(CoapCommandDestination.class);
	addCommonAttributes(mqtt, element, context);

	// Add encoder reference.
	if (!parseBinaryCommandEncoder(element, context, mqtt)) {
	    throw new RuntimeException("Command encoder required for CoAP command destination but was not specified.");
	}

	// Add CoAP command delivery provider bean.
	AbstractBeanDefinition delivery = createCoapDeliveryProvider(element);
	String deliveryName = nameGenerator.generateBeanName(delivery, context.getRegistry());
	context.getRegistry().registerBeanDefinition(deliveryName, delivery);
	mqtt.addPropertyReference("commandDeliveryProvider", deliveryName);

	// Locate parameter extractor reference.
	if (!addParameterExtractor(mqtt, element, context, CoapParameters.class)) {
	    throw new RuntimeException("Parameter extractor required but not specified.");
	}

	return mqtt.getBeanDefinition();
    }

    /**
     * Create MQTT command delivery provider from XML element.
     * 
     * @param element
     * @return
     */
    protected AbstractBeanDefinition createCoapDeliveryProvider(Element element) {
	BeanDefinitionBuilder mqtt = getBuilderFor(CoapCommandDeliveryProvider.class);
	return mqtt.getBeanDefinition();
    }

    /**
     * Parse the Twilio command destination configuration and create beans
     * necessary for implementation.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseTwilioCommandDestination(Element element, ParserContext context) {
	BeanDefinitionBuilder sms = BeanDefinitionBuilder.rootBeanDefinition(SmsCommandDestination.class);
	addCommonAttributes(sms, element, context);

	// Add encoder reference.
	if (!parseStringCommandEncoder(element, context, sms)) {
	    throw new RuntimeException(
		    "Command encoder required for Twilio command destination but was not specified.");
	}

	// Add Twilio command delivery provider bean.
	AbstractBeanDefinition twilioDef = createTwilioDeliveryProvider(element);
	String twilioName = nameGenerator.generateBeanName(twilioDef, context.getRegistry());
	context.getRegistry().registerBeanDefinition(twilioName, twilioDef);
	sms.addPropertyReference("commandDeliveryProvider", twilioName);

	// Locate parameter extractor reference.
	if (!addParameterExtractor(sms, element, context, SmsParameters.class)) {
	    throw new RuntimeException("Parameter extractor required but not specified.");
	}

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
	    if (!IConfigurationElements.SITEWHERE_CE_TENANT_NS.equals(child.getNamespaceURI())) {
		NamespaceHandler nested = context.getReaderContext().getNamespaceHandlerResolver()
			.resolve(child.getNamespaceURI());
		if (nested != null) {
		    BeanDefinition decoderBean = nested.parse(child, context);
		    String decoderName = nameGenerator.generateBeanName(decoderBean, context.getRegistry());
		    context.getRegistry().registerBeanDefinition(decoderName, decoderBean);
		    destination.addPropertyReference("commandExecutionEncoder", decoderName);
		    return true;
		} else {
		    continue;
		}
	    }
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
	    case CommandEncoder: {
		parseEncoderRef(child, context, destination);
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

	Attr scriptPath = encoder.getAttributeNode("scriptPath");
	if (scriptPath == null) {
	    throw new RuntimeException("Script path required but not specified.");
	}
	builder.addPropertyValue("scriptPath", scriptPath.getValue());

	AbstractBeanDefinition bean = builder.getBeanDefinition();
	String name = nameGenerator.generateBeanName(bean, context.getRegistry());
	context.getRegistry().registerBeanDefinition(name, bean);
	destination.addPropertyReference("commandExecutionEncoder", name);
    }

    /**
     * Parse reference to a command encoder defined in an external bean.
     * 
     * @param encoder
     * @param context
     * @param destination
     */
    protected void parseEncoderRef(Element encoder, ParserContext context, BeanDefinitionBuilder destination) {
	Attr encoderRef = encoder.getAttributeNode("ref");
	if (encoderRef == null) {
	    throw new RuntimeException("Command encoder 'ref' attribute is required.");
	}
	destination.addPropertyReference("commandExecutionEncoder", encoderRef.getValue());
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
	    if (!IConfigurationElements.SITEWHERE_CE_TENANT_NS.equals(child.getNamespaceURI())) {
		NamespaceHandler nested = context.getReaderContext().getNamespaceHandlerResolver()
			.resolve(child.getNamespaceURI());
		if (nested != null) {
		    BeanDefinition decoderBean = nested.parse(child, context);
		    String decoderName = nameGenerator.generateBeanName(decoderBean, context.getRegistry());
		    context.getRegistry().registerBeanDefinition(decoderName, decoderBean);
		    destination.addPropertyReference("commandExecutionEncoder", decoderName);
		    return true;
		} else {
		    continue;
		}
	    }
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

	Attr scriptPath = encoder.getAttributeNode("scriptPath");
	if (scriptPath == null) {
	    throw new RuntimeException("Script path required but not specified.");
	}
	builder.addPropertyValue("scriptPath", scriptPath.getValue());

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
		.rootBeanDefinition(HardwareIdMqttParameterExtractor.class);

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
     * Create {@link ICommandDeliveryParameterExtractor} that uses a Groovy
     * script to extract an {@link SmsParameters} object for use by the command
     * destination.
     * 
     * @param element
     * @return
     */
    protected AbstractBeanDefinition createGroovySmsParameterExtractor(Element element) {
	BeanDefinitionBuilder extractor = BeanDefinitionBuilder.rootBeanDefinition(GroovySmsParameterExtractor.class);

	Attr scriptPath = element.getAttributeNode("scriptPath");
	if (scriptPath != null) {
	    extractor.addPropertyValue("scriptPath", scriptPath.getValue());
	}

	return extractor.getBeanDefinition();
    }
}