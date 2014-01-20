/*
 * IDeviceEventProcessorChain.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event.processor;

import java.util.List;

/**
 * Holds a list of {@link IDeviceEventProcessor} objects that are invoked in order.
 * 
 * @author Derek
 */
public interface IDeviceEventProcessorChain {

	/**
	 * Get the list of device event processors.
	 * 
	 * @return
	 */
	public List<IDeviceEventProcessor> getProcessors();
}