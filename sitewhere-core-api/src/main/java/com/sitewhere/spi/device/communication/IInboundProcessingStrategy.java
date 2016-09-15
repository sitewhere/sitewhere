/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.communication;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.processor.IInboundEventProcessorChain;
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
import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

/**
 * Provides a strategy for moving decoded events from an
 * {@link IInboundEventSource} onto the {@link IInboundEventProcessorChain}.
 * 
 * @author Derek
 */
public interface IInboundProcessingStrategy extends ITenantLifecycleComponent {

    /**
     * Process an {@link IDeviceRegistrationRequest}.
     * 
     * @param request
     * @throws SiteWhereException
     */
    public void processRegistration(IDecodedDeviceRequest<IDeviceRegistrationRequest> request)
	    throws SiteWhereException;

    /**
     * Process an {@link IDeviceCommandResponseCreateRequest}.
     * 
     * @param request
     * @throws SiteWhereException
     */
    public void processDeviceCommandResponse(IDecodedDeviceRequest<IDeviceCommandResponseCreateRequest> request)
	    throws SiteWhereException;

    /**
     * Process an {@link IDeviceMeasurementsCreateRequest}.
     * 
     * @param request
     * @throws SiteWhereException
     */
    public void processDeviceMeasurements(IDecodedDeviceRequest<IDeviceMeasurementsCreateRequest> request)
	    throws SiteWhereException;

    /**
     * Process an {@link IDeviceLocationCreateRequest}.
     * 
     * @param request
     * @throws SiteWhereException
     */
    public void processDeviceLocation(IDecodedDeviceRequest<IDeviceLocationCreateRequest> request)
	    throws SiteWhereException;

    /**
     * Process an {@link IDeviceAlertCreateRequest}.
     * 
     * @param request
     * @throws SiteWhereException
     */
    public void processDeviceAlert(IDecodedDeviceRequest<IDeviceAlertCreateRequest> request) throws SiteWhereException;

    /**
     * Process an {@link IDeviceStateChangeCreateRequest}.
     * 
     * @param request
     * @throws SiteWhereException
     */
    public void processDeviceStateChange(IDecodedDeviceRequest<IDeviceStateChangeCreateRequest> request)
	    throws SiteWhereException;

    /**
     * Process an {@link IDeviceStreamCreateRequest}.
     * 
     * @param request
     * @throws SiteWhereException
     */
    public void processDeviceStream(IDecodedDeviceRequest<IDeviceStreamCreateRequest> request)
	    throws SiteWhereException;

    /**
     * Process an {@link IDeviceStreamDataCreateRequest}.
     * 
     * @param request
     * @throws SiteWhereException
     */
    public void processDeviceStreamData(IDecodedDeviceRequest<IDeviceStreamDataCreateRequest> request)
	    throws SiteWhereException;

    /**
     * Process an {@link ISendDeviceStreamDataRequest}.
     * 
     * @param request
     * @throws SiteWhereException
     */
    public void processSendDeviceStreamData(IDecodedDeviceRequest<ISendDeviceStreamDataRequest> request)
	    throws SiteWhereException;

    /**
     * Process an {@link IDeviceMappingCreateRequest}.
     * 
     * @param request
     * @throws SiteWhereException
     */
    public void processCreateDeviceMapping(IDecodedDeviceRequest<IDeviceMappingCreateRequest> request)
	    throws SiteWhereException;

    /**
     * Sends a decoded request to the inbound processing chain.
     * 
     * @param request
     * @throws SiteWhereException
     */
    public void sendToInboundProcessingChain(IDecodedDeviceRequest<?> request) throws SiteWhereException;
}