/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.decoder.composite;

import com.sitewhere.sources.spi.ICompositeDeviceEventDecoder.IDeviceContext;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceType;

/**
 * Device context with a binary payload.
 * 
 * @author Derek
 */
public class BinaryDeviceContext implements IDeviceContext<byte[]> {

    /** Device */
    private IDevice device;

    /** Device type */
    private IDeviceType deviceType;

    /** Message payload */
    private byte[] payload;

    /*
     * @see com.sitewhere.sources.spi.ICompositeDeviceEventDecoder.IDeviceContext#
     * getDevice()
     */
    @Override
    public IDevice getDevice() {
	return device;
    }

    public void setDevice(IDevice device) {
	this.device = device;
    }

    /*
     * @see com.sitewhere.sources.spi.ICompositeDeviceEventDecoder.IDeviceContext#
     * getDeviceType()
     */
    @Override
    public IDeviceType getDeviceType() {
	return deviceType;
    }

    public void setDeviceType(IDeviceType deviceType) {
	this.deviceType = deviceType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.ICompositeDeviceEventDecoder.
     * IDeviceContext#getPayload()
     */
    public byte[] getPayload() {
	return payload;
    }

    public void setPayload(byte[] payload) {
	this.payload = payload;
    }
}
