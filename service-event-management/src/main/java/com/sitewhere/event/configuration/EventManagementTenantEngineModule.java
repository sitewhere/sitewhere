/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.event.configuration;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.event.configuration.providers.InfluxDbClientProvider;
import com.sitewhere.event.configuration.providers.TimeSeriesProvider;
import com.sitewhere.event.configuration.providers.Warp10ClientProvider;
import com.sitewhere.event.persistence.influxdb.InfluxDbDeviceEventManagement;
import com.sitewhere.event.persistence.warp10.Warp10DeviceEventManagement;
import com.sitewhere.event.spi.microservice.IEventManagementTenantEngine;
import com.sitewhere.influxdb.InfluxDbClient;
import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.microservice.configuration.model.instance.persistence.TimeSeriesConfiguration;
import com.sitewhere.microservice.datastore.DatastoreDefinition;
import com.sitewhere.microservice.multitenant.TenantEngineModule;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.warp10.Warp10Client;

/**
 * Guice module used for configuring components associated with an event
 * management tenant engine.
 */
public class EventManagementTenantEngineModule extends TenantEngineModule<EventManagementTenantConfiguration> {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(EventManagementTenantEngineModule.class);

    public EventManagementTenantEngineModule(IEventManagementTenantEngine tenantEngine,
	    EventManagementTenantConfiguration configuration) {
	super(tenantEngine, configuration);
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
	    } else {
		LOGGER.info(String.format("Using global datastore reference '%s' with configuration:\n%s\n\n",
			datastore.getReference(), MarshalUtils.marshalJsonAsPrettyString(config)));
	    }
	    DatastoreDefinition proxy = new DatastoreDefinition();
	    proxy.setType(config.getType());
	    proxy.setConfiguration(config.getConfiguration());
	    datastore = proxy;
	} else {
	    LOGGER.info(String.format("Using local datastore configuration:\n%s\n\n", datastore.getReference(),
		    MarshalUtils.marshalJsonAsPrettyString(datastore.getConfiguration())));
	}
	return datastore;
    }

    /*
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
	bind(IEventManagementTenantEngine.class).toInstance((IEventManagementTenantEngine) getTenantEngine());
	bind(EventManagementTenantConfiguration.class).toInstance(getConfiguration());

	try {
	    // Get local or global datastore information.
	    DatastoreDefinition datastore = getDatastoreDefinition();
	    bind(DatastoreDefinition.class).toInstance(datastore);

	    // Add bindings based on datastore chosen.
	    switch (datastore.getType()) {
	    case TimeSeriesProvider.INFLUX_DB: {
		bind(InfluxDbClient.class).toProvider(InfluxDbClientProvider.class);
		bind(IDeviceEventManagement.class).to(InfluxDbDeviceEventManagement.class);
		break;
	    }
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
}
