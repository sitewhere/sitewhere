/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rabbitmq;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.sitewhere.device.communication.EventProcessingLogic;
import com.sitewhere.device.communication.InboundEventReceiver;
import com.sitewhere.spi.SiteWhereException;

/**
 * Binary inbound event source that consumes messages from a RabbitMQ broker.
 * 
 * @author Derek
 */
public class RabbitMqInboundEventReceiver extends InboundEventReceiver<byte[]> {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Default connection URI */
    private static final String DEFAULT_CONNECTION_URI = "amqp://localhost";

    /** Default queue name */
    private static final String DEFAULT_QUEUE_NAME = "sitewhere.input";

    /** Default number of consumers if not specified */
    private static final int DEFAULT_NUM_CONSUMERS = 5;

    /** Connection URI */
    private String connectionUri = DEFAULT_CONNECTION_URI;

    /** Queue name */
    private String queueName = DEFAULT_QUEUE_NAME;

    /** Number of consumers to use */
    private int numConsumers = DEFAULT_NUM_CONSUMERS;

    /** Indicates if queue should be durable */
    private boolean durable = false;

    /** RabbitMQ connection */
    private Connection connection;

    /** RabbitMQ channel */
    private Channel channel;

    /** Used for consumer thread pool */
    private ExecutorService executors;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
     */
    @Override
    public void start() throws SiteWhereException {
	executors = Executors.newFixedThreadPool(getNumConsumers());
	try {
	    ConnectionFactory factory = new ConnectionFactory();
	    factory.setUri(getConnectionUri());
	    this.connection = factory.newConnection(executors);
	    this.channel = connection.createChannel();

	    LOGGER.info("RabbitMQ receiver connected to: " + getConnectionUri());

	    channel.queueDeclare(getQueueName(), isDurable(), false, false, null);

	    LOGGER.info("RabbitMQ receiver using " + (isDurable() ? "durable " : "") + "queue: " + getQueueName());

	    // Add consumer callback for channel.
	    Consumer consumer = new DefaultConsumer(channel) {

		@Override
		public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
			byte[] body) throws IOException {
		    EventProcessingLogic.processRawPayload(RabbitMqInboundEventReceiver.this, body, null);
		}
	    };
	    channel.basicConsume(getQueueName(), true, consumer);
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to start RabbitMQ event receiver.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
     */
    @Override
    public void stop() throws SiteWhereException {
	try {
	    if (channel != null) {
		channel.close();
	    }
	    if (connection != null) {
		connection.close();
	    }
	} catch (Exception e) {
	    throw new SiteWhereException("Error stopping RabbitMQ event receiver.", e);
	}
	executors.shutdownNow();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IInboundEventReceiver#
     * getDisplayName()
     */
    @Override
    public String getDisplayName() {
	return "RabbitMQ uri=" + getConnectionUri() + " queue=" + getQueueName();
    }

    public String getConnectionUri() {
	return connectionUri;
    }

    public void setConnectionUri(String connectionUri) {
	this.connectionUri = connectionUri;
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

    public boolean isDurable() {
	return durable;
    }

    public void setDurable(boolean durable) {
	this.durable = durable;
    }
}