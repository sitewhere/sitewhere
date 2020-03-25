/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.configuration.eventsource.activemq;

import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.sources.configuration.eventsource.EventSourceConfiguration;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Configuration for an ActiveMQ broker event source.
 */
public class ActiveMqBrokerConfiguration extends EventSourceConfiguration {

    /** Default broker name */
    private static final String DEFAULT_BROKER_NAME = "sitewhere";

    /** Default transport URI */
    private static final String DEFAULT_TRANSPORT_URI = "";

    /** Default queue name */
    private static final String DEFAULT_QUEUE_NAME = "sitewhere";

    /** Defatult number of consumers */
    private static final int DEFAULT_NUM_CONSUMERS = 3;

    /** Unique name of ActiveMQ broker */
    private String brokerName = DEFAULT_BROKER_NAME;

    /** URI for configuring transport */
    private String transportUri = DEFAULT_TRANSPORT_URI;

    /** Queue name used for inbound event data */
    private String queueName = DEFAULT_QUEUE_NAME;

    /** Number of consumers used to read messages from the queue */
    private int numConsumers = DEFAULT_NUM_CONSUMERS;

    public ActiveMqBrokerConfiguration(ITenantEngineLifecycleComponent component) {
	super(component);
    }

    /*
     * @see
     * com.sitewhere.sources.configuration.eventsource.EventSourceConfiguration#
     * loadFrom(com.fasterxml.jackson.databind.JsonNode)
     */
    @Override
    public void loadFrom(JsonNode json) throws SiteWhereException {
	this.brokerName = configurableString("brokerName", json, DEFAULT_BROKER_NAME);
	this.transportUri = configurableString("transportUri", json, DEFAULT_TRANSPORT_URI);
	this.queueName = configurableString("queueName", json, DEFAULT_QUEUE_NAME);
	this.numConsumers = configurableInt("numConsumers", json, DEFAULT_NUM_CONSUMERS);
    }

    public String getBrokerName() {
	return brokerName;
    }

    public void setBrokerName(String brokerName) {
	this.brokerName = brokerName;
    }

    public String getTransportUri() {
	return transportUri;
    }

    public void setTransportUri(String transportUri) {
	this.transportUri = transportUri;
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