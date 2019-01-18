/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.spi.common;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Interface for a component that builds a payload based on a device event.
 */
public interface IPayloadBuilder extends ITenantEngineLifecycleComponent {

    /**
     * Build a custom payload based on an event.
     * 
     * @param context
     * @param event
     * @return
     * @throws SiteWhereException
     */
    public byte[] buildPayload(IDeviceEventContext context, IDeviceEvent event) throws SiteWhereException;
}
