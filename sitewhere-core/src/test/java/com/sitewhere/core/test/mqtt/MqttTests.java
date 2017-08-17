/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.core.test.mqtt;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.fusesource.mqtt.client.Future;
import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sitewhere.rest.model.device.communication.DeviceRequest;
import com.sitewhere.rest.model.device.communication.DeviceRequest.Type;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementsCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceMappingCreateRequest;
import com.sitewhere.spi.SiteWhereException;

public class MqttTests {

    /** MQTT settings */
    private MQTT mqtt;

    /** MQTT connection */
    private FutureConnection connection;

    @Before
    public void setup() {
	try {
	    MQTT mqtt = new MQTT();
	    mqtt.setHost("localhost", 1883);
	    connection = mqtt.futureConnection();
	    Future<Void> future = connection.connect();
	    future.await(3, TimeUnit.SECONDS);
	    System.out.println("Connected to: " + mqtt.getHost());
	} catch (URISyntaxException e) {
	    throw new RuntimeException(e);
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    @After
    public void teardown() {
	if (connection != null) {
	    connection.disconnect();
	}
    }

    /**
     * Send a location event request via JSON/MQTT.
     * 
     * @throws SiteWhereException
     */
    @Test
    public void sendLocationOverMqtt() throws SiteWhereException {
	DeviceRequest request = new DeviceRequest();
	request.setHardwareId("3cad8d84-faca-4a4e-86f3-9a900e6c21c9");
	request.setType(Type.DeviceLocation);
	DeviceLocationCreateRequest location = new DeviceLocationCreateRequest();
	location.setEventDate(new Date());
	location.setLatitude(34.10469794977326);
	location.setLongitude(-84.23966646194458);
	location.setElevation(0.0);
	Map<String, String> metadata = new HashMap<String, String>();
	metadata.put("fromMQTT", "true");
	location.setMetadata(metadata);
	location.setUpdateState(true);
	request.setRequest(location);
	try {
	    String payload = (new ObjectMapper()).writeValueAsString(request);
	    System.out.println("Payload:\n\n" + payload);
	    Future<Void> future = getConnection().publish("SiteWhere/input/json", payload.getBytes(), QoS.AT_LEAST_ONCE,
		    false);
	    future.await(3, TimeUnit.SECONDS);
	    System.out.println("Message sent successfully.");
	} catch (JsonProcessingException e) {
	    throw new SiteWhereException(e);
	} catch (Exception e) {
	    throw new SiteWhereException(e);
	}
    }

    /**
     * Send device measurements request with non-numeric measurement values.
     * 
     * @throws SiteWhereException
     */
    @Test
    public void sendNonStandardMeasurements() throws SiteWhereException {
	DeviceRequest request = new DeviceRequest();
	request.setHardwareId("123-TEST-4567890");
	request.setType(Type.DeviceMeasurements);
	DeviceMeasurementsCreateRequest mxs = new DeviceMeasurementsCreateRequest();
	mxs.addOrReplaceMeasurement("normal", 1.234);
	Map<String, String> metadata = new HashMap<String, String>();
	metadata.put("fromMQTT", "true");
	mxs.setMetadata(metadata);
	mxs.setUpdateState(true);
	request.setRequest(mxs);
	try {
	    ObjectMapper mapper = new ObjectMapper();
	    JsonNode json = mapper.convertValue(request, JsonNode.class);
	    ObjectNode mxsNode = (ObjectNode) json.get("request").get("measurements");
	    mxsNode.put("stringTest", "value");
	    mxsNode.put("booleanTest", true);
	    String payload = mapper.writeValueAsString(json);

	    System.out.println("Payload:\n\n" + payload);
	    Future<Void> future = getConnection().publish("SiteWhere/input/json", payload.getBytes(), QoS.AT_LEAST_ONCE,
		    false);
	    future.await(3, TimeUnit.SECONDS);
	    System.out.println("Message sent successfully.");
	} catch (JsonProcessingException e) {
	    throw new SiteWhereException(e);
	} catch (Exception e) {
	    throw new SiteWhereException(e);
	}
    }

    /**
     * Send request for mapping a device as an element of a composite device.
     * 
     * @throws SiteWhereException
     */
    @Test
    public void sendDeviceMappingCreateRequest() throws SiteWhereException {
	DeviceRequest request = new DeviceRequest();
	request.setHardwareId("19c5a02a-a9a9-4b39-ad4e-1066bb464141");
	request.setType(Type.MapDevice);
	DeviceMappingCreateRequest mapping = new DeviceMappingCreateRequest();
	mapping.setCompositeDeviceHardwareId("072d6db9-5349-4162-bfbe-e4990a101f29");
	mapping.setMappingPath("/default/pci/pci1");
	request.setRequest(mapping);
	try {
	    String payload = (new ObjectMapper()).writeValueAsString(request);
	    System.out.println("Payload:\n\n" + payload);
	    Future<Void> future = getConnection().publish("SiteWhere/input/json", payload.getBytes(), QoS.AT_LEAST_ONCE,
		    false);
	    future.await(3, TimeUnit.SECONDS);
	    System.out.println("Message sent successfully.");
	} catch (JsonProcessingException e) {
	    throw new SiteWhereException(e);
	} catch (Exception e) {
	    throw new SiteWhereException(e);
	}
    }

    public MQTT getMqtt() {
	return mqtt;
    }

    public void setMqtt(MQTT mqtt) {
	this.mqtt = mqtt;
    }

    public FutureConnection getConnection() {
	return connection;
    }

    public void setConnection(FutureConnection connection) {
	this.connection = connection;
    }
}
