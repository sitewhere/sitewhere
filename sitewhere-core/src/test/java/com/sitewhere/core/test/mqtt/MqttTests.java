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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitewhere.rest.model.device.communication.DeviceRequest;
import com.sitewhere.rest.model.device.communication.DeviceRequest.Type;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
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
		request.setHardwareId("d0c76c7e-43f3-47d5-a219-077484584c1a");
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
			Future<Void> future =
					getConnection().publish("SiteWhere/input/json", payload.getBytes(), QoS.AT_LEAST_ONCE,
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
