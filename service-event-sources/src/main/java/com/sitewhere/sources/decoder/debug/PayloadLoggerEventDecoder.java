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
package com.sitewhere.sources.decoder.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.microservice.util.DataUtils;
import com.sitewhere.sources.spi.EventDecodeException;
import com.sitewhere.sources.spi.IDecodedDeviceRequest;
import com.sitewhere.sources.spi.IDeviceEventDecoder;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link IDeviceEventDecoder} that logs the event payload but
 * does not actually produce any events. This is useful for debugging when
 * implementing decoders for hardware sending human-readable commands across the
 * wire.
 */
public class PayloadLoggerEventDecoder extends TenantEngineLifecycleComponent implements IDeviceEventDecoder<byte[]> {

    public PayloadLoggerEventDecoder() {
	super(LifecycleComponentType.DeviceEventDecoder);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IDeviceEventDecoder#decode(java.
     * lang.Object, java.util.Map)
     */
    @Override
    public List<IDecodedDeviceRequest<?>> decode(byte[] payload, Map<String, Object> metadata)
	    throws EventDecodeException {
	getLogger().info("=== EVENT DATA BEGIN ===");
	getLogger().info(new String(payload));
	getLogger().info("(hex) " + DataUtils.bytesToHex(payload));
	getLogger().info("=== EVENT DATA END ===");
	return new ArrayList<IDecodedDeviceRequest<?>>();
    }
}