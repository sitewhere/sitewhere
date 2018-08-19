/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rules.configuration;

import com.sitewhere.configuration.model.ConfigurationModelProvider;
import com.sitewhere.configuration.parser.IRuleProcessingParser;
import com.sitewhere.rest.model.configuration.AttributeNode;
import com.sitewhere.rest.model.configuration.ElementNode;
import com.sitewhere.spi.microservice.configuration.model.AttributeType;
import com.sitewhere.spi.microservice.configuration.model.IAttributeGroup;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider;

/**
 * Configuration model provider for rule processing microservice.
 * 
 * @author Derek
 */
public class RuleProcessingModelProvider extends ConfigurationModelProvider {

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#getDefaultXmlNamespace()
     */
    @Override
    public String getDefaultXmlNamespace() {
	return "http://sitewhere.io/schema/sitewhere/microservice/rule-processing";
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#getRootRole()
     */
    @Override
    public IConfigurationRoleProvider getRootRole() {
	return RuleProcessingRoles.RuleProcessing;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#initializeElements()
     */
    @Override
    public void initializeElements() {
	addElement(createRuleProcessingElement());

	// Zone test elements.
	addElement(createZoneTestProcessorElement());
	addElement(createZoneTestElement());
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#initializeRoles()
     */
    @Override
    public void initializeRoles() {
	for (RuleProcessingRoles role : RuleProcessingRoles.values()) {
	    getRolesById().put(role.getRole().getKey().getId(), role.getRole());
	}
    }

    /**
     * Create rule processing element.
     * 
     * @return
     */
    protected ElementNode createRuleProcessingElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Rule Processing", IRuleProcessingParser.ROOT,
		"code-branch", RuleProcessingRoleKeys.RuleProcessing, this);

	builder.description("Adds processors that apply rules to the stream of device events.");

	return builder.build();
    }

    /**
     * Create a zone test processor.
     * 
     * @return
     */
    protected ElementNode createZoneTestProcessorElement() {
	ElementNode.Builder builder = new ElementNode.Builder(RuleProcessingRoles.ZoneTestProcessor.getRole().getName(),
		IRuleProcessingParser.Elements.ZoneTestProcessor.getLocalName(), "map-pin",
		RuleProcessingRoleKeys.ZoneTestProcessor, this);
	builder.description("Allows alerts to be generated if location events are inside "
		+ "or outside of a zone based on criteria.");
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_GENERAL);

	addCommonRuleProcessorAttributes(builder, ConfigurationModelProvider.ATTR_GROUP_GENERAL);

	return builder.build();
    }

    /**
     * Create a zone test processor element.
     * 
     * @return
     */
    protected ElementNode createZoneTestElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Zone Test", "zone-test", "map-pin",
		RuleProcessingRoleKeys.ZoneTestElement, this);
	builder.description("Describes zone test criteria and alert to be generated in case of a match.");
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_GENERAL);

	builder.attribute((new AttributeNode.Builder("Zone token", "zoneToken", AttributeType.String,
		ConfigurationModelProvider.ATTR_GROUP_GENERAL)
			.description("Unique token for zone locations are to be tested against.").build()));
	builder.attribute((new AttributeNode.Builder("Condition", "condition", AttributeType.String,
		ConfigurationModelProvider.ATTR_GROUP_GENERAL)
			.description("Condition under which alert should be generated.")
			.choice("Location Inside Zone", "inside").choice("Location Outside Zone", "outside").build()));
	builder.attribute((new AttributeNode.Builder("Alert type", "alertType", AttributeType.String,
		ConfigurationModelProvider.ATTR_GROUP_GENERAL).description("Identifier that indicates alert type.")
			.build()));
	builder.attribute((new AttributeNode.Builder("Alert level", "alertLevel", AttributeType.String,
		ConfigurationModelProvider.ATTR_GROUP_GENERAL).description("Level value of alert.")
			.choice("Information", "info").choice("Warning", "warning").choice("Error", "error")
			.choice("Critical", "critical").build()));
	builder.attribute((new AttributeNode.Builder("Alert message", "alertMessage", AttributeType.String,
		ConfigurationModelProvider.ATTR_GROUP_GENERAL).description("Message shown for alert.").build()));
	return builder.build();
    }

    /**
     * Add common rule processor attributes.
     * 
     * @param builder
     */
    public static void addCommonRuleProcessorAttributes(ElementNode.Builder builder, IAttributeGroup group) {
	builder.attribute((new AttributeNode.Builder("Processor id", "processorId", AttributeType.String, group)
		.description("Unique id used for referencing this processor.").makeIndex().makeRequired().build()));
	builder.attribute((new AttributeNode.Builder("Number of processing threads", "numProcessingThreads",
		AttributeType.Integer, group)
			.description("Number of processing threads used to pull events from Kafka into rule processor.")
			.makeRequired().build()));
    }
}