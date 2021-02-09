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
package com.sitewhere.sources.activemq;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.sitewhere.sources.InboundEventReceiver;
import com.sitewhere.sources.configuration.eventsource.activemq.ActiveMqClientConfiguration;
import com.sitewhere.sources.spi.IInboundEventReceiver;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;

/**
 * Implementation of {@link IInboundEventReceiver} that creates multiple
 * ActiveMQ consumer threads to ingest remote data.
 */
public class ActiveMqClientEventReceiver extends InboundEventReceiver<byte[]> {

    /** Configuration */
    private ActiveMqClientConfiguration configuration;

    /** List of consumers reading messages */
    private List<Consumer> consumers = new ArrayList<Consumer>();

    /** Thread pool for consumer processing */
    private ExecutorService consumersPool;

    public ActiveMqClientEventReceiver(ActiveMqClientConfiguration configuration) {
	this.configuration = configuration;
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (getConfiguration().getRemoteUri() == null) {
	    throw new SiteWhereException("Remote URI must be configured.");
	}
	if (getConfiguration().getQueueName() == null) {
	    throw new SiteWhereException("Queue name must be configured.");
	}
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
	getConsumers().clear();
	consumersPool = Executors.newFixedThreadPool(getConfiguration().getNumConsumers(),
		new ConsumersThreadFactory());
	for (int i = 0; i < getConfiguration().getNumConsumers(); i++) {
	    Consumer consumer = new Consumer();
	    consumer.start();
	    getConsumersPool().execute(consumer);
	    getConsumers().add(consumer);
	}
	getLogger().info("Created " + consumers.size() + " consumers for processing ActiveMQ messages.");
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
	getConsumersPool().shutdownNow();
	for (Consumer consumer : getConsumers()) {
	    consumer.stop();
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IInboundEventReceiver#
     * getDisplayName()
     */
    @Override
    public String getDisplayName() {
	return getConfiguration().getRemoteUri();
    }

    /** Used for naming consumer threads */
    private class ConsumersThreadFactory implements ThreadFactory {

	/** Counts threads */
	private AtomicInteger counter = new AtomicInteger();

	public Thread newThread(Runnable r) {
	    return new Thread(r, "SiteWhere ActiveMQ(" + getConfiguration().getQueueName() + ") Consumer "
		    + counter.incrementAndGet());
	}
    }

    /**
     * Reads messages from the ActiveMQ queue and puts the binary content on a queue
     * for SiteWhere to use.
     */
    private class Consumer implements Runnable, ExceptionListener {

	/** Connection to remote broker */
	private Connection connection;

	/** JMS session */
	private Session session;

	/** Consumer for reading data */
	private MessageConsumer consumer;

	public void start() throws SiteWhereException {
	    try {
		// Create a connection to the broker.
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
			getConfiguration().getRemoteUri());
		this.connection = connectionFactory.createConnection();
		getConnection().setExceptionListener(this);
		getConnection().start();

		// Create a Session
		this.session = getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);

		Destination destination = getSession().createQueue(getConfiguration().getQueueName());
		this.consumer = getSession().createConsumer(destination);
	    } catch (Exception e) {
		throw new SiteWhereException("Error starting ActiveMQ consumer.", e);
	    }
	}

	public void stop() throws SiteWhereException {
	    try {
		getConsumer().close();
		getSession().close();
		getConnection().close();
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
		    if (message == null) {
			break;
		    }
		    if (message instanceof TextMessage) {
			TextMessage textMessage = (TextMessage) message;
			onEventPayloadReceived(textMessage.getText().getBytes(), null);
		    } else if (message instanceof BytesMessage) {
			BytesMessage bytesMessage = (BytesMessage) message;
			byte[] buffer = new byte[(int) bytesMessage.getBodyLength()];
			onEventPayloadReceived(buffer, null);
		    } else {
			getLogger().warn("Ignoring unknown JMS message type: " + message.getClass().getName());
		    }
		} catch (Throwable e) {
		    getLogger().error("Error in ActiveMQ message processing.", e);
		    return;
		}
	    }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.jms.ExceptionListener#onException(javax.jms.JMSException)
	 */
	@Override
	public void onException(JMSException e) {
	    try {
		stop();
	    } catch (SiteWhereException e1) {
	    }
	}

	protected Connection getConnection() {
	    return connection;
	}

	protected Session getSession() {
	    return session;
	}

	protected MessageConsumer getConsumer() {
	    return consumer;
	}
    }

    protected ActiveMqClientConfiguration getConfiguration() {
	return configuration;
    }

    protected List<Consumer> getConsumers() {
	return consumers;
    }

    protected ExecutorService getConsumersPool() {
	return consumersPool;
    }
}