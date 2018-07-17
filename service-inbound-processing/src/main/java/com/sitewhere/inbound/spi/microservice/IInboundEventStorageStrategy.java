/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.inbound.spi.microservice;

import com.sitewhere.grpc.model.DeviceEventModel.GInboundEventPayload;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;

/**
 * Strategy for storing inbound events to the event management microservice.
 * 
 * @author Derek
 */
public interface IInboundEventStorageStrategy {

    /**
     * Store a device event.
     * 
     * @param assignment
     * @param payload
     * @throws SiteWhereException
     */
    public void storeDeviceEvent(IDeviceAssignment assignment, GInboundEventPayload payload) throws SiteWhereException;
}