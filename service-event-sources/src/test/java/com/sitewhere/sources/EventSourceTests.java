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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Test cases for various types of event sources.
 */
public class EventSourceTests {

    /** Hostname for SiteWhere instance */
    private static final String HOSTNAME = "192.168.171.129";

    /** Device token for test message */
    private static final String DEVICE_TOKEN = "20544-OPENHAB-7313064";

    @Test
    public void doAzureEventSourceSendTest() throws Exception {
	ExecutorService executor = Executors.newSingleThreadExecutor();
	final ConnectionStringBuilder connStr = new ConnectionStringBuilder().setNamespaceName("sitewhere")
		.setEventHubName("events").setSasKeyName("RootManageSharedAccessKey").setSasKey("xxx");

	byte[] payloadBytes = EventsHelper.generateJsonMeasurementsMessage(DEVICE_TOKEN);
	EventData sendEvent = EventData.create(payloadBytes);

	final EventHubClient ehClient = EventHubClient.createSync(connStr.toString(), executor);
	ehClient.sendSync(sendEvent);
	ehClient.closeSync();
	executor.shutdown();
    }

    @Test
    public void doRabbitMQTest() throws Exception {
	String exchangeName = "sitewhere";
	String queueName = "sitewhere.input";
	String routingKey = "sitewhere";

	ConnectionFactory factory = new ConnectionFactory();
	factory.setUri("amqp://" + HOSTNAME + ":5672");
	Connection connection = factory.newConnection();
	Channel channel = connection.createChannel();

	channel.exchangeDeclare(exchangeName, "direct", true);
	channel.queueDeclare(queueName, true, false, false, null);
	channel.queueBind(queueName, exchangeName, routingKey);

	byte[] messageBodyBytes = EventsHelper.generateJsonMeasurementsMessage(DEVICE_TOKEN);
	channel.basicPublish(exchangeName, routingKey, null, messageBodyBytes);

	channel.close();
	connection.close();
    }
}