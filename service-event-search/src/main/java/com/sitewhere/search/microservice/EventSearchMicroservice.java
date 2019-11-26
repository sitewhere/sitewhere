/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.search.microservice;

import com.sitewhere.microservice.multitenant.MultitenantMicroservice;
import com.sitewhere.search.configuration.EventSearchConfiguration;
import com.sitewhere.search.spi.microservice.IEventSearchMicroservice;
import com.sitewhere.search.spi.microservice.IEventSearchTenantEngine;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Microservice that provides event search functionality.
 */
public class EventSearchMicroservice
	extends MultitenantMicroservice<MicroserviceIdentifier, EventSearchConfiguration, IEventSearchTenantEngine>
	implements IEventSearchMicroservice {

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getName()
     */
    @Override
    public String getName() {
	return "Event Search";
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getIdentifier()
     */
    @Override
    public MicroserviceIdentifier getIdentifier() {
	return MicroserviceIdentifier.EventSearch;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getConfigurationClass()
     */
    @Override
    public Class<EventSearchConfiguration> getConfigurationClass() {
	return EventSearchConfiguration.class;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * createTenantEngine(com.sitewhere.spi.tenant.ITenant)
     */
    @Override
    public IEventSearchTenantEngine createTenantEngine(ITenant tenant) throws SiteWhereException {
	return new EventSearchTenantEngine(tenant);
    }
}