/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.mqtt;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.fusesource.hawtdispatch.ShutdownException;
import org.fusesource.mqtt.client.Future;
import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import com.codahale.metrics.Meter;
import com.sitewhere.communication.mqtt.MqttLifecycleComponent;
import com.sitewhere.sources.messages.EventSourcesMessages;
import com.sitewhere.sources.spi.IInboundEventReceiver;
import com.sitewhere.sources.spi.IInboundEventSource;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link IInboundEventReceiver} that subscribes to an MQTT
 * topic and pulls the message contents into SiteWhere for processing.
 * 
 * @author Derek
 */
public class MqttInboundEventReceiver extends MqttLifecycleComponent implements IInboundEventReceiver<byte[]> {

    /** Default subscribed topic name */
    public static final String DEFAULT_TOPIC = "SiteWhere/input/protobuf";

    /** Number of threads used for processing events */
    public static final int DEFAULT_NUM_THREADS = 5;

    /** MQTT Topic Quality of Service */
    public static final QoS DEFAULT_QoS = QoS.AT_LEAST_ONCE;
    
    /** Parent event source */
    private IInboundEventSource<byte[]> eventSource;

    /** Topic name */
    private String topic = DEFAULT_TOPIC;

    /** Number of threads used for processing */
    private int numThreads = DEFAULT_NUM_THREADS;

    /** Shared MQTT connection */
    private FutureConnection connection;

    /** Used to execute MQTT subscribe in separate thread */
    private ExecutorService subscriptionExecutor;

    /** Used to process MQTT events in a thread pool */
    private ExecutorService processorsExecutor;

    /** Meter for counting received events */
    private Meter receivedEvents;

    public MqttInboundEventReceiver() {
	super(LifecycleComponentType.InboundEventReceiver);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.device.communication.mqtt.MqttLifecycleComponent#start(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);

	this.subscriptionExecutor = Executors.newSingleThreadExecutor(new SubscribersThreadFactory());
	this.processorsExecutor = Executors.newFixedThreadPool(getNumThreads(), new ProcessorsThreadFactory());

	// Set up metrics.
	this.receivedEvents = createMeterMetric(getMetricPrefix() + "receivedEvents");

	getLogger().info("Receiver connecting to MQTT broker at '" + getBrokerInfo() + "'...");
	connection = getConnection();
	getLogger().info("Receiver connected to MQTT broker.");

	getLogger().info("Suscribing using QoS: " + getQos());
	QoS qos = qosFromConfig(getQos());
	
	// Subscribe to chosen topic.
	Topic[] topics = { new Topic(getTopic(), qos) };
	try {
	    Future<byte[]> future = connection.subscribe(topics);
	    future.await();

	    getLogger().info(EventSourcesMessages.SUBSCRIBED_TO_EVENTS_MQTT, getTopic(), getNumThreads());
	} catch (Exception e) {
	    throw new SiteWhereException("Exception while attempting to subscribe to MQTT topic: " + getTopic(), e);
	}

	// Handle message processing in separate thread.
	subscriptionExecutor.execute(new MqttSubscriptionProcessor());
    }

    /**
     * Transform configuration to MQTT QoS
     * @param qos
     * @return
     */
    private static QoS qosFromConfig(String qos) {
	if ("0".equals(qos) || "AT_MOST_ONCE".equals(qos))
	    return QoS.AT_MOST_ONCE;
	if ("1".equals(qos) || "AT_LEAST_ONCE".equals(qos))
	    return QoS.AT_LEAST_ONCE;
	if ("2".equals(qos) || "EXACTLY_ONCE".equals(qos))
	    return QoS.EXACTLY_ONCE;
	return DEFAULT_QoS;
    }

