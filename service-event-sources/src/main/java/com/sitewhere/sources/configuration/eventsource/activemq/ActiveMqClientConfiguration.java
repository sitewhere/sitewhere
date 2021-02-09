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
