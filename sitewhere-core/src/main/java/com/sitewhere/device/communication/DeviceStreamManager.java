/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.SiteWhere;
import com.sitewhere.rest.model.device.command.DeviceStreamAckCommand;
import com.sitewhere.rest.model.device.command.SendDeviceStreamDataCommand;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.command.DeviceStreamStatus;
import com.sitewhere.spi.device.communication.IDeviceStreamManager;
import com.sitewhere.spi.device.event.IDeviceStreamData;
import com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest;
import com.sitewhere.spi.device.event.request.ISendDeviceStreamDataRequest;
import com.sitewhere.spi.device.streaming.IDeviceStream;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Default {@link IDeviceStreamManager} implementation.
 * 
 * @author Derek
 */
public class DeviceStreamManager extends TenantLifecycleComponent implements IDeviceStreamManager {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    public DeviceStreamManager() {
	super(LifecycleComponentType.DeviceStreamManager);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IDeviceStreamManager#
     * handleDeviceStreamRequest (java.lang.String,
     * com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest)
     */
    @Override
    public void handleDeviceStreamRequest(String hardwareId, IDeviceStreamCreateRequest request)
	    throws SiteWhereException {
	IDeviceAssignment assignment = getCurrentAssignment(hardwareId);

	DeviceStreamAckCommand ack = new DeviceStreamAckCommand();
	ack.setStreamId(request.getStreamId());
	IDeviceStream existing = SiteWhere.getServer().getDeviceManagement(getTenant())
		.getDeviceStream(assignment.getToken(), request.getStreamId());
	if (existing != null) {
	    ack.setStatus(DeviceStreamStatus.DeviceStreamExists);
	} else {
	    try {
		SiteWhere.getServer().getDeviceManagement(getTenant()).createDeviceStream(assignment.getToken(),
			request);
		ack.setStatus(DeviceStreamStatus.DeviceStreamCreated);
	    } catch (SiteWhereException e) {
		LOGGER.error("Unable to create device stream.", e);
		ack.setStatus(DeviceStreamStatus.DeviceStreamFailed);
	    }
	}
	SiteWhere.getServer().getDeviceCommunication(getTenant()).deliverSystemCommand(hardwareId, ack);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IDeviceStreamManager#
     * handleDeviceStreamDataRequest(java.lang.String,
     * com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest)
     */
    @Override
    public void handleDeviceStreamDataRequest(String hardwareId, IDeviceStreamDataCreateRequest request)
	    throws SiteWhereException {
	IDeviceAssignment assignment = getCurrentAssignment(hardwareId);
	SiteWhere.getServer().getDeviceEventManagement(getTenant()).addDeviceStreamData(assignment.getToken(), request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IDeviceStreamManager#
     * handleSendDeviceStreamDataRequest(java.lang.String,
     * com.sitewhere.spi.device.event.request.ISendDeviceStreamDataRequest)
     */
    @Override
    public void handleSendDeviceStreamDataRequest(String hardwareId, ISendDeviceStreamDataRequest request)
	    throws SiteWhereException {
	IDeviceAssignment assignment = getCurrentAssignment(hardwareId);
	IDeviceStreamData data = SiteWhere.getServer().getDeviceEventManagement(getTenant())
		.getDeviceStreamData(assignment.getToken(), request.getStreamId(), request.getSequenceNumber());
	SendDeviceStreamDataCommand command = new SendDeviceStreamDataCommand();
	command.setStreamId(request.getStreamId());
	command.setSequenceNumber(request.getSequenceNumber());
	command.setHardwareId(hardwareId);
	if (data != null) {
	    command.setData(data.getData());
	} else {
	    command.setData(new byte[0]);
	}
	SiteWhere.getServer().getDeviceCommunication(getTenant()).deliverSystemCommand(hardwareId, command);
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
     * Get the current assignment or throw errors if it can not be resolved.
     * 
     * @param hardwareId
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceAssignment getCurrentAssignment(String hardwareId) throws SiteWhereException {
	IDevice device = SiteWhere.getServer().getDeviceManagement(getTenant()).getDeviceByHardwareId(hardwareId);
	if (device == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidHardwareId, ErrorLevel.ERROR);
	}
	if (device.getAssignmentToken() == null) {
	    throw new SiteWhereSystemException(ErrorCode.DeviceNotAssigned, ErrorLevel.ERROR);
	}
	return SiteWhere.getServer().getDeviceManagement(getTenant())
		.getDeviceAssignmentByToken(device.getAssignmentToken());
    }
}