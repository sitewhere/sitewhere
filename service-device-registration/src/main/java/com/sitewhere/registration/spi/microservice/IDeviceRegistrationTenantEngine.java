/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.registration.spi.microservice;

import com.sitewhere.registration.configuration.DeviceRegistrationTenantConfiguration;
import com.sitewhere.registration.spi.IRegistrationManager;
import com.sitewhere.registration.spi.kafka.IDeviceRegistrationEventsConsumer;
import com.sitewhere.registration.spi.kafka.IUnregisteredEventsConsumer;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;

/**
 * Extends {@link IMicroserviceTenantEngine} with features specific to device
 * registration.
 */
public interface IDeviceRegistrationTenantEngine extends IMicroserviceTenantEngine<DeviceRegistrationTenantConfiguration> {

    /**
     * Get Kafka consumer for unregistered device events.
     * 
     * @return
     */
    public IUnregisteredEventsConsumer getUnregisteredEventsConsumer();

    /**
     * Get Kafka consumer for new device registrations.
     * 
     * @return
     */
    public IDeviceRegistrationEventsConsumer getDeviceRegistrationEventsConsumer();

    /**
     * Get registration manager implementation.
     * 
     * @return
     */
    public IRegistrationManager getRegistrationManager();
}