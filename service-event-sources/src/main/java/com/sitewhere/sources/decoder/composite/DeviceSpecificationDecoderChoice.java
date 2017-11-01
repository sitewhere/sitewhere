/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.decoder.composite;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.ICompositeDeviceEventDecoder.IDecoderChoice;
import com.sitewhere.spi.device.communication.ICompositeDeviceEventDecoder.IDeviceContext;
import com.sitewhere.spi.device.communication.IDeviceEventDecoder;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Decoder choice which applies if the device implements a given device
 * specification.
 * 
 * @author Derek
 *
 * @param <T>
 */
public class DeviceSpecificationDecoderChoice<T> extends TenantLifecycleComponent implements IDecoderChoice<T> {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Specification token to match */
    private String deviceSpecificationToken;

    /** Decoder used if context applies */
    private IDeviceEventDecoder<T> deviceEventDecoder;

    public DeviceSpecificationDecoderChoice() {
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
	if (criteria.getDeviceSpecification().getToken().equals(getDeviceSpecificationToken())) {
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

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    public String getDeviceSpecificationToken() {
	return deviceSpecificationToken;
    }

    public void setDeviceSpecificationToken(String deviceSpecificationToken) {
	this.deviceSpecificationToken = deviceSpecificationToken;
    }
}