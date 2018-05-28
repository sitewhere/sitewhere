/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicestate.microservice;

import com.sitewhere.devicestate.configuration.DeviceStateModelProvider;
import com.sitewhere.devicestate.spi.microservice.IDeviceStateMicroservice;
import com.sitewhere.devicestate.spi.microservice.IDeviceStateTenantEngine;
import com.sitewhere.microservice.multitenant.MultitenantMicroservice;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationModel;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Microservice that provides device state mangagement functionality.
 * 
 * @author Derek
 */
public class DeviceStateMicroservice extends MultitenantMicroservice<MicroserviceIdentifier, IDeviceStateTenantEngine>
	implements IDeviceStateMicroservice {

    /** Microservice name */
    private static final String NAME = "Presence Management";

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getName()
     */
    @Override
    public String getName() {
	return NAME;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getIdentifier()
     */
    @Override
    public MicroserviceIdentifier getIdentifier() {
	return MicroserviceIdentifier.DeviceState;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#isGlobal()
     */
    @Override
    public boolean isGlobal() {
	return false;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#buildConfigurationModel()
     */
    @Override
    public IConfigurationModel buildConfigurationModel() {
	return new DeviceStateModelProvider().buildModel();
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * createTenantEngine(com.sitewhere.spi.tenant.ITenant)
     */
    @Override
    public IDeviceStateTenantEngine createTenantEngine(ITenant tenant) throws SiteWhereException {
	return new DeviceStateTenantEngine(tenant);
    }
}