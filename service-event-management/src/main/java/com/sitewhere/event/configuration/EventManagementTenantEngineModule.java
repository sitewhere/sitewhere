/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.configuration;

import com.sitewhere.event.configuration.providers.Warp10ClientProvider;
import com.sitewhere.event.persistence.warp10.Warp10DeviceEventManagement;
import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.microservice.multitenant.TenantEngineModule;
import com.sitewhere.warp10.Warp10Client;

/**
 * Guice module used for configuring components associated with a device state
 * tenant engine.
 */
public class EventManagementTenantEngineModule extends TenantEngineModule<EventManagementTenantConfiguration> {

    public EventManagementTenantEngineModule(EventManagementTenantConfiguration configuration) {
	super(configuration);
    }

    /*
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
	bind(EventManagementTenantConfiguration.class).toInstance(getConfiguration());
	bind(Warp10Client.class).toProvider(Warp10ClientProvider.class);
	bind(IDeviceEventManagement.class).to(Warp10DeviceEventManagement.class);
    }
}
