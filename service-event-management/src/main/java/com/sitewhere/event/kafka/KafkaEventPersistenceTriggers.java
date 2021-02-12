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
package com.sitewhere.event.kafka;

import java.util.List;

import com.sitewhere.event.DeviceEventManagementDecorator;
import com.sitewhere.event.processing.OutboundPayloadEnrichmentLogic;
import com.sitewhere.event.spi.microservice.IEventManagementTenantEngine;
import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;

/**
 * Adds triggers to event persistence methods to push the new events into a
 * Kafka topic.
 */
public class KafkaEventPersistenceTriggers extends DeviceEventManagementDecorator {

    public KafkaEventPersistenceTriggers(IEventManagementTenantEngine tenantEngine, IDeviceEventManagement delegate) {
	super(delegate);
    }

    /**
     * Forward the given event to the Kafka persisted events topic.
     * 
     * @param context
     * @param events
     * @return
     * @throws SiteWhereException
     */
    protected <T extends IDeviceEvent> List<T> forwardEvents(IDeviceEventContext context, List<T> events)
	    throws SiteWhereException {
	getLogger().debug(String.format("Forwarding %d events to outbound topic.", events.size()));
	for (T event : events) {
	    OutboundPayloadEnrichmentLogic.enrichAndDeliver(getEventManagementTenantEngine(), context, event);
	}
	return events;
    }

    /*
     * @see
     * com.sitewhere.event.DeviceEventManagementDecorator#addDeviceMeasurements(com.
     * sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest[])
     */
    @Override
    public List<? extends IDeviceMeasurement> addDeviceMeasurements(IDeviceEventContext context,
	    IDeviceMeasurementCreateRequest... measurements) throws SiteWhereException {
	return forwardEvents(context, super.addDeviceMeasurements(context, measurements));
    }

    /*
     * @see
     * com.sitewhere.event.DeviceEventManagementDecorator#addDeviceLocations(com.
     * sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest[])
     */
    @Override
    public List<? extends IDeviceLocation> addDeviceLocations(IDeviceEventContext context,
	    IDeviceLocationCreateRequest... request) throws SiteWhereException {
	return forwardEvents(context, super.addDeviceLocations(context, request));
    }

    /*
     * @see com.sitewhere.event.DeviceEventManagementDecorator#addDeviceAlerts(com.
     * sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest[])
     */
    @Override
    public List<? extends IDeviceAlert> addDeviceAlerts(IDeviceEventContext context,
	    IDeviceAlertCreateRequest... request) throws SiteWhereException {
	return forwardEvents(context, super.addDeviceAlerts(context, request));
    }

    /*
     * @see com.sitewhere.event.DeviceEventManagementDecorator#
     * addDeviceCommandInvocations(com.sitewhere.spi.device.event.
     * IDeviceEventContext,
     * com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest[
     * ])
     */
    @Override
    public List<? extends IDeviceCommandInvocation> addDeviceCommandInvocations(IDeviceEventContext context,
	    IDeviceCommandInvocationCreateRequest... request) throws SiteWhereException {
	return forwardEvents(context, super.addDeviceCommandInvocations(context, request));
    }

    /*
     * @see
     * com.sitewhere.event.DeviceEventManagementDecorator#addDeviceCommandResponses(
     * com.sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest[])
     */
    @Override
    public List<? extends IDeviceCommandResponse> addDeviceCommandResponses(IDeviceEventContext context,
	    IDeviceCommandResponseCreateRequest... request) throws SiteWhereException {
	return forwardEvents(context, super.addDeviceCommandResponses(context, request));
    }

    /*
     * @see
     * com.sitewhere.event.DeviceEventManagementDecorator#addDeviceStateChanges(com.
     * sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest[])
     */
    @Override
    public List<? extends IDeviceStateChange> addDeviceStateChanges(IDeviceEventContext context,
	    IDeviceStateChangeCreateRequest... request) throws SiteWhereException {
	return forwardEvents(context, super.addDeviceStateChanges(context, request));
    }

    protected IEventManagementTenantEngine getEventManagementTenantEngine() {
	return (IEventManagementTenantEngine) getTenantEngine();
    }
}