    /**
     * Get prefix appended to metrics.
     * 
     * @return
     */
    protected String getMetricPrefix() {
	return getEventSource().getSourceId() + ".MqttEventReceiver.";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IInboundEventReceiver#
     * getDisplayName()
     */
    @Override
    public String getDisplayName() {
	return getProtocol() + "://" + getHostname() + ":" + getPort() + "/" + getTopic();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IInboundEventReceiver#
     * onEventPayloadReceived (java.lang.Object, java.util.Map)
     */
    @Override
    public void onEventPayloadReceived(byte[] payload, Map<String, Object> metadata) {
    }

    /**
     * Pulls messages from the MQTT topic and puts them on the queue for this
     * receiver.
     * 
     * @author Derek
     */
    private class MqttSubscriptionProcessor implements Runnable {

	@Override
	public void run() {
	    getLogger().info("Started MQTT subscription processing thread.");
	    while (true) {
		try {
		    Future<Message> future = connection.receive();
		    Message message = future.await();
		    processorsExecutor.execute(new MqttPayloadProcessor(message));
		} catch (Throwable e) {
		    getLogger().error("Error in MQTT subscription processing.", e);
		}
	    }
	}
    }

    /**
     * Processes MQTT message payloads in a separate thread.
     * 
     * @author Derek
     */
    private class MqttPayloadProcessor implements Runnable {

	/** MQTT message */
	private Message message;

	public MqttPayloadProcessor(Message message) {
	    this.message = message;
	}

	@Override
	public void run() {
	    try {
		getReceivedEvents().mark();
		byte[] payload = message.getPayload();
		getEventSource().onEncodedEventReceived(MqttInboundEventReceiver.this, payload, null);
		message.ack();
	    } catch (Throwable e) {
		getLogger().error("Error in MQTT processing.", e);
	    }
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.device.communication.mqtt.MqttLifecycleComponent#stop(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (processorsExecutor != null) {
	    processorsExecutor.shutdownNow();
	}
	if (subscriptionExecutor != null) {
	    subscriptionExecutor.shutdownNow();
	}
	if (connection != null) {
	    try {
		connection.disconnect().await();
		connection.kill().await();
	    } catch (ShutdownException e) {
		getLogger().info("Dispatcher has already been shut down.");
	    } catch (Exception e) {
		getLogger().error("Error shutting down MQTT device event receiver.", e);
	    }
	}
	super.stop(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IInboundEventReceiver#
     * getEventSource()
     */
    public IInboundEventSource<byte[]> getEventSource() {
	return eventSource;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IInboundEventReceiver#
     * setEventSource(com .sitewhere.spi.device.communication.IInboundEventSource)
     */
    public void setEventSource(IInboundEventSource<byte[]> eventSource) {
	this.eventSource = eventSource;
    }

    public String getTopic() {
	return topic;
    }

    public void setTopic(String topic) {
	this.topic = topic;
    }

    public int getNumThreads() {
	return numThreads;
    }

    public void setNumThreads(int numThreads) {
	this.numThreads = numThreads;
    }

    protected Meter getReceivedEvents() {
	return receivedEvents;
    }

    protected void setReceivedEvents(Meter receivedEvents) {
	this.receivedEvents = receivedEvents;
    }

    /** Used for naming consumer threads */
    private class SubscribersThreadFactory implements ThreadFactory {

	/** Counts threads */
	private AtomicInteger counter = new AtomicInteger();

	public Thread newThread(Runnable r) {
	    return new Thread(r, "SiteWhere MQTT(" + getEventSource().getSourceId() + " - " + getTopic() + ") Receiver "
		    + counter.incrementAndGet());
	}
    }

    /** Used for naming processor threads */
    private class ProcessorsThreadFactory implements ThreadFactory {

	/** Counts threads */
	private AtomicInteger counter = new AtomicInteger();

	public Thread newThread(Runnable r) {
	    return new Thread(r, "SiteWhere MQTT(" + getEventSource().getSourceId() + " - " + getTopic()
		    + ") Processor " + counter.incrementAndGet());
	}
    }
}