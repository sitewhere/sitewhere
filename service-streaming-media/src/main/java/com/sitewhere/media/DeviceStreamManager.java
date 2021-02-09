/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.media;

import java.util.List;

import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.stream.IDeviceStreamDataManagement;
import com.sitewhere.microservice.api.stream.IDeviceStreamManagement;
import com.sitewhere.microservice.api.stream.IDeviceStreamManager;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.rest.model.device.command.DeviceStreamAckCommand;
import com.sitewhere.rest.model.device.command.SendDeviceStreamDataCommand;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.command.DeviceStreamStatus;
import com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest;
import com.sitewhere.spi.device.event.request.ISendDeviceStreamDataRequest;
import com.sitewhere.spi.device.streaming.IDeviceStreamData;
import com.sitewhere.spi.device.streaming.request.IDeviceStreamDataCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;

/**
 * Default {@link IDeviceStreamManager} implementation.
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
	List<? extends IDeviceAssignment> assignments = getActiveAssignments(deviceToken);

	DeviceStreamAckCommand ack = new DeviceStreamAckCommand();
	try {
	    // TODO: Send to all active assignments?
	    getDeviceStreamManagement().createDeviceStream(assignments.get(0).getId(), request);
	    ack.setStatus(DeviceStreamStatus.DeviceStreamCreated);
	} catch (SiteWhereException e) {
	    getLogger().error("Unable to create device stream.", e);
	    ack.setStatus(DeviceStreamStatus.DeviceStreamFailed);
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
	// TODO: Send to all active assignments?
	List<? extends IDeviceAssignment> assignments = getActiveAssignments(deviceToken);
	getDeviceStreamDataManagement().addDeviceStreamData(assignments.get(0).getId(), request);
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
	IDeviceStreamData data = getDeviceStreamDataManagement().getDeviceStreamData(request.getStreamId(),
		request.getSequenceNumber());
	SendDeviceStreamDataCommand command = new SendDeviceStreamDataCommand();
	command.setDeviceToken(deviceToken);
	command.setStreamId(request.getStreamId());
	command.setSequenceNumber(request.getSequenceNumber());
	if (data != null) {
	    command.setData(data.getData());
	} else {
	    command.setData(new byte[0]);
	}
	// getDeviceCommunication(getTenantEngine().getTenant()).deliverSystemCommand(hardwareId,
	// command);
    }

    /**
     * Get active assignments for the device.
     * 
     * @param deviceToken
     * @return
     * @throws SiteWhereException
     */
    protected List<? extends IDeviceAssignment> getActiveAssignments(String deviceToken) throws SiteWhereException {
	IDevice device = getDeviceManagement().getDeviceByToken(deviceToken);
	if (device == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceToken, ErrorLevel.ERROR);
	}
	return getDeviceManagement().getActiveDeviceAssignments(device.getId());
    }

    private IDeviceManagement getDeviceManagement() {
	return null;
    }

    private IDeviceStreamManagement getDeviceStreamManagement() {
	return null;
    }

    private IDeviceStreamDataManagement getDeviceStreamDataManagement() {
	return null;
    }
}