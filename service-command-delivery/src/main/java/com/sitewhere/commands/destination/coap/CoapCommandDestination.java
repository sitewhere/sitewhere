/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.destination.coap;

import com.sitewhere.commands.configuration.destinations.coap.CoapConfiguration;
import com.sitewhere.commands.configuration.extractors.coap.MetadataCoapParameterExtractorConfiguration;
import com.sitewhere.commands.destination.CommandDestination;
import com.sitewhere.commands.encoding.json.JsonCommandExecutionEncoder;

/**
 * Command destination that makes a CoAP client request to send command data.
 */
public class CoapCommandDestination extends CommandDestination<byte[], CoapParameters> {

    /** Configuration */
    private CoapConfiguration configuration;

    public CoapCommandDestination(CoapConfiguration configuration) {
	this.configuration = configuration;

	setCommandExecutionEncoder(new JsonCommandExecutionEncoder());
	setCommandDeliveryParameterExtractor(
		new MetadataCoapParameterExtractor(new MetadataCoapParameterExtractorConfiguration(this)));
	setCommandDeliveryProvider(new CoapCommandDeliveryProvider());
    }

    protected CoapConfiguration getConfiguration() {
	return configuration;
    }
}