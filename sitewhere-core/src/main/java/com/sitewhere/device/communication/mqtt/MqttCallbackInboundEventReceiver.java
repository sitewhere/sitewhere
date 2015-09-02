/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication.mqtt;

import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.Listener;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.IInboundEventReceiver;
import com.sitewhere.spi.device.communication.IInboundEventSource;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link IInboundEventReceiver} that uses the Fuse MQTT callback APIs.
 * This implementation is not working yet.
 * 
 * @author Derek
 */
public class MqttCallbackInboundEventReceiver extends LifecycleComponent implements
		IInboundEventReceiver<byte[]> {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(MqttInboundEventReceiver.class);

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

	/** Host name */
	private String hostname = DEFAULT_HOSTNAME;

	/** Port */
	private int port = DEFAULT_PORT;

	/** Topic name */
	private String topic = DEFAULT_TOPIC;

	/** MQTT client */
	private MQTT mqtt;

	/** Shared MQTT connection */
	private CallbackConnection connection;

	public MqttCallbackInboundEventReceiver() {
		super(LifecycleComponentType.InboundEventReceiver);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		try {
			this.mqtt = new MQTT();
			mqtt.setHost(getHostname(), getPort());
		} catch (URISyntaxException e) {
			throw new SiteWhereException("Invalid hostname for MQTT server.", e);
		}
		LOGGER.info("Receiver connecting to MQTT broker at '" + getHostname() + ":" + getPort() + "'...");
		connection = mqtt.callbackConnection();
		createListener();
		connection.connect(new Callback<Void>() {

			@Override
			public void onFailure(Throwable e) {
				LOGGER.error("MQTT connection failed.", e);
			}

			@Override
			public void onSuccess(Void e) {
				Topic[] topics = { new Topic(getTopic(), QoS.AT_LEAST_ONCE) };
				connection.subscribe(topics, new Callback<byte[]>() {

					@Override
					public void onFailure(Throwable e) {
						LOGGER.error("MQTT subscribe failed.", e);
					}

					@Override
					public void onSuccess(byte[] arg0) {
						LOGGER.info("Subscribed to events on MQTT topic: " + getTopic());
					}
				});
			}
		});
	}

	/**
	 * Create a listener for responding to connection events.
	 */
	protected void createListener() {
		connection.listener(new Listener() {

			public void onDisconnected() {
				LOGGER.info("MQTT connection disconnected.");
			}

			public void onConnected() {
				LOGGER.info("MQTT connection established.");
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.fusesource.mqtt.client.Listener#onPublish(org.fusesource.hawtbuf
			 * .UTF8Buffer, org.fusesource.hawtbuf.Buffer, java.lang.Runnable)
			 */
			public void onPublish(UTF8Buffer topic, Buffer payload, Runnable ack) {
				ack.run();
				onEventPayloadReceived(payload.data);
			}

			public void onFailure(Throwable value) {
				LOGGER.info("MQTT connection died.");
				connection.disconnect(new Callback<Void>() {

					@Override
					public void onFailure(Throwable arg0) {
					}

					@Override
					public void onSuccess(Void arg0) {
					}
				});
			}
		});
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
		return getHostname() + ":" + getPort() + "/" + getTopic();
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
		getEventSource().onEncodedEventReceived(MqttCallbackInboundEventReceiver.this, payload);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		if (connection != null) {
			try {
				connection.disconnect(new Callback<Void>() {

					@Override
					public void onFailure(Throwable e) {
						LOGGER.info("MQTT disconnect failed.");
					}

					@Override
					public void onSuccess(Void arg0) {
					}
				});
				connection.transport().stop(new Runnable() {

					@Override
					public void run() {
						LOGGER.info("MQTT connection transport stopped.");
					}
				});
			} catch (Exception e) {
				LOGGER.error("Error shutting down MQTT device event receiver.", e);
			}
		}
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
}