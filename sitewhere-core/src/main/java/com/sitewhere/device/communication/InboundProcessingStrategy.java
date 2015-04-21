/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication;

import com.sitewhere.SiteWhere;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.IDecodedDeviceRequest;
import com.sitewhere.spi.device.communication.IInboundProcessingStrategy;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest;
import com.sitewhere.spi.device.event.request.ISendDeviceStreamDataRequest;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Abstract base class for inbound processing strategies.
 * 
 * @author Derek
 */
public abstract class InboundProcessingStrategy extends LifecycleComponent implements
		IInboundProcessingStrategy {

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
			SiteWhere.getServer().getInboundEventProcessorChain().onRegistrationRequest(
					decoded.getHardwareId(), decoded.getOriginator(),
					((IDeviceRegistrationRequest) decoded.getRequest()));
		} else if (decoded.getRequest() instanceof IDeviceCommandResponseCreateRequest) {
			SiteWhere.getServer().getInboundEventProcessorChain().onDeviceCommandResponseRequest(
					decoded.getHardwareId(), decoded.getOriginator(),
					((IDeviceCommandResponseCreateRequest) decoded.getRequest()));
		} else if (decoded.getRequest() instanceof IDeviceMeasurementsCreateRequest) {
			SiteWhere.getServer().getInboundEventProcessorChain().onDeviceMeasurementsCreateRequest(
					decoded.getHardwareId(), decoded.getOriginator(),
					((IDeviceMeasurementsCreateRequest) decoded.getRequest()));
		} else if (decoded.getRequest() instanceof IDeviceLocationCreateRequest) {
			SiteWhere.getServer().getInboundEventProcessorChain().onDeviceLocationCreateRequest(
					decoded.getHardwareId(), decoded.getOriginator(),
					((IDeviceLocationCreateRequest) decoded.getRequest()));
		} else if (decoded.getRequest() instanceof IDeviceAlertCreateRequest) {
			SiteWhere.getServer().getInboundEventProcessorChain().onDeviceAlertCreateRequest(
					decoded.getHardwareId(), decoded.getOriginator(),
					((IDeviceAlertCreateRequest) decoded.getRequest()));
		} else if (decoded.getRequest() instanceof IDeviceStreamCreateRequest) {
			SiteWhere.getServer().getInboundEventProcessorChain().onDeviceStreamCreateRequest(
					decoded.getHardwareId(), decoded.getOriginator(),
					((IDeviceStreamCreateRequest) decoded.getRequest()));
		} else if (decoded.getRequest() instanceof IDeviceStreamDataCreateRequest) {
			SiteWhere.getServer().getInboundEventProcessorChain().onDeviceStreamDataCreateRequest(
					decoded.getHardwareId(), decoded.getOriginator(),
					((IDeviceStreamDataCreateRequest) decoded.getRequest()));
		} else if (decoded.getRequest() instanceof ISendDeviceStreamDataRequest) {
			SiteWhere.getServer().getInboundEventProcessorChain().onSendDeviceStreamDataRequest(
					decoded.getHardwareId(), decoded.getOriginator(),
					((ISendDeviceStreamDataRequest) decoded.getRequest()));
		} else {
			throw new RuntimeException("Unknown device event type: "
					+ decoded.getRequest().getClass().getName());
		}
	}
}