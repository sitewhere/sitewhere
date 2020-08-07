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
 */
public class BinaryMessageMetadata implements IMessageMetadata<byte[]> {

    /** Device token */
    private String deviceToken;

    /** Message payload */
    private byte[] payload;

    /*
     * @see com.sitewhere.sources.spi.ICompositeDeviceEventDecoder.IMessageMetadata#
     * getDeviceToken()
     */
    @Override
    public String getDeviceToken() {
	return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
	this.deviceToken = deviceToken;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.ICompositeDeviceEventDecoder.
     * IMessageMetadata#getPayload()
     */
    @Override
    public byte[] getPayload() {
	return payload;
    }

    public void setPayload(byte[] payload) {
	this.payload = payload;
    }
}