/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication;

import com.sitewhere.server.lifecycle.LifecycleComponentDecorator;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.IDecodedDeviceRequest;
import com.sitewhere.spi.device.communication.IInboundProcessingStrategy;
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
import com.sitewhere.spi.tenant.ITenant;

/**
 * Allows additional functionality to be triggered as part of the inbound
 * processing strategy.
 * 
 * @author Derek
 */
public class InboundProcessingStrategyDecorator extends LifecycleComponentDecorator
	implements IInboundProcessingStrategy {

    /** Wrapped instance */
    private IInboundProcessingStrategy delegate;

    public InboundProcessingStrategyDecorator(IInboundProcessingStrategy delegate) {
	super(delegate);
	this.delegate = delegate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent#setTenant(
     * com. sitewhere.spi.tenant.ITenant)
     */
    @Override
    public void setTenant(ITenant tenant) {
	delegate.setTenant(tenant);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent#getTenant()
     */
    @Override
    public ITenant getTenant() {
	return delegate.getTenant();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IInboundProcessingStrategy#
     * processRegistration(com.sitewhere.spi.device.communication.
     * IDecodedDeviceRequest)
     */
    @Override
    public void processRegistration(IDecodedDeviceRequest<IDeviceRegistrationRequest> request)
	    throws SiteWhereException {
	delegate.processRegistration(request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IInboundProcessingStrategy#
     * processDeviceCommandResponse(com.sitewhere.spi.device.communication.
     * IDecodedDeviceRequest)
     */
    @Override
    public void processDeviceCommandResponse(IDecodedDeviceRequest<IDeviceCommandResponseCreateRequest> request)
	    throws SiteWhereException {
	delegate.processDeviceCommandResponse(request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IInboundProcessingStrategy#
     * processDeviceMeasurements(com.sitewhere.spi.device.communication.
     * IDecodedDeviceRequest)
     */
    @Override
    public void processDeviceMeasurements(IDecodedDeviceRequest<IDeviceMeasurementsCreateRequest> request)
	    throws SiteWhereException {
	delegate.processDeviceMeasurements(request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IInboundProcessingStrategy#
     * processDeviceLocation(com.sitewhere.spi.device.communication.
     * IDecodedDeviceRequest)
     */
    @Override
    public void processDeviceLocation(IDecodedDeviceRequest<IDeviceLocationCreateRequest> request)
	    throws SiteWhereException {
	delegate.processDeviceLocation(request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IInboundProcessingStrategy#
     * processDeviceAlert(com.sitewhere.spi.device.communication.
     * IDecodedDeviceRequest)
     */
    @Override
    public void processDeviceAlert(IDecodedDeviceRequest<IDeviceAlertCreateRequest> request) throws SiteWhereException {
	delegate.processDeviceAlert(request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IInboundProcessingStrategy#
     * processDeviceStateChange(com.sitewhere.spi.device.communication.
     * IDecodedDeviceRequest)
     */
    @Override
    public void processDeviceStateChange(IDecodedDeviceRequest<IDeviceStateChangeCreateRequest> request)
	    throws SiteWhereException {
	delegate.processDeviceStateChange(request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IInboundProcessingStrategy#
     * processDeviceStream(com.sitewhere.spi.device.communication.
     * IDecodedDeviceRequest)
     */
    @Override
    public void processDeviceStream(IDecodedDeviceRequest<IDeviceStreamCreateRequest> request)
	    throws SiteWhereException {
	delegate.processDeviceStream(request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IInboundProcessingStrategy#
     * processDeviceStreamData(com.sitewhere.spi.device.communication.
     * IDecodedDeviceRequest)
     */
    @Override
    public void processDeviceStreamData(IDecodedDeviceRequest<IDeviceStreamDataCreateRequest> request)
	    throws SiteWhereException {
	delegate.processDeviceStreamData(request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IInboundProcessingStrategy#
     * processSendDeviceStreamData(com.sitewhere.spi.device.communication.
     * IDecodedDeviceRequest)
     */
    @Override
    public void processSendDeviceStreamData(IDecodedDeviceRequest<ISendDeviceStreamDataRequest> request)
	    throws SiteWhereException {
	delegate.processSendDeviceStreamData(request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IInboundProcessingStrategy#
     * processCreateDeviceMapping(com.sitewhere.spi.device.communication.
     * IDecodedDeviceRequest)
     */
    @Override
    public void processCreateDeviceMapping(IDecodedDeviceRequest<IDeviceMappingCreateRequest> request)
	    throws SiteWhereException {
	delegate.processCreateDeviceMapping(request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IInboundProcessingStrategy#
     * sendToInboundProcessingChain(com.sitewhere.spi.device.communication.
     * IDecodedDeviceRequest)
     */
    @Override
    public void sendToInboundProcessingChain(IDecodedDeviceRequest<?> request) throws SiteWhereException {
	delegate.sendToInboundProcessingChain(request);
    }
}