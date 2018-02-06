/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.command;

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

    /** Hardware id */
    private String hardwareId;

    /** Stream id */
    private String streamId;

    /** Sequence number */
    private long sequenceNumber;

    /** Data */
    private byte[] data;

    public SendDeviceStreamDataCommand() {
	super(SystemCommandType.SendDeviceStreamData);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.command.ISendDeviceStreamDataCommand#
     * getHardwareId()
     */
    public String getHardwareId() {
	return hardwareId;
    }

    public void setHardwareId(String hardwareId) {
	this.hardwareId = hardwareId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.command.ISendDeviceStreamDataCommand#getStreamId
     * ()
     */
    public String getStreamId() {
	return streamId;
    }

    public void setStreamId(String streamId) {
	this.streamId = streamId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.command.ISendDeviceStreamDataCommand#
     * getSequenceNumber()
     */
    public long getSequenceNumber() {
	return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
	this.sequenceNumber = sequenceNumber;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.command.ISendDeviceStreamDataCommand#getData()
     */
    public byte[] getData() {
	return data;
    }

    public void setData(byte[] data) {
	this.data = data;
    }
}