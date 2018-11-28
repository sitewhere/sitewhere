/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.command;

import java.util.UUID;

import com.sitewhere.spi.device.command.ISendDeviceStreamDataCommand;
import com.sitewhere.spi.device.command.SystemCommandType;

/**
 * System command that sends a chunk of device stream data to a device.
 * 
 * @author Derek
 */
public class SendDeviceStreamDataCommand extends SystemCommand implements ISendDeviceStreamDataCommand {

    /** Serial version UID */
    private static final long serialVersionUID = -5372263771806975660L;

    /** Device token */
    private String deviceToken;

    /** Stream id */
    private UUID streamId;

    /** Sequence number */
    private long sequenceNumber;

    /** Data */
    private byte[] data;

    public SendDeviceStreamDataCommand() {
	super(SystemCommandType.SendDeviceStreamData);
    }

    /*
     * @see
     * com.sitewhere.spi.device.command.ISendDeviceStreamDataCommand#getDeviceToken(
     * )
     */
    @Override
    public String getDeviceToken() {
	return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
	this.deviceToken = deviceToken;
    }

    /*
     * @see
     * com.sitewhere.spi.device.command.ISendDeviceStreamDataCommand#getStreamId()
     */
    @Override
    public UUID getStreamId() {
	return streamId;
    }

    public void setStreamId(UUID streamId) {
	this.streamId = streamId;
    }

    /*
     * @see com.sitewhere.spi.device.command.ISendDeviceStreamDataCommand#
     * getSequenceNumber()
     */
    @Override
    public long getSequenceNumber() {
	return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
	this.sequenceNumber = sequenceNumber;
    }

    /*
     * @see com.sitewhere.spi.device.command.ISendDeviceStreamDataCommand#getData()
     */
    @Override
    public byte[] getData() {
	return data;
    }

    public void setData(byte[] data) {
	this.data = data;
    }
}