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
package com.sitewhere.sources.configuration.eventsource;

import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.microservice.configuration.json.JsonConfiguration;
import com.sitewhere.sources.configuration.DecoderGenericConfiguration;
import com.sitewhere.sources.configuration.EventSourceGenericConfiguration;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Base class for common event source configuration.
 */
public abstract class EventSourceConfiguration extends JsonConfiguration {

    /** Unique event source id */
    private String id;

    /** Decoder type */
    private DecoderGenericConfiguration decoder;

    public EventSourceConfiguration(ITenantEngineLifecycleComponent component) {
	super(component);
    }

    public void apply(EventSourceGenericConfiguration configuration) throws SiteWhereException {
	this.id = configuration.getId();
	this.decoder = configuration.getDecoder();
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

    public DecoderGenericConfiguration getDecoder() {
	return decoder;
    }

    public void setDecoder(DecoderGenericConfiguration decoder) {
	this.decoder = decoder;
    }
}
