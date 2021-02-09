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