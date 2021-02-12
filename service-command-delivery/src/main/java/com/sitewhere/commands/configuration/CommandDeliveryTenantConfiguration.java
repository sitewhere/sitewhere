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

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.commands.configuration.destinations.CommandDestinationGenericConfiguration;
import com.sitewhere.commands.configuration.router.RouterGenericConfiguration;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineConfiguration;

/**
 * Maps command delivery tenant engine YAML configuration to objects.
 */
public class CommandDeliveryTenantConfiguration implements ITenantEngineConfiguration {

    /** Router configuration */
    private RouterGenericConfiguration router;

    /** List of command destination configurations */
    private List<CommandDestinationGenericConfiguration> commandDestinations = new ArrayList<>();

    public RouterGenericConfiguration getRouter() {
	return router;
    }

    public void setRouter(RouterGenericConfiguration router) {
	this.router = router;
    }

    public List<CommandDestinationGenericConfiguration> getCommandDestinations() {
	return commandDestinations;
    }

    public void setCommandDestinations(List<CommandDestinationGenericConfiguration> commandDestinations) {
	this.commandDestinations = commandDestinations;
    }
}
