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

import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.junit.Test;

import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.sitewhere.microservice.util.MarshalUtils;

/**
 * Test cases for various types of event sources.
 */
public class EventSourceTests {

    /** Hostname for SiteWhere instance */
    private static final String HOSTNAME = "192.168.171.129";

    /** Device token for test message */
    private static final String DEVICE_TOKEN = "20544-OPENHAB-7313064";

    /** Nunber of threads for multithreaded tests */
    private static final int NUM_THREADS = 5;

    /** Nunber of calls performed per thread */
    private static final int NUM_CALLS_PER_THREAD = 100;

    @Test
    public void doJMSTest() throws Exception {
	(new JmsTester(1)).call();
    }

    @Test
    public void doMultithreadedJmsTest() throws Exception {
	ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
	CompletionService<Void> completionService = new ExecutorCompletionService<Void>(executor);

	for (int i = 0; i < NUM_THREADS; i++) {
	    completionService.submit(new JmsTester(NUM_CALLS_PER_THREAD));
	}
	for (int i = 0; i < NUM_THREADS; ++i) {
	    completionService.take().get();
	}
    }

    /**
     * Start a broker that acts as an intermediary for sending JMS messages to
     * SiteWhere. This method starts the broker and the method below submits
     * messages to it so that SiteWhere can process them.
     * 
     * @throws Exception
     */
    @Test
    public void doActiveMqClientTest() throws Exception {
	// Create broker.
	BrokerService brokerService = new BrokerService();
	brokerService.setBrokerName("SiteWhereTest");
	TransportConnector connector = new TransportConnector();
	connector.setUri(new URI("tcp://0.0.0.0:1234"));
	brokerService.addConnector(connector);
	brokerService.setUseShutdownHook(false);
	brokerService.setUseJmx(false);
	brokerService.start();

	// Create connection to pull messages from broker.
	ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://SiteWhereTest");
	javax.jms.Connection connection = connectionFactory.createConnection();
	connection.setExceptionListener(new ExceptionListener() {

	    @Override
	    public void onException(JMSException exception) {
		exception.printStackTrace();
	    }
	});
	connection.start();

	while (true) {
	    Thread.sleep(1000);
	}
    }

    /**
     * Send a single message to the queue established in the method above.
     * 
     * @throws Exception
     */
    @Test
    public void doActiveMqClientSend() throws Exception {
	ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:1234");
	javax.jms.Connection connection = connectionFactory.createConnection();
	connection.start();

	Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	Destination destination = session.createQueue("SITEWHERE.OUT");

	MessageProducer producer = session.createProducer(destination);

	TextMessage message = session.createTextMessage();
	message.setText(MarshalUtils.marshalJsonAsPrettyString(EventsHelper.generateMeasurementsRequest(DEVICE_TOKEN)));
	producer.send(message);

	session.close();
	connection.close();
    }

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

    public class JmsTester implements Callable<Void> {

	private int messageCount;

	public JmsTester(int messageCount) {
	    this.messageCount = messageCount;
	}

	@Override
	public Void call() throws Exception {
	    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://" + HOSTNAME + ":8500");
	    javax.jms.Connection connection = connectionFactory.createConnection();
	    connection.start();

	    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	    Destination destination = session.createQueue("SITEWHERE.IN");

	    MessageProducer producer = session.createProducer(destination);

	    for (int i = 0; i < messageCount; i++) {
		BytesMessage message = session.createBytesMessage();
		message.writeBytes(EventsHelper.generateJsonMeasurementsMessage(DEVICE_TOKEN));
		producer.send(message);
	    }

	    session.close();
	    connection.close();
	    return null;
	}
    }
}