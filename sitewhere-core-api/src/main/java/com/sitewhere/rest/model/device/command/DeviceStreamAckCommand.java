/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.command;

import java.util.UUID;

import com.sitewhere.spi.device.command.DeviceStreamStatus;
import com.sitewhere.spi.device.command.IDeviceStreamAckCommand;
import com.sitewhere.spi.device.command.SystemCommandType;

/**
 * Command sent to a device to indicate status of creating a device stream.
 * 
 * @author Derek
 */
public class DeviceStreamAckCommand extends SystemCommand implements IDeviceStreamAckCommand {

    /** Serial version UID */
    private static final long serialVersionUID = -6363023316760034867L;

    /** Id of stream being created */
    private UUID streamId;

    /** Status of creating device stream */
    private DeviceStreamStatus status;

    public DeviceStreamAckCommand() {
	super(SystemCommandType.DeviceStreamAck);
    }

    /*
     * @see com.sitewhere.spi.device.command.IDeviceStreamAckCommand#getStreamId()
     */
    @Override
    public UUID getStreamId() {
	return streamId;
    }

    public void setStreamId(UUID streamId) {
	this.streamId = streamId;
    }

    /*
     * @see com.sitewhere.spi.device.command.IDeviceStreamAckCommand#getStatus()
     */
    @Override
    public DeviceStreamStatus getStatus() {
	return status;
    }

    public void setStatus(DeviceStreamStatus status) {
	this.status = status;
    }
}