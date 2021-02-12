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
package com.sitewhere.sources.decoder.composite;

import com.sitewhere.sources.spi.ICompositeDeviceEventDecoder.IDeviceContext;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceType;

/**
 * Device context with a binary payload.
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
