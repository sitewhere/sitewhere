/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicestate.spi.microservice;

import com.sitewhere.devicestate.spi.kafka.IDeviceStateEnrichedEventsConsumer;
import com.sitewhere.grpc.service.DeviceStateGrpc;
import com.sitewhere.spi.device.state.IDeviceStateManagement;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;

/**
 * Extends {@link IMicroserviceTenantEngine} with features specific to device
 * state management.
 * 
 * @author Derek
 */
public interface IDeviceStateTenantEngine extends IMicroserviceTenantEngine {

    /**
     * Get associated device state management implementation.
     * 
     * @return
     */
    public IDeviceStateManagement getDeviceStateManagement();

    /**
     * Get implementation class that wraps device state with GRPC conversions.
     * 
     * @return
     */
    public DeviceStateGrpc.DeviceStateImplBase getDeviceStateImpl();

    /**
     * Get Kafka consumer that delivers enriched events for processing.
     * 
     * @return
     */
    public IDeviceStateEnrichedEventsConsumer getDeviceStateEnrichedEventsConsumer();
}