/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.spi.device.communication.EventDecodeException;
import com.sitewhere.spi.device.communication.IDecodedDeviceRequest;
import com.sitewhere.spi.device.communication.IDeviceEventDecoder;

/**
 * Implementation of {@link IDeviceEventDecoder} that does not actually decode
 * anything, but rather prints the payload to the log.
 * 
 * @author Derek
 */
public class EchoStringDecoder implements IDeviceEventDecoder<String> {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.communication.IDeviceEventDecoder#decode(java.
     * lang.Object, java.util.Map)
     */
    @Override
    public List<IDecodedDeviceRequest<?>> decode(String payload, Map<String, String> metadata)
	    throws EventDecodeException {
	LOGGER.info("Payload: " + payload);
	return new ArrayList<IDecodedDeviceRequest<?>>();
    }
}