/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.sources.coap;

import com.sitewhere.sources.InboundEventSource;
import com.sitewhere.sources.decoder.coap.CoapJsonDecoder;

/**
 * Event source that starts a CoAP server and processes messages that are sent
 * to it.
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