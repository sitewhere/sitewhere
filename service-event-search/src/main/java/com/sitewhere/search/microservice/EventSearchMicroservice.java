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
package com.sitewhere.search.microservice;

import javax.enterprise.context.ApplicationScoped;

import com.sitewhere.microservice.multitenant.MultitenantMicroservice;
import com.sitewhere.search.configuration.EventSearchConfiguration;
import com.sitewhere.search.configuration.EventSearchModule;
import com.sitewhere.search.spi.microservice.IEventSearchMicroservice;
import com.sitewhere.search.spi.microservice.IEventSearchTenantEngine;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.configuration.IMicroserviceModule;

import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;

/**
 * Microservice that provides event search functionality.
 */
@ApplicationScoped
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
     * @see com.sitewhere.spi.microservice.IMicroservice#createConfigurationModule()
     */
    @Override
    public IMicroserviceModule<EventSearchConfiguration> createConfigurationModule() {
	return new EventSearchModule(getMicroserviceConfiguration());
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * createTenantEngine(io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine)
     */
    @Override
    public IEventSearchTenantEngine createTenantEngine(SiteWhereTenantEngine engine) throws SiteWhereException {
	return new EventSearchTenantEngine(engine);
    }
}