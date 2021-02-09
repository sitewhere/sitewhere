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
package com.sitewhere.commands.configuration.router.devicetypemapping;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.commands.configuration.router.RouterConfiguration;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Data structure for device type mapping router configuration.
 */
public class DeviceTypeMappingConfiguration extends RouterConfiguration {

    /** Map of device type tokens to command destination ids */
    private List<DeviceTypeMapping> mappings = new ArrayList<>();

    /** Default destination for unmapped specifications */
    private String defaultDestination = null;

    public DeviceTypeMappingConfiguration(ITenantEngineLifecycleComponent component) {
	super(component);
    }

    /*
     * @see com.sitewhere.commands.configuration.destinations.
     * CommandDestinationConfiguration#loadFrom(com.fasterxml.jackson.databind.
     * JsonNode)
     */
    @Override
    @SuppressWarnings("unchecked")
    public void loadFrom(JsonNode json) throws SiteWhereException {
	try {
	    JsonNode mappings = json.findValue("mappings");
	    this.mappings = MarshalUtils.unmarshalJsonNode(mappings, List.class);
	    JsonNode defaultDestination = json.findValue("defaultDestination");
	    this.defaultDestination = defaultDestination.asText();
	} catch (JsonProcessingException e) {
	    throw new SiteWhereException("Invalid device type mapping configuration.", e);
	}
    }

    public List<DeviceTypeMapping> getMappings() {
	return mappings;
    }

    public void setMappings(List<DeviceTypeMapping> mappings) {
	this.mappings = mappings;
    }

    public String getDefaultDestination() {
	return defaultDestination;
    }

    public void setDefaultDestination(String defaultDestination) {
	this.defaultDestination = defaultDestination;
    }
}
