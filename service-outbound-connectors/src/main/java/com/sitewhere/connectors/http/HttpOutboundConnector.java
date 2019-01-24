/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.http;

import org.springframework.web.client.RestTemplate;

import com.sitewhere.connectors.SerialOutboundConnector;
import com.sitewhere.connectors.spi.IOutboundConnector;
import com.sitewhere.connectors.spi.common.IPayloadBuilder;
import com.sitewhere.connectors.spi.common.IUriBuilder;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Implementation of {@link IOutboundConnector} that sends a payload to an HTTP
 * URI.
 */
public class HttpOutboundConnector extends SerialOutboundConnector {

    /** Use Spring RestTemplate to send requests */
    private RestTemplate client;

    /** HTTP method to be invoked */
    private String method = "post";

    /** Component for building URI */
    private IUriBuilder uriBuilder;

    /** Component for building payload */
    private IPayloadBuilder payloadBuilder;

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.initialize(monitor);

	// Verify URI builder is set.
	if (getUriBuilder() == null) {
	    getLogger().warn("No URI builder specified for HTTP outbound connector.");
	} else {
	    initializeNestedComponent(getUriBuilder(), monitor, true);
	}

	// Verify payload builder is set.
	if (getPayloadBuilder() == null) {
	    getLogger().warn("No payload builder specified for HTTP outbound connector.");
	} else {
	    initializeNestedComponent(getUriBuilder(), monitor, true);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.device.event.processor.FilteredOutboundEventProcessor#start
     * (com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Required for filters.
	super.start(monitor);

	// Start URI builder.
	if (getUriBuilder() != null) {
	    startNestedComponent(getUriBuilder(), monitor, true);
	}

	// Start payload builder.
	if (getPayloadBuilder() != null) {
	    startNestedComponent(getUriBuilder(), monitor, true);
	}

	this.client = new RestTemplate();
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onMeasurement(com.sitewhere.
     * spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceMeasurement)
     */
    @Override
    public void onMeasurement(IDeviceEventContext context, IDeviceMeasurement measurements) throws SiteWhereException {
	processDeviceEvent(context, measurements);
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onLocation(com.sitewhere.spi
     * .device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceLocation)
     */
    @Override
    public void onLocation(IDeviceEventContext context, IDeviceLocation location) throws SiteWhereException {
	processDeviceEvent(context, location);
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onAlert(com.sitewhere.spi.
     * device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceAlert)
     */
    @Override
    public void onAlert(IDeviceEventContext context, IDeviceAlert alert) throws SiteWhereException {
	processDeviceEvent(context, alert);
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onCommandInvocation(com.
     * sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceCommandInvocation)
     */
    @Override
    public void onCommandInvocation(IDeviceEventContext context, IDeviceCommandInvocation invocation)
	    throws SiteWhereException {
	processDeviceEvent(context, invocation);
    }

    /*
     * @see com.sitewhere.connectors.SerialOutboundConnector#onCommandResponse(com.
     * sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceCommandResponse)
     */
    @Override
    public void onCommandResponse(IDeviceEventContext context, IDeviceCommandResponse response)
	    throws SiteWhereException {
	processDeviceEvent(context, response);
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onStateChange(com.sitewhere.
     * spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceStateChange)
     */
    @Override
    public void onStateChange(IDeviceEventContext context, IDeviceStateChange state) throws SiteWhereException {
	processDeviceEvent(context, state);
    }

    /**
     * Process a single device event.
     * 
     * @param context
     * @param event
     * @throws SiteWhereException
     */
    protected void processDeviceEvent(IDeviceEventContext context, IDeviceEvent event) throws SiteWhereException {
	if ((getUriBuilder() != null) && (getPayloadBuilder() != null)) {
	    String uri = getUriBuilder().buildUri(this, context, event);
	    byte[] payload = getPayloadBuilder().buildPayload(this, context, event);
	    if ("post".equalsIgnoreCase(method)) {
		getClient().postForLocation(uri, payload);
	    } else if ("put".equalsIgnoreCase(method)) {
		getClient().put(uri, payload);
	    }
	} else {
	    getLogger().warn("Skipping HTTP outbound event due to missing configuration.");
	}
    }

    protected RestTemplate getClient() {
	return client;
    }

    public IUriBuilder getUriBuilder() {
	return uriBuilder;
    }

    public void setUriBuilder(IUriBuilder uriBuilder) {
	this.uriBuilder = uriBuilder;
    }

    public IPayloadBuilder getPayloadBuilder() {
	return payloadBuilder;
    }

    public void setPayloadBuilder(IPayloadBuilder payloadBuilder) {
	this.payloadBuilder = payloadBuilder;
    }

    public String getMethod() {
	return method;
    }

    public void setMethod(String method) {
	this.method = method;
    }
}
