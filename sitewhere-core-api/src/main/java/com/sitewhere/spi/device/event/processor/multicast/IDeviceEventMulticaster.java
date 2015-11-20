/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event.processor.multicast;

import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

/**
 * Adds ability to send events to multiple desinations.
 * 
 * @author Derek
 */
public interface IDeviceEventMulticaster<T> extends ITenantLifecycleComponent {

	/**
	 * Calculates the list of routes to which an event should be sent.
	 * 
	 * @param event
	 * @param deivice
	 * @param assignment
	 * @return
	 * @throws SiteWhereException
	 */
	public List<T> calculateRoutes(IDeviceEvent event, IDevice deivice, IDeviceAssignment assignment)
			throws SiteWhereException;
}