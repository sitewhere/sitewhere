/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.protobuf.test;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.qpid.proton.Proton;
import org.apache.qpid.proton.amqp.Binary;
import org.apache.qpid.proton.amqp.messaging.Data;
import org.apache.qpid.proton.message.Message;
import org.apache.qpid.proton.messenger.Messenger;
import org.junit.Test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.sitewhere.device.provisioning.protobuf.ProtobufDeviceEventEncoder;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementsCreateRequest;
import com.sitewhere.rest.model.device.provisioning.DecodedDeviceEventRequest;
import com.sitewhere.spi.SiteWhereException;

public class ActiveMQTests {

	/** Hardware id for test message */
	private static final String HARDWARE_ID = "12cf0747-9530-42f6-b6a7-5db32c3cfa3e";

	/** Nunber of threads for multithreaded tests */
	private static final int NUM_THREADS = 150;

	/** Nunber of calls performed per thread */
	private static final int NUM_CALLS_PER_THREAD = 1000;

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

	@Test
	public void doRabbitMQTest() throws Exception {
		String exchangeName = "sitewhere";
		String queueName = "SITEWHERE.IN";
		String routingKey = "sitewhere";

		ConnectionFactory factory = new ConnectionFactory();
		factory.setUri("amqp://localhost:5672/SITEWHERE.IN");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.exchangeDeclare(exchangeName, "direct", true);
		channel.queueDeclare(queueName, true, false, false, null);
		channel.queueBind(queueName, exchangeName, routingKey);

		byte[] messageBodyBytes = generateEncodedMeasurementsMessage();
		channel.basicPublish(exchangeName, routingKey, null, messageBodyBytes);

		channel.close();
		connection.close();
	}

	@Test
	public void doQpidTest() throws Exception {
		Messenger messenger = Proton.messenger();
		messenger.start();

		Data data = new Data(new Binary(generateEncodedMeasurementsMessage()));

		Message message = Proton.message();
		message.setAddress("amqp://127.0.0.1:5672/SITEWHERE.IN");
		message.setBody(data);
		messenger.put(message);

		messenger.send();
		messenger.stop();
	}

	/**
	 * Generate an encoded measurements message.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	protected byte[] generateEncodedMeasurementsMessage() throws SiteWhereException {
		DecodedDeviceEventRequest request = new DecodedDeviceEventRequest();
		request.setHardwareId(HARDWARE_ID);

		DeviceMeasurementsCreateRequest mx = new DeviceMeasurementsCreateRequest();
		mx.setEventDate(new Date());
		mx.addOrReplaceMeasurement("fuel.level", 123.4);
		request.setRequest(mx);

		return (new ProtobufDeviceEventEncoder()).encode(request);
	}

	public class JmsTester implements Callable<Void> {

		private int messageCount;

		public JmsTester(int messageCount) {
			this.messageCount = messageCount;
		}

		@Override
		public Void call() throws Exception {
			ActiveMQConnectionFactory connectionFactory =
					new ActiveMQConnectionFactory("tcp://localhost:1234");
			javax.jms.Connection connection = connectionFactory.createConnection();
			connection.start();

			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue("SITEWHERE.IN");

			MessageProducer producer = session.createProducer(destination);

			for (int i = 0; i < messageCount; i++) {
				BytesMessage message = session.createBytesMessage();
				message.writeBytes(generateEncodedMeasurementsMessage());
				producer.send(message);
			}

			session.close();
			connection.close();
			return null;
		}
	}
}