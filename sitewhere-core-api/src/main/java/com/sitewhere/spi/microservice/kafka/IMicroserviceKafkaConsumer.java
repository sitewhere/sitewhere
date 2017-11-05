/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.kafka;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

/**
 * Component that consumes messages that are sent to a Kafka topic.
 * 
 * @author Derek
 */
public interface IMicroserviceKafkaConsumer extends ITenantLifecycleComponent {

    /**
     * Get parent microservice.
     * 
     * @return
     */
    public IMicroservice getMicroservice();

    /**
     * Get tenant engine associated with consumer. May be null for global
     * consumers.
     * 
     * @return
     */
    public IMicroserviceTenantEngine getTenantEngine();

    /**
     * Get unique consumer id.
     * 
     * @return
     * @throws SiteWhereException
     */
    public String getConsumerId() throws SiteWhereException;

    /**
     * Get unique consumer group id.
     * 
     * @return
     * @throws SiteWhereException
     */
    public String getConsumerGroupId() throws SiteWhereException;

    /**
     * Get name of Kafka topic which will provide the messages.
     * 
     * @return
     * @throws SiteWhereException
     */
    public String getSourceTopicName() throws SiteWhereException;

    /**
     * Received a message from the topic.
     * 
     * @param key
     * @param message
     * @throws SiteWhereException
     */
    public void received(String key, byte[] message) throws SiteWhereException;
}