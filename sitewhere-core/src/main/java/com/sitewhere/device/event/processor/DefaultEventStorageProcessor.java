/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.event.processor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.SiteWhere;
import com.sitewhere.rest.model.device.event.request.DeviceCommandResponseCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceAssignmentCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.DeviceAssignmentType;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.processor.IInboundEventProcessor;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;

/**
 * Implementation of {@link IInboundEventProcessor} that attempts to store the
 * inbound event request using device management APIs.
 * 
 * @author Derek
 */
public class DefaultEventStorageProcessor extends InboundEventProcessor {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Cached device management implementation */
    private IDeviceManagement deviceManagement;

    /** Cached device event management implementation */
    private IDeviceEventManagement deviceEventManagement;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.rest.model.device.event.processor.InboundEventProcessor#
     * onDeviceCommandResponseRequest(java.lang.String, java.lang.String,
     * com.sitewhere.spi.device.event.request.
     * IDeviceCommandResponseCreateRequest)
     */
    @Override
    public void onDeviceCommandResponseRequest(String hardwareId, String originator,
	    IDeviceCommandResponseCreateRequest request) throws SiteWhereException {
	IDeviceAssignment assignment = getCurrentAssignment(hardwareId);
	getDeviceEventManagement().addDeviceCommandResponse(assignment.getToken(), request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.rest.model.device.event.processor.InboundEventProcessor#
     * onDeviceMeasurementsCreateRequest(java.lang.String, java.lang.String,
     * com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest)
     */
    @Override
    public void onDeviceMeasurementsCreateRequest(String hardwareId, String originator,
	    IDeviceMeasurementsCreateRequest request) throws SiteWhereException {
	IDeviceAssignment assignment = getCurrentAssignment(hardwareId);
	IDeviceMeasurements measurements = getDeviceEventManagement().addDeviceMeasurements(assignment.getToken(),
		request);
	handleLinkResponseToInvocation(originator, measurements.getId(), assignment);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.rest.model.device.event.processor.InboundEventProcessor#
     * onDeviceLocationCreateRequest(java.lang.String, java.lang.String,
     * com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest)
     */
    @Override
    public void onDeviceLocationCreateRequest(String hardwareId, String originator,
	    IDeviceLocationCreateRequest request) throws SiteWhereException {
	IDeviceAssignment assignment = getCurrentAssignment(hardwareId);
	IDeviceLocation location = getDeviceEventManagement().addDeviceLocation(assignment.getToken(), request);
	handleLinkResponseToInvocation(originator, location.getId(), assignment);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.rest.model.device.event.processor.InboundEventProcessor#
     * onDeviceAlertCreateRequest(java.lang.String, java.lang.String,
     * com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest)
     */
    @Override
    public void onDeviceAlertCreateRequest(String hardwareId, String originator, IDeviceAlertCreateRequest request)
	    throws SiteWhereException {
	IDeviceAssignment assignment = getCurrentAssignment(hardwareId);
	IDeviceAlert alert = getDeviceEventManagement().addDeviceAlert(assignment.getToken(), request);
	handleLinkResponseToInvocation(originator, alert.getId(), assignment);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.device.event.processor.InboundEventProcessor#
     * onDeviceStateChangeCreateRequest(java.lang.String, java.lang.String,
     * com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest)
     */
    @Override
    public void onDeviceStateChangeCreateRequest(String hardwareId, String originator,
	    IDeviceStateChangeCreateRequest request) throws SiteWhereException {
	IDeviceAssignment assignment = getCurrentAssignment(hardwareId);
	IDeviceStateChange state = getDeviceEventManagement().addDeviceStateChange(assignment.getToken(), request);
	handleLinkResponseToInvocation(originator, state.getId(), assignment);
    }

    /**
     * Get the current assignment or throw errors if it can not be resolved.
     * 
     * @param hardwareId
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceAssignment getCurrentAssignment(String hardwareId) throws SiteWhereException {
	IDevice device = getDeviceManagement().getDeviceByHardwareId(hardwareId);
	if (device == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidHardwareId, ErrorLevel.ERROR);
	}
	if (device.getAssignmentToken() == null) {
	    // If no assignment exists, add an unassociated assignment.
	    return createUnassociatedAssignmentFor(hardwareId);
	}
	return getDeviceManagement().getDeviceAssignmentByToken(device.getAssignmentToken());
    }

    /**
     * Create an unassociated assignment for device with the given hardware id.
     * This allows events to be written when a device does not have an existing
     * assignment.
     * 
     * @param hardwareId
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceAssignment createUnassociatedAssignmentFor(String hardwareId) throws SiteWhereException {
	LOGGER.debug("Creating unassociated assignment for {}.", hardwareId);
	DeviceAssignmentCreateRequest assnCreate = new DeviceAssignmentCreateRequest();
	assnCreate.setDeviceHardwareId(hardwareId);
	assnCreate.setAssignmentType(DeviceAssignmentType.Unassociated);
	return SiteWhere.getServer().getDeviceManagement(getTenant()).createDeviceAssignment(assnCreate);
    }

    /**
     * If an originator was assocaited with the event, create a
     * {@link IDeviceCommandResponse} that links back to the original
     * invocation.
     * 
     * @param originator
     * @param eventId
     * @param assignment
     * @throws SiteWhereException
     */
    protected void handleLinkResponseToInvocation(String originator, String eventId, IDeviceAssignment assignment)
	    throws SiteWhereException {
	if ((originator != null) && (!originator.isEmpty())) {
	    DeviceCommandResponseCreateRequest response = new DeviceCommandResponseCreateRequest();
	    response.setOriginatingEventId(originator);
	    response.setResponseEventId(eventId);
	    getDeviceEventManagement().addDeviceCommandResponse(assignment.getToken(), response);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    /**
     * Cache the device management implementation rather than looking it up each
     * time.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceManagement getDeviceManagement() throws SiteWhereException {
	if (deviceManagement == null) {
	    deviceManagement = SiteWhere.getServer().getDeviceManagement(getTenant());
	}
	return deviceManagement;
    }

    /**
     * Cache the device event management implementation rather than looking it
     * up each time.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceEventManagement getDeviceEventManagement() throws SiteWhereException {
	if (deviceEventManagement == null) {
	    deviceEventManagement = SiteWhere.getServer().getDeviceEventManagement(getTenant());
	}
	return deviceEventManagement;
    }
}