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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.commands.configuration.CommandDeliveryTenantConfiguration;
import com.sitewhere.commands.configuration.router.devicetypemapping.DeviceTypeMappingConfiguration;
import com.sitewhere.commands.routing.DeviceTypeMappingCommandRouter;
import com.sitewhere.commands.spi.IOutboundCommandRouter;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Parses generic configuration into command router instances.
 */
public class OutboundCommandRouterParser {

    /** Static logger instance */
    private static final Logger LOGGER = LoggerFactory.getLogger(OutboundCommandRouterParser.class);

    /** Type for device type mapping router */
    public static final String TYPE_DEVICE_TYPE_MAPPING = "device-type-mapping";

    public static IOutboundCommandRouter parse(ITenantEngineLifecycleComponent component,
	    CommandDeliveryTenantConfiguration configuration) throws SiteWhereException {
	RouterGenericConfiguration routerConfig = configuration.getRouter();
	switch (routerConfig.getType()) {
	case TYPE_DEVICE_TYPE_MAPPING: {
	    return createDeviceTypeMappingRouter(component, routerConfig);
	}
	default: {
	    throw new SiteWhereException(String.format("Unknown command router type '%s'.", routerConfig.getType()));
	}
	}
    }

    /**
     * Create a device type mapping router.
     * 
     * @param component
     * @param routerConfig
     * @return
     * @throws SiteWhereException
     */
    protected static IOutboundCommandRouter createDeviceTypeMappingRouter(ITenantEngineLifecycleComponent component,
	    RouterGenericConfiguration routerConfig) throws SiteWhereException {
	DeviceTypeMappingConfiguration config = new DeviceTypeMappingConfiguration(component);
	config.apply(routerConfig);
	LOGGER.info(String.format("Creating device type mapping router with configuration:\n%s\n\n",
		MarshalUtils.marshalJsonAsPrettyString(config)));
	return new DeviceTypeMappingCommandRouter(config);
    }
}
