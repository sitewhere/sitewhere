/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.configuration.providers;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.sitewhere.event.configuration.EventManagementTenantConfiguration;
import com.sitewhere.event.spi.microservice.IEventManagementTenantEngine;
import com.sitewhere.microservice.datastore.DatastoreDefinition;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.warp10.Warp10Client;
import com.sitewhere.warp10.Warp10Configuration;

/**
 * Provides a client connected to Warp 10 based on tenant configuration.
 */
public class Warp10ClientProvider implements Provider<Warp10Client> {

    /** Injected tenant engine */
    private IEventManagementTenantEngine tenantEngine;

    /** Injected configuration */
    private EventManagementTenantConfiguration configuration;

    /** Datastore information */
    private DatastoreDefinition datastore;

    @Inject
    public Warp10ClientProvider(IEventManagementTenantEngine tenantEngine,
	    EventManagementTenantConfiguration configuration, DatastoreDefinition datastore) {
	this.tenantEngine = tenantEngine;
	this.configuration = configuration;
	this.datastore = datastore;
    }

    /*
     * @see com.google.inject.Provider#get()
     */
    @Override
    public Warp10Client get() {
	try {
	    Warp10Configuration warp10 = new Warp10Configuration(getTenantEngine());
	    warp10.loadFrom(getDatastore().getConfiguration());
	    return new Warp10Client(warp10);
	} catch (SiteWhereException e) {
	    throw new RuntimeException("Unable to load Warp 10 configuration.o", e);
	}
    }

    protected EventManagementTenantConfiguration getConfiguration() {
	return configuration;
    }

    protected IEventManagementTenantEngine getTenantEngine() {
	return tenantEngine;
    }

    public DatastoreDefinition getDatastore() {
	return datastore;
    }
}