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
package com.sitewhere.connectors.configuration;

import java.util.List;

import com.sitewhere.spi.microservice.multitenant.ITenantEngineConfiguration;

/**
 * Maps outbound connectors tenant engine YAML configuration to objects.
 */
public class OutboundConnectorsTenantConfiguration implements ITenantEngineConfiguration {

    /** Outbound connector configurations */
    private List<OutboundConnectorGenericConfiguration> outboundConnectors;

    public List<OutboundConnectorGenericConfiguration> getOutboundConnectors() {
	return outboundConnectors;
    }

    public void setOutboundConnectors(List<OutboundConnectorGenericConfiguration> outboundConnectors) {
	this.outboundConnectors = outboundConnectors;
    }
}
