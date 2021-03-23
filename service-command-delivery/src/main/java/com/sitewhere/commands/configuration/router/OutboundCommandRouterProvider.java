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
package com.sitewhere.commands.configuration.router;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.sitewhere.commands.configuration.CommandDeliveryTenantConfiguration;
import com.sitewhere.commands.spi.ICommandDestinationsManager;
import com.sitewhere.commands.spi.IOutboundCommandRouter;
import com.sitewhere.spi.SiteWhereException;

/**
 * Provides a outbound command router based on tenant configuration.
 */
public class OutboundCommandRouterProvider implements Provider<IOutboundCommandRouter> {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(OutboundCommandRouterProvider.class);

    /** Injected configuration */
    private CommandDeliveryTenantConfiguration configuration;

    /** Injected handle to command destinations manager */
    private ICommandDestinationsManager manager;

    @Inject
    public OutboundCommandRouterProvider(CommandDeliveryTenantConfiguration configuration,
	    ICommandDestinationsManager manager) {
	this.configuration = configuration;
    }

    /*
     * @see com.google.inject.Provider#get()
     */
    @Override
    public IOutboundCommandRouter get() {
	try {
	    return OutboundCommandRouterParser.parse(getManager(), getConfiguration());
	} catch (SiteWhereException e) {
	    LOGGER.error("Unable to initialize command router.", e);
	    return null;
	}
    }

    protected CommandDeliveryTenantConfiguration getConfiguration() {
	return configuration;
    }

    protected ICommandDestinationsManager getManager() {
	return manager;
    }
}
