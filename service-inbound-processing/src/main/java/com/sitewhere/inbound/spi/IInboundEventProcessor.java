/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.inbound.spi;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.IDeviceStreamData;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMappingCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest;
import com.sitewhere.spi.device.event.request.ISendDeviceStreamDataRequest;
import com.sitewhere.spi.device.streaming.IDeviceStream;
import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

/**
 * Allows interested entities to interact with SiteWhere inbound event
 * processing.
 * 
 * @author Derek
 */
public interface IInboundEventProcessor extends ITenantLifecycleComponent {

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
     * Called to request the creation of a new {@link IDeviceMeasurements} based
     * on the given information.
     * 
     * @param hardwareId
     * @param originator
     * @param request
     * @throws SiteWhereException
     */
    public void onDeviceMeasurementsCreateRequest(String hardwareId, String originator,
	    IDeviceMeasurementsCreateRequest request) throws SiteWhereException;

    /**
     * Called to request the creation of a new {@link IDeviceLocation} based on
     * the given information.
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
     * Called to request the creation of a new {@link IDeviceStateChange} based
     * on the given information.
     * 
     * @param hardwareId
     * @param originator
     * @param request
     * @throws SiteWhereException
     */
    public void onDeviceStateChangeCreateRequest(String hardwareId, String originator,
	    IDeviceStateChangeCreateRequest request) throws SiteWhereException;

    /**
     * Called to request the creation of a new {@link IDeviceStream} based on
     * the given information.
     * 
     * @param hardwareId
     * @param originator
     * @param request
     * @throws SiteWhereException
     */
    public void onDeviceStreamCreateRequest(String hardwareId, String originator, IDeviceStreamCreateRequest request)
	    throws SiteWhereException;

    /**
     * Called to request the creation of a new {@link IDeviceStreamData} based
     * on the given information.
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

    /**
     * Called to request that a device be mapped to a path on a composite
     * device.
     * 
     * @param hardwareId
     * @param originator
     * @param request
     * @throws SiteWhereException
     */
    public void onDeviceMappingCreateRequest(String hardwareId, String originator, IDeviceMappingCreateRequest request)
	    throws SiteWhereException;
}