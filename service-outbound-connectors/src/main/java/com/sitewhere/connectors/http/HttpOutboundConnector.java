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
package com.sitewhere.connectors.http;

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
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;

/**
 * Implementation of {@link IOutboundConnector} that sends a payload to an HTTP
 * URI.
 */
public class HttpOutboundConnector extends SerialOutboundConnector {

    /** Use Spring RestTemplate to send requests */
    // private RestTemplate client;

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
	    initializeNestedComponent(getPayloadBuilder(), monitor, true);
	}
    }

    /*
     * @see
     * com.sitewhere.connectors.FilteredOutboundConnector#start(com.sitewhere.spi.
     * microservice.lifecycle.ILifecycleProgressMonitor)
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
	    startNestedComponent(getPayloadBuilder(), monitor, true);
	}

	// this.client = new RestTemplate();
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
	// try {
	// if ((getUriBuilder() != null) && (getPayloadBuilder() != null)) {
	// String uri = getUriBuilder().buildUri(this, context, event);
	// byte[] payload = getPayloadBuilder().buildPayload(this, context, event);
	// if ("post".equalsIgnoreCase(method)) {
	// getClient().postForLocation(uri, payload);
	// } else if ("put".equalsIgnoreCase(method)) {
	// getClient().put(uri, payload);
	// }
	// } else {
	// getLogger().warn("Skipping HTTP outbound event due to missing
	// configuration.");
	// }
	// } catch (RestClientException e) {
	// getLogger().error(String.format("Unable to send HTTP payload: %s",
	// e.getMessage()));
	// if (getLogger().isDebugEnabled()) {
	// getLogger().error("Error sending payload via REST client.", e);
	// }
	// }
    }

    // protected RestTemplate getClient() {
    // return client;
    // }

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
