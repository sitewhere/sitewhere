/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
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
