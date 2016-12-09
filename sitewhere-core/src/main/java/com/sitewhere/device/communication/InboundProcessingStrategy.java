/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication;

import com.sitewhere.SiteWhere;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.IDecodedDeviceRequest;
import com.sitewhere.spi.device.communication.IInboundProcessingStrategy;
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
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Abstract base class for inbound processing strategies.
 * 
 * @author Derek
 */
public abstract class InboundProcessingStrategy extends TenantLifecycleComponent implements IInboundProcessingStrategy {

    public InboundProcessingStrategy() {
	super(LifecycleComponentType.InboundProcessingStrategy);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IInboundProcessingStrategy#
     * sendToInboundProcessingChain
     * (com.sitewhere.spi.device.communication.IDecodedDeviceEventRequest)
     */
    @Override
    public void sendToInboundProcessingChain(IDecodedDeviceRequest<?> decoded) throws SiteWhereException {
	if (decoded.getRequest() instanceof IDeviceRegistrationRequest) {
	    getInboundProcessorChain().onRegistrationRequest(decoded.getHardwareId(), decoded.getOriginator(),
		    ((IDeviceRegistrationRequest) decoded.getRequest()));
	} else if (decoded.getRequest() instanceof IDeviceCommandResponseCreateRequest) {
	    getInboundProcessorChain().onDeviceCommandResponseRequest(decoded.getHardwareId(), decoded.getOriginator(),
		    ((IDeviceCommandResponseCreateRequest) decoded.getRequest()));
	} else if (decoded.getRequest() instanceof IDeviceMeasurementsCreateRequest) {
	    getInboundProcessorChain().onDeviceMeasurementsCreateRequest(decoded.getHardwareId(),
		    decoded.getOriginator(), ((IDeviceMeasurementsCreateRequest) decoded.getRequest()));
	} else if (decoded.getRequest() instanceof IDeviceLocationCreateRequest) {
	    getInboundProcessorChain().onDeviceLocationCreateRequest(decoded.getHardwareId(), decoded.getOriginator(),
		    ((IDeviceLocationCreateRequest) decoded.getRequest()));
	} else if (decoded.getRequest() instanceof IDeviceAlertCreateRequest) {
	    getInboundProcessorChain().onDeviceAlertCreateRequest(decoded.getHardwareId(), decoded.getOriginator(),
		    ((IDeviceAlertCreateRequest) decoded.getRequest()));
	} else if (decoded.getRequest() instanceof IDeviceStateChangeCreateRequest) {
	    getInboundProcessorChain().onDeviceStateChangeCreateRequest(decoded.getHardwareId(),
		    decoded.getOriginator(), ((IDeviceStateChangeCreateRequest) decoded.getRequest()));
	} else if (decoded.getRequest() instanceof IDeviceStreamCreateRequest) {
	    getInboundProcessorChain().onDeviceStreamCreateRequest(decoded.getHardwareId(), decoded.getOriginator(),
		    ((IDeviceStreamCreateRequest) decoded.getRequest()));
	} else if (decoded.getRequest() instanceof IDeviceStreamDataCreateRequest) {
	    getInboundProcessorChain().onDeviceStreamDataCreateRequest(decoded.getHardwareId(), decoded.getOriginator(),
		    ((IDeviceStreamDataCreateRequest) decoded.getRequest()));
	} else if (decoded.getRequest() instanceof ISendDeviceStreamDataRequest) {
	    getInboundProcessorChain().onSendDeviceStreamDataRequest(decoded.getHardwareId(), decoded.getOriginator(),
		    ((ISendDeviceStreamDataRequest) decoded.getRequest()));
	} else if (decoded.getRequest() instanceof IDeviceMappingCreateRequest) {
	    getInboundProcessorChain().onDeviceMappingCreateRequest(decoded.getHardwareId(), decoded.getOriginator(),
		    ((IDeviceMappingCreateRequest) decoded.getRequest()));
	} else {
	    throw new RuntimeException("Unknown device event type: " + decoded.getRequest().getClass().getName());
	}
    }

    /**
     * Get the inbound processing chain implementation for this tenant.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected IInboundEventProcessorChain getInboundProcessorChain() throws SiteWhereException {
	return SiteWhere.getServer().getEventProcessing(getTenant()).getInboundEventProcessorChain();
    }
}