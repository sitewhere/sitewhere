/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.registration.configuration;

import com.sitewhere.microservice.multitenant.TenantEngineModule;
import com.sitewhere.registration.DeviceRegistrationManager;
import com.sitewhere.registration.spi.IRegistrationManager;

/**
 * Guice module used for configuring components associated with a device
 * registration tenant engine.
 */
public class DeviceRegistrationTenantEngineModule extends TenantEngineModule<DeviceRegistrationTenantConfiguration> {

    public DeviceRegistrationTenantEngineModule(DeviceRegistrationTenantConfiguration configuration) {
	super(configuration);
    }

    /*
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
	bind(DeviceRegistrationTenantConfiguration.class).toInstance(getConfiguration());
	bind(IRegistrationManager.class).to(DeviceRegistrationManager.class);
    }
}
