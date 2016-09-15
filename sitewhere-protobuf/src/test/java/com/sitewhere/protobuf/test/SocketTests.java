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
    public void doInteractiveSocketTest() throws Exception {
	Socket socket = new Socket("localhost", 5432);

	System.out.println("Sending header...");
	byte[] header = { 0x01, 0x00, 0x01, 0x17 };
	socket.getOutputStream().write(header);
	socket.getInputStream().read();

	System.out.println("Sending id...");
	byte[] id = { 0x02, 0x00, 0x0a, 0x03, 0x55, 0x27, (byte) 0x80, 0x58, 0x28, 0x11, 0x25 };
	socket.getOutputStream().write(id);
	socket.getInputStream().read();

	System.out.println("Sending data...");
	byte[] data1 = { 0x02, 0x00, 0x17, 0x02, 0x01, 0x00, 0x01, 0x00, 0x0f, 0x0f, 0x00, 0x00, 0x03, 0x16, 0x05, 0x30,
		0x16, 0x24, 0x6b, 0x01, 0x00, 0x00, 0x6c, 0x03 };
	socket.getOutputStream().write(data1);
	socket.getInputStream().read();

	System.out.println("Closing session.");
	byte[] end = { 0x03, 0x04 };
	socket.getOutputStream().write(end);

	socket.getOutputStream().flush();
	socket.getOutputStream().close();
	socket.close();
    }

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
    public void doOssTest() throws Exception {
	Socket socket = new Socket("localhost", 8484);
	String message = "{ \"deveui\": \"hex\", \"dataFrame\": \"AB==\","
		+ "\"port\": 1, \"timestamp\": \"2015-02-11 10:33:00.578\","
		+ "\"fcnt\": 138,\"rssi\": -111,\"snr\": -6,\"sf_used\": \"8\",\"id\": 278998,"
		+ "\"live\": true,\"decrypted\":false}";
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