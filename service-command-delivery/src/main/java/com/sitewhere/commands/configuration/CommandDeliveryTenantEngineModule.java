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
package com.sitewhere.commands.configuration;

import com.sitewhere.commands.configuration.destinations.CommandDestinationsManagerProvider;
import com.sitewhere.commands.configuration.router.OutboundCommandRouterProvider;
import com.sitewhere.commands.spi.ICommandDestinationsManager;
import com.sitewhere.commands.spi.IOutboundCommandRouter;
import com.sitewhere.commands.spi.microservice.ICommandDeliveryTenantEngine;
import com.sitewhere.microservice.multitenant.TenantEngineModule;

/**
 * Guice module used for configuring components associated with a command
 * delivery tenant engine.
 */
public class CommandDeliveryTenantEngineModule extends TenantEngineModule<CommandDeliveryTenantConfiguration> {

    public CommandDeliveryTenantEngineModule(ICommandDeliveryTenantEngine tenantEngine,
	    CommandDeliveryTenantConfiguration configuration) {
	super(tenantEngine, configuration);
    }

    /*
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
	bind(ICommandDeliveryTenantEngine.class).toInstance((ICommandDeliveryTenantEngine) getTenantEngine());
	bind(CommandDeliveryTenantConfiguration.class).toInstance(getConfiguration());
	bind(ICommandDestinationsManager.class).toProvider(CommandDestinationsManagerProvider.class);
	bind(IOutboundCommandRouter.class).toProvider(OutboundCommandRouterProvider.class);
    }
}
