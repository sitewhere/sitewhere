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
package com.sitewhere.sources.rabbitmq;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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
import com.sitewhere.sources.configuration.eventsource.rabbitmq.RabbitMqConfiguration;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;

/**
 * Binary inbound event source that consumes messages from a RabbitMQ broker.
 */
public class RabbitMqInboundEventReceiver extends InboundEventReceiver<byte[]> {

    /** Configuration */
    private RabbitMqConfiguration configuration;

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

    public RabbitMqInboundEventReceiver(RabbitMqConfiguration configuration) {
	this.configuration = configuration;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	executors = Executors.newFixedThreadPool(getConfiguration().getNumConsumers());
	connectionExecutor = Executors.newScheduledThreadPool(1);
	factory = new ConnectionFactory();

	try {
	    factory.setUri(getConfiguration().getConnectionUri());
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

	getLogger().info("Scheduling reconnect");

	Runnable task = () -> connect();
	connectionFuture = connectionExecutor.schedule(task, getConfiguration().getReconnectInterval(),
		TimeUnit.SECONDS);

    }

    /*
     * Connect to RabbitMQ
     */
    private void connect() {

	try {

	    this.connection = factory.newConnection(executors);

	    connection.addShutdownListener(new ShutdownListener() {
		public void shutdownCompleted(ShutdownSignalException cause) {
		    getLogger().info("shutdown signal received", cause);

		    // Do nothing if SiteWhere initiated the connection close
		    if (!cause.isInitiatedByApplication()) {
			connection = null;
			scheduleReconnect();
		    }
		}
	    });

	    this.channel = connection.createChannel();

	    getLogger().info("RabbitMQ receiver connected to: " + getConfiguration().getConnectionUri());

	    channel.queueDeclare(getConfiguration().getQueueName(), getConfiguration().isDurable(), false, false, null);

	    getLogger().info("RabbitMQ receiver using " + (getConfiguration().isDurable() ? "durable " : "") + "queue: "
		    + getConfiguration().getQueueName());

	    // Add consumer callback for channel.
	    Consumer consumer = new DefaultConsumer(channel) {
		@Override
		public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
			byte[] body) throws IOException {
		    onEventPayloadReceived(body, null);
		}
	    };

	    channel.basicConsume(getConfiguration().getQueueName(), true, consumer);

	} catch (Exception e) {
	    getLogger().error("Connection Error", e);
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
     * @see com.sitewhere.spi.device.communication.IInboundEventReceiver#
     * getDisplayName()
     */
    @Override
    public String getDisplayName() {
	return "RabbitMQ uri=" + getConfiguration().getConnectionUri() + " queue=" + getConfiguration().getQueueName();
    }

    protected RabbitMqConfiguration getConfiguration() {
	return configuration;
    }
}