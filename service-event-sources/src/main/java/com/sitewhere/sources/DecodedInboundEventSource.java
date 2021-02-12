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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.sources.spi.EventDecodeException;
import com.sitewhere.sources.spi.IDecodedDeviceRequest;
import com.sitewhere.sources.spi.IDeviceEventDecoder;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link InboundEventSource} where event receivers return
 * decoded events and the decoder just passes the events through without
 * changing them.
 */
public class DecodedInboundEventSource extends InboundEventSource<DecodedDeviceRequest<?>> {

    public DecodedInboundEventSource() {
	setDeviceEventDecoder(new NoOpDecoder());
    }

    /*
     * @see
     * com.sitewhere.spi.device.communication.IInboundEventSource#getRawPayload(
     * java.lang.Object)
     */
    @Override
    public byte[] getRawPayload(DecodedDeviceRequest<?> payload) {
	return new byte[0];
    }

    /**
     * Decoder that just returns the decoded events.
     */
    public static class NoOpDecoder extends TenantEngineLifecycleComponent
	    implements IDeviceEventDecoder<DecodedDeviceRequest<?>> {

	public NoOpDecoder() {
	    super(LifecycleComponentType.DeviceEventDecoder);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.communication.IDeviceEventDecoder#decode(
	 * java.lang. Object, java.util.Map)
	 */
	@Override
	public List<IDecodedDeviceRequest<?>> decode(DecodedDeviceRequest<?> payload, Map<String, Object> metadata)
		throws EventDecodeException {
	    List<IDecodedDeviceRequest<?>> results = new ArrayList<IDecodedDeviceRequest<?>>();
	    results.add(payload);
	    return results;
	}
    }
}