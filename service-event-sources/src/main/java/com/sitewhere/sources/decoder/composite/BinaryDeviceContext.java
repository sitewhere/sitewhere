/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.decoder.composite;

import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.communication.ICompositeDeviceEventDecoder.IDeviceContext;

/**
 * Device context with a binary payload.
 * 
 * @author Derek
 */
public class BinaryDeviceContext implements IDeviceContext<byte[]> {

    /** Device */
    private IDevice device;

    /** Device specification */
    private IDeviceSpecification deviceSpecification;

    /** Message payload */
    private byte[] payload;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.ICompositeDeviceEventDecoder.
     * IDeviceContext#getDevice()
     */
    public IDevice getDevice() {
	return device;
    }

    public void setDevice(IDevice device) {
	this.device = device;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.ICompositeDeviceEventDecoder.
     * IDeviceContext#getDeviceSpecification()
     */
    public IDeviceSpecification getDeviceSpecification() {
	return deviceSpecification;
    }

    public void setDeviceSpecification(IDeviceSpecification deviceSpecification) {
	this.deviceSpecification = deviceSpecification;
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
