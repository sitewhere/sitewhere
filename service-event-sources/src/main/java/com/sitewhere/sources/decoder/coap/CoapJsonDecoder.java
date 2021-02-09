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
package com.sitewhere.sources.decoder.coap;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.rest.model.device.communication.DeviceRequest;
import com.sitewhere.sources.DecodedDeviceRequest;
import com.sitewhere.sources.decoder.json.JsonDeviceRequestMarshaler;
import com.sitewhere.sources.spi.EventDecodeException;
import com.sitewhere.sources.spi.IDecodedDeviceRequest;
import com.sitewhere.sources.spi.IDeviceEventDecoder;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;

/**
 * Decodes CoAP requests which have pre-parsed some of the metadata and pass the
 * rest of the payload as a JSON body.
 */
public class CoapJsonDecoder extends TenantEngineLifecycleComponent implements IDeviceEventDecoder<byte[]> {

    /** Indicates type of event (detected from URI) */
    public static final String META_EVENT_TYPE = "eventType";

    /** Indicates device token (detected from URI) */
    public static final String META_DEVICE_TOKEN = "token";

    /** Used to map data into an object based on JSON parsing */
    private static ObjectMapper MAPPER = new ObjectMapper();

    public CoapJsonDecoder() {
	super(LifecycleComponentType.DeviceEventDecoder);
    }

    /*
     * @see com.sitewhere.sources.spi.IDeviceEventDecoder#decode(java.lang.Object,
     * java.util.Map)
     */
    @Override
    public List<IDecodedDeviceRequest<?>> decode(byte[] payload, Map<String, Object> metadata)
	    throws EventDecodeException {
	String typeStr = (String) metadata.get(META_EVENT_TYPE);
	String deviceToken = (String) metadata.get(META_DEVICE_TOKEN);
	DeviceRequest.Type type = DeviceRequest.Type.valueOf(typeStr);

	try {
	    JsonNode request = MAPPER.readTree(payload);
	    DecodedDeviceRequest<?> decoded = JsonDeviceRequestMarshaler.unmarshal(deviceToken, null, type, request);
	    return Collections.singletonList(decoded);
	} catch (IOException e) {
	    throw new EventDecodeException("Unable to decode CoAP event.", e);
	}
    }
}