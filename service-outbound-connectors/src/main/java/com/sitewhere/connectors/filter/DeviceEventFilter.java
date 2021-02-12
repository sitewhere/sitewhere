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
package com.sitewhere.connectors.filter;

import com.sitewhere.connectors.spi.IDeviceEventFilter;
import com.sitewhere.connectors.spi.microservice.IOutboundConnectorsMicroservice;
import com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiChannel;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;

/**
 * Abstract base class for common filtering functionality.
 */
public abstract class DeviceEventFilter extends TenantEngineLifecycleComponent implements IDeviceEventFilter {

    public DeviceEventFilter() {
	super(LifecycleComponentType.OutboundEventProcessorFilter);
    }

    /**
     * Allow access to the device management API channel.
     * 
     * @return
     */
    protected IDeviceManagement getDeviceManagement() {
	return ((IOutboundConnectorsMicroservice) getMicroservice()).getDeviceManagement();
    }

    /**
     * Allow access to the device event management API channel.
     * 
     * @return
     */
    protected IDeviceEventManagementApiChannel<?> getDeviceEventManagementApiChannel() {
	return ((IOutboundConnectorsMicroservice) getMicroservice()).getDeviceEventManagementApiChannel();
    }
}