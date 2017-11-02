/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication.mqtt;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.sitewhere.device.communication.EventProcessingLogic;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.EventDecodeException;
import com.sitewhere.spi.device.communication.IInboundEventReceiver;
import com.sitewhere.spi.device.communication.IInboundEventSource;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link IInboundEventReceiver} that subscribes to an MQTT
 * topic and pulls the message contents into SiteWhere for processing.
 * 
 * @author Derek
 */
public class MqttInboundEventReceiver extends MqttLifecycleComponent implements IInboundEventReceiver<byte[]> {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Default subscribed topic name */
    public static final String DEFAULT_TOPIC = "SiteWhere/input/protobuf";

    /** Parent event source */
    private IInboundEventSource<byte[]> eventSource;

    /** Topic name */
    private String topic = DEFAULT_TOPIC;

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
	LOGGER.info("Receiver connected to MQTT broker at '" + getHostname() + ":" + getPort() + "'...");

	// Subscribe to chosen topic.
	try {
	    getMqttClient().setCallback(new MqttCallback() {

		/*
		 * @see
		 * org.eclipse.paho.client.mqttv3.MqttCallback#messageArrived(
		 * java.lang.String, org.eclipse.paho.client.mqttv3.MqttMessage)
		 */
		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception {
		    EventProcessingLogic.processRawPayload(MqttInboundEventReceiver.this, message.getPayload(), null);
		}

		/*
		 * @see
		 * org.eclipse.paho.client.mqttv3.MqttCallback#deliveryComplete(
		 * org.eclipse.paho.client.mqttv3.IMqttDeliveryToken)
		 */
		@Override
		public void deliveryComplete(IMqttDeliveryToken token) {
		}

		/*
		 * @see
		 * org.eclipse.paho.client.mqttv3.MqttCallback#connectionLost(
		 * java.lang.Throwable)
		 */
		@Override
		public void connectionLost(Throwable cause) {
		    LOGGER.info("Detected lost connection.", cause);
		}
	    });
	    getMqttClient().subscribe(getTopic(), getQos());
	    LOGGER.info("Subscribed to events on MQTT topic: " + getTopic() + " with QOS " + getQos());
	} catch (MqttException e) {
	    throw new SiteWhereException("Exception while attempting to subscribe to MQTT topic: " + getTopic(), e);
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
	return getProtocol() + "://" + getHostname() + ":" + getPort() + "/" + getTopic();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IInboundEventReceiver#
     * onEventPayloadReceived (java.lang.Object, java.util.Map)
     */
    @Override
    public void onEventPayloadReceived(byte[] payload, Map<String, Object> metadata) throws EventDecodeException {
	getEventSource().onEncodedEventReceived(MqttInboundEventReceiver.this, payload, metadata);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.device.communication.mqtt.MqttLifecycleComponent#stop(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if ((getMqttClient() != null) && (getMqttClient().isConnected())) {
	    try {
		getMqttClient().disconnect();
	    } catch (MqttException e) {
		LOGGER.error("Error shutting down MQTT device event receiver.", e);
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
     * setEventSource(com
     * .sitewhere.spi.device.communication.IInboundEventSource)
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
}