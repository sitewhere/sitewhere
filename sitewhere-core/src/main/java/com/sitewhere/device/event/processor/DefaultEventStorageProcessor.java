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
import com.sitewhere.rest.model.device.event.request.DeviceCommandResponseCreateRequest;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.processor.IInboundEventProcessor;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
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
	@SuppressWarnings("unused")
	private static Logger LOGGER = Logger.getLogger(DefaultEventStorageProcessor.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.rest.model.device.event.processor.InboundEventProcessor#
	 * onRegistrationRequest(java.lang.String, java.lang.String,
	 * com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest)
	 */
	@Override
	public void onRegistrationRequest(String hardwareId, String originator, IDeviceRegistrationRequest request)
			throws SiteWhereException {
		SiteWhereServer.getInstance().getDeviceProvisioning().getRegistrationManager().handleDeviceRegistration(
				request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.rest.model.device.event.processor.InboundEventProcessor#
	 * onDeviceCommandResponseRequest(java.lang.String, java.lang.String,
	 * com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest)
	 */
	@Override
	public void onDeviceCommandResponseRequest(String hardwareId, String originator,
			IDeviceCommandResponseCreateRequest request) throws SiteWhereException {
		IDeviceAssignment assignment = getCurrentAssignment(hardwareId);
		SiteWhereServer.getInstance().getDeviceManagement().addDeviceCommandResponse(assignment.getToken(),
				request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.rest.model.device.event.processor.InboundEventProcessor#
	 * onDeviceMeasurementsCreateRequest(java.lang.String, java.lang.String,
	 * com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest)
	 */
	@Override
	public void onDeviceMeasurementsCreateRequest(String hardwareId, String originator,
			IDeviceMeasurementsCreateRequest request) throws SiteWhereException {
		IDeviceAssignment assignment = getCurrentAssignment(hardwareId);
		IDeviceMeasurements measurements =
				SiteWhereServer.getInstance().getDeviceManagement().addDeviceMeasurements(
						assignment.getToken(), request, true);
		handleLinkResponseToInvocation(originator, measurements.getId(), assignment);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.rest.model.device.event.processor.InboundEventProcessor#
	 * onDeviceLocationCreateRequest(java.lang.String, java.lang.String,
	 * com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest)
	 */
	@Override
	public void onDeviceLocationCreateRequest(String hardwareId, String originator,
			IDeviceLocationCreateRequest request) throws SiteWhereException {
		IDeviceAssignment assignment = getCurrentAssignment(hardwareId);
		IDeviceLocation location =
				SiteWhereServer.getInstance().getDeviceManagement().addDeviceLocation(assignment.getToken(),
						request, true);
		handleLinkResponseToInvocation(originator, location.getId(), assignment);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.rest.model.device.event.processor.InboundEventProcessor#
	 * onDeviceAlertCreateRequest(java.lang.String, java.lang.String,
	 * com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest)
	 */
	@Override
	public void onDeviceAlertCreateRequest(String hardwareId, String originator,
			IDeviceAlertCreateRequest request) throws SiteWhereException {
		IDeviceAssignment assignment = getCurrentAssignment(hardwareId);
		IDeviceAlert alert =
				SiteWhereServer.getInstance().getDeviceManagement().addDeviceAlert(assignment.getToken(),
						request, true);
		handleLinkResponseToInvocation(originator, alert.getId(), assignment);
	}

	/**
	 * Get the current assignment or throw errors if it can not be resolved.
	 * 
	 * @param hardwareId
	 * @return
	 * @throws SiteWhereException
	 */
	protected IDeviceAssignment getCurrentAssignment(String hardwareId) throws SiteWhereException {
		IDevice device =
				SiteWhereServer.getInstance().getDeviceManagement().getDeviceByHardwareId(hardwareId);
		if (device == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidHardwareId, ErrorLevel.ERROR);
		}
		if (device.getAssignmentToken() == null) {
			throw new SiteWhereSystemException(ErrorCode.DeviceNotAssigned, ErrorLevel.ERROR);
		}
		return SiteWhereServer.getInstance().getDeviceManagement().getDeviceAssignmentByToken(
				device.getAssignmentToken());
	}

	/**
	 * If an originator was assocaited with the event, create a
	 * {@link IDeviceCommandResponse} that links back to the original invocation.
	 * 
	 * @param originator
	 * @param eventId
	 * @param assignment
	 * @throws SiteWhereException
	 */
	protected void handleLinkResponseToInvocation(String originator, String eventId,
			IDeviceAssignment assignment) throws SiteWhereException {
		if ((originator != null) && (!originator.isEmpty())) {
			DeviceCommandResponseCreateRequest response = new DeviceCommandResponseCreateRequest();
			response.setOriginatingEventId(originator);
			response.setResponseEventId(eventId);
			SiteWhereServer.getInstance().getDeviceManagement().addDeviceCommandResponse(
					assignment.getToken(), response);
		}
	}
}