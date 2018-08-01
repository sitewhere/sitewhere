/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.coap;

import com.sitewhere.sources.InboundEventSource;
import com.sitewhere.sources.decoder.coap.CoapJsonDecoder;

/**
 * Event source that starts a CoAP server and processes messages that are sent
 * to it.
 * 
 * @author Derek
 */
public class CoapServerEventSource extends InboundEventSource<byte[]> {

    public CoapServerEventSource() {
	super();
	setDeviceEventDecoder(new CoapJsonDecoder());
    }

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