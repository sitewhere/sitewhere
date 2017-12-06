/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.registration.microservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.microservice.multitenant.MultitenantMicroservice;
import com.sitewhere.registration.configuration.DeviceRegistrationModel;
import com.sitewhere.registration.spi.microservice.IDeviceRegistrationMicroservice;
import com.sitewhere.registration.spi.microservice.IDeviceRegistrationTenantEngine;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroserviceIdentifiers;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationModel;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Microservice that provides device registration functionality.
 * 
 * @author Derek
 */
public class DeviceRegistrationMicroservice extends MultitenantMicroservice<IDeviceRegistrationTenantEngine>
	implements IDeviceRegistrationMicroservice {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Microservice name */
    private static final String NAME = "Device Registration";

    /** Configuration model */
    private IConfigurationModel configurationModel = new DeviceRegistrationModel(this);

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
    public String getIdentifier() {
	return IMicroserviceIdentifiers.DEVICE_REGISTRATION;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getConfigurationModel()
     */
    @Override
    public IConfigurationModel getConfigurationModel() {
	return configurationModel;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * createTenantEngine(com.sitewhere.spi.tenant.ITenant)
     */
    @Override
    public IDeviceRegistrationTenantEngine createTenantEngine(ITenant tenant) throws SiteWhereException {
	return new DeviceRegistrationTenantEngine(this, tenant);
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }
}