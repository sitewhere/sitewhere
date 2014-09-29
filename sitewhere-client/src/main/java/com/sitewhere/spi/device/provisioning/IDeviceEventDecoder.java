/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.provisioning;

import java.util.List;

import com.sitewhere.spi.SiteWhereException;

/**
 * Decodes inbound device event messages.
 * 
 * @author Derek
 */
public interface IDeviceEventDecoder<T> {

	/**
	 * Decodes a payload into one or more {@link IDecodedDeviceEventRequest} objects.
	 * 
	 * @param payload
	 * @return
	 * @throws SiteWhereException
	 */
	public List<IDecodedDeviceEventRequest> decode(T payload) throws SiteWhereException;
}