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
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
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

    /** Nunber of threads for multithreaded tests */
    private static final int NUM_THREADS = 20;

    /** Nunber of calls performed per thread */
    private static final int NUM_CALLS_PER_THREAD = 100;

    @Test
    public void runMqttTest() throws Exception {
	ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
	CompletionService<Void> completionService = new ExecutorCompletionService<Void>(executor);

	for (int i = 0; i < NUM_THREADS; i++) {
	    completionService.submit(new MqttTester(NUM_CALLS_PER_THREAD));
	}
	for (int i = 0; i < NUM_THREADS; ++i) {
	    completionService.take().get();
	}
    }

    public class MqttTester implements Callable<Void> {

	/** Message count */
	private int messageCount;

	/** MQTT settings */
	private MQTT mqtt;

	/** MQTT connection */
	private BlockingConnection connection;

	/** Global object mapper instance */
	private ObjectMapper MAPPER = new ObjectMapper();

	public MqttTester(int messageCount) {
	    this.messageCount = messageCount;
	}

	@Override
	public Void call() throws Exception {
	    try {
		this.mqtt = new MQTT();
		mqtt.setHost("localhost", 1883);
		this.connection = mqtt.blockingConnection();
		connection.connect();
		System.out.println("Connected to: " + mqtt.getHost());

		long start = System.currentTimeMillis();
		for (int i = 0; i < messageCount; i++) {
		    sendLocationOverMqtt();
		}
		System.out.println(
			"Sent " + messageCount + " locations in " + (System.currentTimeMillis() - start) + "ms.");

		connection.disconnect();
		return null;
	    } catch (URISyntaxException e) {
		throw new RuntimeException(e);
	    } catch (Exception e) {
		throw new RuntimeException(e);
	    }
	}

	/**
	 * Send a location event request via JSON/MQTT.
	 * 
	 * @throws SiteWhereException
	 */
	public void sendLocationOverMqtt() throws SiteWhereException {
	    DeviceRequest request = new DeviceRequest();
	    request.setHardwareId("760f6a59-b6b6-441f-8b28-ca0ec3a9d91e");
	    request.setType(Type.DeviceLocation);
	    DeviceLocationCreateRequest location = new DeviceLocationCreateRequest();
	    location.setEventDate(new Date());
	    location.setLatitude(34.10469794977326);
	    location.setLongitude(-84.23966646194458);
	    location.setElevation(0.0);
	    Map<String, String> metadata = new HashMap<String, String>();
	    metadata.put("fromMQTT", "true");
	    location.setMetadata(metadata);
	    location.setUpdateState(false);
	    request.setRequest(location);
	    try {
		String payload = MAPPER.writeValueAsString(request);
		connection.publish("SiteWhere/input/json", payload.getBytes(), QoS.AT_MOST_ONCE, false);
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
		connection.publish("SiteWhere/input/json", payload.getBytes(), QoS.AT_LEAST_ONCE, false);
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
		connection.publish("SiteWhere/input/json", payload.getBytes(), QoS.AT_LEAST_ONCE, false);
		System.out.println("Message sent successfully.");
	    } catch (JsonProcessingException e) {
		throw new SiteWhereException(e);
	    } catch (Exception e) {
		throw new SiteWhereException(e);
	    }
	}
    }
}
