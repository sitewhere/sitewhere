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
