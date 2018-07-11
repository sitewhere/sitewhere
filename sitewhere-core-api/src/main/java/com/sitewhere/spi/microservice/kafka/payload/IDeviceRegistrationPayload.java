/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.kafka.payload;

import java.io.Serializable;

import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;

/**
 * Get payload passed for inbound device registration requests after they have
 * been decoded in the event sources.
 * 
 * @author Derek
 */
public interface IDeviceRegistrationPayload extends Serializable {

    /**
     * Get unique event source id.
     * 
     * @return
     */
    public String getSourceId();

    /**
     * Get device token.
     * 
     * @return
     */
    public String getDeviceToken();

    /**
     * Get id of originating event.
     * 
     * @return
     */
    public String getOriginator();

    /**
     * Get device registration request.
     * 
     * @return
     */
    public IDeviceRegistrationRequest getDeviceRegistrationRequest();
}
