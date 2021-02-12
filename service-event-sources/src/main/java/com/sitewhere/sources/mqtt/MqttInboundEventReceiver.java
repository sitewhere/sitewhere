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
package com.sitewhere.sources.mqtt;

import java.io.EOFException;
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

import com.sitewhere.communication.mqtt.MqttLifecycleComponent;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.sources.configuration.eventsource.mqtt.MqttConfiguration;
import com.sitewhere.sources.messages.EventSourcesMessages;
import com.sitewhere.sources.spi.IInboundEventReceiver;
import com.sitewhere.sources.spi.IInboundEventSource;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;

import io.prometheus.client.Counter;

/**
 * Implementation of {@link IInboundEventReceiver} that subscribes to an MQTT
 * topic and pulls the message contents into SiteWhere for processing.
 */
public class MqttInboundEventReceiver extends MqttLifecycleComponent implements IInboundEventReceiver<byte[]> {

    /** Meter for counting received events */
    private static final Counter RECEIVED_EVENTS = TenantEngineLifecycleComponent
	    .createCounterMetric("mqtt_events_received_count", "Count of MQTT events received", "source_id");

    /** Parent event source */
    private IInboundEventSource<byte[]> eventSource;

    /** Configuration */
    private MqttConfiguration configuration;

    /** Shared MQTT connection */
    private FutureConnection connection;

    /** Used to execute MQTT subscribe in separate thread */
    private ExecutorService subscriptionExecutor;

    /** Used to process MQTT events in a thread pool */
    private ExecutorService processorsExecutor;

    public MqttInboundEventReceiver(MqttConfiguration configuration) {
	super(LifecycleComponentType.InboundEventReceiver, configuration);
	this.configuration = configuration;
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
	this.processorsExecutor = Executors.newFixedThreadPool(getConfiguration().getNumThreads(),
		new ProcessorsThreadFactory());

	getLogger().info("Receiver connecting to MQTT broker at '" + getBrokerInfo() + "'...");
	connection = getConnection();
	getLogger().info("Receiver connected to MQTT broker.");

	getLogger().info("Suscribing using QoS: " + getConfiguration().getQos());
	QoS qos = qosFromConfig(getConfiguration().getQos());

	// Subscribe to chosen topic.
	Topic[] topics = { new Topic(getConfiguration().getTopic(), qos) };
	try {
	    Future<byte[]> future = connection.subscribe(topics);
	    future.await();

	    getLogger().info(EventSourcesMessages.SUBSCRIBED_TO_EVENTS_MQTT, getConfiguration().getTopic(),
		    getConfiguration().getNumThreads());
	} catch (Exception e) {
	    throw new SiteWhereException(
		    "Exception while attempting to subscribe to MQTT topic: " + getConfiguration().getTopic(), e);
	}

	// Handle message processing in separate thread.
	subscriptionExecutor.execute(new MqttSubscriptionProcessor());
    }

    /**
     * Transform configuration to MQTT QoS
     * 
     * @param qos
     * @return
     */
    private static QoS qosFromConfig(int qos) {
	if (qos == 0) {
	    return QoS.AT_MOST_ONCE;
	} else if (qos == 1) {
	    return QoS.AT_LEAST_ONCE;
	} else if (qos == 2) {
	    return QoS.EXACTLY_ONCE;
	}
	return QoS.AT_LEAST_ONCE;
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
	return getConfiguration().getProtocol() + "://" + getConfiguration().getHostname() + ":"
		+ getConfiguration().getPort() + "/" + getConfiguration().getTopic();
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
		} catch (EOFException e) {
		    getLogger().error("Connection terminated by remote. Subscription processor terminating.", e);
		    return;
		} catch (InterruptedException e) {
		    getLogger().info("Subcription processor shutdown requested.");
		    return;
		} catch (Throwable e) {
		    getLogger().error("Error in MQTT subscription processing.", e);
		}
	    }
	}
    }

    /**
     * Processes MQTT message payloads in a separate thread.
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
		RECEIVED_EVENTS.labels(buildLabels(getEventSource().getSourceId())).inc();
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

    protected MqttConfiguration getConfiguration() {
	return configuration;
    }

    /** Used for naming consumer threads */
    private class SubscribersThreadFactory implements ThreadFactory {

	/** Counts threads */
	private AtomicInteger counter = new AtomicInteger();

	public Thread newThread(Runnable r) {
	    return new Thread(r, "SiteWhere MQTT(" + getEventSource().getSourceId() + " - "
		    + getConfiguration().getTopic() + ") Receiver " + counter.incrementAndGet());
	}
    }

    /** Used for naming processor threads */
    private class ProcessorsThreadFactory implements ThreadFactory {

	/** Counts threads */
	private AtomicInteger counter = new AtomicInteger();

	public Thread newThread(Runnable r) {
	    return new Thread(r, "SiteWhere MQTT(" + getEventSource().getSourceId() + " - "
		    + getConfiguration().getTopic() + ") Processor " + counter.incrementAndGet());
	}
    }
}