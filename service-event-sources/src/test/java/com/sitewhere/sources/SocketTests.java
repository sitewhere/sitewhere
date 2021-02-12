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
package com.sitewhere.sources;

import java.net.Socket;

import org.junit.Test;

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
	byte[] encoded = EventsHelper.generateJsonMeasurementsMessage(HARDWARE_ID);
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
    public void doLoraTest() throws Exception {
	Socket socket = new Socket("localhost", 8484);
	String message = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		+ "<DevEUI_uplink xmlns=\"http://uri.actility.com/lora\">"
		+ "<Time>2016-05-31T20:20:05.186+02:00</Time><DevEUI>0018B20000000999</DevEUI>"
		+ "<FPort>1</FPort><FCntUp>309</FCntUp><ADRbit>1</ADRbit><FCntDn>6</FCntDn>"
		+ "<payload_hex>0264060a0000000000000000</payload_hex><mic_hex>220bd706</mic_hex>"
		+ "<Lrcid>00000065</Lrcid><LrrRSSI>-24.000000</LrrRSSI><LrrSNR>10.000000</LrrSNR>"
		+ "<SpFact>7</SpFact><SubBand>G1</SubBand><Channel>LC1</Channel><DevLrrCnt>1</DevLrrCnt>"
		+ "<Lrrid>004a059a</Lrrid><Lrrs><Lrr><Lrrid>004a059a</Lrrid><LrrRSSI>-24.000000</LrrRSSI>"
		+ "<LrrSNR>10.000000</LrrSNR></Lrr></Lrrs>"
		+ "<CustomerID>999999999</CustomerID><CustomerData>{\"alr\":{\"pro\":\"LORA/genericA.1\",\"ver\":\"1\"}}</CustomerData>"
		+ "<ModelCfg>0</ModelCfg></DevEUI_uplink>";
	byte[] encoded = message.getBytes();
	socket.getOutputStream().write(encoded);
	socket.getOutputStream().flush();
	socket.getOutputStream().close();
	socket.close();
    }

    @Test
    public void doMqttTest() throws Exception {
	// MQTT mqtt = new MQTT();
	// try {
	// mqtt.setHost("juju-azure-4w909gp487.cloudapp.net", 1883);
	// BlockingConnection connection = mqtt.blockingConnection();
	// connection.connect();
	// connection.publish("SiteWhere/input/protobuf",
	// EventsHelper.generateEncodedMeasurementsMessage("3aa3303a-6e96-4f5d-b31c-0f5361ee3d3d"),
	// QoS.AT_MOST_ONCE, false);
	// connection.disconnect();
	// } catch (URISyntaxException e) {
	// throw new SiteWhereException("Invalid hostname for MQTT server.", e);
	// }
    }
}