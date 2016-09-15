/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.communication;

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
     * Encode an {@link IDecodedDeviceRequest} into another representation.
     * 
     * @param request
     *            device event request to encode
     * @return encoded version of the request
     * @throws SiteWhereException
     *             if request can not be encoded
     */
    public T encode(IDecodedDeviceRequest<?> request) throws SiteWhereException;
}