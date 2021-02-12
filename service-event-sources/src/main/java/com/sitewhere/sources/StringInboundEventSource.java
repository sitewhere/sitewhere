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