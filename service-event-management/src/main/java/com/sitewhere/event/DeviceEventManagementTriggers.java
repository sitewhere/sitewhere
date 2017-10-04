/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.event.persistence.DeviceEventManagementPersistence;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEventBatch;
import com.sitewhere.spi.device.event.IDeviceEventBatchResponse;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.IEventProcessing;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Adds triggers for processing related to device event management API calls.
 * 
 * @author Derek
 */
public class DeviceEventManagementTriggers extends DeviceEventManagementDecorator {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Event processing */
    private IEventProcessing eventProcessing;

    public DeviceEventManagementTriggers(IDeviceEventManagement delegate, IEventProcessing eventProcessing) {
	super(delegate);
	this.eventProcessing = eventProcessing;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.device.DeviceEventManagementDecorator#addDeviceEventBatch(
     * java.lang .String, com.sitewhere.spi.device.event.IDeviceEventBatch)
     */
    @Override
    public IDeviceEventBatchResponse addDeviceEventBatch(String assignmentToken, IDeviceEventBatch batch)
	    throws SiteWhereException {
	return DeviceEventManagementPersistence.deviceEventBatchLogic(assignmentToken, batch, this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.device.DeviceEventManagementDecorator#addDeviceMeasurements
     * (java. lang.String,
     * com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest)
     */
    @Override
    public IDeviceMeasurements addDeviceMeasurements(String assignmentToken, IDeviceMeasurementsCreateRequest request)
	    throws SiteWhereException {
	IDeviceMeasurements result = super.addDeviceMeasurements(assignmentToken, request);
	if (isReadyForOutboundProcessing()) {
	    getEventProcessing().getOutboundProcessingStrategy().onMeasurements(result);
	} else {
	    handleOutboundProcessingNotAvailable(result);
	}
	return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.device.DeviceEventManagementDecorator#addDeviceLocation(
     * java.lang .String,
     * com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest)
     */
    @Override
    public IDeviceLocation addDeviceLocation(String assignmentToken, IDeviceLocationCreateRequest request)
	    throws SiteWhereException {
	IDeviceLocation result = super.addDeviceLocation(assignmentToken, request);
	if (isReadyForOutboundProcessing()) {
	    getEventProcessing().getOutboundProcessingStrategy().onLocation(result);
	}
	return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.device.DeviceEventManagementDecorator#addDeviceAlert(java.
     * lang.String ,
     * com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest)
     */
    @Override
    public IDeviceAlert addDeviceAlert(String assignmentToken, IDeviceAlertCreateRequest request)
	    throws SiteWhereException {
	IDeviceAlert result = super.addDeviceAlert(assignmentToken, request);
	if (isReadyForOutboundProcessing()) {
	    getEventProcessing().getOutboundProcessingStrategy().onAlert(result);
	}
	return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.device.DeviceEventManagementDecorator#addDeviceStateChange(
     * java.lang. String,
     * com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest)
     */
    @Override
    public IDeviceStateChange addDeviceStateChange(String assignmentToken, IDeviceStateChangeCreateRequest request)
	    throws SiteWhereException {
	IDeviceStateChange result = super.addDeviceStateChange(assignmentToken, request);
	if (isReadyForOutboundProcessing()) {
	    getEventProcessing().getOutboundProcessingStrategy().onStateChange(result);
	}
	return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.event.DeviceEventManagementDecorator#
     * addDeviceCommandInvocation(java.lang.String,
     * com.sitewhere.spi.device.event.request.
     * IDeviceCommandInvocationCreateRequest)
     */
    @Override
    public IDeviceCommandInvocation addDeviceCommandInvocation(String assignmentToken,
	    IDeviceCommandInvocationCreateRequest request) throws SiteWhereException {
	IDeviceCommandInvocation result = super.addDeviceCommandInvocation(assignmentToken, request);
	if (isReadyForOutboundProcessing()) {
	    getEventProcessing().getOutboundProcessingStrategy().onCommandInvocation(result);
	}
	return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.device.DeviceEventManagementDecorator#
     * addDeviceCommandResponse(java .lang.String,
     * com.sitewhere.spi.device.event.request.
     * IDeviceCommandResponseCreateRequest)
     */
    @Override
    public IDeviceCommandResponse addDeviceCommandResponse(String assignmentToken,
	    IDeviceCommandResponseCreateRequest request) throws SiteWhereException {
	IDeviceCommandResponse result = super.addDeviceCommandResponse(assignmentToken, request);
	if (isReadyForOutboundProcessing()) {
	    getEventProcessing().getOutboundProcessingStrategy().onCommandResponse(result);
	}
	return result;
    }

    /**
     * Handle case where outbound processing is not available to save event.
     * 
     * @param unhandled
     */
    protected void handleOutboundProcessingNotAvailable(Object unhandled) {
	LOGGER.debug("Outbound processing not started. Outbound event ignored.");
    }

    /**
     * 
     * @return
     */
    protected boolean isReadyForOutboundProcessing() {
	return (getEventProcessing().getLifecycleStatus() == LifecycleStatus.Started) && (getEventProcessing()
		.getOutboundProcessingStrategy().getLifecycleStatus() == LifecycleStatus.Started);
    }

    public IEventProcessing getEventProcessing() {
	return eventProcessing;
    }

    public void setEventProcessing(IEventProcessing eventProcessing) {
	this.eventProcessing = eventProcessing;
    }
}