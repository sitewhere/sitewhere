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
package com.sitewhere.commands.destination.mqtt;

import java.util.List;

import org.apache.commons.text.StringSubstitutor;

import com.sitewhere.commands.configuration.extractors.mqtt.DefaultMqttParameterExtractorConfiguration;
import com.sitewhere.commands.destination.CommandDestinationStringLookup;
import com.sitewhere.commands.spi.ICommandDeliveryParameterExtractor;
import com.sitewhere.commands.spi.ICommandDestination;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;

/**
 * Implements {@link ICommandDeliveryParameterExtractor} for
 * {@link MqttParameters}, allowing expressions to be defined such that the
 * tenant id and device token may be included in the topic name to target a
 * specific device.
 */
public class DefaultMqttParameterExtractor extends TenantEngineLifecycleComponent
	implements ICommandDeliveryParameterExtractor<MqttParameters> {

    /** Configuration */
    private DefaultMqttParameterExtractorConfiguration configuration;

    public DefaultMqttParameterExtractor(DefaultMqttParameterExtractorConfiguration configuration) {
	super(LifecycleComponentType.CommandParameterExtractor);
	this.configuration = configuration;
    }

    /*
     * @see com.sitewhere.commands.spi.ICommandDeliveryParameterExtractor#
     * extractDeliveryParameters(com.sitewhere.commands.spi.ICommandDestination,
     * com.sitewhere.spi.device.IDeviceNestingContext, java.util.List,
     * com.sitewhere.spi.device.command.IDeviceCommandExecution)
     */
    @Override
    public MqttParameters extractDeliveryParameters(ICommandDestination<?, ?> destination,
	    IDeviceNestingContext nesting, List<? extends IDeviceAssignment> assignments,
	    IDeviceCommandExecution execution) throws SiteWhereException {
	StringSubstitutor substitutor = new StringSubstitutor(
		new CommandDestinationStringLookup(this, destination, nesting));
	MqttParameters params = new MqttParameters();
	params.setCommandTopic(substitutor.replace(getConfiguration().getCommandTopicExpression()));
	params.setSystemTopic(substitutor.replace(getConfiguration().getSystemTopicExpression()));
	return params;
    }

    protected DefaultMqttParameterExtractorConfiguration getConfiguration() {
	return configuration;
    }
}