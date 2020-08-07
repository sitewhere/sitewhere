/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.destination.coap;

import java.util.List;
import java.util.Map;

import com.sitewhere.commands.configuration.extractors.coap.MetadataCoapParameterExtractorConfiguration;
import com.sitewhere.commands.spi.ICommandDeliveryParameterExtractor;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;

/**
 * Pulls CoAP parameters from device metadata.
 */
public class MetadataCoapParameterExtractor extends TenantEngineLifecycleComponent
	implements ICommandDeliveryParameterExtractor<CoapParameters> {

    private MetadataCoapParameterExtractorConfiguration configuration;

    public MetadataCoapParameterExtractor(MetadataCoapParameterExtractorConfiguration configuration) {
	super(LifecycleComponentType.CommandParameterExtractor);
	this.configuration = configuration;
    }

    /*
     * @see com.sitewhere.commands.spi.ICommandDeliveryParameterExtractor#
     * extractDeliveryParameters(com.sitewhere.spi.device.IDeviceNestingContext,
     * java.util.List, com.sitewhere.spi.device.command.IDeviceCommandExecution)
     */
    @Override
    public CoapParameters extractDeliveryParameters(IDeviceNestingContext nesting,
	    List<? extends IDeviceAssignment> assignments, IDeviceCommandExecution execution)
	    throws SiteWhereException {
	// Load hostname and port from device metadata.
	Map<String, String> deviceMeta = nesting.getGateway().getMetadata();
	String hostname = deviceMeta.get(getConfiguration().getHostnameMetadataField());
	String port = deviceMeta.get(getConfiguration().getPortMetadataField());

	// Load url and method from command metadata.
	Map<String, String> commandMeta = execution.getCommand().getMetadata();
	String url = commandMeta.get(getConfiguration().getUrlMetadataField());
	String method = commandMeta.get(getConfiguration().getMethodMetadataField());

	CoapParameters coap = new CoapParameters();
	coap.setHostname(hostname);
	if (hostname == null) {
	    throw new SiteWhereException("Hostname not found in device metadata. Unable to deliver command.");
	}
	if (port != null) {
	    coap.setPort(Integer.parseInt(port));
	}
	if (url != null) {
	    coap.setUrl(url);
	}
	if (method != null) {
	    coap.setMethod(method);
	}
	return coap;
    }

    protected MetadataCoapParameterExtractorConfiguration getConfiguration() {
	return configuration;
    }
}