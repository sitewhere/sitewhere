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
package com.sitewhere.commands.configuration.destinations;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.sitewhere.commands.configuration.CommandDeliveryTenantConfiguration;
import com.sitewhere.commands.manager.CommandDestinationsManager;

/**
 * Provides a command destinations manager based on tenant configuration.
 */
public class CommandDestinationsManagerProvider implements Provider<CommandDestinationsManager> {

    /** Injected configuration */
    private CommandDeliveryTenantConfiguration configuration;

    @Inject
    public CommandDestinationsManagerProvider(CommandDeliveryTenantConfiguration configuration) {
	this.configuration = configuration;
    }

    /*
     * @see com.google.inject.Provider#get()
     */
    @Override
    public CommandDestinationsManager get() {
	CommandDestinationsManager manager = new CommandDestinationsManager(getConfiguration());
	return manager;
    }

    protected CommandDeliveryTenantConfiguration getConfiguration() {
	return configuration;
    }
}
