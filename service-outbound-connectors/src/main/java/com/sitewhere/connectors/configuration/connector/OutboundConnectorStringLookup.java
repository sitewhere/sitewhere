/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
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
