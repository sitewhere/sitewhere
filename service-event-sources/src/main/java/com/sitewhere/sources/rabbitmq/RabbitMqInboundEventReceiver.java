/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.rabbitmq;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;
import com.sitewhere.sources.InboundEventReceiver;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Binary inbound event source that consumes messages from a RabbitMQ broker.
 * 
 * @author Derek
 */
public class RabbitMqInboundEventReceiver extends InboundEventReceiver<byte[]> {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(RabbitMqInboundEventReceiver.class);

    /** Default connection URI */
    private static final String DEFAULT_CONNECTION_URI = "amqp://localhost";

    /** Default queue name */
    private static final String DEFAULT_QUEUE_NAME = "sitewhere.input";

    /** Default number of consumers if not specified */
    private static final int DEFAULT_NUM_CONSUMERS = 5;

    /** Default period in which to attempt connects/re-connects to RabbitMQ */
    private static final int DEFAULT_RECONNECT_INTERVAL = 10;

    /** Connection URI */
    private String connectionUri = DEFAULT_CONNECTION_URI;

    /** Queue name */
    private String queueName = DEFAULT_QUEUE_NAME;

    /** Number of consumers to use */
    private int numConsumers = DEFAULT_NUM_CONSUMERS;

    /** Reconnect interval */
    private int reconnectInterval = DEFAULT_RECONNECT_INTERVAL;

    /** Indicates if queue should be durable */
    private boolean durable = false;

    /** RabbitMQ connection factory **/
    private ConnectionFactory factory;

    /** RabbitMQ connection */
    private Connection connection;

    /** RabbitMQ channel */
    private Channel channel;

    /** Used for consumer thread pool */
    private ExecutorService executors;

    /** Holds reference to scheduled reconnection task */
    private ScheduledFuture<?> connectionFuture;

    /** Shedules reconnection attempts */
    private ScheduledExecutorService connectionExecutor;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	executors = Executors.newFixedThreadPool(getNumConsumers());
	connectionExecutor = Executors.newScheduledThreadPool(1);
	factory = new ConnectionFactory();

	try {
	    factory.setUri(getConnectionUri());
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to start RabbitMQ event receiver.", e);
	}

	connect();
    }

    /*
     * Schedules a reconnect attempt after network interruption
     */
    private void scheduleReconnect() {

	if (connectionFuture != null) {
	    connectionFuture.cancel(true);
	}

	LOGGER.info("Scheduling reconnect");

	Runnable task = () -> connect();
	connectionFuture = connectionExecutor.schedule(task, this.getReconnectInterval(), TimeUnit.SECONDS);

    }

    /*
     * Connect to RabbitMQ
     */
    private void connect() {

	try {

	    this.connection = factory.newConnection(executors);

	    connection.addShutdownListener(new ShutdownListener() {
		public void shutdownCompleted(ShutdownSignalException cause) {
		    LOGGER.info("shutdown signal received", cause);

		    // Do nothing if SiteWhere initiated the connection close
		    if (!cause.isInitiatedByApplication()) {
			connection = null;
			scheduleReconnect();
		    }
		}
	    });

	    this.channel = connection.createChannel();

	    LOGGER.info("RabbitMQ receiver connected to: " + getConnectionUri());

	    channel.queueDeclare(getQueueName(), isDurable(), false, false, null);

	    LOGGER.info("RabbitMQ receiver using " + (isDurable() ? "durable " : "") + "queue: " + getQueueName());

	    // Add consumer callback for channel.
	    Consumer consumer = new DefaultConsumer(channel) {
		@Override
		public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
			byte[] body) throws IOException {
		    onEventPayloadReceived(body, null);
		}
	    };

	    channel.basicConsume(getQueueName(), true, consumer);

	} catch (Exception e) {
	    LOGGER.error("Connection Error", e);
	    connection = null;
	    scheduleReconnect();
	}

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {

	// cancel any reconnection tasks that may be in progress
	if (connectionFuture != null && !connectionFuture.isDone()) {
	    connectionFuture.cancel(true);
	}

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

	connectionExecutor.shutdownNow();
	executors.shutdownNow();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Log getLogger() {
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

    public int getReconnectInterval() {
	return this.reconnectInterval;
    }

    public void setReconnectInterval(int reconnectInterval) {
	this.reconnectInterval = reconnectInterval;
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