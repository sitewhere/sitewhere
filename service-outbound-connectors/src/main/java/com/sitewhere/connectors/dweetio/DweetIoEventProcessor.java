/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.dweetio;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.sitewhere.connectors.FilteredOutboundConnector;
import com.sitewhere.connectors.spi.IOutboundConnector;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Implmentation of {@link IOutboundConnector} that sends events to the cloud
 * provider at dweet.io.
 * 
 * @author Derek
 */
public class DweetIoEventProcessor extends FilteredOutboundConnector {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(DweetIoEventProcessor.class);

    /** Base URI for REST calls */
    private static final String API_BASE = "https://dweet.io:443/dweet/for/";

    /** Use Spring RestTemplate to send requests */
    private RestTemplate client;

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

	this.client = new RestTemplate();
    }

    /*
     * @see com.sitewhere.outbound.FilteredOutboundEventProcessor#
     * onMeasurementsNotFiltered(com.sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceMeasurements)
     */
    @Override
    public void onMeasurementsNotFiltered(IDeviceEventContext context, IDeviceMeasurements measurements)
	    throws SiteWhereException {
	sendDweet(measurements);
    }

    /*
     * @see
     * com.sitewhere.outbound.FilteredOutboundEventProcessor#onLocationNotFiltered(
     * com.sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceLocation)
     */
    @Override
    public void onLocationNotFiltered(IDeviceEventContext context, IDeviceLocation location) throws SiteWhereException {
	sendDweet(location);
    }

    /*
     * @see
     * com.sitewhere.outbound.FilteredOutboundEventProcessor#onAlertNotFiltered(com.
     * sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceAlert)
     */
    @Override
    public void onAlertNotFiltered(IDeviceEventContext context, IDeviceAlert alert) throws SiteWhereException {
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
	try {
	    String url = API_BASE + event.getDeviceAssignmentId().toString();
	    ResponseEntity<String> response = getClient().postForEntity(url, event, String.class);
	    if (response.getStatusCode() == HttpStatus.OK) {
		return true;
	    }
	    throw new SiteWhereException("Unable to create dweet. Status code was: " + response.getStatusCode());
	} catch (ResourceAccessException e) {
	    throw new SiteWhereException(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Log getLogger() {
	return LOGGER;
    }

    public RestTemplate getClient() {
	return client;
    }

    public void setClient(RestTemplate client) {
	this.client = client;
    }
}