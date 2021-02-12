/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
