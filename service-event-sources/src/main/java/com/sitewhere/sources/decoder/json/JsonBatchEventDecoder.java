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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitewhere.rest.model.device.event.DeviceEventBatch;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.sources.DecodedDeviceRequest;
import com.sitewhere.sources.spi.EventDecodeException;
import com.sitewhere.sources.spi.IDecodedDeviceRequest;
import com.sitewhere.sources.spi.IDeviceEventDecoder;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Event decoder that converts a binary payload into the default SiteWhere REST
 * implementations using Jackson to marshal them as JSON.
 * 
 * DEPRECATED: This only supports events that can be wrapped in a
 * {@link DeviceEventBatch} object and does not offer full-featured support. Use
 * {@link JsonDeviceRequestDecoder} instead.
 * 
 * @author Derek
 */
public class JsonBatchEventDecoder extends TenantEngineLifecycleComponent implements IDeviceEventDecoder<byte[]> {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Used to map data into an object based on JSON parsing */
    private ObjectMapper mapper = new ObjectMapper();

    public JsonBatchEventDecoder() {
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
    public List<IDecodedDeviceRequest<?>> decode(byte[] payload, Map<String, Object> metadata)
	    throws EventDecodeException {
	try {
	    List<IDecodedDeviceRequest<?>> events = new ArrayList<IDecodedDeviceRequest<?>>();
	    DeviceEventBatch batch = mapper.readValue(payload, DeviceEventBatch.class);
	    for (IDeviceLocationCreateRequest lc : batch.getLocations()) {
		DecodedDeviceRequest<IDeviceLocationCreateRequest> decoded = new DecodedDeviceRequest<IDeviceLocationCreateRequest>();
		decoded.setHardwareId(batch.getHardwareId());
		decoded.setRequest(lc);
		events.add(decoded);
	    }
	    for (IDeviceMeasurementsCreateRequest mc : batch.getMeasurements()) {
		DecodedDeviceRequest<IDeviceMeasurementsCreateRequest> decoded = new DecodedDeviceRequest<IDeviceMeasurementsCreateRequest>();
		decoded.setHardwareId(batch.getHardwareId());
		decoded.setRequest(mc);
		events.add(decoded);
	    }
	    for (IDeviceAlertCreateRequest ac : batch.getAlerts()) {
		DecodedDeviceRequest<IDeviceAlertCreateRequest> decoded = new DecodedDeviceRequest<IDeviceAlertCreateRequest>();
		decoded.setHardwareId(batch.getHardwareId());
		decoded.setRequest(ac);
		events.add(decoded);
	    }
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
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }
}