/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.configuration;

import java.util.Map;

import com.sitewhere.event.configuration.providers.TimeSeriesProvider;
import com.sitewhere.event.configuration.providers.Warp10ClientProvider;
import com.sitewhere.event.persistence.warp10.Warp10DeviceEventManagement;
import com.sitewhere.event.spi.microservice.IEventManagementTenantEngine;
import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.microservice.configuration.model.instance.persistence.TimeSeriesConfiguration;
import com.sitewhere.microservice.datastore.DatastoreDefinition;
import com.sitewhere.microservice.multitenant.TenantEngineModule;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.warp10.Warp10Client;

/**
 * Guice module used for configuring components associated with an event
 * management tenant engine.
 */
public class EventManagementTenantEngineModule extends TenantEngineModule<EventManagementTenantConfiguration> {

    /** Tenant engine */
    private IEventManagementTenantEngine tenantEngine;

    public EventManagementTenantEngineModule(IEventManagementTenantEngine tenantEngine,
	    EventManagementTenantConfiguration configuration) {
	super(configuration);
	this.tenantEngine = tenantEngine;
    }

    /**
     * Get datastore definition taking into account global references.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected DatastoreDefinition getDatastoreDefinition() throws SiteWhereException {
	DatastoreDefinition datastore = getConfiguration().getDatastore();
	if (datastore.getReference() != null) {
	    Map<String, TimeSeriesConfiguration> tsConfigs = getTenantEngine().getMicroservice()
		    .getInstanceConfiguration().getPersistenceConfigurations().getTimeSeriesConfigurations();
	    TimeSeriesConfiguration config = tsConfigs.get(datastore.getReference());
	    if (config == null) {
		throw new SiteWhereException(
			String.format("Global reference not found for '%s.'", datastore.getReference()));
	    }
	    DatastoreDefinition proxy = new DatastoreDefinition();
	    proxy.setType(config.getType());
	    proxy.setConfiguration(config.getConfiguration());
	    datastore = proxy;
	}
	return datastore;
    }

    /*
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
	bind(IEventManagementTenantEngine.class).toInstance(getTenantEngine());
	bind(EventManagementTenantConfiguration.class).toInstance(getConfiguration());

	try {
	    // Get local or global datastore information.
	    DatastoreDefinition datastore = getDatastoreDefinition();
	    bind(DatastoreDefinition.class).toInstance(datastore);

	    // Add bindings based on datastore chosen.
	    switch (datastore.getType()) {
	    case TimeSeriesProvider.WARP_10: {
		bind(Warp10Client.class).toProvider(Warp10ClientProvider.class);
		bind(IDeviceEventManagement.class).to(Warp10DeviceEventManagement.class);
		break;
	    }
	    default: {
		throw new RuntimeException(String.format("Unknown event management provider: %s",
			getConfiguration().getDatastore().getType()));
	    }
	    }
	} catch (SiteWhereException e) {
	    throw new RuntimeException(e);
	}
    }

    protected IEventManagementTenantEngine getTenantEngine() {
	return tenantEngine;
    }
}
