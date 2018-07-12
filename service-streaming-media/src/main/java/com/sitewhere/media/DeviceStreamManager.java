/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.media;

import com.sitewhere.rest.model.device.command.DeviceStreamAckCommand;
import com.sitewhere.rest.model.device.command.SendDeviceStreamDataCommand;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.command.DeviceStreamStatus;
import com.sitewhere.spi.device.communication.IDeviceStreamManager;
import com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest;
import com.sitewhere.spi.device.event.request.ISendDeviceStreamDataRequest;
import com.sitewhere.spi.device.streaming.IDeviceStream;
import com.sitewhere.spi.device.streaming.IDeviceStreamData;
import com.sitewhere.spi.device.streaming.IDeviceStreamManagement;
import com.sitewhere.spi.device.streaming.request.IDeviceStreamDataCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Default {@link IDeviceStreamManager} implementation.
 * 
 * @author Derek
 */
public class DeviceStreamManager extends TenantEngineLifecycleComponent implements IDeviceStreamManager {

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
    public void handleDeviceStreamRequest(String deviceToken, IDeviceStreamCreateRequest request)
	    throws SiteWhereException {
	IDeviceAssignment assignment = getCurrentAssignment(deviceToken);

	DeviceStreamAckCommand ack = new DeviceStreamAckCommand();
	ack.setStreamId(request.getStreamId());
	IDeviceStream existing = getDeviceManagement(getTenantEngine().getTenant()).getDeviceStream(assignment.getId(),
		request.getStreamId());
	if (existing != null) {
	    ack.setStatus(DeviceStreamStatus.DeviceStreamExists);
	} else {
	    try {
		getDeviceManagement(getTenantEngine().getTenant()).createDeviceStream(assignment.getId(), request);
		ack.setStatus(DeviceStreamStatus.DeviceStreamCreated);
	    } catch (SiteWhereException e) {
		getLogger().error("Unable to create device stream.", e);
		ack.setStatus(DeviceStreamStatus.DeviceStreamFailed);
	    }
	}
	// getDeviceCommunication(getTenantEngine().getTenant()).deliverSystemCommand(hardwareId,
	// ack);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IDeviceStreamManager#
     * handleDeviceStreamDataRequest(java.lang.String,
     * com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest)
     */
    @Override
    public void handleDeviceStreamDataRequest(String deviceToken, IDeviceStreamDataCreateRequest request)
	    throws SiteWhereException {
	IDeviceAssignment assignment = getCurrentAssignment(deviceToken);
	getDeviceStreamManagement().addDeviceStreamData(assignment.getId(), null, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IDeviceStreamManager#
     * handleSendDeviceStreamDataRequest(java.lang.String,
     * com.sitewhere.spi.device.event.request.ISendDeviceStreamDataRequest)
     */
    @Override
    public void handleSendDeviceStreamDataRequest(String deviceToken, ISendDeviceStreamDataRequest request)
	    throws SiteWhereException {
	IDeviceAssignment assignment = getCurrentAssignment(deviceToken);
	IDeviceStreamData data = getDeviceStreamManagement().getDeviceStreamData(assignment.getId(),
		request.getStreamId(), request.getSequenceNumber());
	SendDeviceStreamDataCommand command = new SendDeviceStreamDataCommand();
	command.setStreamId(request.getStreamId());
	command.setSequenceNumber(request.getSequenceNumber());
	command.setHardwareId(deviceToken);
	if (data != null) {
	    command.setData(data.getData());
	} else {
	    command.setData(new byte[0]);
	}
	// getDeviceCommunication(getTenantEngine().getTenant()).deliverSystemCommand(hardwareId,
	// command);
    }

    /**
     * Get the current assignment or throw errors if it can not be resolved.
     * 
     * @param deviceToken
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceAssignment getCurrentAssignment(String deviceToken) throws SiteWhereException {
	IDevice device = getDeviceManagement(getTenantEngine().getTenant()).getDeviceByToken(deviceToken);
	if (device == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceToken, ErrorLevel.ERROR);
	}
	if (device.getDeviceAssignmentId() == null) {
	    throw new SiteWhereSystemException(ErrorCode.DeviceNotAssigned, ErrorLevel.ERROR);
	}
	return getDeviceManagement(getTenantEngine().getTenant()).getDeviceAssignment(device.getDeviceAssignmentId());
    }

    private IDeviceManagement getDeviceManagement(ITenant tenant) {
	return null;
    }

    private IDeviceStreamManagement getDeviceStreamManagement() {
	return null;
    }
}