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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitewhere.rest.model.device.communication.DecodedDeviceRequest;
import com.sitewhere.rest.model.device.event.DeviceEventBatch;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.IDecodedDeviceRequest;
import com.sitewhere.spi.device.communication.IDeviceEventDecoder;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;

/**
 * Event decoder that converts a binary payload into the default SiteWhere REST
 * implementations using Jackson to marshal them as JSON.
 * 
 * @author Derek
 */
public class JsonBatchEventDecoder implements IDeviceEventDecoder<byte[]> {

	/** Used to map data into an object based on JSON parsing */
	private ObjectMapper mapper = new ObjectMapper();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.communication.IDeviceEventDecoder#decode(java.lang.Object)
	 */
	@Override
	public List<IDecodedDeviceRequest<?>> decode(byte[] payload) throws SiteWhereException {
		try {
			List<IDecodedDeviceRequest<?>> events = new ArrayList<IDecodedDeviceRequest<?>>();
			DeviceEventBatch batch = mapper.readValue(payload, DeviceEventBatch.class);
			for (IDeviceLocationCreateRequest lc : batch.getLocations()) {
				DecodedDeviceRequest<IDeviceLocationCreateRequest> decoded =
						new DecodedDeviceRequest<IDeviceLocationCreateRequest>();
				decoded.setHardwareId(batch.getHardwareId());
				decoded.setRequest(lc);
				events.add(decoded);
			}
			for (IDeviceMeasurementsCreateRequest mc : batch.getMeasurements()) {
				DecodedDeviceRequest<IDeviceMeasurementsCreateRequest> decoded =
						new DecodedDeviceRequest<IDeviceMeasurementsCreateRequest>();
				decoded.setHardwareId(batch.getHardwareId());
				decoded.setRequest(mc);
				events.add(decoded);
			}
			for (IDeviceAlertCreateRequest ac : batch.getAlerts()) {
				DecodedDeviceRequest<IDeviceAlertCreateRequest> decoded =
						new DecodedDeviceRequest<IDeviceAlertCreateRequest>();
				decoded.setHardwareId(batch.getHardwareId());
				decoded.setRequest(ac);
				events.add(decoded);
			}
			return events;
		} catch (JsonParseException e) {
			throw new SiteWhereException(e);
		} catch (JsonMappingException e) {
			throw new SiteWhereException(e);
		} catch (IOException e) {
			throw new SiteWhereException(e);
		}
	}
}