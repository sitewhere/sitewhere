/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.activemq;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.device.communication.EventProcessingLogic;
import com.sitewhere.device.communication.InboundEventReceiver;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.IInboundEventReceiver;

/**
 * Implementation of {@link IInboundEventReceiver} that creates multiple
 * ActiveMQ consumer threads to ingest remote data.
 * 
 * @author Derek
 */
public class ActiveMQClientEventReceiver extends InboundEventReceiver<byte[]> {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Number of consumers reading messages from the queue */
    private static final int DEFAULT_NUM_CONSUMERS = 3;

    /** Number of consumers used to read messages from the queue */
    private int numConsumers = DEFAULT_NUM_CONSUMERS;

    /** List of consumers reading messages */
    private List<Consumer> consumers = new ArrayList<Consumer>();

    /** Thread pool for consumer processing */
    private ExecutorService consumersPool;

    /** Remote URI used for connection */
    private String remoteUri;

    /** Queue name used for inbound event data */
    private String queueName;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
     */
    @Override
    public void start() throws SiteWhereException {
	if (getRemoteUri() == null) {
	    throw new SiteWhereException("Remote URI is required.");
	}
	if (getQueueName() == null) {
	    throw new SiteWhereException("Queue name is required.");
	}
	startConsumers();
    }

    /**
     * Starts consumers for reading messages into SiteWhere.
     * 
     * @throws SiteWhereException
     */
    protected void startConsumers() throws SiteWhereException {
	consumers.clear();
	consumersPool = Executors.newFixedThreadPool(getNumConsumers(), new ConsumersThreadFactory());
	for (int i = 0; i < getNumConsumers(); i++) {
	    Consumer consumer = new Consumer();
	    consumer.start();
	    consumersPool.execute(consumer);
	    consumers.add(consumer);
	}
	LOGGER.info("Created " + consumers.size() + " consumers for processing ActiveMQ messages.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
     */
    @Override
    public void stop() throws SiteWhereException {
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
	return getRemoteUri();
    }

    /** Used for naming consumer threads */
    private class ConsumersThreadFactory implements ThreadFactory {

	/** Counts threads */
	private AtomicInteger counter = new AtomicInteger();

	public Thread newThread(Runnable r) {
	    return new Thread(r, "SiteWhere ActiveMQ(" + getQueueName() + ") Consumer " + counter.incrementAndGet());
	}
    }

    /**
     * Reads messages from the ActiveMQ queue and puts the binary content on a
     * queue for SiteWhere to use.
     * 
     * @author Derek
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
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(getRemoteUri());
		this.connection = connectionFactory.createConnection();
		connection.setExceptionListener(this);
		connection.start();

		// Create a Session
		this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		Destination destination = session.createQueue(getQueueName());
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
		    if (message == null) {
			break;
		    }
		    if (message instanceof TextMessage) {
			TextMessage textMessage = (TextMessage) message;
			EventProcessingLogic.processRawPayload(ActiveMQClientEventReceiver.this,
				textMessage.getText().getBytes(), null);
		    } else if (message instanceof BytesMessage) {
			BytesMessage bytesMessage = (BytesMessage) message;
			byte[] buffer = new byte[(int) bytesMessage.getBodyLength()];
			EventProcessingLogic.processRawPayload(ActiveMQClientEventReceiver.this, buffer, null);
		    } else {
			LOGGER.warn("Ignoring unknown JMS message type: " + message.getClass().getName());
		    }
		} catch (Throwable e) {
		    LOGGER.error("Error in ActiveMQ message processing.", e);
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
    }

    public String getRemoteUri() {
	return remoteUri;
    }

    public void setRemoteUri(String remoteUri) {
	this.remoteUri = remoteUri;
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