/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.activemq;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.log4j.Logger;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.provisioning.IInboundEventReceiver;

/**
 * Implementation of {@link IInboundEventReceiver} that uses an ActiveMQ broker to listen
 * on a transport for messages.
 * 
 * @author Derek
 */
public class ActiveMQInboundEventReceiver implements IInboundEventReceiver<byte[]> {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(ActiveMQInboundEventReceiver.class);

	/** Maximum number of backlogged messages before blocking */
	private static final int MAX_MESSAGE_BACKLOG = 1000;

	/** Number of consumers reading messages from the queue */
	private static final int DEFAULT_NUM_CONSUMERS = 3;

	/** ActiveMQ broker service */
	private BrokerService brokerService;

	/** Unique name of ActiveMQ broker */
	private String brokerName;

	/** URI for configuring transport */
	private String transportUri;

	/** Queue name used for inbound event data */
	private String queueName;

	/** Number of consumers used to read messages from the queue */
	private int numConsumers = DEFAULT_NUM_CONSUMERS;

	/** List of consumers reading messages */
	private List<Consumer> consumers = new ArrayList<Consumer>();

	/** Queue that holds messages to be processed */
	private BlockingQueue<byte[]> encodedMessages = new ArrayBlockingQueue<byte[]>(MAX_MESSAGE_BACKLOG);

	/** Thread pool for consumer processing */
	private ExecutorService consumersPool;

	public ActiveMQInboundEventReceiver() {
		this.brokerService = new BrokerService();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		if (getBrokerName() == null) {
			throw new SiteWhereException("Broker name is required for ActiveMQ broker.");
		}
		if (getTransportUri() == null) {
			throw new SiteWhereException("Transport URI is required for ActiveMQ broker.");
		}
		if (getQueueName() == null) {
			throw new SiteWhereException("Queue name is required for ActiveMQ broker.");
		}
		TransportConnector connector = new TransportConnector();
		try {
			connector.setUri(new URI(getTransportUri()));
			brokerService.addConnector(connector);
			brokerService.setBrokerName(getBrokerName());
			brokerService.start();
			startConsumers();
		} catch (Exception e) {
			throw new SiteWhereException("Error starting ActiveMQ inbound event receiver.", e);
		}
	}

	/**
	 * Starts consumers for reading messages into SiteWhere.
	 * 
	 * @throws SiteWhereException
	 */
	protected void startConsumers() throws SiteWhereException {
		consumers.clear();
		consumersPool = Executors.newFixedThreadPool(getNumConsumers());
		for (int i = 0; i < getNumConsumers(); i++) {
			Consumer consumer = new Consumer();
			consumer.start();
			consumersPool.equals(consumer);
			consumers.add(consumer);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		if (brokerService != null) {
			try {
				brokerService.stop();
			} catch (Exception e) {
				throw new SiteWhereException("Error stopping ActiveMQ broker.", e);
			}
		}
		stopConsumers();
	}

	/**
	 * Stops all consumers.
	 * 
	 * @throws SiteWhereException
	 */
	protected void stopConsumers() throws SiteWhereException {
		consumersPool.shutdownNow();
		for (Consumer consumer : consumers) {
			consumer.stop();
		}
	}

	/**
	 * Reads messages from the ActiveMQ queue and puts the binary content on a queue for
	 * SiteWhere to use.
	 * 
	 * @author Derek
	 */
	public class Consumer implements Runnable {

		/** Connection to ActiveMQ */
		private Connection connection;

		/** JMS session */
		private Session session;

		/** Consumer for reading data */
		private MessageConsumer consumer;

		public void start() throws SiteWhereException {
			try {
				// Create a ConnectionFactory
				ActiveMQConnectionFactory connectionFactory =
						new ActiveMQConnectionFactory("vm://" + getBrokerName());

				// Create a Connection
				this.connection = connectionFactory.createConnection();
				connection.start();

				// Create a Session
				this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

				// Create the destination (Topic or Queue)
				Destination destination = session.createQueue(getQueueName());

				// Create a MessageConsumer from the Session to the Topic or Queue
				this.consumer = session.createConsumer(destination);
			} catch (Exception e) {
				throw new SiteWhereException("Error starting ActiveMQ consumer.", e);
			}
		}

		public void stop() throws SiteWhereException {
			try {
				consumer.close();
				session.close();
				connection.close();
			} catch (Exception e) {
				throw new SiteWhereException("Error shutting down ActiveMQ consumer.", e);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			while (true) {
				try {
					Message message = consumer.receive();
					if (message instanceof TextMessage) {
						TextMessage textMessage = (TextMessage) message;
						getEncodedMessages().offer(textMessage.getText().getBytes());
					} else if (message instanceof BytesMessage) {
						BytesMessage bytesMessage = (BytesMessage) message;
						byte[] buffer = new byte[(int) bytesMessage.getBodyLength()];
						bytesMessage.readBytes(buffer);
						getEncodedMessages().offer(buffer);
					} else {
						LOGGER.warn("Ignoring unknown JMS message type: " + message.getClass().getName());
					}
				} catch (Exception e) {
					LOGGER.error("Error in ActiveMQ message processing.", e);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.provisioning.IInboundEventReceiver#getEncodedMessages()
	 */
	public BlockingQueue<byte[]> getEncodedMessages() {
		return encodedMessages;
	}

	public void setEncodedMessages(BlockingQueue<byte[]> encodedMessages) {
		this.encodedMessages = encodedMessages;
	}

	public String getBrokerName() {
		return brokerName;
	}

	public void setBrokerName(String brokerName) {
		this.brokerName = brokerName;
	}

	public String getTransportUri() {
		return transportUri;
	}

	public void setTransportUri(String transportUri) {
		this.transportUri = transportUri;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public int getNumConsumers() {
		return numConsumers;
	}

	public void setNumConsumers(int numConsumers) {
		this.numConsumers = numConsumers;
	}
}