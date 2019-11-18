/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.mqtt;

import java.net.URISyntaxException;

import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.Listener;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import com.sitewhere.sources.InboundEventReceiver;
import com.sitewhere.sources.spi.IInboundEventReceiver;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;

/**
 * Implementation of {@link IInboundEventReceiver} that uses the Fuse MQTT
 * callback APIs. This implementation is not working yet.
 */
@SuppressWarnings("deprecation")
public class MqttCallbackInboundEventReceiver extends InboundEventReceiver<byte[]> {

    /** Default hostname if not set via Spring */
    public static final String DEFAULT_HOSTNAME = "localhost";

    /** Default port if not set from Spring */
    public static final int DEFAULT_PORT = 1883;

    /** Default connection timeout in seconds */
    public static final long DEFAULT_CONNECT_TIMEOUT_SECS = 5;

    /** Default subscribed topic name */
    public static final String DEFAULT_TOPIC = "SiteWhere/input/protobuf";

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

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	try {
	    this.mqtt = new MQTT();
	    mqtt.setHost(getHostname(), getPort());
	} catch (URISyntaxException e) {
	    throw new SiteWhereException("Invalid hostname for MQTT server.", e);
	}
	getLogger().info("Receiver connecting to MQTT broker at '" + getHostname() + ":" + getPort() + "'...");
	connection = mqtt.callbackConnection();
	createListener();
	connection.connect(new Callback<Void>() {

	    @Override
	    public void onFailure(Throwable e) {
		getLogger().error("MQTT connection failed.", e);
	    }

	    @Override
	    public void onSuccess(Void e) {
		Topic[] topics = { new Topic(getTopic(), QoS.AT_LEAST_ONCE) };
		connection.subscribe(topics, new Callback<byte[]>() {

		    @Override
		    public void onFailure(Throwable e) {
			getLogger().error("MQTT subscribe failed.", e);
		    }

		    @Override
		    public void onSuccess(byte[] arg0) {
			getLogger().info("Subscribed to events on MQTT topic: " + getTopic());
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
		getLogger().info("MQTT connection disconnected.");
	    }

	    public void onConnected() {
		getLogger().info("MQTT connection established.");
	    }

	    /*
	     * (non-Javadoc)
	     * 
	     * @see org.fusesource.mqtt.client.Listener#onPublish(org.fusesource. hawtbuf
	     * .UTF8Buffer, org.fusesource.hawtbuf.Buffer, java.lang.Runnable)
	     */
	    public void onPublish(UTF8Buffer topic, Buffer payload, Runnable ack) {
		ack.run();
		onEventPayloadReceived(payload.data, null);
	    }

	    public void onFailure(Throwable value) {
		getLogger().info("MQTT connection died.");
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
     * @see com.sitewhere.spi.device.communication.IInboundEventReceiver#
     * getDisplayName()
     */
    @Override
    public String getDisplayName() {
	return getHostname() + ":" + getPort() + "/" + getTopic();
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
	if (connection != null) {
	    try {
		connection.disconnect(new Callback<Void>() {

		    @Override
		    public void onFailure(Throwable e) {
			getLogger().info("MQTT disconnect failed.");
		    }

		    @Override
		    public void onSuccess(Void arg0) {
		    }
		});
		connection.transport().stop(new Runnable() {

		    @Override
		    public void run() {
			getLogger().info("MQTT connection transport stopped.");
		    }
		});
	    } catch (Exception e) {
		getLogger().error("Error shutting down MQTT device event receiver.", e);
	    }
	}
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