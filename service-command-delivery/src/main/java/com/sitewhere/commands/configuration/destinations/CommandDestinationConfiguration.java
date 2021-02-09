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
package com.sitewhere.commands.configuration.destinations;

import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.microservice.configuration.json.JsonConfiguration;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Base class for common command destination configuration.
 */
public abstract class CommandDestinationConfiguration extends JsonConfiguration {

    /** Unique event source id */
    private String id;

    public CommandDestinationConfiguration(ITenantEngineLifecycleComponent component) {
	super(component);
    }

    public void apply(CommandDestinationGenericConfiguration configuration) throws SiteWhereException {
	this.id = configuration.getId();
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
}
