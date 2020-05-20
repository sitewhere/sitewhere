/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.configuration.router.devicetypemapping;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.commands.configuration.router.RouterConfiguration;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

public class DeviceTypeMappingConfiguration extends RouterConfiguration {

    /** Map of device type tokens to command destination ids */
    private Map<String, String> mappings = new HashMap<String, String>();

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
	    this.mappings = MarshalUtils.unmarshalJsonNode(mappings, Map.class);
	    JsonNode defaultDestination = json.findValue("defaultDestination");
	    this.defaultDestination = defaultDestination.asText();
	} catch (JsonProcessingException e) {
	    throw new SiteWhereException("Invalid device type mapping configuration.", e);
	}
    }

    public Map<String, String> getMappings() {
	return mappings;
    }

    public void setMappings(Map<String, String> mappings) {
	this.mappings = mappings;
    }

    public String getDefaultDestination() {
	return defaultDestination;
    }

    public void setDefaultDestination(String defaultDestination) {
	this.defaultDestination = defaultDestination;
    }
}
