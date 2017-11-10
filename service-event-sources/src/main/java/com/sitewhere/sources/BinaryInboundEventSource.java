/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources;

import com.sitewhere.sources.spi.IInboundEventSource;

/**
 * Implementation of {@link IInboundEventSource} that deals with binary data.
 * 
 * @author Derek
 */
public class BinaryInboundEventSource extends InboundEventSource<byte[]> {

    /*
     * @see
     * com.sitewhere.spi.device.communication.IInboundEventSource#getRawPayload(
     * java.lang.Object)
     */
    @Override
    public byte[] getRawPayload(byte[] payload) {
	return payload;
    }
}