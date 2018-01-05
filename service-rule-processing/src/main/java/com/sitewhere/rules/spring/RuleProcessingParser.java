/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rules.spring;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.configuration.parser.IRuleProcessingParser.Elements;
import com.sitewhere.rules.RuleProcessorsManager;
import com.sitewhere.rules.processors.geospatial.ZoneTest;
import com.sitewhere.rules.processors.geospatial.ZoneTestRuleProcessor;
import com.sitewhere.spi.device.event.AlertLevel;
import com.sitewhere.spi.geospatial.ZoneContainment;
import com.sitewhere.spi.microservice.spring.RuleProcessingBeans;

/**
 * Parses elements related to rule processing.
 * 
 * @author Derek
 */
public class RuleProcessingParser extends AbstractBeanDefinitionParser {

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
	ManagedList<Object> processors = new ManagedList<Object>();
	List<Element> children = DomUtils.getChildElements(element);
	for (Element child : children) {
	    Elements type = Elements.getByLocalName(child.getLocalName());
	    if (type == null) {
		throw new RuntimeException("Unknown outbound connectors element: " + child.getLocalName());
	    }
	    switch (type) {
	    case ZoneTestProcessor: {
		processors.add(parseZoneTestProcessor(child, context));
		break;
	    }
	    }
	}

	// Build rule processors manager and inject the list of beans.
	BeanDefinitionBuilder manager = BeanDefinitionBuilder.rootBeanDefinition(RuleProcessorsManager.class);
	manager.addPropertyValue("ruleProcessors", processors);
	context.getRegistry().registerBeanDefinition(RuleProcessingBeans.BEAN_RULE_PROCESSORS_MANAGER,
		manager.getBeanDefinition());

	return null;
    }

    /**
     * Parse attributes that are common to all rule processors.
     * 
     * @param element
     * @param builder
     */
    protected void parseCommonRuleProcessorAttributes(Element element, BeanDefinitionBuilder builder) {
    }

    /**
     * Parse configuration for rule processor that tests location events against
     * zone boundaries for firing alert conditions.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseZoneTestProcessor(Element element, ParserContext context) {
	BeanDefinitionBuilder processor = BeanDefinitionBuilder.rootBeanDefinition(ZoneTestRuleProcessor.class);

	// Parse common rule processor attributes.
	parseCommonRuleProcessorAttributes(element, processor);

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
	    ZoneContainment containment = (condition.getValue().equalsIgnoreCase("inside") ? ZoneContainment.Inside
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
}