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
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.rest.model.device.event.DeviceEventBatch;
import com.sitewhere.sources.DecodedDeviceRequest;
import com.sitewhere.sources.spi.EventDecodeException;
import com.sitewhere.sources.spi.IDecodedDeviceRequest;
import com.sitewhere.sources.spi.IDeviceEventDecoder;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;

/**
 * Event decoder that converts a binary payload into the default SiteWhere REST
 * implementations using Jackson to marshal them as JSON.
 * 
 * DEPRECATED: This only supports events that can be wrapped in a
 * {@link DeviceEventBatch} object and does not offer full-featured support. Use
 * {@link JsonDeviceRequestDecoder} instead.
 */
public class JsonBatchEventDecoder extends TenantEngineLifecycleComponent implements IDeviceEventDecoder<byte[]> {

    /** Used to map data into an object based on JSON parsing */
    private ObjectMapper mapper = new ObjectMapper();

    public JsonBatchEventDecoder() {
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
	    DeviceEventBatch batch = mapper.readValue(payload, DeviceEventBatch.class);
	    for (IDeviceLocationCreateRequest lc : batch.getLocations()) {
		DecodedDeviceRequest<IDeviceLocationCreateRequest> decoded = new DecodedDeviceRequest<IDeviceLocationCreateRequest>();
		decoded.setDeviceToken(batch.getDeviceToken());
		decoded.setRequest(lc);
		events.add(decoded);
	    }
	    for (IDeviceMeasurementCreateRequest mc : batch.getMeasurements()) {
		DecodedDeviceRequest<IDeviceMeasurementCreateRequest> decoded = new DecodedDeviceRequest<IDeviceMeasurementCreateRequest>();
		decoded.setDeviceToken(batch.getDeviceToken());
		decoded.setRequest(mc);
		events.add(decoded);
	    }
	    for (IDeviceAlertCreateRequest ac : batch.getAlerts()) {
		DecodedDeviceRequest<IDeviceAlertCreateRequest> decoded = new DecodedDeviceRequest<IDeviceAlertCreateRequest>();
		decoded.setDeviceToken(batch.getDeviceToken());
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
}