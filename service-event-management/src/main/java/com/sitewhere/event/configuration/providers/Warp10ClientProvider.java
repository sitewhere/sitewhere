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