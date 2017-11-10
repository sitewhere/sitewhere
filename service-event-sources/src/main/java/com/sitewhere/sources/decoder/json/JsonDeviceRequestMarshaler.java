/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.decoder.json;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitewhere.rest.model.device.communication.DeviceRequest.Type;
import com.sitewhere.rest.model.device.event.request.DeviceAlertCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceCommandResponseCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceEventCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementsCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceRegistrationRequest;
import com.sitewhere.rest.model.device.event.request.DeviceStreamDataCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceMappingCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceStreamCreateRequest;
import com.sitewhere.sources.DecodedDeviceRequest;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMappingCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest;

/**
 * Custom marshaler for converting JSON payloads to {@link DecodedDeviceRequest}
 * objects.
 * 
 * @author Derek
 */
public class JsonDeviceRequestMarshaler extends JsonDeserializer<DecodedDeviceRequest<?>> {

    /** Used to map data into an object based on JSON parsing */
    private static ObjectMapper MAPPER = new ObjectMapper();

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml
     * .jackson. core.JsonParser,
     * com.fasterxml.jackson.databind.DeserializationContext)
     */
    @Override
    @SuppressWarnings("deprecation")
    public DecodedDeviceRequest<?> deserialize(JsonParser parser, DeserializationContext context)
	    throws IOException, JsonProcessingException {
	JsonNode node = parser.getCodec().readTree(parser);

	// Get type and validate its in the enum.
	JsonNode typeNode = node.get("type");
	if (typeNode == null) {
	    throw new JsonMappingException("Event type is required.");
	}
	try {
	    Type type = Type.valueOf(typeNode.asText());
	    JsonNode request = node.get("request");
	    if (request == null) {
		throw new IOException("Request is missing.");
	    }

	    JsonNode hardwareId = node.get("hardwareId");
	    if (hardwareId == null) {
		throw new IOException("Hardware id is missing.");
	    }

	    JsonNode originator = node.get("originator");
	    return unmarshal(hardwareId.textValue(), (originator == null) ? null : originator.textValue(), type,
		    request);
	} catch (IllegalArgumentException e) {
	    throw new JsonMappingException("Event type is not valid.");
	}
    }

    /**
     * Unmarshal payload based on type indicator.
     * 
     * @param hardwareId
     * @param originator
     * @param type
     * @param json
     * @return
     * @throws JsonProcessingException
     */
    @SuppressWarnings("deprecation")
    protected DecodedDeviceRequest<?> unmarshal(String hardwareId, String originator, Type type, JsonNode json)
	    throws JsonProcessingException {
	switch (type) {
	case RegisterDevice: {
	    DecodedDeviceRequest<IDeviceRegistrationRequest> decoded = new DecodedDeviceRequest<IDeviceRegistrationRequest>();
	    decoded.setHardwareId(hardwareId);
	    decoded.setOriginator(originator);
	    IDeviceRegistrationRequest req = MAPPER.treeToValue(json, DeviceRegistrationRequest.class);
	    decoded.setRequest(req);
	    return decoded;
	}
	case DeviceLocation: {
	    DecodedDeviceRequest<IDeviceLocationCreateRequest> decoded = new DecodedDeviceRequest<IDeviceLocationCreateRequest>();
	    decoded.setHardwareId(hardwareId);
	    decoded.setOriginator(originator);
	    IDeviceLocationCreateRequest req = MAPPER.treeToValue(json, DeviceLocationCreateRequest.class);
	    decoded.setRequest(req);
	    return decoded;
	}
	case DeviceMeasurements: {
	    DecodedDeviceRequest<IDeviceMeasurementsCreateRequest> decoded = new DecodedDeviceRequest<IDeviceMeasurementsCreateRequest>();
	    decoded.setHardwareId(hardwareId);
	    decoded.setOriginator(originator);
	    IDeviceMeasurementsCreateRequest req = parseMeasurementsRequest(json);
	    decoded.setRequest(req);
	    return decoded;
	}
	case DeviceAlert: {
	    DecodedDeviceRequest<IDeviceAlertCreateRequest> decoded = new DecodedDeviceRequest<IDeviceAlertCreateRequest>();
	    decoded.setHardwareId(hardwareId);
	    decoded.setOriginator(originator);
	    IDeviceAlertCreateRequest req = MAPPER.treeToValue(json, DeviceAlertCreateRequest.class);
	    decoded.setRequest(req);
	    return decoded;
	}
	case DeviceStream: {
	    DecodedDeviceRequest<IDeviceStreamCreateRequest> decoded = new DecodedDeviceRequest<IDeviceStreamCreateRequest>();
	    decoded.setHardwareId(hardwareId);
	    decoded.setOriginator(originator);
	    IDeviceStreamCreateRequest req = MAPPER.treeToValue(json, DeviceStreamCreateRequest.class);
	    decoded.setRequest(req);
	    return decoded;
	}
	case DeviceStreamData: {
	    DecodedDeviceRequest<IDeviceStreamDataCreateRequest> decoded = new DecodedDeviceRequest<IDeviceStreamDataCreateRequest>();
	    decoded.setHardwareId(hardwareId);
	    decoded.setOriginator(originator);
	    IDeviceStreamDataCreateRequest req = MAPPER.treeToValue(json, DeviceStreamDataCreateRequest.class);
	    decoded.setRequest(req);
	    return decoded;
	}
	case Acknowledge: {
	    DecodedDeviceRequest<IDeviceCommandResponseCreateRequest> decoded = new DecodedDeviceRequest<IDeviceCommandResponseCreateRequest>();
	    decoded.setHardwareId(hardwareId);
	    decoded.setOriginator(originator);
	    IDeviceCommandResponseCreateRequest req = MAPPER.treeToValue(json,
		    DeviceCommandResponseCreateRequest.class);
	    decoded.setRequest(req);
	    return decoded;
	}
	case MapDevice: {
	    DecodedDeviceRequest<IDeviceMappingCreateRequest> decoded = new DecodedDeviceRequest<IDeviceMappingCreateRequest>();
	    decoded.setHardwareId(hardwareId);
	    decoded.setOriginator(originator);
	    IDeviceMappingCreateRequest req = MAPPER.treeToValue(json, DeviceMappingCreateRequest.class);
	    decoded.setRequest(req);
	    return decoded;
	}
	default: {
	    throw new JsonMappingException("Unhandled event type: " + type.name());
	}
	}
    }

    /**
     * Support JSON representations of device measurements that do not
     * necessarily conform to object model. Converts boolean values to 1.0 or
     * 0.0. Adds String values as metadata.
     * 
     * @param json
     * @return
     * @throws JsonProcessingException
     */
    @SuppressWarnings("deprecation")
    protected IDeviceMeasurementsCreateRequest parseMeasurementsRequest(JsonNode json) throws JsonProcessingException {
	try {
	    return MAPPER.treeToValue(json, DeviceMeasurementsCreateRequest.class);
	} catch (JsonProcessingException e) {
	    DeviceMeasurementsCreateRequest mxs = new DeviceMeasurementsCreateRequest();
	    try {
		MAPPER.readerForUpdating(mxs).forType(DeviceEventCreateRequest.class)
			.without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(json);
		JsonNode mxsJson = json.get("measurements");
		if (mxsJson != null) {
		    Iterator<String> mxNames = mxsJson.fieldNames();
		    Map<String, String> metadata = new HashMap<String, String>();
		    while (mxNames.hasNext()) {
			String mxName = mxNames.next();
			JsonNode mxJson = mxsJson.get(mxName);
			if (mxJson.isFloatingPointNumber()) {
			    mxs.addOrReplaceMeasurement(mxName, mxJson.asDouble());
			} else if (mxJson.isBoolean()) {
			    boolean mxBoolean = mxJson.asBoolean();
			    mxs.addOrReplaceMeasurement(mxName, (mxBoolean) ? 1.0 : 0.0);
			} else if (mxJson.isTextual()) {
			    metadata.put(mxName, mxJson.asText());
			}
		    }
		    if (metadata.size() > 0) {
			mxs.setMetadata(metadata);
		    }
		}
		return mxs;
	    } catch (IOException ioe) {
		throw new JsonMappingException("Error parsing device event fields.", ioe);
	    } catch (Throwable t) {
		throw new JsonMappingException("Error parsing device measurements.", t);
	    }
	}
    }
}