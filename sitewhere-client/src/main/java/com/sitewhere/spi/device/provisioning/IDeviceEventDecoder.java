/*
 * IDeviceEventDecoder.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.provisioning;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceEvent;

/**
 * Decodes inbound device event messages.
 * 
 * @author Derek
 */
public interface IDeviceEventDecoder {

	/**
	 * Decodes a message payload into a device event.
	 * 
	 * @param payload
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceEvent decode(byte[] payload) throws SiteWhereException;
}