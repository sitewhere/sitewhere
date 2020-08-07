/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.dweetio;

import com.sitewhere.connectors.SerialOutboundConnector;
import com.sitewhere.connectors.spi.IOutboundConnector;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;

/**
 * Implmentation of {@link IOutboundConnector} that sends events to the cloud
 * provider at dweet.io.
 */
@SuppressWarnings("unused")
public class DweetIoOutboundConnector extends SerialOutboundConnector {

    /** Base URI for REST calls */
    private static final String API_BASE = "https://dweet.io:443/dweet/for/";

    /** Use Spring RestTemplate to send requests */
    // private RestTemplate client;

    /*
     * @see
     * com.sitewhere.connectors.FilteredOutboundConnector#start(com.sitewhere.spi.
     * microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Required for filters.
	super.start(monitor);

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
	sendDweet(measurements);
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onLocation(com.sitewhere.spi
     * .device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceLocation)
     */
    @Override
    public void onLocation(IDeviceEventContext context, IDeviceLocation location) throws SiteWhereException {
	sendDweet(location);
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onAlert(com.sitewhere.spi.
     * device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceAlert)
     */
    @Override
    public void onAlert(IDeviceEventContext context, IDeviceAlert alert) throws SiteWhereException {
	sendDweet(alert);
    }

    /**
     * Send a Dweet with the measurements information.
     * 
     * @param event
     * @return
     * @throws SiteWhereException
     */
    protected boolean sendDweet(IDeviceEvent event) throws SiteWhereException {
	// try {
	// String url = API_BASE + event.getDeviceAssignmentId().toString();
	// ResponseEntity<String> response = getClient().postForEntity(url, event,
	// String.class);
	// if (response.getStatusCode() == HttpStatus.OK) {
	// return true;
	// }
	// throw new SiteWhereException("Unable to create dweet. Status code was: " +
	// response.getStatusCode());
	// } catch (ResourceAccessException e) {
	// throw new SiteWhereException(e);
	// }
	return false;
    }

    // public RestTemplate getClient() {
    // return client;
    // }
    //
    // public void setClient(RestTemplate client) {
    // this.client = client;
    // }
}