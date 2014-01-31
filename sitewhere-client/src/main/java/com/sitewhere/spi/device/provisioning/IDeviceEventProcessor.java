/*
 * IDeviceEventProcessor.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.provisioning;

import java.util.List;

import com.sitewhere.spi.ISiteWhereLifecycle;

/**
 * Agent that decodes device events an submits them
 * 
 * @author Derek
 * 
 */
public interface IDeviceEventProcessor extends ISiteWhereLifecycle {

	/**
	 * Set the device event decoder.
	 * 
	 * @param decoder
	 */
	public void setDeviceEventDecoder(IDeviceEventDecoder decoder);

	/**
	 * Set the list of event receivers that use this processor.
	 * 
	 * @param receivers
	 */
	public void setDeviceEventReceivers(List<IDeviceEventReceiver> receivers);
}