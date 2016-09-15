/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event.processor;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

/**
 * Simple interface to support event filtering.
 * 
 * @author Derek
 */
public interface IDeviceEventFilter extends ITenantLifecycleComponent {

    /**
     * Indicates if an event should be filtered.
     * 
     * @param event
     * @param device
     * @param assignment
     * @return
     * @throws SiteWhereException
     */
    public boolean isFiltered(IDeviceEvent event, IDevice device, IDeviceAssignment assignment)
	    throws SiteWhereException;
}