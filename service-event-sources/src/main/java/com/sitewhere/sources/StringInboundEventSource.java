/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources;

import com.sitewhere.sources.decoder.json.JsonStringDeviceRequestDecoder;
import com.sitewhere.sources.spi.IInboundEventSource;

/**
 * Implementation of {@link IInboundEventSource} that deals with String data.
 */
public class StringInboundEventSource extends InboundEventSource<String> {

    public StringInboundEventSource() {
	super();
	setDeviceEventDecoder(new JsonStringDeviceRequestDecoder());
    }

    /*
     * @see
     * com.sitewhere.spi.device.communication.IInboundEventSource#getRawPayload(
     * java.lang.Object)
     */
    @Override
    public byte[] getRawPayload(String payload) {
	return payload.getBytes();
    }
}