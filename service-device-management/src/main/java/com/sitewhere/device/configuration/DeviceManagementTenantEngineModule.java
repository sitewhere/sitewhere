/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.configuration;

import com.sitewhere.device.persistence.rdb.RdbDeviceManagement;
import com.sitewhere.device.spi.microservice.IDeviceManagementTenantEngine;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.multitenant.TenantEngineModule;

/**
 * Guice module used for configuring components associated with a device
 * management tenant engine.
 */
public class DeviceManagementTenantEngineModule extends TenantEngineModule<DeviceManagementTenantConfiguration> {

    public DeviceManagementTenantEngineModule(IDeviceManagementTenantEngine tenantEngine,
	    DeviceManagementTenantConfiguration configuration) {
	super(tenantEngine, configuration);
    }

    /*
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
	bind(IDeviceManagementTenantEngine.class).toInstance((IDeviceManagementTenantEngine) getTenantEngine());
	bind(IDeviceManagement.class).to(RdbDeviceManagement.class);
    }
}
