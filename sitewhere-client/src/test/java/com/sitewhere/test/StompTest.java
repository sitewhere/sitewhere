/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.test;

import java.util.Date;

import org.apache.activemq.transport.stomp.StompConnection;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitewhere.rest.model.device.event.DeviceEventBatch;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementsCreateRequest;

/**
 * Test methods for sending messages over the Stomp protocol.
 * 
 * @author Derek
 */
public class StompTest {

	/** Hardware id for test message */
	private static final String HARDWARE_ID = "26ac91a5-aa59-44ef-af77-85e470ab0c16";

	/** Mapper used for marshaling event create requests */
	private static ObjectMapper MAPPER = new ObjectMapper();

	@Test
	public void doStompTest() throws Exception {
		StompConnection connection = new StompConnection();
		connection.open("localhost", 2345);
		connection.connect("system", "manager");

		String payload = createMeasurementsJson(HARDWARE_ID);

		connection.begin("tx1");
		connection.send("/queue/SITEWHERE.STOMP", payload, "tx1", null);
		connection.commit("tx1");

		connection.disconnect();
	}

	/**
	 * Create a JSON request for creating a new measurements event.
	 * 
	 * @param hardwareId
	 * @return
	 * @throws Exception
	 */
	protected String createMeasurementsJson(String hardwareId) throws Exception {
		DeviceEventBatch batch = new DeviceEventBatch();
		batch.setHardwareId(hardwareId);
		DeviceMeasurementsCreateRequest request = new DeviceMeasurementsCreateRequest();
		request.setEventDate(new Date());
		request.addOrReplaceMeasurement("engine.temp", 98.76);
		batch.getMeasurements().add(request);
		return MAPPER.writeValueAsString(batch);
	}
}