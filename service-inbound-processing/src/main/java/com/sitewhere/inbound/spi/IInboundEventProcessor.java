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
package com.sitewhere.inbound.spi;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest;
import com.sitewhere.spi.device.event.request.ISendDeviceStreamDataRequest;
import com.sitewhere.spi.device.streaming.IDeviceStream;
import com.sitewhere.spi.device.streaming.IDeviceStreamData;
import com.sitewhere.spi.device.streaming.request.IDeviceStreamDataCreateRequest;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Allows interested entities to interact with SiteWhere inbound event
 * processing.
 */
public interface IInboundEventProcessor extends ITenantEngineLifecycleComponent {

    /**
     * Called when a {@link IDeviceRegistrationRequest} is received.
     * 
     * @param hardwareId
     * @param originator
     * @param request
     * @throws SiteWhereException
     */
    public void onRegistrationRequest(String hardwareId, String originator, IDeviceRegistrationRequest request)
	    throws SiteWhereException;

    /**
     * Called when an {@link IDeviceCommandResponseCreateRequest} is received.
     * 
     * @param hardwareId
     * @param originator
     * @param request
     * @throws SiteWhereException
     */
    public void onDeviceCommandResponseRequest(String hardwareId, String originator,
	    IDeviceCommandResponseCreateRequest request) throws SiteWhereException;

    /**
     * Called to request the creation of a new {@link IDeviceMeasurement} based on
     * the given information.
     * 
     * @param hardwareId
     * @param originator
     * @param request
     * @throws SiteWhereException
     */
    public void onDeviceMeasurementsCreateRequest(String hardwareId, String originator,
	    IDeviceMeasurementCreateRequest request) throws SiteWhereException;

    /**
     * Called to request the creation of a new {@link IDeviceLocation} based on the
     * given information.
     * 
     * @param hardwareId
     * @param originator
     * @param request
     * @throws SiteWhereException
     */
    public void onDeviceLocationCreateRequest(String hardwareId, String originator,
	    IDeviceLocationCreateRequest request) throws SiteWhereException;

    /**
     * Called to request the creation of a new {@link IDeviceAlert} based on the
     * given information.
     * 
     * @param hardwareId
     * @param originator
     * @param request
     * @throws SiteWhereException
     */
    public void onDeviceAlertCreateRequest(String hardwareId, String originator, IDeviceAlertCreateRequest request)
	    throws SiteWhereException;

    /**
     * Called to request the creation of a new {@link IDeviceStateChange} based on
     * the given information.
     * 
     * @param hardwareId
     * @param originator
     * @param request
     * @throws SiteWhereException
     */
    public void onDeviceStateChangeCreateRequest(String hardwareId, String originator,
	    IDeviceStateChangeCreateRequest request) throws SiteWhereException;

    /**
     * Called to request the creation of a new {@link IDeviceStream} based on the
     * given information.
     * 
     * @param hardwareId
     * @param originator
     * @param request
     * @throws SiteWhereException
     */
    public void onDeviceStreamCreateRequest(String hardwareId, String originator, IDeviceStreamCreateRequest request)
	    throws SiteWhereException;

    /**
     * Called to request the creation of a new {@link IDeviceStreamData} based on
     * the given information.
     * 
     * @param hardwareId
     * @param originator
     * @param request
     * @throws SiteWhereException
     */
    public void onDeviceStreamDataCreateRequest(String hardwareId, String originator,
	    IDeviceStreamDataCreateRequest request) throws SiteWhereException;

    /**
     * Called to request that a chunk of {@link IDeviceStreamData} be sent to a
     * device.
     * 
     * @param hardwareId
     * @param originator
     * @param request
     * @throws SiteWhereException
     */
    public void onSendDeviceStreamDataRequest(String hardwareId, String originator,
	    ISendDeviceStreamDataRequest request) throws SiteWhereException;
}