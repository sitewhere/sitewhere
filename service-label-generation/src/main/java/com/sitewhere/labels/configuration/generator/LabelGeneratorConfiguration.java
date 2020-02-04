/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.labels.configuration.generator;

import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.labels.configuration.manager.LabelGeneratorGenericConfiguration;
import com.sitewhere.microservice.configuration.json.JsonConfiguration;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Base class for common label generator configuration.
 */
public abstract class LabelGeneratorConfiguration extends JsonConfiguration {

    /** Unique generator id */
    private String id;

    /** Generator name */
    private String name;

    public LabelGeneratorConfiguration(ITenantEngineLifecycleComponent component) {
	super(component);
    }

    public void apply(LabelGeneratorGenericConfiguration configuration) throws SiteWhereException {
	this.id = configuration.getId();
	this.name = configuration.getName();
	loadFrom(configuration.getConfiguration());
    }

    /**
     * Load subclass from JSON.
     * 
     * @param json
     * @throws SiteWhereException
     */
    public abstract void loadFrom(JsonNode json) throws SiteWhereException;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }
}
