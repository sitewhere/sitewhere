/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.communication;

import java.util.List;

import com.sitewhere.spi.SiteWhereException;

/**
 * Decodes inbound device event messages.
 * 
 * @author Derek
 */
public interface IDeviceEventDecoder<T> {

	/**
	 * Decodes a payload into one or more {@link IDecodedDeviceRequest} objects.
	 * 
	 * @param payload the payload that will be decoded
	 * @return a list of decoded device requests to be processed
	 * @throws SiteWhereException if the payload can not be decoded
	 */
	public List<IDecodedDeviceRequest<?>> decode(T payload) throws SiteWhereException;
}