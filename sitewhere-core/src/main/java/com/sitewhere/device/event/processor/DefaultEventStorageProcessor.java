/*
 * DefaultEventStorageProcessor.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.event.processor;

import org.apache.log4j.Logger;

import com.sitewhere.rest.model.device.event.processor.InboundEventProcessor;
import com.sitewhere.rest.model.device.request.DeviceAssignmentCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceCreateRequest;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.event.processor.IInboundEventProcessor;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;

/**
 * Implementation of {@link IInboundEventProcessor} that attempts to store the inbound
 * event request using device management APIs.
 * 
 * @author Derek
 */
public class DefaultEventStorageProcessor extends InboundEventProcessor {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(DefaultEventStorageProcessor.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IInboundEventProcessor#onRegistrationRequest
	 * (com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest)
	 */
	@Override
	public void onRegistrationRequest(IDeviceRegistrationRequest request) throws SiteWhereException {
		LOGGER.info("Handling device registration request.");
		IDevice device =
				SiteWhereServer.getInstance().getDeviceManagement().getDeviceByHardwareId(
						request.getHardwareId());
		IDeviceSpecification specification =
				SiteWhereServer.getInstance().getDeviceManagement().getDeviceSpecificationByToken(
						request.getSpecificationToken());
		// Create device if it does not already exist.
		if (device == null) {
			LOGGER.info("Creating new device as part of registration.");
			if (specification == null) {
				throw new SiteWhereSystemException(ErrorCode.InvalidDeviceSpecificationToken,
						ErrorLevel.ERROR);
			}
			DeviceCreateRequest deviceCreate = new DeviceCreateRequest();
			deviceCreate.setHardwareId(request.getHardwareId());
			deviceCreate.setSpecificationToken(request.getSpecificationToken());
			deviceCreate.setComments("Device created by on-demand registration.");
			device = SiteWhereServer.getInstance().getDeviceManagement().createDevice(deviceCreate);
		} else if (!device.getSpecificationToken().equals(request.getSpecificationToken())) {
			// TODO: Is this an error or a valid use case?
			throw new SiteWhereException(
					"Attempting to register device with different specification that currently assigned");
		}
		// Make sure device is assigned.
		if (device.getAssignmentToken() == null) {
			LOGGER.info("Handling unassigned device for registration.");
			DeviceAssignmentCreateRequest assnCreate = new DeviceAssignmentCreateRequest();
		}
	}
}