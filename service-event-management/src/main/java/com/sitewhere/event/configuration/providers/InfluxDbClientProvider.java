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
import com.sitewhere.influxdb.InfluxDbClient;
import com.sitewhere.influxdb.InfluxDbConfiguration;
import com.sitewhere.microservice.datastore.DatastoreDefinition;
import com.sitewhere.spi.SiteWhereException;

/**
 * Provides a client connected to InfluxDB based on tenant configuration.
 */
public class InfluxDbClientProvider implements Provider<InfluxDbClient> {

    /** Injected tenant engine */
    private IEventManagementTenantEngine tenantEngine;

    /** Injected configuration */
    private EventManagementTenantConfiguration configuration;

    /** Datastore information */
    private DatastoreDefinition datastore;

    @Inject
    public InfluxDbClientProvider(IEventManagementTenantEngine tenantEngine,
	    EventManagementTenantConfiguration configuration, DatastoreDefinition datastore) {
	this.tenantEngine = tenantEngine;
	this.configuration = configuration;
	this.datastore = datastore;
    }

    /*
     * @see com.google.inject.Provider#get()
     */
    @Override
    public InfluxDbClient get() {
	try {
	    InfluxDbConfiguration influx = new InfluxDbConfiguration(getTenantEngine());
	    influx.loadFrom(getDatastore().getConfiguration());
	    return new InfluxDbClient(influx);
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
