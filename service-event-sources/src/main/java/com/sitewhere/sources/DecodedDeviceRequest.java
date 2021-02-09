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
package com.sitewhere.sources;

import com.sitewhere.sources.spi.IDecodedDeviceRequest;

/**
 * Default implementation of {@link IDecodedDeviceRequest}.
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