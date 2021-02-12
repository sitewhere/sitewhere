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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitewhere.rest.model.device.communication.DeviceRequest.Type;
import com.sitewhere.rest.model.device.event.request.DeviceAlertCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceCommandResponseCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceRegistrationRequest;
import com.sitewhere.rest.model.device.request.DeviceStreamCreateRequest;
import com.sitewhere.rest.model.device.streaming.request.DeviceStreamDataCreateRequest;
import com.sitewhere.sources.DecodedDeviceRequest;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest;
import com.sitewhere.spi.device.streaming.request.IDeviceStreamDataCreateRequest;

/**
 * Custom marshaler for converting JSON payloads to {@link DecodedDeviceRequest}
 * objects.
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

	    JsonNode deviceToken = node.get("deviceToken");
	    if (deviceToken == null) {
		throw new IOException("Device token is missing.");
	    }

	    JsonNode originator = node.get("originator");
	    return unmarshal(deviceToken.textValue(), (originator == null) ? null : originator.textValue(), type,
		    request);
	} catch (IllegalArgumentException e) {
	    throw new JsonMappingException("Event type is not valid.");
	}
    }

    /**
     * Unmarshal payload based on type indicator.
     * 
     * @param deviceToken
     * @param originator
     * @param type
     * @param json
     * @return
     * @throws JsonProcessingException
     */
    @SuppressWarnings("deprecation")
    public static DecodedDeviceRequest<?> unmarshal(String deviceToken, String originator, Type type, JsonNode json)
	    throws JsonProcessingException {
	switch (type) {
	case RegisterDevice: {
	    DecodedDeviceRequest<IDeviceRegistrationRequest> decoded = new DecodedDeviceRequest<IDeviceRegistrationRequest>();
	    decoded.setDeviceToken(deviceToken);
	    decoded.setOriginator(originator);
	    IDeviceRegistrationRequest req = MAPPER.treeToValue(json, DeviceRegistrationRequest.class);
	    decoded.setRequest(req);
	    return decoded;
	}
	case DeviceLocation: {
	    DecodedDeviceRequest<IDeviceLocationCreateRequest> decoded = new DecodedDeviceRequest<IDeviceLocationCreateRequest>();
	    decoded.setDeviceToken(deviceToken);
	    decoded.setOriginator(originator);
	    IDeviceLocationCreateRequest req = MAPPER.treeToValue(json, DeviceLocationCreateRequest.class);
	    decoded.setRequest(req);
	    return decoded;
	}
	case DeviceMeasurement: {
	    DecodedDeviceRequest<IDeviceMeasurementCreateRequest> decoded = new DecodedDeviceRequest<IDeviceMeasurementCreateRequest>();
	    decoded.setDeviceToken(deviceToken);
	    decoded.setOriginator(originator);
	    IDeviceMeasurementCreateRequest req = MAPPER.treeToValue(json, DeviceMeasurementCreateRequest.class);
	    decoded.setRequest(req);
	    return decoded;
	}
	case DeviceAlert: {
	    DecodedDeviceRequest<IDeviceAlertCreateRequest> decoded = new DecodedDeviceRequest<IDeviceAlertCreateRequest>();
	    decoded.setDeviceToken(deviceToken);
	    decoded.setOriginator(originator);
	    IDeviceAlertCreateRequest req = MAPPER.treeToValue(json, DeviceAlertCreateRequest.class);
	    decoded.setRequest(req);
	    return decoded;
	}
	case DeviceStream: {
	    DecodedDeviceRequest<IDeviceStreamCreateRequest> decoded = new DecodedDeviceRequest<IDeviceStreamCreateRequest>();
	    decoded.setDeviceToken(deviceToken);
	    decoded.setOriginator(originator);
	    IDeviceStreamCreateRequest req = MAPPER.treeToValue(json, DeviceStreamCreateRequest.class);
	    decoded.setRequest(req);
	    return decoded;
	}
	case DeviceStreamData: {
	    DecodedDeviceRequest<IDeviceStreamDataCreateRequest> decoded = new DecodedDeviceRequest<IDeviceStreamDataCreateRequest>();
	    decoded.setDeviceToken(deviceToken);
	    decoded.setOriginator(originator);
	    IDeviceStreamDataCreateRequest req = MAPPER.treeToValue(json, DeviceStreamDataCreateRequest.class);
	    decoded.setRequest(req);
	    return decoded;
	}
	case Acknowledge: {
	    DecodedDeviceRequest<IDeviceCommandResponseCreateRequest> decoded = new DecodedDeviceRequest<IDeviceCommandResponseCreateRequest>();
	    decoded.setDeviceToken(deviceToken);
	    decoded.setOriginator(originator);
	    IDeviceCommandResponseCreateRequest req = MAPPER.treeToValue(json,
		    DeviceCommandResponseCreateRequest.class);
	    decoded.setRequest(req);
	    return decoded;
	}
	default: {
	    throw new JsonMappingException("Unhandled event type: " + type.name());
	}
	}
    }
}