/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication.mqtt;

import java.io.File;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.log4j.Logger;
import org.fusesource.hawtdispatch.internal.DispatcherConfig;
import org.fusesource.mqtt.client.Future;
import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.IInboundEventReceiver;
import com.sitewhere.spi.device.communication.IInboundEventSource;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link IInboundEventReceiver} that subscribes to an MQTT topic and
 * pulls the message contents into SiteWhere for processing.
 * 
 * @author Derek
 */
public class MqttInboundEventReceiver extends LifecycleComponent implements IInboundEventReceiver<byte[]> {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(MqttInboundEventReceiver.class);

	/** Default protocol if not set via Spring */
	public static final String DEFAULT_PROTOCOL = "tcp";

	/** Default hostname if not set via Spring */
	public static final String DEFAULT_HOSTNAME = "localhost";

	/** Default port if not set from Spring */
	public static final int DEFAULT_PORT = 1883;

	/** Default connection timeout in seconds */
	public static final long DEFAULT_CONNECT_TIMEOUT_SECS = 5;

	/** Default subscribed topic name */
	public static final String DEFAULT_TOPIC = "SiteWhere/input/protobuf";

	/** Parent event source */
	private IInboundEventSource<byte[]> eventSource;

	private String protocol = DEFAULT_PROTOCOL;

	/** Host name */
	private String hostname = DEFAULT_HOSTNAME;

	/** Port */
	private int port = DEFAULT_PORT;

	/** Topic name */
	private String topic = DEFAULT_TOPIC;

	/** TrustStore path */
	private String trustStorePath;

	/** TrustStore password */
	private String trustStorePassword;

	/** MQTT client */
	private MQTT mqtt;

	/** Shared MQTT connection */
	private FutureConnection connection;

	/** Used to execute MQTT subscribe in separate thread */
	private ExecutorService executor;

	public MqttInboundEventReceiver() {
		super(LifecycleComponentType.InboundEventReceiver);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		this.executor = Executors.newSingleThreadExecutor(new SubscribersThreadFactory());
		this.mqtt = new MQTT();
		if ((getProtocol().startsWith("ssl")) || (getProtocol().startsWith("tls"))) {
			if ((getTrustStorePath() != null) && (getTrustStorePassword() != null)) {
				try {
					SSLContext sslContext = SSLContext.getInstance("TLS");
					TrustManagerFactory tmf =
							TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
					KeyStore ks = KeyStore.getInstance("JKS");
					File trustFile = new File(getTrustStorePath());
					ks.load(new FileInputStream(trustFile), getTrustStorePassword().toCharArray());
					tmf.init(ks);
					sslContext.init(null, tmf.getTrustManagers(), null);
					mqtt.setSslContext(sslContext);
					LOGGER.info("Created SSL context for MQTT receiver.");
				} catch (Exception e) {
					throw new SiteWhereException("Unable to load SSL context.", e);
				}
			}
		}
		try {
			mqtt.setHost(getProtocol() + "://" + getHostname() + ":" + getPort());
		} catch (URISyntaxException e) {
			throw new SiteWhereException("Invalid hostname for MQTT server.", e);
		}
		LOGGER.info("Receiver connecting to MQTT broker at '" + mqtt.getHost() + "'...");
		connection = mqtt.futureConnection();
		try {
			Future<Void> future = connection.connect();
			future.await(DEFAULT_CONNECT_TIMEOUT_SECS, TimeUnit.SECONDS);
		} catch (Exception e) {
			throw new SiteWhereException("Unable to connect to MQTT broker.", e);
		}
		LOGGER.info("Receiver connected to MQTT broker.");

		// Subscribe to chosen topic.
		Topic[] topics = { new Topic(getTopic(), QoS.AT_LEAST_ONCE) };
		try {
			Future<byte[]> future = connection.subscribe(topics);
			future.await();

			LOGGER.info("Subscribed to events on MQTT topic: " + getTopic());
		} catch (Exception e) {
			throw new SiteWhereException("Exception while attempting to subscribe to MQTT topic: "
					+ getTopic(), e);
		}

		// Handle message processing in separate thread.
		executor.execute(new MqttSubscriptionProcessor());
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
	 * @see com.sitewhere.spi.device.communication.IInboundEventReceiver#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return getProtocol() + "://" + getHostname() + ":" + getPort() + "/" + getTopic();
	}

	/** Used for naming consumer threads */
	private class SubscribersThreadFactory implements ThreadFactory {

		/** Counts threads */
		private AtomicInteger counter = new AtomicInteger();

		public Thread newThread(Runnable r) {
			return new Thread(r, "SiteWhere MQTT(" + getEventSource().getSourceId() + " - " + getTopic()
					+ ") Receiver " + counter.incrementAndGet());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.communication.IInboundEventReceiver#onEventPayloadReceived
	 * (java.lang.Object)
	 */
	@Override
	public void onEventPayloadReceived(byte[] payload) {
		getEventSource().onEncodedEventReceived(MqttInboundEventReceiver.this, payload);
	}

	/**
	 * Pulls messages from the MQTT topic and puts them on the queue for this receiver.
	 * 
	 * @author Derek
	 */
	private class MqttSubscriptionProcessor implements Runnable {

		@Override
		public void run() {
			LOGGER.info("Started MQTT subscription processing thread.");
			while (true) {
				try {
					Future<Message> future = connection.receive();
					Message message = future.await();
					message.ack();
					onEventPayloadReceived(message.getPayload());
				} catch (InterruptedException e) {
					break;
				} catch (Throwable e) {
					LOGGER.error(e);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		if (executor != null) {
			executor.shutdownNow();
		}
		if (connection != null) {
			try {
				connection.disconnect();
				connection.kill();
			} catch (Exception e) {
				LOGGER.error("Error shutting down MQTT device event receiver.", e);
			}
		}
		DispatcherConfig.getDefaultDispatcher().shutdown();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.communication.IInboundEventReceiver#getEventSource()
	 */
	public IInboundEventSource<byte[]> getEventSource() {
		return eventSource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.communication.IInboundEventReceiver#setEventSource(com
	 * .sitewhere.spi.device.communication.IInboundEventSource)
	 */
	public void setEventSource(IInboundEventSource<byte[]> eventSource) {
		this.eventSource = eventSource;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getTrustStorePath() {
		return trustStorePath;
	}

	public void setTrustStorePath(String trustStorePath) {
		this.trustStorePath = trustStorePath;
	}

	public String getTrustStorePassword() {
		return trustStorePassword;
	}

	public void setTrustStorePassword(String trustStorePassword) {
		this.trustStorePassword = trustStorePassword;
	}
}