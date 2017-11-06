/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.core.test.mqtt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
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
    private MqttAsyncClient mqttClient;

    @Before
    public void setup() {
	try {
	    CountDownLatch latch = new CountDownLatch(1);
	    this.mqttClient = new MqttAsyncClient("tcp://localhost:1883", "sitewhere", new MemoryPersistence());
	    MqttConnectOptions options = new MqttConnectOptions();
	    options.setMaxInflight(5000);
	    mqttClient.connect(options, new IMqttActionListener() {

		@Override
		public void onSuccess(IMqttToken asyncActionToken) {
		    latch.countDown();
		}

		@Override
		public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
		    throw new RuntimeException(exception);
		}
	    });
	    latch.await();
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    @After
    public void teardown() {
	if ((mqttClient != null) && (mqttClient.isConnected())) {
	    try {
		mqttClient.disconnect();
	    } catch (MqttException e) {
		throw new RuntimeException(e);
	    }
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
	request.setHardwareId("c23e7a22-b98d-4dcf-80db-e95e6990fe41");
	request.setType(Type.DeviceLocation);
	DeviceLocationCreateRequest location = new DeviceLocationCreateRequest();
	location.setEventDate(new Date());
	location.setLatitude(34.10469794977326);
	location.setLongitude(-84.23966646194458);
	location.setElevation(0.0);
	// location.setAlternateId("my_alternate_id");
	Map<String, String> metadata = new HashMap<String, String>();
	metadata.put("fromMQTT", "true");
	location.setMetadata(metadata);
	location.setUpdateState(true);
	request.setRequest(location);
	try {
	    String payload = (new ObjectMapper()).writeValueAsString(request);
	    // System.out.println("Payload:\n\n" + payload);
	    getMqttClient().publish("SiteWhere/input/json", payload.getBytes(), 1, false);
	    System.out.println("Message sent successfully.");
	} catch (JsonProcessingException e) {
	    throw new SiteWhereException(e);
	} catch (Exception e) {
	    throw new SiteWhereException(e);
	}
    }

    @Test
    public void sendMany() throws SiteWhereException {
	int numThreads = 10;
	ExecutorService executor = Executors.newFixedThreadPool(numThreads);
	CountDownLatch latch = new CountDownLatch(numThreads);
	for (int i = 0; i < numThreads; i++) {
	    executor.execute(new Runnable() {

		@Override
		public void run() {
		    for (int i = 0; i < 100; i++) {
			try {
			    sendLocationOverMqtt();
			    try {
				Thread.sleep(50);
			    } catch (InterruptedException e) {
				System.err.println(e.getMessage());
			    }
			} catch (SiteWhereException e) {
			    System.err.println(e.getMessage());
			}
		    }
		    latch.countDown();
		}
	    });
	}
	try {
	    latch.await();
	} catch (InterruptedException e) {
	    System.err.println("Interrupted.");
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
	    getMqttClient().publish("SiteWhere/input/json", payload.getBytes(), 1, false);
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
	    getMqttClient().publish("SiteWhere/input/json", payload.getBytes(), 1, false);
	    System.out.println("Message sent successfully.");
	} catch (JsonProcessingException e) {
	    throw new SiteWhereException(e);
	} catch (Exception e) {
	    throw new SiteWhereException(e);
	}
    }

    public MqttAsyncClient getMqttClient() {
	return mqttClient;
    }

    public void setMqttClient(MqttAsyncClient mqttClient) {
	this.mqttClient = mqttClient;
    }
}
