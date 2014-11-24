/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.provisioning;

import com.sitewhere.spi.SiteWhereException;

/**
 * Encodes a device event into another representation.
 * 
 * @author Derek
 *
 * @param <T>
 */
public interface IDeviceEventEncoder<T> {

	/**
	 * Encode an {@link IDecodedDeviceEventRequest} into another representation.
	 * 
	 * @param event
	 * @return
	 * @throws SiteWhereException
	 */
	public T encode(IDecodedDeviceEventRequest request) throws SiteWhereException;
}