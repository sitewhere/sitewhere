/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.decoder.coap;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitewhere.rest.model.device.communication.DeviceRequest;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.sources.DecodedDeviceRequest;
import com.sitewhere.sources.decoder.json.JsonDeviceRequestMarshaler;
import com.sitewhere.sources.spi.EventDecodeException;
import com.sitewhere.sources.spi.IDecodedDeviceRequest;
import com.sitewhere.sources.spi.IDeviceEventDecoder;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Decodes CoAP requests which have pre-parsed some of the metadata and pass the
 * rest of the payload as a JSON body.
 * 
 * @author Derek
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