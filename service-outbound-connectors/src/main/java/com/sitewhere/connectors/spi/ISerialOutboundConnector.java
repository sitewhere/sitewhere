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
package com.sitewhere.connectors.spi;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.kafka.IProcessedEventPayload;

/**
 * Outbound connector that processes each batch event individually.
 */
public interface ISerialOutboundConnector extends IOutboundConnector {

    /**
     * Executes processing code for a device measurement event.
     * 
     * @param context
     * @param measurement
     * @throws SiteWhereException
     */
    public void onMeasurement(IDeviceEventContext context, IDeviceMeasurement measurement) throws SiteWhereException;

    /**
     * Executes processing code for a device location event.
     * 
     * @param context
     * @param location
     * @throws SiteWhereException
     */
    public void onLocation(IDeviceEventContext context, IDeviceLocation location) throws SiteWhereException;

    /**
     * Executes processing code for a device alert event.
     * 
     * @param context
     * @param alert
     * @throws SiteWhereException
     */
    public void onAlert(IDeviceEventContext context, IDeviceAlert alert) throws SiteWhereException;

    /**
     * Executes processing code for a device command invocation event.
     * 
     * @param context
     * @param invocation
     * @throws SiteWhereException
     */
    public void onCommandInvocation(IDeviceEventContext context, IDeviceCommandInvocation invocation)
	    throws SiteWhereException;

    /**
     * Executes processing code for a device command response event.
     * 
     * @param context
     * @param response
     * @throws SiteWhereException
     */
    public void onCommandResponse(IDeviceEventContext context, IDeviceCommandResponse response)
	    throws SiteWhereException;

    /**
     * Executes processing code for a device state change event.
     * 
     * @param context
     * @param state
     * @throws SiteWhereException
     */
    public void onStateChange(IDeviceEventContext context, IDeviceStateChange state) throws SiteWhereException;

    /**
     * Handle a single failed record.
     * 
     * @param payload
     * @param t
     * @throws SiteWhereException
     */
    public void handleFailedRecord(IProcessedEventPayload payload, Throwable t) throws SiteWhereException;
}