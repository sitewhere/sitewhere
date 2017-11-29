/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.destinations.coap;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.communication.ICommandDeliveryParameterExtractor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Pulls CoAP parameters from device metadata.
 * 
 * @author Derek
 */
public class MetadataCoapParameterExtractor extends TenantEngineLifecycleComponent
	implements ICommandDeliveryParameterExtractor<CoapParameters> {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Default metadata field for remote hostname */
    public static final String DEFAULT_HOSTNAME_METADATA = "hostname";

    /** Default metadata field for remote port */
    public static final String DEFAULT_PORT_METADATA = "port";

    /** Default metadata field for CoAP URL */
    public static final String DEFAULT_URL_METADATA = "url";

    /** Default metadata field for CoAP invocation method */
    public static final String DEFAULT_METHOD_METADATA = "method";

    /** Hostname metadata field name */
    private String hostnameMetadataField = DEFAULT_HOSTNAME_METADATA;

    /** Port metadata field name */
    private String portMetadataField = DEFAULT_PORT_METADATA;

    /** CoAP URL metadata field name */
    private String urlMetadataField = DEFAULT_URL_METADATA;

    /** CoAP invocation method metadata field name */
    private String methodMetadataField = DEFAULT_METHOD_METADATA;

    /** Overrides port metadata */
    private Integer portOverride;

    public MetadataCoapParameterExtractor() {
	super(LifecycleComponentType.CommandParameterExtractor);
    }

    public MetadataCoapParameterExtractor(int portOverride) {
	super(LifecycleComponentType.CommandParameterExtractor);
	this.portOverride = portOverride;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.
     * ICommandDeliveryParameterExtractor#
     * extractDeliveryParameters(com.sitewhere.spi.device.IDeviceNestingContext,
     * com.sitewhere.spi.device.IDeviceAssignment,
     * com.sitewhere.spi.device.command.IDeviceCommandExecution)
     */
    @Override
    public CoapParameters extractDeliveryParameters(IDeviceNestingContext nesting, IDeviceAssignment assignment,
	    IDeviceCommandExecution execution) throws SiteWhereException {
	Map<String, String> metadata = nesting.getGateway().getMetadata();
	String hostname = metadata.get(getHostnameMetadataField());
	String port = metadata.get(getPortMetadataField());
	String url = metadata.get(getUrlMetadataField());
	String method = metadata.get(getMethodMetadataField());
	CoapParameters coap = new CoapParameters();
	coap.setHostname(hostname);
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

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
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

    public Integer getPortOverride() {
	return portOverride;
    }

    public void setPortOverride(Integer portOverride) {
	this.portOverride = portOverride;
    }
}