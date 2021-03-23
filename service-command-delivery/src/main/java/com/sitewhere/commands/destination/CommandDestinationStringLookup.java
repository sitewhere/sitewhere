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
package com.sitewhere.commands.destination;

import org.apache.commons.text.lookup.StringLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.commands.spi.ICommandDestination;
import com.sitewhere.microservice.configuration.json.SiteWhereStringLookup;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Implementation of {@link StringLookup} that handles substitutions specific to
 * command destinations.
 */
public class CommandDestinationStringLookup implements StringLookup {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(SiteWhereStringLookup.class);

    /** Replace with id of target command destination */
    private static final String DESTINATION_ID = "destination.id";

    /** Replace with token of device */
    private static final String DEVICE_TOKEN = "device.token";

    /** Replace with id of device */
    private static final String DEVICE_ID = "device.id";

    /** Component to resolve against */
    private ITenantEngineLifecycleComponent component;

    /** Command destination */
    private ICommandDestination<?, ?> commandDestination;

    /** Device nesting context */
    private IDeviceNestingContext deviceNestingContext;

    public CommandDestinationStringLookup(ITenantEngineLifecycleComponent component,
	    ICommandDestination<?, ?> commandDestination, IDeviceNestingContext deviceNestingContext) {
	this.component = component;
	this.commandDestination = commandDestination;
	this.deviceNestingContext = deviceNestingContext;
    }

    /*
     * @see org.apache.commons.text.lookup.StringLookup#lookup(java.lang.String)
     */
    @Override
    public String lookup(String key) {
	if (getCommandDestination() != null) {
	    // Handle replacement for destination id.
	    if (DESTINATION_ID.equals(key)) {
		return getCommandDestination().getDestinationId();
	    }
	} else {
	    LOGGER.warn("Skipping string resolution because command destination context is not set.");
	}

	// Only resolve nesting-related references if available.
	if (getDeviceNestingContext() != null) {
	    // Handle replacement for device token.
	    if (DEVICE_TOKEN.equals(key)) {
		return getDeviceNestingContext().getGateway().getToken();
	    }
	    // Handle replacement for tenant token.
	    else if (DEVICE_ID.equals(key)) {
		return getDeviceNestingContext().getGateway().getId().toString();
	    }
	} else {
	    LOGGER.warn("Skipping string resolution because device nesting context is not set.");
	}
	return null;
    }

    protected ITenantEngineLifecycleComponent getComponent() {
	return component;
    }

    protected ICommandDestination<?, ?> getCommandDestination() {
	return commandDestination;
    }

    protected IDeviceNestingContext getDeviceNestingContext() {
	return deviceNestingContext;
    }
}
