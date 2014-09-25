/*
 * CommandAgentsParser.java 
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

import com.sitewhere.device.provisioning.sms.SmsOutboundCommandAgent;
import com.sitewhere.spi.device.provisioning.IOutboundCommandAgent;

/**
 * Parses the list of {@link IOutboundCommandAgent} elements used in provisioning.
 * 
 * @author Derek
 */
public class CommandAgentsParser {

	/** Used to generate unique names for nested beans */
	private DefaultBeanNameGenerator nameGenerator = new DefaultBeanNameGenerator();

	/**
	 * Parse the list of command agents.
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
				throw new RuntimeException("Unknown command agent element: " + child.getLocalName());
			}
			switch (type) {
			case CommandAgent: {
				result.add(parseCommandAgentReference(child, context));
				break;
			}
			case TwilioCommandAgent: {
				result.add(parseTwilioCommandAgent(child, context));
				break;
			}
			}
		}
		return result;
	}

	/**
	 * Parse a command agent reference.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected RuntimeBeanReference parseCommandAgentReference(Element element, ParserContext context) {
		Attr ref = element.getAttributeNode("ref");
		if (ref != null) {
			return new RuntimeBeanReference(ref.getValue());
		}
		throw new RuntimeException("Command agent reference not contain ref attribute.");
	}

	/**
	 * Parse the Twilio command agent configuration and create beans necessary for
	 * implementation.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected AbstractBeanDefinition parseTwilioCommandAgent(Element element, ParserContext context) {
		BeanDefinitionBuilder sms = BeanDefinitionBuilder.rootBeanDefinition(SmsOutboundCommandAgent.class);
		Attr agentId = element.getAttributeNode("agentId");
		if (agentId == null) {
			throw new RuntimeException("Command agent does not contain agentId attribute.");
		}
		sms.addPropertyValue("agentId", agentId.getValue());

		// Add Twilio command delivery provider bean.
		AbstractBeanDefinition twilioDef = createTwilioDeliveryProvider(element);
		String twilioName = nameGenerator.generateBeanName(twilioDef, context.getRegistry());
		context.getRegistry().registerBeanDefinition(twilioName, twilioDef);
		sms.addPropertyReference("commandDeliveryProvider", twilioName);

		// Locate encoder reference.
		Element encoder = DomUtils.getChildElementByTagName(element, "encoder");
		if (encoder == null) {
			throw new RuntimeException("Command encoder required but not specified.");
		}
		Attr ref = encoder.getAttributeNode("ref");
		if (ref == null) {
			throw new RuntimeException("Command encoder ref required but not specified.");
		}
		sms.addPropertyReference("commandExecutionEncoder", ref.getValue());

		// Locate parameter extractor reference.
		Element extractor = DomUtils.getChildElementByTagName(element, "parameter-extractor");
		if (extractor == null) {
			throw new RuntimeException("Parameter extractor required but not specified.");
		}
		Attr pref = extractor.getAttributeNode("ref");
		if (pref == null) {
			throw new RuntimeException("Parameter extractor ref required but not specified.");
		}
		sms.addPropertyReference("commandDeliveryParameterExtractor", pref.getValue());

		return sms.getBeanDefinition();
	}

	/**
	 * Create Twilio command delivery provider from XML element.
	 * 
	 * @param element
	 * @return
	 */
	protected AbstractBeanDefinition createTwilioDeliveryProvider(Element element) {
		BeanDefinitionBuilder twilio =
				BeanDefinitionBuilder.rootBeanDefinition("com.sitewhere.twilio.TwilioCommandDeliveryProvider");

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
	 * Expected child elements.
	 * 
	 * @author Derek
	 */
	public static enum Elements {

		/** Command agent */
		CommandAgent("command-agent"),

		/** Twilio command agent */
		TwilioCommandAgent("twilio-command-agent");

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