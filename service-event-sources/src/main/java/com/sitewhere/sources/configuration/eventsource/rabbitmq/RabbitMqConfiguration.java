/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.configuration.eventsource.rabbitmq;

import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.sources.configuration.eventsource.EventSourceConfiguration;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Configuration for an RabbitMQ event source.
 */
public class RabbitMqConfiguration extends EventSourceConfiguration {

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

    public RabbitMqConfiguration(ITenantEngineLifecycleComponent component) {
	super(component);
    }

    /*
     * @see
     * com.sitewhere.sources.configuration.eventsource.EventSourceConfiguration#
     * loadFrom(com.fasterxml.jackson.databind.JsonNode)
     */
    @Override
    public void loadFrom(JsonNode json) throws SiteWhereException {
	this.connectionUri = configurableString("connectionUri", json, DEFAULT_CONNECTION_URI);
	this.queueName = configurableString("queueName", json, DEFAULT_QUEUE_NAME);
	this.numConsumers = configurableInt("numConsumers", json, DEFAULT_NUM_CONSUMERS);
	this.reconnectInterval = configurableInt("reconnectInterval", json, DEFAULT_RECONNECT_INTERVAL);
	this.durable = configurableBoolean("durable", json, false);
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

    public int getReconnectInterval() {
	return reconnectInterval;
    }

    public void setReconnectInterval(int reconnectInterval) {
	this.reconnectInterval = reconnectInterval;
    }

    public boolean isDurable() {
	return durable;
    }

    public void setDurable(boolean durable) {
	this.durable = durable;
    }
}