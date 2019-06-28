/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.filter;

import com.sitewhere.connectors.spi.IDeviceEventFilter;
import com.sitewhere.connectors.spi.microservice.IOutboundConnectorsMicroservice;
import com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiChannel;
import com.sitewhere.grpc.client.spi.client.IDeviceManagementApiChannel;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Abstract base class for common filtering functionality.
 * 
 * @author Derek
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
    protected IDeviceManagementApiChannel<?> getDeviceManagementApiChannel() {
	return ((IOutboundConnectorsMicroservice) getMicroservice()).getDeviceManagementApiChannel();
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