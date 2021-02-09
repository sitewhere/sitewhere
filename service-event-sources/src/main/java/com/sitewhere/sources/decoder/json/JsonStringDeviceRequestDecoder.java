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
package com.sitewhere.sources.decoder.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.sources.DecodedDeviceRequest;
import com.sitewhere.sources.spi.EventDecodeException;
import com.sitewhere.sources.spi.IDecodedDeviceRequest;
import com.sitewhere.sources.spi.IDeviceEventDecoder;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;

/**
 * Decodes binary String messages in JSON format into device requests for
 * processing.
 */
public class JsonStringDeviceRequestDecoder extends TenantEngineLifecycleComponent
	implements IDeviceEventDecoder<String> {

    /** Used to map data into an object based on JSON parsing */
    private static ObjectMapper MAPPER = getObjectMapper();

    public JsonStringDeviceRequestDecoder() {
	super(LifecycleComponentType.DeviceEventDecoder);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IDeviceEventDecoder#decode(java.
     * lang.Object, java.util.Map)
     */
    @Override
    public List<IDecodedDeviceRequest<?>> decode(String payload, Map<String, Object> metadata)
	    throws EventDecodeException {
	try {
	    List<IDecodedDeviceRequest<?>> events = new ArrayList<IDecodedDeviceRequest<?>>();
	    DecodedDeviceRequest<?> decoded = MAPPER.readValue(payload, DecodedDeviceRequest.class);
	    events.add(decoded);
	    return events;
	} catch (JsonParseException e) {
	    throw new EventDecodeException(e);
	} catch (JsonMappingException e) {
	    throw new EventDecodeException(e);
	} catch (IOException e) {
	    throw new EventDecodeException(e);
	}
    }

    /**
     * Get configured {@link ObjectMapper}.
     * 
     * @return
     */
    public static ObjectMapper getObjectMapper() {
	if (MAPPER == null) {
	    MAPPER = new ObjectMapper();
	    SimpleModule module = new SimpleModule();
	    module.addDeserializer(DecodedDeviceRequest.class, new JsonDeviceRequestMarshaler());
	    MAPPER.registerModule(module);
	}
	return MAPPER;
    }
}
