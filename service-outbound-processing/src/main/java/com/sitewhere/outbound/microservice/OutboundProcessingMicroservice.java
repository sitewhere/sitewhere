/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.outbound.microservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.microservice.multitenant.MultitenantMicroservice;
import com.sitewhere.outbound.spi.microservice.IOutboundProcessingMicroservice;
import com.sitewhere.outbound.spi.microservice.IOutboundProcessingTenantEngine;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Microservice that provides outbound event processing functionality.
 * 
 * @author Derek
 */
public class OutboundProcessingMicroservice extends MultitenantMicroservice<IOutboundProcessingTenantEngine>
	implements IOutboundProcessingMicroservice {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Microservice name */
    private static final String NAME = "Outbound Processing";

    /** Identifies module resources such as configuration file */
    private static final String MODULE_IDENTIFIER = "outbound-processing";

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
	return MODULE_IDENTIFIER;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * createTenantEngine(com.sitewhere.spi.tenant.ITenant)
     */
    @Override
    public IOutboundProcessingTenantEngine createTenantEngine(ITenant tenant) throws SiteWhereException {
	return new OutboundProcessingTenantEngine(this, tenant);
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }
}