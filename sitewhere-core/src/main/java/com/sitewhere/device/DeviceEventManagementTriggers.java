/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.SiteWhere;
import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.communication.IOutboundProcessingStrategy;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEventBatch;
import com.sitewhere.spi.device.event.IDeviceEventBatchResponse;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceStateChange;
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

    /** Cached strategy */
    private IOutboundProcessingStrategy strategy;

    public DeviceEventManagementTriggers(IDeviceEventManagement delegate) {
	super(delegate);
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
	return SiteWherePersistence.deviceEventBatchLogic(assignmentToken, batch, this);
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
	if (getOutboundProcessingStrategy().getLifecycleStatus() == LifecycleStatus.Started) {
	    getOutboundProcessingStrategy().onMeasurements(result);
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
	if (getOutboundProcessingStrategy().getLifecycleStatus() == LifecycleStatus.Started) {
	    getOutboundProcessingStrategy().onLocation(result);
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
	if (getOutboundProcessingStrategy().getLifecycleStatus() == LifecycleStatus.Started) {
	    getOutboundProcessingStrategy().onAlert(result);
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
	if (getOutboundProcessingStrategy().getLifecycleStatus() == LifecycleStatus.Started) {
	    getOutboundProcessingStrategy().onStateChange(result);
	}
	return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.device.DeviceEventManagementDecorator#
     * addDeviceCommandInvocation( java.lang.String,
     * com.sitewhere.spi.device.command.IDeviceCommand,
     * com.sitewhere.spi.device.event.request.
     * IDeviceCommandInvocationCreateRequest)
     */
    @Override
    public IDeviceCommandInvocation addDeviceCommandInvocation(String assignmentToken, IDeviceCommand command,
	    IDeviceCommandInvocationCreateRequest request) throws SiteWhereException {
	IDeviceCommandInvocation result = super.addDeviceCommandInvocation(assignmentToken, command, request);
	if (getOutboundProcessingStrategy().getLifecycleStatus() == LifecycleStatus.Started) {
	    getOutboundProcessingStrategy().onCommandInvocation(result);
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
	if (getOutboundProcessingStrategy().getLifecycleStatus() == LifecycleStatus.Started) {
	    getOutboundProcessingStrategy().onCommandResponse(result);
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
     * Get the configured outbound processing strategy.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected IOutboundProcessingStrategy getOutboundProcessingStrategy() throws SiteWhereException {
	if (strategy == null) {
	    strategy = SiteWhere.getServer().getEventProcessing(getTenant()).getOutboundProcessingStrategy();
	}
	return strategy;
    }
}