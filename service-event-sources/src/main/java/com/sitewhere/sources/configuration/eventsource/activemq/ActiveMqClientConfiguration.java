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
 * Configuration for an ActiveMQ client event source.
 */
public class ActiveMqClientConfiguration extends EventSourceConfiguration {

    /** Default remote URI */
    private static final String DEFAULT_REMOTE_URI = "";

    /** Default queue name */
    private static final String DEFAULT_QUEUE_NAME = "sitewhere";

    /** Number of consumers reading messages from the queue */
    private static final int DEFAULT_NUM_CONSUMERS = 3;

    /** Remote URI used for connection */
    private String remoteUri = DEFAULT_REMOTE_URI;

    /** Queue name used for inbound event data */
    private String queueName = DEFAULT_QUEUE_NAME;

    /** Number of consumers used to read messages from the queue */
    private int numConsumers = DEFAULT_NUM_CONSUMERS;

    public ActiveMqClientConfiguration(ITenantEngineLifecycleComponent component) {
	super(component);
    }

    /*
     * @see
     * com.sitewhere.sources.configuration.eventsource.EventSourceConfiguration#
     * loadFrom(com.fasterxml.jackson.databind.JsonNode)
     */
    @Override
    public void loadFrom(JsonNode json) throws SiteWhereException {
	this.remoteUri = configurableString("remoteUri", json, DEFAULT_REMOTE_URI);
	this.queueName = configurableString("queueName", json, DEFAULT_QUEUE_NAME);
	this.numConsumers = configurableInt("numConsumers", json, DEFAULT_NUM_CONSUMERS);
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
