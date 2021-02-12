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
package com.sitewhere.connectors.configuration.connector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.microservice.configuration.json.SiteWhereStringLookup;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Token lookup for MQTT outbound connector configuration.
 */
public class OutboundConnectorStringLookup extends SiteWhereStringLookup {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LoggerFactory.getLogger(SiteWhereStringLookup.class);

    /** Token for connector id */
    private static final String CONNECTOR_ID = "connector.id";

    /** Configuration */
    private OutboundConnectorConfiguration configuration;

    public OutboundConnectorStringLookup(ITenantEngineLifecycleComponent component,
	    OutboundConnectorConfiguration configuration) {
	super(component);
	this.configuration = configuration;
    }

    /*
     * @see org.apache.commons.text.lookup.StringLookup#lookup(java.lang.String)
     */
    @Override
    public String lookup(String key) {
	if (CONNECTOR_ID.equals(key)) {
	    return getConfiguration().getId();
	}
	return super.lookup(key);
    }

    protected OutboundConnectorConfiguration getConfiguration() {
	return configuration;
    }
}
