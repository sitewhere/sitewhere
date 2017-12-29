/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.registration.spi.microservice;

import com.sitewhere.registration.spi.IRegistrationManager;
import com.sitewhere.registration.spi.kafka.IUnregisteredEventsConsumer;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;

/**
 * Extends {@link IMicroserviceTenantEngine} with features specific to device
 * registration.
 * 
 * @author Derek
 */
public interface IDeviceRegistrationTenantEngine extends IMicroserviceTenantEngine {

    /**
     * Get Kafka consumer for unregistered device events.
     * 
     * @return
     */
    public IUnregisteredEventsConsumer getUnregisteredEventsConsumer();

    /**
     * Get registration manager implementation.
     * 
     * @return
     */
    public IRegistrationManager getRegistrationManager();
}