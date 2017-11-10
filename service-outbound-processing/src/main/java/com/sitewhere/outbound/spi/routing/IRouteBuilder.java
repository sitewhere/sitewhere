/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.outbound.spi.routing;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

/**
 * Builds routes of a given type.
 * 
 * @author Derek
 *
 * @param <T>
 */
public interface IRouteBuilder<T> extends ITenantLifecycleComponent {

    /**
     * Build a route based on information about a device event.
     * 
     * @param event
     * @param device
     * @param assignment
     * @return
     * @throws SiteWhereException
     */
    public T build(IDeviceEvent event, IDevice device, IDeviceAssignment assignment) throws SiteWhereException;
}