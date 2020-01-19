/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicestate.configuration;

import com.sitewhere.devicestate.persistence.rdb.RdbDeviceStateManagement;
import com.sitewhere.microservice.api.state.IDeviceStateManagement;
import com.sitewhere.microservice.multitenant.TenantEngineModule;

/**
 * Guice module used for configuring components associated with a device state
 * tenant engine.
 */
public class DeviceStateTenantEngineModule extends TenantEngineModule<DeviceStateTenantConfiguration> {

    public DeviceStateTenantEngineModule(DeviceStateTenantConfiguration configuration) {
	super(configuration);
    }

    /*
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
	bind(IDeviceStateManagement.class).to(RdbDeviceStateManagement.class);
    }
}
