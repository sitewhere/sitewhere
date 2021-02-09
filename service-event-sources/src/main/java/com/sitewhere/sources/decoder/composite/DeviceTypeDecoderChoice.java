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

import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.sources.spi.ICompositeDeviceEventDecoder.IDecoderChoice;
import com.sitewhere.sources.spi.ICompositeDeviceEventDecoder.IDeviceContext;
import com.sitewhere.sources.spi.IDeviceEventDecoder;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;

/**
 * Decoder choice which applies if the device is of a given device type.
 *
 * @param <T>
 */
public class DeviceTypeDecoderChoice<T> extends TenantEngineLifecycleComponent implements IDecoderChoice<T> {

    /** Device type token to match */
    private String deviceTypeToken;

    /** Decoder used if context applies */
    private IDeviceEventDecoder<T> deviceEventDecoder;

    public DeviceTypeDecoderChoice() {
	super(LifecycleComponentType.DeviceEventDecoder);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.ICompositeDeviceEventDecoder.
     * IDecoderChoice#appliesTo(com.sitewhere.spi.device.communication.
     * ICompositeDeviceEventDecoder.IDeviceContext)
     */
    @Override
    public boolean appliesTo(IDeviceContext<T> criteria) {
	if (criteria.getDeviceType().getToken().equals(getDeviceTypeToken())) {
	    return true;
	}
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.ICompositeDeviceEventDecoder.
     * IDecoderChoice#getDeviceEventDecoder()
     */
    public IDeviceEventDecoder<T> getDeviceEventDecoder() {
	return deviceEventDecoder;
    }

    public void setDeviceEventDecoder(IDeviceEventDecoder<T> deviceEventDecoder) {
	this.deviceEventDecoder = deviceEventDecoder;
    }

    /*
     * @see
     * com.sitewhere.microservice.lifecycle.LifecycleComponent#start(com.sitewhere.
     * spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	startNestedComponent(getDeviceEventDecoder(), monitor, true);
    }

    /*
     * @see
     * com.sitewhere.microservice.lifecycle.LifecycleComponent#stop(com.sitewhere.
     * spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getDeviceEventDecoder().lifecycleStop(monitor);
    }

    public String getDeviceTypeToken() {
	return deviceTypeToken;
    }

    public void setDeviceTypeToken(String deviceTypeToken) {
	this.deviceTypeToken = deviceTypeToken;
    }
}