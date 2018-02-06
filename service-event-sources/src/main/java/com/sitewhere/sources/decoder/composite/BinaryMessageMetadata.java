/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.decoder.composite;

import com.sitewhere.sources.spi.ICompositeDeviceEventDecoder.IMessageMetadata;

/**
 * Device criteria model object.
 * 
 * @author Derek
 */
public class BinaryMessageMetadata implements IMessageMetadata<byte[]> {

    /** Device hardware id */
    private String hardwareId;

    /** Message payload */
    private byte[] payload;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.ICompositeDeviceEventDecoder.
     * IMessageMetadata#getHardwareId()
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
     * @see com.sitewhere.spi.device.communication.ICompositeDeviceEventDecoder.
     * IMessageMetadata#getPayload()
     */
    public byte[] getPayload() {
	return payload;
    }

    public void setPayload(byte[] payload) {
	this.payload = payload;
    }
}