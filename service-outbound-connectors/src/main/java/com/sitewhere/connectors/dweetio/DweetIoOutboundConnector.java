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