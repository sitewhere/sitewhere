/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.sitewhere.rest.model.device.communication.DecodedDeviceRequest;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.EventDecodeException;
import com.sitewhere.spi.device.communication.IDecodedDeviceRequest;
import com.sitewhere.spi.device.communication.IDeviceEventDecoder;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Decodes binary device messages in JSON format into device requests for
 * processing.
 * 
 * @author Derek
 */
public class JsonDeviceRequestDecoder extends TenantLifecycleComponent implements IDeviceEventDecoder<byte[]> {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Used to map data into an object based on JSON parsing */
    private static ObjectMapper MAPPER = getObjectMapper();

    public JsonDeviceRequestDecoder() {
	super(LifecycleComponentType.DeviceEventDecoder);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.communication.IDeviceEventDecoder#decode(java.
     * lang.Object, java.util.Map)
     */
    @Override
    public List<IDecodedDeviceRequest<?>> decode(byte[] payload, Map<String, String> metadata)
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

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
     */
    @Override
    public void start() throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
     */
    @Override
    public void stop() throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
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