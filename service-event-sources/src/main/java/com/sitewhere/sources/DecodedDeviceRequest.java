/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources;

import com.sitewhere.sources.spi.IDecodedDeviceRequest;

/**
 * Default implementation of {@link IDecodedDeviceRequest}.
 * 
 * @author Derek
 */
public class DecodedDeviceRequest<T> implements IDecodedDeviceRequest<T> {

    /** Serial version UID */
    private static final long serialVersionUID = 4280270339471220181L;

    /** Device token the request applies to */
    private String deviceToken;

    /** Originating invocation if available */
    private String originator;

    /** Event create request */
    private T request;

    public DecodedDeviceRequest() {
    }

    public DecodedDeviceRequest(String deviceToken, String originator, T request) {
	setDeviceToken(deviceToken);
	setOriginator(originator);
	setRequest(request);
    }

    /*
     * @see com.sitewhere.sources.spi.IDecodedDeviceRequest#getDeviceToken()
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
     * @see com.sitewhere.spi.device.communication.IDecodedDeviceRequest#
     * getOriginator()
     */
    @Override
    public String getOriginator() {
	return originator;
    }

    public void setOriginator(String originator) {
	this.originator = originator;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.communication.IDecodedDeviceRequest#getRequest()
     */
    @Override
    public T getRequest() {
	return request;
    }

    public void setRequest(T request) {
	this.request = request;
    }
}