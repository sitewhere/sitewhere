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
package com.sitewhere.connectors;

import java.util.List;

import com.sitewhere.connectors.spi.ISerialOutboundConnector;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.kafka.IProcessedEventPayload;

/**
 * Outbound connector that routes each event in a batch to a handler in a serial
 * fashion.
 */
public class SerialOutboundConnector extends FilteredOutboundConnector implements ISerialOutboundConnector {

    /*
     * @see com.sitewhere.connectors.spi.IFilteredOutboundConnector#
     * processFilteredEventBatch(java.util.List)
     */
    @Override
    public void processFilteredEventBatch(List<IProcessedEventPayload> payloads) throws SiteWhereException {
	for (IProcessedEventPayload payload : payloads) {
	    try {
		IDeviceEventContext context = payload.getEventContext();
		IDeviceEvent event = payload.getEvent();
		switch (event.getEventType()) {
		case Alert: {
		    onAlert(context, (IDeviceAlert) event);
		    break;
		}
		case CommandInvocation: {
		    onCommandInvocation(context, (IDeviceCommandInvocation) event);
		    break;
		}
		case CommandResponse: {
		    onCommandResponse(context, (IDeviceCommandResponse) event);
		    break;
		}
		case Location: {
		    onLocation(context, (IDeviceLocation) event);
		    break;
		}
		case Measurement: {
		    onMeasurement(context, (IDeviceMeasurement) event);
		    break;
		}
		case StateChange: {
		    onStateChange(context, (IDeviceStateChange) event);
		    break;
		}
		default: {
		    throw new SiteWhereException("Unknown event type. " + event.getEventType().name());
		}
		}
	    } catch (Throwable e) {
		handleFailedRecord(payload, e);
	    }
	}
    }

    /*
     * @see
     * com.sitewhere.connectors.spi.ISerialOutboundConnector#handleFailedRecord(com.
     * sitewhere.spi.device.event.kafka.IEnrichedEventPayload, java.lang.Throwable)
     */
    @Override
    public void handleFailedRecord(IProcessedEventPayload payload, Throwable t) throws SiteWhereException {
	getLogger().warn("Failed to process outbound connector record.", t);
    }

    /*
     * @see
     * com.sitewhere.connectors.spi.IOutboundConnector#onMeasurement(com.sitewhere.
     * spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceMeasurement)
     */
    @Override
    public void onMeasurement(IDeviceEventContext context, IDeviceMeasurement measurements) throws SiteWhereException {
    }

    /*
     * @see
     * com.sitewhere.connectors.spi.IOutboundConnector#onLocation(com.sitewhere.spi.
     * device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceLocation)
     */
    @Override
    public void onLocation(IDeviceEventContext context, IDeviceLocation location) throws SiteWhereException {
    }

    /*
     * @see
     * com.sitewhere.connectors.spi.IOutboundConnector#onAlert(com.sitewhere.spi.
     * device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceAlert)
     */
    @Override
    public void onAlert(IDeviceEventContext context, IDeviceAlert alert) throws SiteWhereException {
    }

    /*
     * @see com.sitewhere.connectors.spi.IOutboundConnector#onCommandInvocation(com.
     * sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceCommandInvocation)
     */
    @Override
    public void onCommandInvocation(IDeviceEventContext context, IDeviceCommandInvocation invocation)
	    throws SiteWhereException {
    }

    /*
     * @see com.sitewhere.connectors.spi.IOutboundConnector#onCommandResponse(com.
     * sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceCommandResponse)
     */
    @Override
    public void onCommandResponse(IDeviceEventContext context, IDeviceCommandResponse response)
	    throws SiteWhereException {
    }

    /*
     * @see
     * com.sitewhere.connectors.spi.IOutboundConnector#onStateChange(com.sitewhere.
     * spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceStateChange)
     */
    @Override
    public void onStateChange(IDeviceEventContext context, IDeviceStateChange state) throws SiteWhereException {
    }

    /*
     * @see
     * com.sitewhere.connectors.spi.IOutboundConnector#handleFailedBatch(java.util.
     * List, java.lang.Throwable)
     */
    @Override
    public void handleFailedBatch(List<IProcessedEventPayload> payloads, Throwable failReason)
	    throws SiteWhereException {
    }
}