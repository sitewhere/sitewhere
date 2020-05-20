/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.configuration.extractors.mqtt;

import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.commands.configuration.extractors.ParameterExtractorConfiguration;
import com.sitewhere.commands.destination.mqtt.DefaultMqttParameterExtractor;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Configuration parameters for {@link DefaultMqttParameterExtractor}.
 */
public class DefaultMqttParameterExtractorConfiguration extends ParameterExtractorConfiguration {

    /** Default command topic */
    public static final String DEFAULT_COMMAND_EXPRESSION = "SiteWhere/${tenant}/command/${device}";

    /** Default system topic */
    public static final String DEFAULT_SYSTEM_EXPRESSION = "SiteWhere/${tenant}/system/${device}";

    /** Command topic expression */
    private String commandTopicExpression;

    /** System topic expression */
    private String systemTopicExpression;

    public DefaultMqttParameterExtractorConfiguration(ITenantEngineLifecycleComponent component) {
	super(component);
    }

    /*
     * @see com.sitewhere.commands.configuration.extractors.
     * ParameterExtractorConfiguration#loadFrom(com.fasterxml.jackson.databind.
     * JsonNode)
     */
    @Override
    public void loadFrom(JsonNode json) throws SiteWhereException {
	this.commandTopicExpression = configurableString("commandTopicExpression", json, DEFAULT_COMMAND_EXPRESSION);
	this.systemTopicExpression = configurableString("systemTopicExpression", json, DEFAULT_SYSTEM_EXPRESSION);
    }

    public String getCommandTopicExpression() {
	return commandTopicExpression;
    }

    public void setCommandTopicExpression(String commandTopicExpression) {
	this.commandTopicExpression = commandTopicExpression;
    }

    public String getSystemTopicExpression() {
	return systemTopicExpression;
    }

    public void setSystemTopicExpression(String systemTopicExpression) {
	this.systemTopicExpression = systemTopicExpression;
    }
}
