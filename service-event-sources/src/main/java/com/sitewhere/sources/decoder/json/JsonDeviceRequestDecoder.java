/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
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
 * Decodes binary device messages in JSON format into device requests for
 * processing.
 */
public class JsonDeviceRequestDecoder extends TenantEngineLifecycleComponent implements IDeviceEventDecoder<byte[]> {

    /** Used to map data into an object based on JSON parsing */
    private static ObjectMapper MAPPER = getObjectMapper();

    public JsonDeviceRequestDecoder() {
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