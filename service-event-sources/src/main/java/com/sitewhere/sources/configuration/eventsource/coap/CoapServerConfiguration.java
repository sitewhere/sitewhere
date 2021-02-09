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
package com.sitewhere.sources.configuration.eventsource.coap;

import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.sources.configuration.eventsource.EventSourceConfiguration;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Configuration for an CoAP server event source.
 */
public class CoapServerConfiguration extends EventSourceConfiguration {

    /** Supplies standard CoAP port */
    private static final int COAP_PORT = 8583;

    /** Port for binding socket */
    private int port = COAP_PORT;

    public CoapServerConfiguration(ITenantEngineLifecycleComponent component) {
	super(component);
    }

    /*
     * @see
     * com.sitewhere.sources.configuration.eventsource.EventSourceConfiguration#
     * loadFrom(com.fasterxml.jackson.databind.JsonNode)
     */
    @Override
    public void loadFrom(JsonNode json) throws SiteWhereException {
	this.port = configurableInt("port", json, COAP_PORT);
    }

    public int getPort() {
	return port;
    }

    public void setPort(int port) {
	this.port = port;
    }
}
