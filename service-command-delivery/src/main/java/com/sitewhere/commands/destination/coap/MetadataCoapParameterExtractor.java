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

import com.sitewhere.commands.spi.ICommandDeliveryParameterExtractor;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Pulls CoAP parameters from device metadata.
 */
public class MetadataCoapParameterExtractor extends TenantEngineLifecycleComponent
	implements ICommandDeliveryParameterExtractor<CoapParameters> {

    /** Default metadata field for remote hostname */
    public static final String DEFAULT_HOSTNAME_METADATA = "coap_hostname";

    /** Default metadata field for remote port */
    public static final String DEFAULT_PORT_METADATA = "coap_port";

    /** Default metadata field for CoAP URL */
    public static final String DEFAULT_URL_METADATA = "coap_url";

    /** Default metadata field for CoAP invocation method */
    public static final String DEFAULT_METHOD_METADATA = "coap_method";

    /** Hostname metadata field name */
    private String hostnameMetadataField = DEFAULT_HOSTNAME_METADATA;

    /** Port metadata field name */
    private String portMetadataField = DEFAULT_PORT_METADATA;

    /** CoAP URL metadata field name */
    private String urlMetadataField = DEFAULT_URL_METADATA;

    /** CoAP invocation method metadata field name */
    private String methodMetadataField = DEFAULT_METHOD_METADATA;

    public MetadataCoapParameterExtractor() {
	super(LifecycleComponentType.CommandParameterExtractor);
    }

    /*
     * @see com.sitewhere.commands.spi.ICommandDeliveryParameterExtractor#
     * extractDeliveryParameters(com.sitewhere.spi.device.IDeviceNestingContext,
     * java.util.List, com.sitewhere.spi.device.command.IDeviceCommandExecution)
     */
    @Override
    public CoapParameters extractDeliveryParameters(IDeviceNestingContext nesting, List<IDeviceAssignment> assignments,
	    IDeviceCommandExecution execution) throws SiteWhereException {
	// Load hostname and port from device metadata.
	Map<String, String> deviceMeta = nesting.getGateway().getMetadata();
	String hostname = deviceMeta.get(getHostnameMetadataField());
	String port = deviceMeta.get(getPortMetadataField());

	// Load url and method from command metadata.
	Map<String, String> commandMeta = execution.getCommand().getMetadata();
	String url = commandMeta.get(getUrlMetadataField());
	String method = commandMeta.get(getMethodMetadataField());

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

    public String getHostnameMetadataField() {
	return hostnameMetadataField;
    }

    public void setHostnameMetadataField(String hostnameMetadataField) {
	this.hostnameMetadataField = hostnameMetadataField;
    }

    public String getPortMetadataField() {
	return portMetadataField;
    }

    public void setPortMetadataField(String portMetadataField) {
	this.portMetadataField = portMetadataField;
    }

    public String getUrlMetadataField() {
	return urlMetadataField;
    }

    public void setUrlMetadataField(String urlMetadataField) {
	this.urlMetadataField = urlMetadataField;
    }

    public String getMethodMetadataField() {
	return methodMetadataField;
    }

    public void setMethodMetadataField(String methodMetadataField) {
	this.methodMetadataField = methodMetadataField;
    }
}