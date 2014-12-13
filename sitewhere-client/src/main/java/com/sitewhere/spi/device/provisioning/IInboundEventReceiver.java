/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.provisioning;

import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Handles receipt of device event information from an underlying transport.
 * 
 * @author Derek
 */
public interface IInboundEventReceiver<T> extends ILifecycleComponent {

	/**
	 * Set the parent event source that will process events.
	 * 
	 * @param source
	 */
	public void setEventSource(IInboundEventSource<T> source);
}