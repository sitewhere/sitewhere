/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.decoder.composite;

import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.sources.spi.ICompositeDeviceEventDecoder.IDecoderChoice;
import com.sitewhere.sources.spi.ICompositeDeviceEventDecoder.IDeviceContext;
import com.sitewhere.sources.spi.IDeviceEventDecoder;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Decoder choice which applies if the device is of a given device type.
 * 
 * @author Derek
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
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	startNestedComponent(getDeviceEventDecoder(), monitor, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop(com.sitewhere
     * .spi.server.lifecycle.ILifecycleProgressMonitor)
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