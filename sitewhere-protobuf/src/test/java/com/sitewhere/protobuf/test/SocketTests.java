/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.protobuf.test;

import java.net.Socket;
import java.net.URISyntaxException;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.junit.Test;

import com.sitewhere.spi.SiteWhereException;

public class SocketTests {

	/** Hardware id for test message */
	private static final String HARDWARE_ID = "74c79297-6197-47b2-85b1-ba140968f7c8";

	/** Port that server socket listens on */
	public static final int SERVER_SOCKET_PORT = 8585;

	@Test
	public void doSocketTest() throws Exception {
		Socket socket = new Socket("localhost", SERVER_SOCKET_PORT);
		byte[] encoded = EventsHelper.generateEncodedMeasurementsMessage(HARDWARE_ID);
		socket.getOutputStream().write(encoded);
		socket.getOutputStream().flush();
		socket.getOutputStream().close();
		socket.close();
	}

	@Test
	public void doSocketTest2() throws Exception {
		Socket socket = new Socket("localhost", SERVER_SOCKET_PORT);
		String message = "LOC," + HARDWARE_ID + ",33.7550,-84.3900";
		byte[] encoded = message.getBytes();
		socket.getOutputStream().write(encoded);
		socket.getOutputStream().flush();
		socket.getOutputStream().close();
		socket.close();
	}

	@Test
	public void doMqttTest() throws Exception {
		MQTT mqtt = new MQTT();
		try {
			mqtt.setHost("juju-azure-4w909gp487.cloudapp.net", 1883);
			BlockingConnection connection = mqtt.blockingConnection();
			connection.connect();
			connection.publish("SiteWhere/input/protobuf",
					EventsHelper.generateEncodedMeasurementsMessage("3aa3303a-6e96-4f5d-b31c-0f5361ee3d3d"),
					QoS.AT_MOST_ONCE, false);
			connection.disconnect();
		} catch (URISyntaxException e) {
			throw new SiteWhereException("Invalid hostname for MQTT server.", e);
		}
	}
}