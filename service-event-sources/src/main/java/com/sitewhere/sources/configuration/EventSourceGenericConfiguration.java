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
package com.sitewhere.sources.configuration;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Configuration entry for an event source.
 */
public class EventSourceGenericConfiguration {

    /** Unique id */
    private String id;

    /** Event source type */
    private String type;

    /** Payload decoder */
    private DecoderGenericConfiguration decoder;

    /** Type-specific configuration parameters */
    private JsonNode configuration;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public DecoderGenericConfiguration getDecoder() {
	return decoder;
    }

    public void setDecoder(DecoderGenericConfiguration decoder) {
	this.decoder = decoder;
    }

    public JsonNode getConfiguration() {
	return configuration;
    }

    public void setConfiguration(JsonNode configuration) {
	this.configuration = configuration;
    }
}
