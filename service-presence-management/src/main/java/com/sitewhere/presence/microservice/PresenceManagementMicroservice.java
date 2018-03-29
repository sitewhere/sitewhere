/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.presence.microservice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.microservice.multitenant.MultitenantMicroservice;
import com.sitewhere.presence.configuration.PresenceManagementModelProvider;
import com.sitewhere.presence.spi.microservice.IPresenceManagementMicroservice;
import com.sitewhere.presence.spi.microservice.IPresenceManagementTenantEngine;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationModel;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Microservice that provides device presence mangagement functionality.
 * 
 * @author Derek
 */
public class PresenceManagementMicroservice extends MultitenantMicroservice<IPresenceManagementTenantEngine>
	implements IPresenceManagementMicroservice {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(PresenceManagementMicroservice.class);

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
	return MicroserviceIdentifier.PresenceManagement;
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
	return new PresenceManagementModelProvider().buildModel();
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * createTenantEngine(com.sitewhere.spi.tenant.ITenant)
     */
    @Override
    public IPresenceManagementTenantEngine createTenantEngine(ITenant tenant) throws SiteWhereException {
	return new PresenceManagementTenantEngine(this, tenant);
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Log getLogger() {
	return LOGGER;
    }
}